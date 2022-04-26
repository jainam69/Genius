package com.example.genius.Activity.User_Permission;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.RoleModel;
import com.example.genius.Model.RolesModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.R;
import com.example.genius.databinding.ActivityRoleBinding;
import com.example.genius.databinding.RowRoleListBinding;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Staff_Entry_Fragment.staff_entry_fragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoleActivity extends AppCompatActivity {

    ActivityRoleBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    long id = 0,transactionid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Role Master");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        context = RoleActivity.this;
        progressBarHelper = new ProgressBarHelper(this, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        if (Function.isNetworkAvailable(context)){
            progressBarHelper.showProgressDialog();
            GetAllRoles();
        }else {
            Toast.makeText(context, "Please check your internet connectivity.", Toast.LENGTH_SHORT).show();
        }

        binding.saveRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)){
                    if (Validate_Data()){
                        progressBarHelper.showProgressDialog();
                        Call<RoleModel.RoleResponse> call;
                        if (id == 0){
                            call = apiCalling.Save_Role(new RoleModel(id,
                                    binding.edtRoleName.getText().toString(),new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),
                                    0,Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)),new RowStatusModel(1),
                                    new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID))));
                        }else {
                            call = apiCalling.Save_Role(new RoleModel(id,
                                    binding.edtRoleName.getText().toString(),new TransactionModel(transactionid,Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),
                                    0),new RowStatusModel(1),
                                    new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID))));
                        }
                        call.enqueue(new Callback<RoleModel.RoleResponse>() {
                            @Override
                            public void onResponse(Call<RoleModel.RoleResponse> call, Response<RoleModel.RoleResponse> response) {
                                if (response.isSuccessful()){
                                    RoleModel.RoleResponse data = response.body();
                                    if (data.Completed){
                                        Toast.makeText(context, data.Message, Toast.LENGTH_SHORT).show();
                                        binding.edtRoleName.setText("");
                                        id = 0;
                                        GetAllRoles();
                                    }else {
                                        Toast.makeText(context, data.Message, Toast.LENGTH_SHORT).show();
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<RoleModel.RoleResponse> call, Throwable t) {
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context,UserPermissionSelectActivity.class));
        finish();
    }

    public void GetAllRoles(){
        Call<RoleModel.RoleData> call = apiCalling.Get_All_Role(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<RoleModel.RoleData>() {
            @Override
            public void onResponse(Call<RoleModel.RoleData> call, Response<RoleModel.RoleData> response) {
                if (response.isSuccessful()){
                    RoleModel.RoleData data = response.body();
                    if (data != null && data.Completed){
                        if (data.Data.size() > 0){
                            binding.text.setVisibility(View.VISIBLE);
                            LinearLayoutManager linear = new LinearLayoutManager(context);
                            binding.roleRv.setLayoutManager(linear);
                            RoleAdapter adapter = new RoleAdapter(context,data.Data);
                            binding.roleRv.setAdapter(adapter);
                        }else {
                            binding.text.setVisibility(View.GONE);
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

    public Boolean Validate_Data(){
        if (binding.edtRoleName.getText().toString().isEmpty()){
            Toast.makeText(context, "Please Enter Role Name.", Toast.LENGTH_SHORT).show(); return false;
        }
        return true;
    }

    public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.ViewHolder>{

        Context context;
        List<RoleModel> model;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;

        public RoleAdapter(Context context, List<RoleModel> model) {
            this.context = context;
            this.model = model;
        }

        @NonNull
        @Override
        public RoleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(RowRoleListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull RoleAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.binding.roleName.setText(model.get(position).RoleName);
            holder.binding.editRole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                    View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                    builder.setView(dialogView);
                    builder.setCancelable(true);
                    TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                    TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                    ImageView image = dialogView.findViewById(R.id.image);
                    TextView title = dialogView.findViewById(R.id.title);
                    title.setText("Are you sure that you want to Edit Role?");
                    image.setImageResource(R.drawable.ic_edit);
                    AlertDialog dialog = builder.create();

                    btn_edit_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    btn_edit_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            binding.edtRoleName.setText(model.get(position).RoleName);
                            id = model.get(position).RoleID;
                            transactionid = model.get(position).Transaction.getTransactionId();
                        }
                    });
                    dialog.show();
                }
            });
            holder.binding.deleteRole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                    View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                    builder.setView(dialogView);
                    builder.setCancelable(true);
                    TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                    TextView btn_delete = dialogView.findViewById(R.id.btn_delete);
                    TextView title = dialogView.findViewById(R.id.title);
                    ImageView image = dialogView.findViewById(R.id.image);
                    image.setImageResource(R.drawable.delete);
                    title.setText("Are you sure that you want to delete this Role Name?");
                    AlertDialog dialog = builder.create();

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    btn_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBarHelper.showProgressDialog();
                            Call<CommonModel> call = apiCalling.Remove_Role(model.get(position).RoleID, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                            call.enqueue(new Callback<CommonModel>() {
                                @Override
                                public void onResponse(Call<CommonModel> call, Response<CommonModel> response) {
                                    if (response.isSuccessful()) {
                                        progressBarHelper.hideProgressDialog();
                                        CommonModel data = response.body();
                                        if (data.isCompleted()) {
                                            if (data.isData()) {
                                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                                model.remove(position);
                                                notifyItemRemoved(position);
                                                notifyDataSetChanged();
                                            }else {
                                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            Toast.makeText(context, data.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        progressBarHelper.hideProgressDialog();
                                    }
                                }

                                @Override
                                public void onFailure(Call<CommonModel> call, Throwable t) {
                                    progressBarHelper.hideProgressDialog();
                                    Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return model.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            RowRoleListBinding binding;

            public ViewHolder(@NonNull RowRoleListBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }
    }
}