package com.example.genius.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.se.omapi.Session;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.ProfileModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.StaffModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    EditText name, mobile_no, email;
    Button btn_save;
    ApiCalling apiCalling;
    ProgressBarHelper progressBarHelper;
    Context context;
    long TransactionId = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_arrow_left_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        context = ProfileActivity.this;
        progressBarHelper = new ProgressBarHelper(ProfileActivity.this, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        name = findViewById(R.id.name);
        mobile_no = findViewById(R.id.mobile_no);
        email = findViewById(R.id.email);
        btn_save = findViewById(R.id.btn_save);

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            Call<StaffModel.StaffData1> call = apiCalling.GetStaffByID(Preferences.getInstance(context).getLong(Preferences.KEY_STAFF_ID));
            call.enqueue(new Callback<StaffModel.StaffData1>() {
                @Override
                public void onResponse(@NonNull Call<StaffModel.StaffData1> call, @NonNull Response<StaffModel.StaffData1> response) {
                    if (response.isSuccessful()) {
                        StaffModel.StaffData1 model = response.body();
                        if (model != null && model.isCompleted()) {
                            name.setText(model.getData().getName());
                            mobile_no.setText(model.getData().getMobileNo());
                            email.setText(model.getData().getEmailID());
                            TransactionId = model.getData().getTransaction().getTransactionId();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<StaffModel.StaffData1> call, @NonNull Throwable t) {
                    progressBarHelper.hideProgressDialog();
                }
            });
        } else {
            Function.showToast(context, "Please check your internet connectivity...");
        }

        btn_save.setOnClickListener(v -> {
            if (name.getText().toString().trim().equals("")) {
                Function.showToast(context, "Please enter name");
            } else if (mobile_no.getText().toString().trim().equals("")) {
                Function.showToast(context, "Please enter mobile no (login id)");
            } else if (email.getText().toString().trim().equals("")) {
                Function.showToast(context, "Please enter email");
            } else {
                if (Function.isNetworkAvailable(context)) {
                    progressBarHelper.showProgressDialog();
                    Call<ProfileModel> call = apiCalling.UpdateProfile(new StaffModel(Preferences.getInstance(context).getLong(Preferences.KEY_STAFF_ID)
                            , Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), name.getText().toString().trim()
                            , email.getText().toString().trim(), mobile_no.getText().toString().trim()
                            , new TransactionModel(TransactionId, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID))
                            , new RowStatusModel(1), new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID))));
                    call.enqueue(new Callback<ProfileModel>() {
                        @Override
                        public void onResponse(@NonNull Call<ProfileModel> call, @NonNull Response<ProfileModel> response) {
                            if (response.isSuccessful()) {
                                ProfileModel model = response.body();
                                if (model != null && model.isCompleted()) {
                                    if (Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME).equals(mobile_no.getText().toString().trim())) {
                                        Function.showToast(context, model.getMessage());
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("Your Mobile Number has Changed!! Please Login Again!!");
                                        builder.setPositiveButton("OK", (dialog, which) -> {
                                            Preferences.getInstance(context).setBoolean(Preferences.KEY_LOGIN, false);
                                            startActivity(new Intent(context, LoginActivity.class));
                                            finish();
                                        });
                                        AlertDialog dialog = builder.create();
                                        dialog.setCancelable(false);
                                        dialog.setOnShowListener(arg0 -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple_200)));
                                        dialog.show();
                                        Preferences.getInstance(context).setBoolean(Preferences.KEY_LOGIN, false);
                                    }
                                } else {
                                    if (model != null) {
                                        Function.showToast(context, model.getMessage());
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NonNull Call<ProfileModel> call, @NonNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                } else {
                    Function.showToast(context, "Please check your internet connectivity...");
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
