package com.safe.passtrack.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.safe.passtrack.Model.Account;
import com.safe.passtrack.R;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private List<Account> accountCategoryList;
    private Context context;
    private String show;
    private onItemClickListener adapterListener;
    private onLongPressListener pressListener;

    public interface onItemClickListener {
        void onItemClicked(int pos, String name);
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        adapterListener = onItemClickListener;
    }

    public interface onLongPressListener{
        void onLongPressedListener(int pos,String accName);
    }

    public void setOnLongPressedListener(onLongPressListener onLongPressListener){
        pressListener = onLongPressListener;
    }

    public AccountAdapter(Context context, List<Account> accountCategoryList, String show) {
        this.context = context;
        this.accountCategoryList = accountCategoryList;
        this.show = show;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        if (show == "category") {
            holder.accountName.setText(accountCategoryList.get(position).getAccountCategory());
        } else if (show == "name") {
            holder.accountName.setText(accountCategoryList.get(position).getAccountName());
        }
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_layout, parent, false);
        AccountViewHolder avh = new AccountViewHolder(view);
        return avh;
    }

    @Override
    public int getItemCount() {
        return accountCategoryList.size();
    }

    public class AccountViewHolder extends RecyclerView.ViewHolder {
        public MaterialTextView accountName;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.accountName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapterListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            adapterListener.onItemClicked(pos, accountName.getText().toString());
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (pressListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            pressListener.onLongPressedListener(pos, accountName.getText().toString());
                        }
                    }
                    return false;
                }
            });
        }
    }
}
