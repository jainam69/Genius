package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Activity.User_Permission.Role_Rights.RoleRightsActivity;
import com.example.genius.Activity.User_Permission.User_Rights.UserRightsActivity;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.StaffModel;
import com.example.genius.Model.UserRightsModel;
import com.example.genius.R;
import com.example.genius.databinding.RowListroleBinding;
import com.example.genius.databinding.RowUseradapterListBinding;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRightsList_Adapter extends RecyclerView.Adapter<UserRightsList_Adapter.ViewHolder> {

    Context context;
    List<UserRightsModel> model;
    int flag = 0;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;

    public UserRightsList_Adapter(Context context, List<UserRightsModel> model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public UserRightsList_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserRightsList_Adapter.ViewHolder(RowUseradapterListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserRightsList_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.roleName.setText(model.get(position).Roleinfo.RoleName);
        holder.binding.userName.setText(model.get(position).userinfo.getStaffDetail().getName());
        holder.binding.upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0){
                    holder.binding.upArrow.setImageResource(R.drawable.down_arrow);
                    holder.binding.userRightsSubRv.setVisibility(View.VISIBLE);
                    holder.binding.linearAction.setVisibility(View.VISIBLE);
                    UserRightsSubList_Adapter adapter = new UserRightsSubList_Adapter(context, model.get(position).list);
                    holder.binding.userRightsSubRv.setLayoutManager(new LinearLayoutManager(context));
                    holder.binding.userRightsSubRv.setAdapter(adapter);
                    flag = 1;
                }else {
                    holder.binding.upArrow.setImageResource(R.drawable.up_arrow);
                    holder.binding.userRightsSubRv.setVisibility(View.GONE);
                    holder.binding.linearAction.setVisibility(View.GONE);
                    flag = 0;
                }
            }
        });
        holder.binding.imgEdit.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to Edit User Rights?");
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
                        Intent i = new Intent(context, UserRightsActivity.class);
                        i.putExtra("MODEL_DATA",new Gson().toJson(model.get(position)));
                        context.startActivity(i);
                    }
                });
                dialog.show();
            }
        });
        holder.binding.imgDelete.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to delete this User Rights?");
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
                        Call<CommonModel> call = apiCalling.Remove_User_Rights(model.get(position).UserWiseRightsID, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
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

    public void filterList(List<UserRightsModel> filteredList) {
        model = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowUseradapterListBinding binding;

        public ViewHolder(@NonNull RowUseradapterListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }
}
