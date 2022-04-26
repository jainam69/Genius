package com.example.genius.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.Model.RoleRightsModel;
import com.example.genius.R;
import com.example.genius.databinding.RowRoleSubListBinding;

import java.util.List;

public class RoleRightsSubList_Adapter extends RecyclerView.Adapter<RoleRightsSubList_Adapter.ViewHolder> {

    Context context;
    List<RoleRightsModel> model;

    public RoleRightsSubList_Adapter(Context context, List<RoleRightsModel> model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public RoleRightsSubList_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowRoleSubListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RoleRightsSubList_Adapter.ViewHolder holder, int position) {
        holder.binding.pageName.setText(model.get(position).PageInfo.getPage());
        if (!model.get(position).Createstatus){ holder.binding.checkCreate.setImageResource(R.drawable.uncheck_arrow); }else {
            holder.binding.checkCreate.setImageResource(R.drawable.check_arrow);
        }
        if (!model.get(position).Deletestatus){ holder.binding.checkDelete.setImageResource(R.drawable.uncheck_arrow); }else {
            holder.binding.checkDelete.setImageResource(R.drawable.check_arrow);
        }
        if (!model.get(position).Viewstatus){ holder.binding.checkView.setImageResource(R.drawable.uncheck_arrow); }else {
            holder.binding.checkView.setImageResource(R.drawable.check_arrow);
        }
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowRoleSubListBinding binding;

        public ViewHolder(@NonNull RowRoleSubListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
