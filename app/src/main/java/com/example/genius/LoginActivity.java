package com.example.genius;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.RolesModel;
import com.example.genius.Model.UserModel;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    EditText mobile_no,password;
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

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.checkNetworkConnection(LoginActivity.this)) {
                    if (mobile_no.getText().toString().length() > 0 && password.getText().toString().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Please enter Password", Toast.LENGTH_LONG).show();
                    }
                    else if (mobile_no.getText().toString().length() == 0 && password.getText().toString().length() > 0) {
                        Toast.makeText(getApplicationContext(), "Please enter User Name", Toast.LENGTH_LONG).show();
                    }
                    else if (mobile_no.getText().toString().length() > 0 || password.getText().toString().length() > 0) {
                        progressBarHelper.showProgressDialog();
                        Call<UserModel.UserData> call = apiCalling.ValidateUser(mobile_no.getText().toString(),password.getText().toString());
                        call.enqueue(new Callback<UserModel.UserData>() {
                            @Override
                            public void onResponse(Call<UserModel.UserData> call, Response<UserModel.UserData> response) {
                                progressBarHelper.hideProgressDialog();
                                if (response.isSuccessful()){
                                    UserModel.UserData data = response.body();
                                    if (data.isCompleted()){
                                        UserModel model = data.getData();
                                        if (model.getUserID()>0){
                                            Preferences.getInstance(context).setBoolean(Preferences.KEY_LOGIN,true);
                                            Preferences.getInstance(context).setLong(Preferences.KEY_USER_ID,model.getUserID());
                                            Preferences.getInstance(context).setLong(Preferences.KEY_BRANCH_ID,model.getBranchInfo().getBranchID());
                                            Preferences.getInstance(context).setString(Preferences.KEY_BRANCH_NAME,model.getBranchInfo().getBranchName());
                                            Preferences.getInstance(context).setString(Preferences.KEY_USER_NAME,model.getUsername());
                                            Preferences.getInstance(context).setInt(Preferences.KEY_USER_TYPE,Integer.parseInt(model.getUserType()));
                                            if (model.getRoles() != null){
                                                if(model.getRoles().size()>0){
                                                    List<RolesModel> list = model.getRoles();
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_STUDENTS, list.get(0).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_STAFF_MASTER, list.get(1).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_STANDARD_MASTER, list.get(2).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_SCHOOL_MASTER, list.get(3).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_SUBJECT_MASTER, list.get(4).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_ANNOUNCEMENT_MASTER, list.get(5).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_BATCH, list.get(6).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_ONLINE_PAYMENT, list.get(7).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_ADD_UPI_DETAIL, list.get(8).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_ATTENDANCE, list.get(9).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_TEST_SCHEDULE, list.get(10).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_TEST_MARKS, list.get(11).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_FEE_STRUCTURE, list.get(12).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_PRACTICE_PAPER, list.get(13).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_HOMEWORK, list.get(14).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_GALLERY, list.get(15).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_VIDEO, list.get(16).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_LIVE_VIDEO, list.get(17).getRoleID());
                                                    Preferences.getInstance(context).setLong(Preferences.KEY_UPLOAD_PAPER, list.get(18).getRoleID());

                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_STUDENTS_PERMISSION, list.get(0).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_STAFF_MASTER_PERMISSION, list.get(1).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_STANDARD_MASTER_PERMISSION, list.get(2).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_SCHOOL_MASTER_PERMISSION, list.get(3).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_SUBJECT_MASTER_PERMISSION, list.get(4).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_ANNOUNCEMENT_MASTER_PERMISSION, list.get(5).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_BATCH_PERMISSION, list.get(6).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_ONLINE_PAYMENT_PERMISSION, list.get(7).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_ADD_UPI_DETAIL_PERMISSION, list.get(8).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_ATTENDANCE_PERMISSION, list.get(9).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_TEST_SCHEDULE_PERMISSION, list.get(10).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_TEST_MARKS_PERMISSION, list.get(11).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_FEE_STRUCTURE_PERMISSION, list.get(12).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_PRACTICE_PAPER_PERMISSION, list.get(13).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_HOMEWORK_PERMISSION, list.get(14).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_GALLERY_PERMISSION, list.get(15).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_VIDEO_PERMISSION, list.get(16).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_LIVE_VIDEO_PERMISSION, list.get(17).isHasAccess());
                                                    Preferences.getInstance(context).setBoolean(Preferences.KEY_UPLOAD_PAPER_PERMISSION, list.get(18).isHasAccess());
                                                }
                                            }else{

                                            }
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            LoginActivity.this.finish();
                                        }else{
                                            Toast.makeText(LoginActivity.this, "Invalid Credentials passed..!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }else{
                                    Toast.makeText(LoginActivity.this, "Something Went Wrong..!!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<UserModel.UserData> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(LoginActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter User Name and Password", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
