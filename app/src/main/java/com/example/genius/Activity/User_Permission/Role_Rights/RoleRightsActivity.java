package com.example.genius.Activity.User_Permission.Role_Rights;

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
import com.example.genius.Activity.User_Permission.RoleActivity;
import com.example.genius.Activity.User_Permission.UserPermissionSelectActivity;
import com.example.genius.Adapter.BranchClassAdapter;
import com.example.genius.Adapter.RoleRightsAdapter;
import com.example.genius.Adapter.RoleRightsCheck_Adapter;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.RoleModel;
import com.example.genius.Model.RoleRightsModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.databinding.ActivityRoleRightsBinding;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoleRightsActivity extends AppCompatActivity {

    ActivityRoleRightsBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> rolename = new ArrayList<>(); List<Long> roleid = new ArrayList<>();
    String[] ROLENAME; Long[] ROLEID; long rolename_id;
    List<RoleRightsModel> list;
    RoleRightsModel adaptermodel; Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoleRightsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Role Rights Master Entry");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        context = RoleRightsActivity.this;
        progressBarHelper = new ProgressBarHelper(this, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        i = getIntent();
        if (i.hasExtra("MODEL_DATA")){
            adaptermodel = new Gson().fromJson(i.getStringExtra("MODEL_DATA"),RoleRightsModel.class);
            if (adaptermodel.list.size() > 0){
                List<RoleRightsModel> data = new ArrayList<>();
                for (int i = 0; i < adaptermodel.list.size(); i++){
                    adaptermodel.list.get(i).PageInfo = new UserModel.PageInfoEntity(adaptermodel.list.get(i).PageInfo.getPage(),
                            adaptermodel.list.get(i).PageInfo.getPageID(),adaptermodel.list.get(i).PageInfo.Createstatus,
                            adaptermodel.list.get(i).PageInfo.Deletestatus,adaptermodel.list.get(i).PageInfo.Viewstatus);
                    adaptermodel.list.get(i).Createstatus = adaptermodel.list.get(i).Createstatus;
                    adaptermodel.list.get(i).Deletestatus = adaptermodel.list.get(i).Deletestatus;
                    adaptermodel.list.get(i).Viewstatus = adaptermodel.list.get(i).Viewstatus;
                    data.add(adaptermodel.list.get(i));
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                binding.roleRightsRv.setLayoutManager(linearLayoutManager);
                RoleRightsCheck_Adapter adapter = new RoleRightsCheck_Adapter(context, data);
                binding.roleRightsRv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }else {
            if (Function.isNetworkAvailable(context)){
                progressBarHelper.showProgressDialog();
                GetAllPageRoleRights();
            }else {
                Toast.makeText(context, "Please check your internet connectivity.", Toast.LENGTH_SHORT).show();
            }
        }

        if (Function.isNetworkAvailable(context)){
            progressBarHelper.showProgressDialog();
            GetAllRoles();
        }else {
            Toast.makeText(context, "Please check your internet connectivity.", Toast.LENGTH_SHORT).show();
        }

        binding.saveRoleRights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)){
                    if (binding.spRolename.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please Select Role Name.", Toast.LENGTH_SHORT).show();
                    }else {
                        progressBarHelper.showProgressDialog();
                        list = new ArrayList<>();
                        if(adaptermodel == null){
                            if (RoleRightsAdapter.model.size() > 0){
                                for (int i = 0; i < RoleRightsAdapter.model.size(); i++){
                                    RoleRightsModel r = new RoleRightsModel(0,new RoleModel(rolename_id),
                                            RoleRightsAdapter.model.get(i).Createstatus,false,RoleRightsAdapter.model.get(i).Deletestatus,
                                            RoleRightsAdapter.model.get(i).Viewstatus,RoleRightsAdapter.model.get(i).PageInfo,
                                            new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),0,Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)),
                                            new RowStatusModel(1),new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)));
                                    list.add(r);
                                }
                            }
                        }else {
                            if (RoleRightsCheck_Adapter.model.size() > 0){
                                for (int i = 0; i < RoleRightsCheck_Adapter.model.size(); i++){
                                    RoleRightsModel r = new RoleRightsModel(adaptermodel.list.get(i).RoleRightsId,new RoleModel(rolename_id),
                                            RoleRightsCheck_Adapter.model.get(i).Createstatus,false,RoleRightsCheck_Adapter.model.get(i).Deletestatus,
                                            RoleRightsCheck_Adapter.model.get(i).Viewstatus,RoleRightsCheck_Adapter.model.get(i).PageInfo,
                                            new TransactionModel(adaptermodel.list.get(i).Transaction.getTransactionId(),Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),0),
                                            new RowStatusModel(1),new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)));
                                    list.add(r);
                                }
                            }
                        }
                        Call<RoleRightsModel.RoleRightsResponse> call = apiCalling.Save_Role_Rights(list);
                        call.enqueue(new Callback<RoleRightsModel.RoleRightsResponse>() {
                            @Override
                            public void onResponse(Call<RoleRightsModel.RoleRightsResponse> call, Response<RoleRightsModel.RoleRightsResponse> response) {
                                if (response.isSuccessful()){
                                    RoleRightsModel.RoleRightsResponse data = response.body();
                                    if (data.Completed){
                                        Toast.makeText(context, data.Message, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(context,RoleRightListActivity.class));
                                        finish();
                                    }else {
                                        Toast.makeText(context, data.Message, Toast.LENGTH_SHORT).show();
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<RoleRightsModel.RoleRightsResponse> call, Throwable t) {
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

    public void GetAllRoles(){
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
            int r = roleid.indexOf(adaptermodel.Roleinfo.RoleID);
            binding.spRolename.setSelection(r);
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
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            };

    public void GetAllPageRoleRights(){
        Call<RoleRightsModel.RoleRightsData> call = apiCalling.Get_Page_Role_Right_List(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<RoleRightsModel.RoleRightsData>() {
            @Override
            public void onResponse(Call<RoleRightsModel.RoleRightsData> call, Response<RoleRightsModel.RoleRightsData> response) {
                if (response.isSuccessful()){
                    RoleRightsModel.RoleRightsData data = response.body();
                    if (data != null && data.Completed){
                        if (data.Data.size() > 0){
                            LinearLayoutManager linear = new LinearLayoutManager(context);
                            binding.roleRightsRv.setLayoutManager(linear);
                            RoleRightsAdapter adapter = new RoleRightsAdapter(context,data.Data);
                            binding.roleRightsRv.setAdapter(adapter);
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<RoleRightsModel.RoleRightsData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, RoleRightListActivity.class));
        finish();
    }
}