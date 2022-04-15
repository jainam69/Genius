package com.example.genius.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.databinding.ActivityForgotPasswordBinding;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBarHelper = new ProgressBarHelper(this, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        context = ForgotPasswordActivity.this;

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)){
                    if (binding.mobileNo.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please enter Mobile Number.", Toast.LENGTH_SHORT).show();
                    }else if (binding.mobileNo.getText().toString().length() < 10){
                        Toast.makeText(context, "Please enter valid Mobile Number.", Toast.LENGTH_SHORT).show();
                    }else {
                        progressBarHelper.showProgressDialog();
                        UserModel user = new UserModel(binding.mobileNo.getText().toString());
                        Call<CommonModel.ResponseModel> call = apiCalling.Forgot_Password(user);
                        call.enqueue(new Callback<CommonModel.ResponseModel>() {
                            @Override
                            public void onResponse(Call<CommonModel.ResponseModel> call, Response<CommonModel.ResponseModel> response) {
                                if (response.isSuccessful()){
                                    CommonModel.ResponseModel data = response.body();
                                    if (data.isCompleted()){
                                        CommonModel model = data.getData();
                                        if (model.isStatus()){
                                            Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                        }else {
                                            Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<CommonModel.ResponseModel> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}