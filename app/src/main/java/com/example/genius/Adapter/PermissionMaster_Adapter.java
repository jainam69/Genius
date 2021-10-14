package com.example.genius.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Permission.PermissionFragment;

import java.util.List;

public class PermissionMaster_Adapter extends RecyclerView.Adapter<PermissionMaster_Adapter.ViewHolder> {

    Context context;
    List<UserModel> userModels;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    String bn;

    public PermissionMaster_Adapter(Context context, List<UserModel> userModels) {
        this.context = context;
        this.userModels = userModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PermissionMaster_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.permission_master_deatil_list, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.no.setText("" + userModels.get(position).getUserID());
        holder.emp_name.setText("" + userModels.get(position).getUsername());
//        holder.date.setText(userModels.get(position).getDate());
        holder.permission_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Edit User Permission?");
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
                        PermissionFragment orderplace = new PermissionFragment();
                        Bundle bundle = new Bundle();
                        bundle.putLong("No",userModels.get(position).getUserID());
                        bundle.putString("Emp_Name",userModels.get(position).getUsername());
                        bundle.putInt("RoleSize",userModels.get(position).getRoles().size());
                        orderplace.setArguments(bundle);
                        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
//                        if (Function.checkNetworkConnection(context))
//                        {
//                            progressBarHelper.showProgressDialog();
//                            PermissionMaster_Model per = new PermissionMaster_Model(String.valueOf(no));
//                            Call<PermissionMaster_Model> call = apiCalling.GetEditPermission(per);
//                            call.enqueue(new Callback<PermissionMaster_Model>() {
//                                @Override
//                                public void onResponse(Call<PermissionMaster_Model> call, Response<PermissionMaster_Model> response) {
//                                    if (response.isSuccessful()) {
//                                        PermissionMaster_Model permissionMaster_model = response.body();
//                                        if (permissionMaster_model != null) {
//                                            List<PermissionMaster_Model.PermissionDetails> permissionDetailsList = permissionMaster_model.getResponse();
//                                            if (permissionDetailsList != null) {
//                                                if (permissionDetailsList.size() > 0) {
//                                                    for (PermissionMaster_Model.PermissionDetails studentDetails1 : permissionDetailsList) {
//                                                        permission_fragment orderplace = new permission_fragment();
//                                                        Bundle bundle = new Bundle();
//                                                        bundle.putInt("No",studentDetails1.getNo());
//                                                        bundle.putString("Date",studentDetails1.getDate());
//                                                        bundle.putString("Emp_Name",studentDetails1.getEmp_Name());
//                                                        bundle.putString("Page",studentDetails1.getPage());
//                                                        Boolean b = studentDetails1.getPermission();
//                                                        bundle.putBoolean("Permission",b);
//                                                        orderplace.setArguments(bundle);
//                                                        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
//                                                        FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
//                                                        fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
//                                                        fragmentTransaction.addToBackStack(null);
//                                                        fragmentTransaction.commit();
//                                                    }
//                                                }
//                                                progressBarHelper.hideProgressDialog();
//                                            }
//                                        }
//                                    }
//                                }
//                                @Override
//                                public void onFailure(Call<PermissionMaster_Model> call, Throwable t) {
//                                    progressBarHelper.hideProgressDialog();
//                                }
//                            });
//                        }
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView no, emp_name, date;
        ImageView permission_edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            no = itemView.findViewById(R.id.no);
            emp_name = itemView.findViewById(R.id.emp_name);
            date = itemView.findViewById(R.id.date);
            permission_edit = itemView.findViewById(R.id.permission_edit);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }

}
