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
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    SearchableSpinner financial_year;
    TextView forgot_password;
    EditText mobile_no, password;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    Context context;
    String deviceToken,year;
    List<String> financialyearlist = new ArrayList<>();
    List<String> yearname = new ArrayList<>();
    String[] YEARNAME;

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
        forgot_password = findViewById(R.id.forgot_password);
        financial_year = findViewById(R.id.financial_year);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                deviceToken = task.getResult();
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForgotPasswordActivity.class));
            }
        });

       /*if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
            ((ActivityManager) context.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
            return;
       }*/

        btn_login.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(LoginActivity.this)) {
                //if (!password.getText().toString().matches( "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,15}$") || !password.getText().toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,15}$"))
                if (mobile_no.getText().toString().length() > 0 && password.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter Password", Toast.LENGTH_LONG).show();
                } else if (mobile_no.getText().toString().length() == 0 && password.getText().toString().length() > 0) {
                    Toast.makeText(getApplicationContext(), "Please enter User Name", Toast.LENGTH_LONG).show();
                }else if (mobile_no.getText().toString().length() > 0 && password.getText().toString().length() > 0) {
                    progressBarHelper.showProgressDialog();
                    Call<UserModel.UserData> call = apiCalling.ValidateUser(mobile_no.getText().toString(), password.getText().toString(),"");
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
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
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

    public void GetFinancialYear()
    {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        for (int i = 2020; i <= year; i++){
            String financialyear = "";
            if (i == year){
                if ((month + 1) > 3){
                    financialyear = year + " - " + (year + 1);
                    financialyearlist.add(financialyear);
                }
            }else{
                financialyear = i + " - " + (i + 1);
                financialyearlist.add(financialyear);
            }
        }
    }

    public void GetAllFinancialYear()
    {
        yearname.clear();
        yearname.add("Select Financial Year");
        yearname.addAll(financialyearlist);

        YEARNAME = new String[yearname.size()];
        YEARNAME = yearname.toArray(YEARNAME);
        bindyear();
    }

    public void bindyear()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,R.layout.support_simple_spinner_dropdown_item,YEARNAME);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        financial_year.setAdapter(adapter);
        financial_year.setSelection(financialyearlist.size());
        financial_year.setOnItemSelectedListener(selectyear);
    }

    AdapterView.OnItemSelectedListener selectyear = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            year = yearname.get(position);
            if (financial_year.getSelectedItem().equals("Select Financial Year")){
                ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                ((TextView) parent.getChildAt(0)).setTextSize(13);
            }else {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(13);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
