package com.safe.passtrack.ViewHolder;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.safe.passtrack.Database.Account_TextDetailsDatabase;
import com.safe.passtrack.Security.Encrypt;
import com.safe.passtrack.Model.Text_Details;
import com.safe.passtrack.R;

import java.util.List;

public class Text_DetailsAdapter extends RecyclerView.Adapter<Text_DetailsAdapter.TextViewHolder> {

    private Context context;
    private List<Text_Details> textDetailsList;
    private Encrypt encrypt;
    private Account_TextDetailsDatabase database;
    private SharedPreferences sPref;
    private SharedPreferences.Editor editor;
    private int i;
    private DevicePolicyManager manager;
    private onLongPressListener adapterListener;

    public interface onLongPressListener{
        void onLongPressedListener(int pos,String username);
    }

    public void setOnLongPressedListener(onLongPressListener onLongPressListener){
        adapterListener = onLongPressListener;
    }

    public Text_DetailsAdapter(Context context, List<Text_Details> textDetailsList) {
        this.context = context;
        this.textDetailsList = textDetailsList;
        encrypt = new Encrypt();
        database = new Account_TextDetailsDatabase(context);
        sPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = sPref.edit();
        int i = 0;
        manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    @Override
    public void onBindViewHolder(@NonNull final TextViewHolder holder, int position) {
        holder.showDetails1.setText(textDetailsList.get(position).getName());
        final String type = textDetailsList.get(position).getType();
        holder.encryptionOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder pd = new AlertDialog.Builder(context);
                pd.setMessage("Enter Password");
                final EditText passEdit = new EditText(context);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                passEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passEdit.setHint("Password");
                pd.setView(passEdit);
                pd.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pass = passEdit.getText().toString();
                        if (pass.isEmpty()) {
                            passEdit.setError("Incorrect Password");
                        } else {
                            String code = sPref.getString("pass", "");
                            String decPass = encrypt.DecryptText(code);
                            if (decPass.equals(pass)) {
                                String txt = database.getPass(type,holder.showDetails1.getText().toString());
                                String accPass = encrypt.DecryptText(txt);
                                holder.showDetails2.setText(accPass);
                                holder.encryptionOff.setVisibility(View.VISIBLE);
                                holder.encryptionOn.setVisibility(View.GONE);
                            } else if (!decPass.equals(pass)) {
                                if (i >= 4) {
                                    boolean admin = sPref.getBoolean("adminMode", false);
                                    if (admin) {
                                        manager.lockNow();
                                    } else {
                                        holder.encryptionOn.setEnabled(false);
                                    }
                                } else {
                                    ++i;
                                    Toast.makeText(context, "Incorrect Password " + (5 - i) + " tries remaining", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                pd.show();
            }
        });
        holder.encryptionOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showDetails2.setText("***********************");
                holder.encryptionOn.setVisibility(View.VISIBLE);
                holder.encryptionOff.setVisibility(View.GONE);
            }
        });
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_details_layout, parent, false);
        TextViewHolder tvh = new TextViewHolder(view);
        return tvh;
    }

    @Override
    public int getItemCount() {
        return textDetailsList.size();
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        public MaterialTextView showDetails1, showDetails2;
        public ImageView encryptionOn, encryptionOff;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);

            showDetails1 = itemView.findViewById(R.id.showDetails1);
            showDetails2 = itemView.findViewById(R.id.showDetails2);
            encryptionOn = itemView.findViewById(R.id.encryptionToggleOn);
            encryptionOff = itemView.findViewById(R.id.encryptionToggleOff);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(adapterListener!=null){
                        int pos = getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            adapterListener.onLongPressedListener(pos,showDetails1.getText().toString());
                        }
                    }
                    return false;
                }
            });
        }
    }
}
