package com.example.genius.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.HomeworkModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeworkCheckingAdapter extends RecyclerView.Adapter<HomeworkCheckingAdapter.ViewHolder> {

    List<HomeworkModel> homeworkModels;
    Context context;
    String pending_done,remarks,created_by;
    int select,status;
    long hwID,StdID,created_id;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");

    public HomeworkCheckingAdapter(List<HomeworkModel> homeworkModels, Context context) {
        this.homeworkModels = homeworkModels;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeworkCheckingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeworkCheckingAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.homework_master_check_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeworkCheckingAdapter.ViewHolder holder, int position) {
        String a = homeworkModels.get(position).getHomeworkDate().replace("T00:00:00", "");
        try {
            Date d = actualdate.parse(a);
            if (d != null) {
                holder.submit_date.setText(displaydate.format(d));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.standard.setText(homeworkModels.get(position).getStandardInfo().getStandard());
        holder.student_name.setText(homeworkModels.get(position).getStudentInfo().getName());
        int st = homeworkModels.get(position).getStatus();
        if (st == 2){
            holder.status.setText("Pending");
        }else {
            holder.status.setText("Done");
        }
        holder.remark.setText(homeworkModels.get(position).getRemarks());
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
                title.setText("Are you sure that you want to Edit Homework Checking?");
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                        View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_homework_checking, null);
                        builder.setView(dialogView);
                        builder.setCancelable(true);
                        Button edit = dialogView.findViewById(R.id.home_check_edit);
                        RadioGroup rg = dialogView.findViewById(R.id.rg);
                        RadioButton pending = dialogView.findViewById(R.id.pending);
                        RadioButton done = dialogView.findViewById(R.id.done);
                        EditText rm = dialogView.findViewById(R.id.remarks);
                        AlertDialog dialog = builder.create();
                        int rb = homeworkModels.get(position).getStatus();
                        if (rb == 2)
                        {
                            pending.setChecked(true);
                            done.setChecked(false);
                        }
                        if (rb == 1)
                        {
                            pending.setChecked(false);
                            done.setChecked(true);
                        }
                        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                holder.rb1 = dialogView.findViewById(checkedId);
                                pending_done = holder.rb1.getText().toString();
                            }
                        });
                        select = rg.getCheckedRadioButtonId();
                        holder.rb1 = dialogView.findViewById(select);
                        pending_done = holder.rb1.getText().toString();
                        String remark = homeworkModels.get(position).getRemarks();
                        rm.setText(remark);

                        dialog.show();
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progressBarHelper.showProgressDialog();
                                hwID = homeworkModels.get(position).getHomeworkID();
                                StdID = homeworkModels.get(position).getStudentInfo().getStudentID();
                                remarks = rm.getText().toString();
                                if (pending_done.equals("Pending")){
                                    status = 2;
                                }else {
                                    status = 1;
                                }
                                created_by = Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME);
                                created_id = Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID);
                                Call<HomeworkModel.HomeworkDetailData> call = apiCalling.Update_Homework_Checking(hwID,StdID,remarks,status,created_by,created_id);
                                call.enqueue(new Callback<HomeworkModel.HomeworkDetailData>() {
                                    @Override
                                    public void onResponse(Call<HomeworkModel.HomeworkDetailData> call, Response<HomeworkModel.HomeworkDetailData> response) {
                                        if (response.isSuccessful()){
                                            HomeworkModel.HomeworkDetailData data = response.body();
                                            if (data.isCompleted()){
                                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                                holder.status.setText(pending_done);
                                                holder.remark.setText(rm.getText().toString());
                                                if (pending_done.equals("Pending")){
                                                    homeworkModels.get(position).setStatus(2);
                                                }else {
                                                    homeworkModels.get(position).setStatus(1);
                                                }
                                                homeworkModels.get(position).setRemarks(rm.getText().toString());
                                                dialog.dismiss();
                                            }else {
                                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                            progressBarHelper.hideProgressDialog();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<HomeworkModel.HomeworkDetailData> call, Throwable t) {
                                        progressBarHelper.hideProgressDialog();
                                        Toast.makeText(context,t.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeworkModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView submit_date,standard,student_name,status,remark;
        ImageView img_edit,img_download;
        RadioButton rb1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            submit_date = itemView.findViewById(R.id.submit_date);
            standard = itemView.findViewById(R.id.standard);
            student_name = itemView.findViewById(R.id.student_name);
            status = itemView.findViewById(R.id.status);
            remark = itemView.findViewById(R.id.remark);
            img_edit = itemView.findViewById(R.id.img_edit);
            img_download = itemView.findViewById(R.id.img_download);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }
}
