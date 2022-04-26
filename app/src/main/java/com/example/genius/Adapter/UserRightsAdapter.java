package com.example.genius.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.Model.UserRightsModel;
import com.example.genius.R;
import com.example.genius.databinding.RowRoleSubListBinding;
import com.example.genius.databinding.RowUserRightsRoleListBinding;

import java.util.List;

public class UserRightsAdapter extends RecyclerView.Adapter<UserRightsAdapter.ViewHolder> {

    Context context;
    List<UserRightsModel> model;

    public UserRightsAdapter(Context context, List<UserRightsModel> model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public UserRightsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserRightsAdapter.ViewHolder(RowUserRightsRoleListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserRightsAdapter.ViewHolder holder, int position) {
        holder.binding.pageName.setText(model.get(position).PageInfo.getPage());
        if (!model.get(position).RoleRightinfo.Createstatus){ holder.binding.checkCreate.setImageResource(R.drawable.uncheck_arrow); }else {
            holder.binding.checkCreate.setImageResource(R.drawable.check_arrow);
        }
        if (!model.get(position).RoleRightinfo.Deletestatus){ holder.binding.checkDelete.setImageResource(R.drawable.uncheck_arrow); }else {
            holder.binding.checkDelete.setImageResource(R.drawable.check_arrow);
        }
        if (!model.get(position).RoleRightinfo.Viewstatus){ holder.binding.checkView.setImageResource(R.drawable.uncheck_arrow); }else {
            holder.binding.checkView.setImageResource(R.drawable.check_arrow);
        }
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowUserRightsRoleListBinding binding;

        public ViewHolder(@NonNull RowUserRightsRoleListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
