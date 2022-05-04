package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.example.genius.Model.RoleRightsModel;
import com.example.genius.Model.UserModel;
import com.example.genius.databinding.RowRolerightsListBinding;
import java.util.ArrayList;
import java.util.List;

public class RoleRightsAdapter extends RecyclerView.Adapter<RoleRightsAdapter.ViewHolder> {

    Context context;
    public static List<RoleRightsModel> model;
    boolean isAllCreate,isAllDelete,isAllView;

    public RoleRightsAdapter(Context context, List<RoleRightsModel> Model) {
        this.context = context;
        model = Model;
    }

    @NonNull
    @Override
    public RoleRightsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowRolerightsListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RoleRightsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.pageName.setText(model.get(position).PageInfo.getPage());
        UserModel.PageInfoEntity da = new UserModel.PageInfoEntity();
        da.setPageID(model.get(position).PageInfo.getPageID());da.setPage(model.get(position).PageInfo.getPage());
        da.Createstatus = model.get(position).PageInfo.Createstatus;da.Deletestatus = model.get(position).PageInfo.Deletestatus;
        da.Viewstatus = model.get(position).PageInfo.Viewstatus;
        model.get(position).PageInfo = da;
        holder.binding.checkCreate.setChecked(isAllCreate);holder.binding.checkDelete.setChecked(isAllDelete);holder.binding.checkView.setChecked(isAllView);
        holder.binding.checkCreate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!model.get(position).PageInfo.Createstatus){ holder.binding.checkCreate.setChecked(false); }
                if (holder.binding.checkCreate.isChecked()){ model.get(position).Createstatus = true; }
                else { model.get(position).Createstatus = false; }
            }
        });
        holder.binding.checkDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!model.get(position).PageInfo.Deletestatus){ holder.binding.checkDelete.setChecked(false); }
                if (holder.binding.checkDelete.isChecked()){ model.get(position).Deletestatus = true; }
                else { model.get(position).Deletestatus = false; }
            }
        });
        holder.binding.checkView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!model.get(position).PageInfo.Viewstatus){ holder.binding.checkView.setChecked(false); }
                if (holder.binding.checkView.isChecked()){ model.get(position).Viewstatus = true; }
                else { model.get(position).Viewstatus = false; }
            }
        });
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowRolerightsListBinding binding;

        public ViewHolder(@NonNull RowRolerightsListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void AllCreate() {
        isAllCreate = true;
        model.forEach((u) -> u.Createstatus = true);
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void AllDelete() {
        isAllDelete = true;
        model.forEach((u) -> u.Deletestatus = true);
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void AllView() {
        isAllView = true;
        model.forEach((u) -> u.Viewstatus = true);
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void unselectCreate() {
        isAllCreate = false;
        model.forEach((u) -> u.Createstatus = false);
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void unselectDelete() {
        isAllDelete = false;
        model.forEach((u) -> u.Deletestatus = false);
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void unselectView() {
        isAllView = false;
        model.forEach((u) -> u.Viewstatus = false);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
