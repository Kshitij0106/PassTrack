package com.safe.passtrack.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.safe.passtrack.Database.AccountNamesDatabase;
import com.safe.passtrack.Database.Account_TextDetailsDatabase;
import com.safe.passtrack.Model.Account;
import com.safe.passtrack.R;
import com.safe.passtrack.ViewHolder.AccountAdapter;

import java.util.ArrayList;
import java.util.List;

public class AccountNamesFragment extends Fragment {
    private MaterialTextView categoryName;
    private RecyclerView recyclerViewAccounts;
    private Button addAccountButton;
    private AccountNamesDatabase namesDatabase;
    private List<Account> namesList;
    private Account_TextDetailsDatabase detailsDatabase;

    public AccountNamesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_names, container, false);

        Bundle bundle = this.getArguments();
        String cName = bundle.getString("category");

        categoryName = view.findViewById(R.id.categoryName);
        recyclerViewAccounts = view.findViewById(R.id.recyclerViewAccounts);
        addAccountButton = view.findViewById(R.id.addAccountButton);
        namesDatabase = new AccountNamesDatabase(getActivity());
        namesList = new ArrayList<>();
        detailsDatabase = new Account_TextDetailsDatabase(getActivity());

        categoryName.setText(cName);

        addAccount();
        showAccounts();

        return view;
    }

    private void showAccounts() {
        namesList = namesDatabase.getList(categoryName.getText().toString());
        AccountAdapter adapter = new AccountAdapter(getContext(),namesList,"name");
        adapter.setOnItemClickListener(new AccountAdapter.onItemClickListener() {
            @Override
            public void onItemClicked(int pos, String name) {
                Bundle accNameBundle = new Bundle();
                accNameBundle.putString("accName",name);
                AccountFragment acct = new AccountFragment();
                acct.setArguments(accNameBundle);
                getFragmentManager().beginTransaction().replace(R.id.mainActivity,acct).addToBackStack(" ").commit();
            }
        });
        adapter.setOnLongPressedListener(new AccountAdapter.onLongPressListener() {
            @Override
            public void onLongPressedListener(int pos, final String accName) {
                boolean rem = detailsDatabase.checkDetails(accName);
                if(rem){
                    Toast.makeText(getActivity(), "It contains multiple Accounts", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder delDialog = new AlertDialog.Builder(getActivity());
                    delDialog.setMessage("Delete Account");
                    delDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            namesDatabase.removeFromList(accName);
                            Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    delDialog.show();
                }
            }
        });
        recyclerViewAccounts.setHasFixedSize(true);
        recyclerViewAccounts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAccounts.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void addAccount() {
        addAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder accName = new AlertDialog.Builder(getActivity());
                accName.setTitle("Account Name");
                final EditText nameEdit = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                nameEdit.setLayoutParams(lp);
                accName.setView(nameEdit);
                accName.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEdit.getText().toString();
                        if(!name.isEmpty()){
                            boolean check = namesDatabase.addToList(name,categoryName.getText().toString());

                            if(check){
                                Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(), "Try Again!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getActivity(), "Can't be Empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                accName.show();
            }
        });
    }

}