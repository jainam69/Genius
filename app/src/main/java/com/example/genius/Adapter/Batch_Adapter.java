package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BatchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.StudentModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.databinding.RowBatchListBinding;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Batch_Fragment.batch_fragment;
import com.example.genius.ui.Library_Fragment.library_fragment;
import com.example.genius.ui.Library_Fragment.library_video_fragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Batch_Adapter extends RecyclerView.Adapter<Batch_Adapter.ViewHolder> {

    List<BatchModel> batchModels;
    Context context;
    UserModel.PageData userpermission;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;

    public Batch_Adapter(List<BatchModel> batchModels, Context context) {
        this.batchModels = batchModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowBatchListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        for (UserModel.PageInfoEntity model : userpermission.Data)
        {
            if (model.getPageID() == 11){
                if (!model.Createstatus){
                    holder.binding.batchEdit.setVisibility(View.GONE);
                }
                if (!model.Deletestatus){
                    holder.binding.batchDelete.setVisibility(View.GONE);
                }
            }
        }
        holder.binding.course.setText(batchModels.get(position).getBranchCourse().getCourse().getCourseName());
        holder.binding.standard.setText(batchModels.get(position).getBranchClass().getClassModel().getClassName());
        int time = batchModels.get(position).getBatchTime();
        if (time == 1){
            holder.binding.batchTime.setText("Morning");
        }else if (time == 2){
            holder.binding.batchTime.setText("Afternoon");
        }else {
            holder.binding.batchTime.setText("Evening");
        }
        holder.binding.monFri.setText(batchModels.get(position).getMonFriBatchTime());
        holder.binding.saturday.setText(batchModels.get(position).getSatBatchTime());
        holder.binding.sunday.setText(batchModels.get(position).getSunBatchTime());
        holder.binding.batchEdit.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to Edit Batch?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();
                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());
                btn_edit_yes.setOnClickListener(v12 -> {
                    batch_fragment orderplace = new batch_fragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("BATCH_DATA", batchModels.get(position));
                    orderplace.setArguments(bundle);
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = (fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    dialog.dismiss();
                });
                dialog.show();
            }
        });
        holder.binding.batchDelete.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to delete this Batch?");
                AlertDialog dialog = builder.create();
                btn_cancel.setOnClickListener(v13 -> dialog.dismiss());
                btn_delete.setOnClickListener(v14 -> {
                    dialog.dismiss();
                    progressBarHelper.showProgressDialog();
                    Call<CommonModel> call = apiCalling.Remove_Batch(batchModels.get(position).getBatchID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    call.enqueue(new Callback<CommonModel>() {
                        @Override
                        public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                            if (response.isSuccessful()) {
                                CommonModel model = response.body();
                                if (model != null && model.isCompleted()) {
                                    if (model.isData()) {
                                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                        batchModels.remove(position);
                                        notifyItemRemoved(position);
                                        notifyDataSetChanged();
                                    }else {
                                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CommonModel> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return batchModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowBatchListBinding binding;

        public ViewHolder(@NonNull RowBatchListBinding itemView) {
            super(itemView.getRoot());
           binding = itemView;
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.PageData.class);
        }
    }

    public void filterList(List<BatchModel> filteredList) {
        batchModels = filteredList;
        notifyDataSetChanged();
    }
}
