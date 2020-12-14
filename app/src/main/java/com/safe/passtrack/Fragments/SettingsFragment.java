package com.safe.passtrack.Fragments;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.safe.passtrack.Controller;
import com.safe.passtrack.R;
import com.safe.passtrack.Security.Encrypt;

public class SettingsFragment extends Fragment {

    private SwitchMaterial switchAdmin;
    private MaterialTextView changePassword;
    private SharedPreferences sPref;
    private SharedPreferences.Editor editor;
    DevicePolicyManager manager;
    ComponentName componentName;
    private Encrypt encrypt;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        switchAdmin = view.findViewById(R.id.switchAdmin);
        changePassword = view.findViewById(R.id.changePassword);
        sPref = getActivity().getSharedPreferences("Settings",Context.MODE_PRIVATE);
        editor = sPref.edit();
        manager = (DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(getActivity(), Controller.class);
        encrypt = new Encrypt();

        changePass();

        boolean active = manager.isAdminActive(componentName);
        if(active){
            switchAdmin.setChecked(true);
        }

        switchAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
                    startActivityForResult(intent,101);
                    editor.putBoolean("adminMode",true);
                    editor.commit();
                }else{
                    manager.removeActiveAdmin(componentName);
                    editor.putBoolean("adminMode",false);
                    editor.commit();
                }
            }
        });

        return view;
    }

    private void changePass(){
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog changeDialog = new Dialog(getActivity());
                changeDialog.setContentView(R.layout.add_details_layout);

                MaterialTextView heading;
                final EditText passText,cnfrmPass,oldPassword;
                Button changePassword;

                heading = changeDialog.findViewById(R.id.detailsDialogHeading);
                heading.setText("Change Password");
                passText = changeDialog.findViewById(R.id.addDetails1);
                passText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passText.setHint("New Password");
                cnfrmPass = changeDialog.findViewById(R.id.addDetails2);
                cnfrmPass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                cnfrmPass.setHint("Confirm Password");
                oldPassword = changeDialog.findViewById(R.id.addDetails3);
                oldPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                oldPassword.setHint("Old Password");
                oldPassword.setVisibility(View.VISIBLE);
                changePassword = changeDialog.findViewById(R.id.encryptDetails);
                changePassword.setText("Change Password");

                changePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String oldPass = sPref.getString("pass",null);
                        String oldPassText = oldPassword.getText().toString();
                        String password = passText.getText().toString();
                        String cnfrmPassword = cnfrmPass.getText().toString();

                        if(!oldPass.equals(oldPassText)){
                            oldPassword.setError("old Password is Incorrect!");
                        } if(password.isEmpty() && cnfrmPassword.isEmpty()){
                            passText.setError("Can't be Empty!");
                            cnfrmPass.setError("Can't be Empty!");
                        }else if(password.isEmpty()){
                            passText.setError("Can't be Empty!");
                        }else if(cnfrmPassword.isEmpty()){
                            cnfrmPass.setError("Can't be Empty!");
                        }else if(!password.equals(cnfrmPassword)){
                            cnfrmPass.setError("Password doesn't match");
                        }else if(cnfrmPassword.length() < 8){
                            cnfrmPass.setError("Password Too Weak");
                            Toast.makeText(getActivity(), "Password must be of 8 characters", Toast.LENGTH_SHORT).show();
                        }else{
                            String encPass = encrypt.EncryptText(cnfrmPassword);
                            editor.putString("pass",encPass);
                            editor.commit();
                            Toast.makeText(getActivity(),"Done", Toast.LENGTH_SHORT).show();
                            changeDialog.dismiss();
                        }
                    }
                });
                changeDialog.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}