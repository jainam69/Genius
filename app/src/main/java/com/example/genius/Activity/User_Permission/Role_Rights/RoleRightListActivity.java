package com.example.genius.Activity.User_Permission.Role_Rights;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.genius.API.ApiCalling;
import com.example.genius.Activity.User_Permission.UserPermissionSelectActivity;
import com.example.genius.Adapter.RoleRightsList_Adapter;
import com.example.genius.Model.RoleRightsModel;
import com.example.genius.Model.UserRightsModel;
import com.example.genius.R;
import com.example.genius.databinding.ActivityRoleRightListBinding;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoleRightListActivity extends AppCompatActivity {

    ActivityRoleRightListBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling; List<RoleRightsModel> model;
    RoleRightsList_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoleRightListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Role Rights Master");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        context = RoleRightListActivity.this;
        progressBarHelper = new ProgressBarHelper(this, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        if (Function.isNetworkAvailable(context)){
            progressBarHelper.showProgressDialog();
            GetAllRoleRightList();
        }else {
            Toast.makeText(context, "Please check your internet connectivity.", Toast.LENGTH_SHORT).show();
        }

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                GetSearch(s.toString());
            }
        });

        binding.fabContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,RoleRightsActivity.class));
            }
        });
    }

    public void GetAllRoleRightList(){
        Call<RoleRightsModel.RoleRightsData> call = apiCalling.Get_All_Role_Rights(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<RoleRightsModel.RoleRightsData>() {
            @Override
            public void onResponse(Call<RoleRightsModel.RoleRightsData> call, Response<RoleRightsModel.RoleRightsData> response) {
                if (response.isSuccessful()){
                    RoleRightsModel.RoleRightsData data = response.body();
                    if (data != null && data.Completed){
                        if (data.Data.size() > 0){
                            model = data.Data;
                            binding.txtNodata.setVisibility(View.GONE);
                            binding.roleRightsListRv.setVisibility(View.VISIBLE);
                            LinearLayoutManager linear = new LinearLayoutManager(context);
                            binding.roleRightsListRv.setLayoutManager(linear);
                            adapter = new RoleRightsList_Adapter(context,data.Data);
                            binding.roleRightsListRv.setAdapter(adapter);
                        }else {
                            binding.txtNodata.setVisibility(View.VISIBLE);
                            binding.roleRightsListRv.setVisibility(View.GONE);
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
        startActivity(new Intent(context, UserPermissionSelectActivity.class));
        finish();
    }

    private void GetSearch(String text) {
        ArrayList<RoleRightsModel> list = new ArrayList<>();
        for (RoleRightsModel item : model) {
            if (item.Roleinfo.RoleName.toLowerCase().contains(text.toLowerCase())) {
                list.add(item);
            }
        }
        adapter.filterList(list);
    }
}