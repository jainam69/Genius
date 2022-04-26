package com.example.genius.Activity.User_Permission.User_Rights;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genius.API.ApiCalling;
import com.example.genius.Activity.User_Permission.Role_Rights.RoleRightsActivity;
import com.example.genius.Activity.User_Permission.UserPermissionSelectActivity;
import com.example.genius.Adapter.UserRightsAdapter;
import com.example.genius.Model.RoleModel;
import com.example.genius.Model.RoleRightsModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Model.UserData1;
import com.example.genius.Model.UserModel;
import com.example.genius.Model.UserRightsModel;
import com.example.genius.R;
import com.example.genius.databinding.ActivityUserRightsBinding;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRightsActivity extends AppCompatActivity {

    ActivityUserRightsBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> rolename = new ArrayList<>(); List<Long> roleid = new ArrayList<>(); List<String> username = new ArrayList<>();
        List<Long> userid = new ArrayList<>();
    String[] ROLENAME; Long[] ROLEID; String[] USERNAME;Long[] USERID; long rolename_id;long username_id;
    Intent i;UserRightsModel adaptermodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserRightsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("User Rights Master Entry");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        context = UserRightsActivity.this;
        progressBarHelper = new ProgressBarHelper(this, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        i = getIntent();
        if (i.hasExtra("MODEL_DATA")){
            adaptermodel = new Gson().fromJson(i.getStringExtra("MODEL_DATA"),UserRightsModel.class);
        }

        if (Function.isNetworkAvailable(context)){
            progressBarHelper.showProgressDialog();
            GetUserList();
            GetRoleList();
        }else {
            Toast.makeText(context, "Please check your internet connectivity.", Toast.LENGTH_SHORT).show();
        }

        binding.saveUserRights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)){
                    if (Validate_Data()){
                        Call<UserRightsModel.UserRightsResponse> call;
                        progressBarHelper.showProgressDialog();
                        if (adaptermodel == null){
                            call = apiCalling.Save_User_Rights(new UserRightsModel(0L,
                                    username_id,new RoleModel(rolename_id),new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),
                                    0,Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)),new RowStatusModel(1)));
                        }else {
                            call = apiCalling.Save_User_Rights(new UserRightsModel(adaptermodel.UserWiseRightsID,
                                    username_id,new RoleModel(rolename_id),new TransactionModel(adaptermodel.Transaction.getTransactionId(),Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),
                                    0),new RowStatusModel(1)));
                        }
                        call.enqueue(new Callback<UserRightsModel.UserRightsResponse>() {
                            @Override
                            public void onResponse(Call<UserRightsModel.UserRightsResponse> call, Response<UserRightsModel.UserRightsResponse> response) {
                                if (response.isSuccessful()){
                                    UserRightsModel.UserRightsResponse data = response.body();
                                    if (data.Completed){
                                        Toast.makeText(context, data.Message, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(context,UserRightsListActivity.class));finish();
                                    }else {
                                        Toast.makeText(context, data.Message, Toast.LENGTH_SHORT).show();
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<UserRightsModel.UserRightsResponse> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    Toast.makeText(context, "Please check your internet connectivity.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void GetUserList(){
        username.clear(); userid.clear();
        username.add("Select User Name"); userid.add(0L);
        Call<UserData1> call = apiCalling.Get_All_User_By_Branch(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<UserData1>() {
            @Override
            public void onResponse(@NotNull Call<UserData1> call, @NotNull Response<UserData1> response) {
                if (response.isSuccessful()) {
                    UserData1 userData1 = response.body();
                    if (userData1 != null && userData1.isCompleted()) {
                        if (userData1.getData().size() > 0) {
                            for (UserModel model : userData1.getData()) {
                                String name = model.getStaffDetail().getName(); long id = model.getUserID();
                                userid.add(id); username.add(name);
                            }
                            USERNAME = new String[username.size()];USERID = new Long[userid.size()];
                            USERNAME = username.toArray(USERNAME);USERID = userid.toArray(USERID);
                            bindUser();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserData1> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindUser() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, USERNAME);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spUser.setAdapter(adapter);
        if (i.hasExtra("MODEL_DATA")){
            binding.spUser.setSelection(userid.indexOf(adaptermodel.userinfo.getUserID()));
        }
        binding.spUser.setOnItemSelectedListener(selectuser);
    }

    AdapterView.OnItemSelectedListener selectuser = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            username_id = userid.get(position);
            if (binding.spUser.getSelectedItem().toString().equals("Select User Name")) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
            } else { ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK); }
            ((TextView) parent.getChildAt(0)).setTextSize(16);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    };

    public void GetRoleList(){
        rolename.clear(); roleid.clear();
        rolename.add("Select Role Name"); roleid.add(0L);
        Call<RoleModel.RoleData> call = apiCalling.Get_All_Role(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<RoleModel.RoleData>() {
            @Override
            public void onResponse(Call<RoleModel.RoleData> call, Response<RoleModel.RoleData> response) {
                if (response.isSuccessful()){
                    RoleModel.RoleData data = response.body();
                    if (data != null && data.Completed){
                        if (data.Data.size() > 0) {
                            for(RoleModel model: data.Data){
                                long id = model.RoleID; String name = model.RoleName;
                                roleid.add(id); rolename.add(name);
                            }
                            ROLENAME = new String[rolename.size()];ROLEID = new Long[roleid.size()];
                            ROLENAME = rolename.toArray(ROLENAME);ROLEID = roleid.toArray(ROLEID);
                            bindRole();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<RoleModel.RoleData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindRole() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, ROLENAME);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spRolename.setAdapter(adapter);
        if (i.hasExtra("MODEL_DATA")){
            binding.spRolename.setSelection(roleid.indexOf(adaptermodel.Roleinfo.RoleID));
        }
        binding.spRolename.setOnItemSelectedListener(selectrole);
    }

    AdapterView.OnItemSelectedListener selectrole = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            rolename_id = roleid.get(position);
            if (binding.spRolename.getSelectedItem().toString().equals("Select Role Name")) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
            } else { ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK); }
            ((TextView) parent.getChildAt(0)).setTextSize(16);
            if (binding.spRolename.getSelectedItemId() != 0){
                GetUserRightsRoleList(rolename_id);
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    };

    public void GetUserRightsRoleList(long id){
        progressBarHelper.showProgressDialog();
        Call<UserRightsModel.UserRightsData> call = apiCalling.Get_User_Rights_Role_List(id);
        call.enqueue(new Callback<UserRightsModel.UserRightsData>() {
            @Override
            public void onResponse(Call<UserRightsModel.UserRightsData> call, Response<UserRightsModel.UserRightsData> response) {
                if (response.isSuccessful()){
                    UserRightsModel.UserRightsData data = response.body();
                    if (data != null && data.Completed){
                        if (data.Data.size() > 0){
                            LinearLayoutManager linear = new LinearLayoutManager(context);
                            binding.userRightsRv.setLayoutManager(linear);
                            UserRightsAdapter adapter = new UserRightsAdapter(context,data.Data);
                            binding.userRightsRv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<UserRightsModel.UserRightsData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean Validate_Data(){
        if (binding.spUser.getSelectedItemId() == 0){
            Toast.makeText(context, "Please Select User Name.", Toast.LENGTH_SHORT).show();return false;
        }else if (binding.spRolename.getSelectedItemId() == 0){
            Toast.makeText(context, "Please Select Role Name.", Toast.LENGTH_SHORT).show();return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, UserRightsListActivity.class));
        finish();
    }
}