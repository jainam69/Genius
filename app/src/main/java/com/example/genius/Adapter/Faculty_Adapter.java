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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genius.API.ApiCalling;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.FacultyModel;
import com.example.genius.Model.StaffModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.databinding.RowFacultyListBinding;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Faculty_Fragment.faculty_fragment;
import com.example.genius.ui.Library_Fragment.library_fragment;
import com.example.genius.ui.Library_Fragment.library_video_fragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Faculty_Adapter extends RecyclerView.Adapter<Faculty_Adapter.ViewHolder> {

    Context context;
    List<FacultyModel> facultyModelList;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    UserModel userpermission;

    public Faculty_Adapter(Context context, List<FacultyModel> facultyModelList) {
        this.context = context;
        this.facultyModelList = facultyModelList;
    }

    @NonNull
    @Override
    public Faculty_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowFacultyListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Faculty_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        for (UserModel.UserPermission model : userpermission.getPermission())
        {
            if (model.getPageInfo().getPageID() == 77){
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
        Glide.with(context).load(facultyModelList.get(position).getFilePath()).into(holder.binding.facultyImage);
        holder.binding.facultyName.setText(facultyModelList.get(position).getStaff().getName());
        holder.binding.courseName.setText(facultyModelList.get(position).getBranchCourse().getCourse().getCourseName());
        holder.binding.standard.setText(facultyModelList.get(position).getBranchClass().getClassModel().getClassName());
        holder.binding.subject.setText(facultyModelList.get(position).getBranchSubject().getSubject().getSubjectName());

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
                title.setText("Are you sure that you want to Edit Faculty?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();
                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());
                btn_edit_yes.setOnClickListener(v12 -> {
                    faculty_fragment orderplace = new faculty_fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("FACULTY_LIST", new Gson().toJson(facultyModelList.get(position)));
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
                title.setText("Are you sure that you want to delete this Faculty?");
                AlertDialog dialog = builder.create();
                btn_cancel.setOnClickListener(v13 -> dialog.dismiss());
                btn_delete.setOnClickListener(v14 -> {
                    dialog.dismiss();
                    progressBarHelper.showProgressDialog();
                    Call<CommonModel> call = apiCalling.Remove_Faculty(facultyModelList.get(position).getFacultyID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    call.enqueue(new Callback<CommonModel>() {
                        @Override
                        public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                            if (response.isSuccessful()) {
                                CommonModel model = response.body();
                                if (model.isCompleted()) {
                                    if (model.isData()) {
                                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                        facultyModelList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyDataSetChanged();
                                    }else {
                                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
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
        return facultyModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowFacultyListBinding binding;

        public ViewHolder(@NonNull RowFacultyListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }

    public void filterList(List<FacultyModel> filteredList) {
        facultyModelList = filteredList;
        notifyDataSetChanged();
    }
}
