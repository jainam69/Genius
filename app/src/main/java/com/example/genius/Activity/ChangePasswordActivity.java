package com.example.genius.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.CommonModel;
import com.example.genius.databinding.ActivityChangePasswordBinding;
import com.example.genius.helper.Function;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    ActivityChangePasswordBinding binding;
    String oldpwd, newpwd, retypepwd;
    ApiCalling apiCalling;
    ProgressBarHelper progressBarHelper;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_arrow_left_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Password");
        context = ChangePasswordActivity.this;
        progressBarHelper = new ProgressBarHelper(ChangePasswordActivity.this, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)){
                    oldpwd = binding.oldPwd.getText().toString();
                    newpwd = binding.newPwd.getText().toString();
                    retypepwd = binding.retypePwd.getText().toString();
                    if (oldpwd.equalsIgnoreCase(""))
                        Toast.makeText(ChangePasswordActivity.this, "Please enter Old Password.", Toast.LENGTH_SHORT).show();
                    else if (newpwd.equalsIgnoreCase(""))
                        Toast.makeText(ChangePasswordActivity.this, "Please enter New Password.", Toast.LENGTH_SHORT).show();
                    else if (retypepwd.equalsIgnoreCase(""))
                        Toast.makeText(ChangePasswordActivity.this, "Please fill Re-type new Password.", Toast.LENGTH_SHORT).show();
                    else if (!retypepwd.equals(newpwd))
                        Toast.makeText(ChangePasswordActivity.this, "Password and Re-enter Password not match.", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(ChangePasswordActivity.this, model.getMessage(), Toast.LENGTH_SHORT).show();
                                            Preferences.getInstance(context).setBoolean(Preferences.KEY_LOGIN,false);
                                            startActivity(new Intent(context, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(ChangePasswordActivity.this, model.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<CommonModel> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}