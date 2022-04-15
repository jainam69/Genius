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
import com.example.genius.databinding.ActivityProfileBinding;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    ApiCalling apiCalling;
    ProgressBarHelper progressBarHelper;
    Context context;
    long TransactionId = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_arrow_left_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
        context = ProfileActivity.this;
        progressBarHelper = new ProgressBarHelper(ProfileActivity.this, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            Call<StaffModel.StaffData1> call = apiCalling.GetStaffByID(Preferences.getInstance(context).getLong(Preferences.KEY_STAFF_ID));
            call.enqueue(new Callback<StaffModel.StaffData1>() {
                @Override
                public void onResponse(@NonNull Call<StaffModel.StaffData1> call, @NonNull Response<StaffModel.StaffData1> response) {
                    if (response.isSuccessful()) {
                        StaffModel.StaffData1 model = response.body();
                        if (model != null && model.isCompleted()) {
                            binding.name.setText(model.getData().getUserNameNew());
                            binding.mobileNo.setText(model.getData().getMobileNo());
                            binding.email.setText(model.getData().getEmailID());
                            binding.fullName.setText(model.getData().getName());
                            TransactionId = model.getData().getTransaction().getTransactionId();
                        }
                        progressBarHelper.hideProgressDialog();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<StaffModel.StaffData1> call, @NonNull Throwable t) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Function.showToast(context, "Please check your internet connectivity...");
        }

        binding.btnSave.setOnClickListener(v -> {
            if (binding.fullName.getText().toString().isEmpty()) {
                Function.showToast(context, "Please enter Full Name");
            } else if (binding.name.getText().toString().isEmpty()){
                Toast.makeText(context, "Please enter User Name(Login ID).", Toast.LENGTH_SHORT).show();
            } else if (binding.mobileNo.getText().toString().trim().equals("")) {
                Function.showToast(context, "Please enter mobile no(Forgot Password).");
            } else if (binding.email.getText().toString().trim().equals("")) {
                Function.showToast(context, "Please enter email");
            } else {
                if (Function.isNetworkAvailable(context)) {
                    progressBarHelper.showProgressDialog();
                    Call<ProfileModel> call = apiCalling.UpdateProfile(new StaffModel(Preferences.getInstance(context).getLong(Preferences.KEY_STAFF_ID)
                            , Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), binding.fullName.getText().toString().trim()
                            , binding.email.getText().toString().trim(), binding.mobileNo.getText().toString().trim()
                            , new TransactionModel(TransactionId, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID))
                            , new RowStatusModel(1), new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)),binding.name.getText().toString()));
                    call.enqueue(new Callback<ProfileModel>() {
                        @Override
                        public void onResponse(@NonNull Call<ProfileModel> call, @NonNull Response<ProfileModel> response) {
                            if (response.isSuccessful()) {
                                ProfileModel model = response.body();
                                if (model.isCompleted()) {
                                    if (Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME).equals(binding.name.getText().toString().trim())) {
                                        Function.showToast(context, model.getMessage());
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage(model.getMessage());
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
                                    Function.showToast(context, model.getMessage());
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ProfileModel> call, @NonNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
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
