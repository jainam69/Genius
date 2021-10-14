package com.example.genius;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.CommonModel;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText old_pwd, new_pwd, retype_pwd;
    Button btn_save;
    String oldpwd, newpwd, retypepwd;
    ApiCalling apiCalling;
    ProgressBarHelper progressBarHelper;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        try {
            ActionBar ac = getSupportActionBar();
            ac.setTitle("Change Password");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        old_pwd = findViewById(R.id.old_pwd);
        new_pwd = findViewById(R.id.new_pwd);
        retype_pwd = findViewById(R.id.retype_pwd);
        btn_save = findViewById(R.id.btn_save);
        context = ChangePasswordActivity.this;
        progressBarHelper = new ProgressBarHelper(ChangePasswordActivity.this, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldpwd = old_pwd.getText().toString();
                newpwd = new_pwd.getText().toString();
                retypepwd = retype_pwd.getText().toString();
                if (oldpwd.equalsIgnoreCase(""))
                    Toast.makeText(ChangePasswordActivity.this, "Please enter Old Password..!!", Toast.LENGTH_SHORT).show();
                else if (newpwd.equalsIgnoreCase(""))
                    Toast.makeText(ChangePasswordActivity.this, "Please enter New Password..!!", Toast.LENGTH_SHORT).show();
                else if (retypepwd.equalsIgnoreCase(""))
                    Toast.makeText(ChangePasswordActivity.this, "Please fill Re-type new Password..!!", Toast.LENGTH_SHORT).show();
                else if (!retypepwd.equals(newpwd))
                    Toast.makeText(ChangePasswordActivity.this, "Password and Re-enter Password not match..", Toast.LENGTH_SHORT).show();
                else {
                    progressBarHelper.showProgressDialog();
                    Call<CommonModel> call = apiCalling.ChangePassword(Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), newpwd, oldpwd);
                    call.enqueue(new Callback<CommonModel>() {
                        @Override
                        public void onResponse(Call<CommonModel> call, Response<CommonModel> response) {
                            if (response.isSuccessful()) {
                                CommonModel model = response.body();
                                if (model.isCompleted()) {
                                    if (model.isData()) {
                                        Toast.makeText(ChangePasswordActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "Password Not Changed..", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(Call<CommonModel> call, Throwable t) {
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            }
        });
    }
}