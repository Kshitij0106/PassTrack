package com.safe.passtrack.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.safe.passtrack.Database.Account_TextDetailsDatabase;
import com.safe.passtrack.Security.Encrypt;
import com.safe.passtrack.Model.Text_Details;
import com.safe.passtrack.R;
import com.safe.passtrack.ViewHolder.Text_DetailsAdapter;

import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment {
    private MaterialTextView personalAccountName;
    private RecyclerView recyclerViewPersonalAccounts;
    private Button addPersonalAccountButton;
    private Account_TextDetailsDatabase detailsDatabase;
    private List<Text_Details> accDetailsList;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Encrypt encrypt;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        Bundle nameBundle = this.getArguments();
        String personalAccName = nameBundle.getString("accName");

        personalAccountName = view.findViewById(R.id.personalAccountName);
        recyclerViewPersonalAccounts = view.findViewById(R.id.recyclerViewPersonalAccounts);
        addPersonalAccountButton = view.findViewById(R.id.addPersonalAccountButton);
        detailsDatabase = new Account_TextDetailsDatabase(getContext());
        accDetailsList = new ArrayList<>();
        pref = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = pref.edit();
        encrypt = new Encrypt();

        personalAccountName.setText(personalAccName);

        showPersonalAccount(personalAccName);
        addPersonalAccount(personalAccName);

        return view;
    }

    private void showPersonalAccount(final String personalAccName) {
        accDetailsList = detailsDatabase.showDetails(personalAccName);

        Text_DetailsAdapter adapter = new Text_DetailsAdapter(getActivity(), accDetailsList);
        adapter.setOnLongPressedListener(new Text_DetailsAdapter.onLongPressListener() {
            @Override
            public void onLongPressedListener(int pos, final String username) {
                AlertDialog.Builder delDialog = new AlertDialog.Builder(getContext());
                delDialog.setTitle("Delete Account");
                delDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog passDialog = passwordDialog("Delete", personalAccName, username);
                        passDialog.show();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                delDialog.show();
            }
        });
        recyclerViewPersonalAccounts.setHasFixedSize(true);
        recyclerViewPersonalAccounts.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPersonalAccounts.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void addPersonalAccount(final String personalAccName) {
        final Dialog detailsDialog = new Dialog(getActivity());
        detailsDialog.setCancelable(true);
        detailsDialog.setContentView(R.layout.add_details_layout);
        final TextInputEditText userName, password;
        Button encryptDetails;

        userName = detailsDialog.findViewById(R.id.addDetails1);
        userName.setHint("Username/Email/Phone");
        password = detailsDialog.findViewById(R.id.addDetails2);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setHint("Password/Pin");
        encryptDetails = detailsDialog.findViewById(R.id.encryptDetails);

        encryptDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accUsername = userName.getText().toString();
                String accPassword = password.getText().toString();

                if (accUsername.isEmpty() && accPassword.isEmpty()) {
                    userName.setError("Can't be Empty!");
                    password.setError("Can't be Empty!");
                } else if (accUsername.isEmpty()) {
                    userName.setError("Can't be Empty!");
                } else if (accPassword.isEmpty()) {
                    password.setError("Can't be Empty!");
                } else {
                    // save it in database
                    String encPass = encrypt.EncryptText(accPassword);
                    boolean check = detailsDatabase.addDetails(personalAccName, accUsername, encPass);
                    if (check) {
                        Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                        userName.setText("");
                        password.setText("");
                    } else {
                        userName.setText("");
                        password.setText("");
                        Toast.makeText(getActivity(), "Try Again!", Toast.LENGTH_SHORT).show();
                    }

                    detailsDialog.dismiss();
                }
            }
        });

        addPersonalAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pref.contains("pass")) {
                    Dialog pd = passwordDialog("Add", personalAccName, userName.getText().toString());
                    pd.show();
                } else {
                    detailsDialog.show();
                }
            }
        });
    }

    private Dialog passwordDialog(final String work, final String accName, final String userName) {
        final Dialog passwordDialog = new Dialog(getActivity());
        passwordDialog.setCancelable(true);
        passwordDialog.setContentView(R.layout.add_details_layout);
        final TextInputEditText passwordEdit, cnfrmPasswordEdit;
        MaterialTextView detailsDialogHeading;
        Button setPasswordButton;

        passwordEdit = passwordDialog.findViewById(R.id.addDetails1);
        passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordEdit.setHint("Password");
        cnfrmPasswordEdit = passwordDialog.findViewById(R.id.addDetails2);
        cnfrmPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        cnfrmPasswordEdit.setHint("Confirm Password");
        detailsDialogHeading = passwordDialog.findViewById(R.id.detailsDialogHeading);
        detailsDialogHeading.setText("Create Password");
        setPasswordButton = passwordDialog.findViewById(R.id.encryptDetails);
        if(work.equals("Add")) {
            setPasswordButton.setText("Save");
        }else if(work.equals("Delete")){
            setPasswordButton.setText("Delete");
        }

        setPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEdit.getText().toString();
                String cnfrmPassword = cnfrmPasswordEdit.getText().toString();

                if (password.isEmpty() && cnfrmPassword.isEmpty()) {
                    passwordEdit.setError("Can't be Empty!");
                    cnfrmPasswordEdit.setError("Can't be Empty!");
                } else if (password.isEmpty()) {
                    passwordEdit.setError("Can't be Empty!");
                } else if (cnfrmPassword.isEmpty()) {
                    cnfrmPasswordEdit.setError("Can't be Empty!");
                } else if (!password.equals(cnfrmPassword)) {
                    cnfrmPasswordEdit.setError("Password doesn't match");
                } else if (password.length() < 8) {
                    passwordEdit.setError("Password Too Weak");
                    Toast.makeText(getActivity(), "Password must be of 8 characters", Toast.LENGTH_SHORT).show();
                } else {
                    if (work.equals("Add")) {
                        String encPass = encrypt.EncryptText(cnfrmPassword);
                        editor.putString("pass", encPass);
                        editor.commit();
                        Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
                        passwordDialog.dismiss();
                    }
                    if (work.equals("Delete")) {
                        boolean rem = detailsDatabase.removeAccount(accName, userName);
                        if (rem) {
                            Toast.makeText(getContext(), "Account Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Try again Later", Toast.LENGTH_SHORT).show();
                        }
                        passwordDialog.dismiss();
                    }
                }
            }
        });
        return passwordDialog;
    }

}