package com.safe.passtrack.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.safe.passtrack.Database.AccountCategoriesDatabase;
import com.safe.passtrack.Database.AccountNamesDatabase;
import com.safe.passtrack.Model.Account;
import com.safe.passtrack.R;
import com.safe.passtrack.ViewHolder.AccountAdapter;

import java.util.ArrayList;
import java.util.List;

public class AccountCategoriesFragment extends Fragment {
    private RecyclerView recyclerViewAccountsCategories;
    private Button addCategoryButton;
    private AccountCategoriesDatabase categoriesDatabase;
    private List<Account> categoriesList;
    private AccountNamesDatabase namesDatabase;

    public AccountCategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_categories, container, false);

        recyclerViewAccountsCategories = view.findViewById(R.id.recyclerViewAccountsCategories);
        addCategoryButton = view.findViewById(R.id.addCategoryButton);
        categoriesDatabase = new AccountCategoriesDatabase(getContext());
        categoriesList = new ArrayList<>();
        namesDatabase = new AccountNamesDatabase(getContext());

        showCategories();
        addCategory();

        return view;
    }

    private void showCategories() {
        categoriesList = categoriesDatabase.getCategories();

        AccountAdapter adapter = new AccountAdapter(getActivity(), categoriesList, "category");
        adapter.setOnItemClickListener(new AccountAdapter.onItemClickListener() {
            @Override
            public void onItemClicked(int pos, String name) {
                Bundle categoryBundle = new Bundle();
                categoryBundle.putString("category", name);
                AccountNamesFragment namesFragment = new AccountNamesFragment();
                namesFragment.setArguments(categoryBundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity, namesFragment).addToBackStack("").commit();
            }
        });
        adapter.setOnLongPressedListener(new AccountAdapter.onLongPressListener() {
            @Override
            public void onLongPressedListener(int pos, final String accName) {
                boolean check = namesDatabase.checkCategory(accName);
                if (check) {
                    Toast.makeText(getActivity(), "It contains multiple Accounts", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder delDialog = new AlertDialog.Builder(getContext());
                    delDialog.setTitle("Delete Category");
                    delDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            categoriesDatabase.deleteCategory(accName);
                            Toast.makeText(getActivity(), "Category Deleted", Toast.LENGTH_SHORT).show();
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
        recyclerViewAccountsCategories.setHasFixedSize(true);
        recyclerViewAccountsCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAccountsCategories.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void addCategory() {
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder accType = new AlertDialog.Builder(getActivity());
                accType.setTitle("Type Of Account");

                final EditText typeEdit = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                typeEdit.setLayoutParams(lp);

                accType.setView(typeEdit);
                accType.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String type = typeEdit.getText().toString();
                        if (type.isEmpty()) {
                            Toast.makeText(getActivity(), "Can't be Empty", Toast.LENGTH_SHORT).show();
                        } else {
                            boolean check = categoriesDatabase.addCategory(type);
                            if (check) {
                                Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Try Again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                accType.show();
            }
        });
    }

}