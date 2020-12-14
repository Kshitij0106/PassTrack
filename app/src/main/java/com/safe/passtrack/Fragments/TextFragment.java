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

public class TextFragment extends Fragment {
    private RecyclerView recyclerViewTexts;
    private Button addTextButton;
    private SharedPreferences sPref;
    private SharedPreferences.Editor editor;
    private Encrypt encrypt;
    private Account_TextDetailsDatabase database;
    private List<Text_Details> textDetailsList;

    public TextFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_text, container, false);

        recyclerViewTexts = view.findViewById(R.id.recyclerViewTexts);
        addTextButton = view.findViewById(R.id.addTextButton);
        sPref = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = sPref.edit();
        encrypt = new Encrypt();
        database = new Account_TextDetailsDatabase(getContext());
        textDetailsList = new ArrayList<>();

        showText();
        addText();

        return view;
    }

    private void showText() {
        textDetailsList = database.showDetails("Notes");

        final Text_DetailsAdapter adapter = new Text_DetailsAdapter(getContext(), textDetailsList);
        adapter.setOnLongPressedListener(new Text_DetailsAdapter.onLongPressListener() {
            @Override
            public void onLongPressedListener(int pos, final String username) {
                AlertDialog.Builder delDialog = new AlertDialog.Builder(getContext());
                delDialog.setTitle("Delete Account");
                delDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog passDialog = passwordDialog("Delete", "Notes", username);
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
        recyclerViewTexts.setHasFixedSize(true);
        recyclerViewTexts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTexts.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void addText() {
        final Dialog detailsDialog = new Dialog(getActivity());
        detailsDialog.setCancelable(true);
        detailsDialog.setContentView(R.layout.add_details_layout);

        final TextInputEditText nameEdit, textEdit;
        MaterialTextView detailsDialogHeading;
        Button encryptButton;

        nameEdit = detailsDialog.findViewById(R.id.addDetails1);
        nameEdit.setHint("Name");
        textEdit = detailsDialog.findViewById(R.id.addDetails2);
        textEdit.setHint("Text");
        detailsDialogHeading = detailsDialog.findViewById(R.id.detailsDialogHeading);
        detailsDialogHeading.setText("New Text");
        encryptButton = detailsDialog.findViewById(R.id.encryptDetails);

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEdit.getText().toString();
                String text = textEdit.getText().toString();

                if (name.isEmpty() && text.isEmpty()) {
                    nameEdit.setError("Can't be Empty!");
                    textEdit.setError("Can't be Empty!");
                } else if (name.isEmpty()) {
                    nameEdit.setError("Can't be Empty!");
                } else if (text.isEmpty()) {
                    textEdit.setError("Can't be Empty!");
                } else {
                    String encText = encrypt.EncryptText(text);
                    boolean check = database.addDetails("Notes", name, encText);
                    if (check) {
                        Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                        nameEdit.setText("");
                        textEdit.setText("");
                    } else {
                        nameEdit.setText("");
                        textEdit.setText("");
                        Toast.makeText(getActivity(), "Try Again!", Toast.LENGTH_SHORT).show();
                    }
                    detailsDialog.dismiss();
                }
            }
        });

        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sPref.contains("pass")) {
                    Dialog passDialog = passwordDialog("Add","Notes",nameEdit.getText().toString());
                    passDialog.show();
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
                } else if (cnfrmPassword.length() < 8) {
                    passwordEdit.setError("Password Too Weak");
                    Toast.makeText(getActivity(), "Password must be of 8 characters", Toast.LENGTH_SHORT).show();
                } else {
                    if (work.equals("Add")) {
                        // encrypt it and save in shared pref
                        String encPass = encrypt.EncryptText(cnfrmPassword);
                        editor.putString("pass", encPass);
                        editor.commit();
                        Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
                        passwordDialog.dismiss();
                    }
                    if(work.equals("Delete")){
                        boolean rem = database.removeAccount(accName, userName);
                        if (rem) {
                            Toast.makeText(getContext(), "Note Deleted", Toast.LENGTH_SHORT).show();
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