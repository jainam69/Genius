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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.StudentModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.databinding.StudentMasterDeatilListBinding;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Student_Registration_Fragment.student_registration_fragment;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StudentMaster_Adapter extends RecyclerView.Adapter<StudentMaster_Adapter.ViewHolder> {

    Context context;
    List<StudentModel> studentDetails;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");
    UserModel.PageData userpermission;

    public StudentMaster_Adapter(Context context, List<StudentModel> studentDetails) {
        this.context = context;
        this.studentDetails = studentDetails;
    }

    @Override
    public StudentMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(StudentMasterDeatilListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull StudentMaster_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        for (UserModel.PageInfoEntity model : userpermission.Data){
            if (model.getPageID() == 8 && !model.Createstatus){
                holder.binding.studentEdit.setVisibility(View.GONE);
            }
        }
        holder.binding.studentName.setText(studentDetails.get(position).getFirstName() + " " + studentDetails.get(position).getLastName());
        if (studentDetails.get(position).getAdmissionDate() != null) {
            String a = studentDetails.get(position).getAdmissionDate().replace("T00:00:00", "");
            try {
                Date d = actualdate.parse(a);
                holder.binding.admissionDate.setText(displaydate.format(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        holder.binding.course.setText(""+studentDetails.get(position).getBranchCourse().getCourse().getCourseName());
        holder.binding.standard.setText(""+studentDetails.get(position).getBranchClass().classModel.getClassName());
        int a = studentDetails.get(position).getBatchInfo().getBatchTime();
        if (a == 1) {
            holder.binding.batchTime.setText("Morning");
        } else if (a == 2) {
            holder.binding.batchTime.setText("Afternoon");
        } else {
            holder.binding.batchTime.setText("Evening");
        }
        holder.binding.contactNo.setText(studentDetails.get(position).getContactNo());
        holder.binding.studentEdit.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to Edit Student?");
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
                        student_registration_fragment orderplace = new student_registration_fragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("STUDENT_DATA", new Gson().toJson(studentDetails.get(position)));
                        orderplace.setArguments(bundle);
                        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
                dialog.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return studentDetails.size();
    }

    public void filterList(List<StudentModel> filteredList) {
        studentDetails = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        StudentMasterDeatilListBinding binding;

        public ViewHolder(@NonNull StudentMasterDeatilListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.PageData.class);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }
}
