package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.databinding.RowBranchCourseListLineBinding;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.BranchCource.BranchCourseFragment;
import com.example.genius.ui.Staff_Entry_Fragment.staff_entry_fragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchCourseList_Adapter extends RecyclerView.Adapter<BranchCourseList_Adapter.ViewHolder> {

    Context context;
    BranchCourseModel branchCourceData;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    UserModel userpermission;

    public BranchCourseList_Adapter(Context context, BranchCourseModel branchCourceData) {
        this.context = context;
        this.branchCourceData = branchCourceData;
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
    }

    @NonNull
    @Override
    public BranchCourseList_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowBranchCourseListLineBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BranchCourseList_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        for (UserModel.UserPermission model : userpermission.getPermission())
        {
            if (model.getPageInfo().getPageID() == 75){
                if (!model.getPackageRightinfo().isCreatestatus()){
                    holder.binding.imgEdit.setVisibility(View.GONE);
                }
                if (!model.getPackageRightinfo().isDeletestatus()){
                    holder.binding.imgDelete.setVisibility(View.GONE);
                }
                if (!model.getPackageRightinfo().isCreatestatus() && !model.getPackageRightinfo().isDeletestatus()){
                    holder.binding.linearActions.setVisibility(View.GONE);
                }
            }
        }
        holder.binding.branchName.setText(Preferences.getInstance(context).getString(Preferences.KEY_BRANCH_NAME));
        holder.binding.courseSublistRv.setLayoutManager(new LinearLayoutManager(context));
        holder.binding.courseSublistRv.setAdapter(new BranchCourseSubList_Adapter(context, branchCourceData.getData()));
        holder.binding.imgEdit.setOnClickListener((View.OnClickListener) v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to Edit Branch Course?");
            image.setImageResource(R.drawable.ic_edit);
            AlertDialog dialog = builder.create();

            btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

            btn_edit_yes.setOnClickListener((View.OnClickListener) v12 -> {
                dialog.dismiss();
                BranchCourseFragment orderplace = new BranchCourseFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("COURSE_DTL", (Serializable) branchCourceData.getData());
                orderplace.setArguments(bundle);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            });
            dialog.show();
        });
        holder.binding.imgDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
            TextView btn_delete = dialogView.findViewById(R.id.btn_delete);
            TextView title = dialogView.findViewById(R.id.title);
            ImageView image = dialogView.findViewById(R.id.image);
            image.setImageResource(R.drawable.delete);
            title.setText("Are you sure that you want to delete this Course?");
            AlertDialog dialog = builder.create();

            btn_cancel.setOnClickListener(v13 -> dialog.dismiss());

            btn_delete.setOnClickListener(v14 -> {
                progressBarHelper.showProgressDialog();
                Call<CommonModel.ResponseModel> call = apiCalling.RemoveBranchCourse(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                call.enqueue(new Callback<CommonModel.ResponseModel>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonModel.ResponseModel> call, @NotNull Response<CommonModel.ResponseModel> response) {
                        if (response.isSuccessful()) {
                            CommonModel.ResponseModel data = response.body();
                            if (data.isCompleted()) {
                                CommonModel model = data.getData();
                                if (model.isStatus()) {
                                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
                                }else {
                                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonModel.ResponseModel> call, @NotNull Throwable t) {
                        progressBarHelper.hideProgressDialog();
                        Toast.makeText(context,t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            });
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowBranchCourseListLineBinding binding;

        public ViewHolder(@NonNull RowBranchCourseListLineBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);
        }
    }
}
