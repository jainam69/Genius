package com.example.genius.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.Model.RolesModel;
import com.example.genius.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UserRole_Adapter extends RecyclerView.Adapter<UserRole_Adapter.ViewHolder> {

    Context context;
    HashMap<String, Integer> permissionDetails;
    List<RolesModel> model;
    public static List<RolesModel> permissionlist = new ArrayList<>();

//    public UserRole_Adapter(Context context, HashMap<String, Integer> permissionDetails) {
//        this.context = context;
//        this.permissionDetails = permissionDetails;
//    }

    public UserRole_Adapter(Context context, List<RolesModel> model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserRole_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.userpermission_master_deatil_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.masters.setText("" + model.get(position).getRoleName());
        RolesModel rolesModel = new RolesModel();
        rolesModel.setPermission(model.get(position).getRoleName());
        rolesModel.setRoleID(model.get(position).getRoleID());
        rolesModel.setHasAccess(model.get(position).isHasAccess());
        rolesModel.setUserID(model.get(position).getUserID());
        holder.masters.setChecked(model.get(position).isHasAccess());
        permissionlist.add(rolesModel);
        holder.masters.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (holder.masters.isChecked()) {
                    permissionlist.get(position).setHasAccess(true);
                } else {
                    permissionlist.get(position).setHasAccess(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox masters;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            masters = itemView.findViewById(R.id.masters);
        }
    }
}
