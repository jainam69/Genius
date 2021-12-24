package com.example.genius.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.UserModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    EditText mobile_no, password;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);
        mobile_no = findViewById(R.id.mobile_no);
        password = findViewById(R.id.password);
        progressBarHelper = new ProgressBarHelper(this, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        context = LoginActivity.this;

        btn_login.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(LoginActivity.this)) {
                if (mobile_no.getText().toString().length() > 0 && password.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter Password", Toast.LENGTH_LONG).show();
                } else if (mobile_no.getText().toString().length() == 0 && password.getText().toString().length() > 0) {
                    Toast.makeText(getApplicationContext(), "Please enter User Name", Toast.LENGTH_LONG).show();
                } else if (mobile_no.getText().toString().length() > 0 || password.getText().toString().length() > 0) {
                    progressBarHelper.showProgressDialog();
                    Call<UserModel.UserData> call = apiCalling.ValidateUser(mobile_no.getText().toString(), password.getText().toString());
                    call.enqueue(new Callback<UserModel.UserData>() {
                        @Override
                        public void onResponse(Call<UserModel.UserData> call, Response<UserModel.UserData> response) {
                            if (response.isSuccessful()) {
                                UserModel.UserData data = response.body();
                                if (data.isCompleted()) {
                                    UserModel model = data.getData();
                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_LOGIN, true);
                                    Preferences.getInstance(context).setLong(Preferences.KEY_USER_ID, model.getUserID());
                                    Preferences.getInstance(context).setLong(Preferences.KEY_BRANCH_ID, model.getBranchInfo().getBranchID());
                                    Preferences.getInstance(context).setString(Preferences.KEY_BRANCH_NAME, model.getBranchInfo().getBranchName());
                                    Preferences.getInstance(context).setString(Preferences.KEY_USER_NAME, model.getUsername());
                                    Preferences.getInstance(context).setInt(Preferences.KEY_USER_TYPE, Integer.parseInt(model.getUserType()));
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    LoginActivity.this.finish();
                                }else {
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
