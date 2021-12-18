package com.example.genius.Adapter;

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
        return new Faculty_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_faculty_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Faculty_Adapter.ViewHolder holder, int position) {
        if (userpermission.getPermission().get(11).getPageInfo().getPageID() == 77){
            if (!userpermission.getPermission().get(11).getPackageRightinfo().isCreatestatus()){
                holder.img_edit.setVisibility(View.GONE);
            }
            if (!userpermission.getPermission().get(11).getPackageRightinfo().isDeletestatus()){
                holder.img_delete.setVisibility(View.GONE);
            }
            if (!userpermission.getPermission().get(11).getPackageRightinfo().isCreatestatus() && !userpermission.getPermission().get(11).getPackageRightinfo().isDeletestatus()){
                holder.linear_actions.setVisibility(View.GONE);
            }
        }
        Glide.with(context).load(facultyModelList.get(position).getFilePath()).into(holder.faculty_image);
        holder.faculty_name.setText(facultyModelList.get(position).getStaff().getName());
        holder.course_name.setText(facultyModelList.get(position).getBranchCourse().getCourse().getCourseName());
        holder.standard.setText(facultyModelList.get(position).getBranchClass().getClassModel().getClassName());
        holder.subject.setText(facultyModelList.get(position).getBranchSubject().getSubject().getSubjectName());

        holder.img_edit.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to Edit Faculty?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();
                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());
                btn_edit_yes.setOnClickListener(v12 -> {
                    faculty_fragment orderplace = new faculty_fragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("FACULTY_LIST", facultyModelList.get(position));
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

        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                Button btn_delete = dialogView.findViewById(R.id.btn_delete);
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
                                if (model != null && model.isCompleted()) {
                                    if (model.isData()) {
                                        Toast.makeText(context, "Faculty Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                        facultyModelList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyDataSetChanged();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<CommonModel> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
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

        ImageView faculty_image,img_edit,img_delete;
        TextView faculty_name,course_name,standard,subject;
        LinearLayout linear_actions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            faculty_image = itemView.findViewById(R.id.faculty_image);
            img_edit = itemView.findViewById(R.id.img_edit);
            img_delete = itemView.findViewById(R.id.img_delete);
            faculty_name = itemView.findViewById(R.id.faculty_name);
            course_name = itemView.findViewById(R.id.course_name);
            standard = itemView.findViewById(R.id.standard);
            subject = itemView.findViewById(R.id.subject);
            linear_actions = itemView.findViewById(R.id.linear_actions);
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
