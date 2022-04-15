package com.example.genius.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.UserModel;
import com.example.genius.databinding.ActivityLoginBinding;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.google.gson.Gson;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    Context context;
    String year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBarHelper = new ProgressBarHelper(this, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        context = LoginActivity.this;

        if (Preferences.getInstance(context).getBoolean(Preferences.KEY_LOGIN)){
            startActivity(new Intent(getApplicationContext(),SplashActivity.class));
            finish();
        }

        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForgotPasswordActivity.class));
            }
        });

        binding.btnLogin.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(LoginActivity.this)) {
                //if (!password.getText().toString().matches( "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,15}$") || !password.getText().toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,15}$"))
                if (binding.mobileNo.getText().toString().length() > 0 && binding.password.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter Password", Toast.LENGTH_LONG).show();
                } else if (binding.mobileNo.getText().toString().length() == 0 && binding.password.getText().toString().length() > 0) {
                    Toast.makeText(getApplicationContext(), "Please enter User Name", Toast.LENGTH_LONG).show();
                }else if (binding.mobileNo.getText().toString().length() > 0 && binding.password.getText().toString().length() > 0) {
                    progressBarHelper.showProgressDialog();
                    Call<UserModel.UserData> call = apiCalling.ValidateUser(binding.mobileNo.getText().toString(), binding.password.getText().toString(),"");
                    call.enqueue(new Callback<UserModel.UserData>() {
                        @Override
                        public void onResponse(Call<UserModel.UserData> call, Response<UserModel.UserData> response) {
                            if (response.isSuccessful()) {
                                UserModel.UserData data = response.body();
                                if (data.isCompleted()) {
                                    UserModel model = data.getData();
                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_LOGIN, true);
                                    Preferences.getInstance(context).setLong(Preferences.KEY_USER_ID, model.getUserID());
                                    Preferences.getInstance(context).setLong(Preferences.KEY_STAFF_ID, model.getStaffID());
                                    Preferences.getInstance(context).setLong(Preferences.KEY_BRANCH_ID, model.getBranchInfo().getBranchID());
                                    Preferences.getInstance(context).setString(Preferences.KEY_BRANCH_NAME, model.getBranchInfo().getBranchName());
                                    Preferences.getInstance(context).setString(Preferences.KEY_USER_NAME, model.getUsername());
                                    Preferences.getInstance(context).setInt(Preferences.KEY_USER_TYPE, Integer.parseInt(model.getUserType()));
                                    Preferences.getInstance(context).setString(Preferences.KEY_FINANCIAL_YEAR,year);
                                    Preferences.getInstance(context).setString(Preferences.KEY_APP_LOGO,model.getBranchInfo().getAppImagePath());
                                    startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                                    LoginActivity.this.finish();
                                } else {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBarHelper.hideProgressDialog();
                            } else {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(LoginActivity.this, "Something Went Wrong..!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel.UserData> call, Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(LoginActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Please enter User Name and Password", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
