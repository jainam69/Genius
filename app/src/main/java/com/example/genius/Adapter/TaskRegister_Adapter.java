package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
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
import com.example.genius.Model.PaperByIdData;
import com.example.genius.Model.PaperModel;
import com.example.genius.Model.ToDoByIdData;
import com.example.genius.Model.TodoModel;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;

import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class TaskRegister_Adapter extends RecyclerView.Adapter<TaskRegister_Adapter.ViewHolder> {

    Context context;
    List<TodoModel> todoModels;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    String sts, Name;
    int select, id;
    long downloadID;
    byte[] imageVal;

    public TaskRegister_Adapter(Context context, List<TodoModel> todoModels) {
        this.context = context;
        this.todoModels = todoModels;
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_register_master_deatil_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bname.setText("" + todoModels.get(position).getBranchInfo().getBranchName());
        holder.task_date.setText("" + todoModels.get(position).getToDoDate());
        holder.staff_name.setText("" + todoModels.get(position).getUserInfo().getUsername());
//        holder.status.setText(""+todoModels.get(position).getStatus());
        holder.task_reg_edit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to Edit Task?");
            image.setImageResource(R.drawable.ic_edit);
            AlertDialog dialog = builder.create();

            btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

            btn_edit_yes.setOnClickListener(v12 -> {
                dialog.dismiss();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView1 = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_task_register, null);
                builder1.setView(dialogView1);
                builder1.setCancelable(true);
                Button edit = dialogView1.findViewById(R.id.edit_task_reg);
                RadioGroup rg = dialogView1.findViewById(R.id.status_rg);
                RadioButton pending = dialogView1.findViewById(R.id.pending);
                RadioButton done = dialogView1.findViewById(R.id.done);
                EditText remark = dialogView1.findViewById(R.id.remarks);
                AlertDialog dialog1 = builder1.create();
//                        String st = todoModels.get(position).getStatus();
//                        if (st.equals("Pending"))
//                        {
//                            pending.setChecked(true);
//                            done.setChecked(false);
//                        }
//                        if (st.equals("Done"))
//                        {
//                            pending.setChecked(false);
//                            done.setChecked(true);
//                        }
                rg.setOnCheckedChangeListener((group, checkedId) -> {
                    holder.rb1 = dialogView1.findViewById(checkedId);
                    sts = holder.rb1.getText().toString();
                });
                select = rg.getCheckedRadioButtonId();
                holder.rb1 = dialogView1.findViewById(select);
                sts = holder.rb1.getText().toString();

                dialog1.show();
                edit.setOnClickListener(v121 -> {
//                                progressBarHelper.showProgressDialog();
                    id = (int) todoModels.get(position).getToDoID();
                });
            });
            dialog.show();
        });
        holder.task_reg_download.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to Download Document?");
            image.setImageResource(R.drawable.download);
            AlertDialog dialog = builder.create();

            btn_edit_no.setOnClickListener(v13 -> dialog.dismiss());

            btn_edit_yes.setOnClickListener(v14 -> {
                dialog.dismiss();
                progressBarHelper.showProgressDialog();
                Call<ToDoByIdData> call = apiCalling.GetToDoByHWID(todoModels.get(position).getToDoID());
                call.enqueue(new Callback<ToDoByIdData>() {
                    @Override
                    public void onResponse(@NotNull Call<ToDoByIdData> call, @NotNull Response<ToDoByIdData> response) {
                        if (response.isSuccessful()) {
                            progressBarHelper.hideProgressDialog();
                            ToDoByIdData paperData = response.body();
                            if (paperData.Completed) {
                                TodoModel paperModelList = paperData.Data;
                                if (paperModelList != null) {
                                    String a = paperModelList.getToDoContentText();
                                    imageVal = Base64.decode(a, Base64.DEFAULT);
                                    try {
                                        FileOutputStream out = new FileOutputStream(
                                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                                        + "/" + paperModelList.getToDoFileName());
                                        out.write(imageVal);
                                        out.close();
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                        Log.e("Error", e.toString());
                                    }
                                    String path = Environment.getExternalStorageDirectory() + "/" + paperModelList.getToDoFileName();
                                    Toast.makeText(context, "File Stored in " + path, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ToDoByIdData> call, @NotNull Throwable t) {
                        Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show();
                        progressBarHelper.hideProgressDialog();
                    }
                });
            });
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return todoModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView bname, task_date, staff_name, status;
        ImageView task_reg_edit, task_reg_download;
        RadioButton rb1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bname = itemView.findViewById(R.id.bname);
            task_date = itemView.findViewById(R.id.task_date);
            staff_name = itemView.findViewById(R.id.staff_name);
            status = itemView.findViewById(R.id.status);
            task_reg_edit = itemView.findViewById(R.id.task_reg_edit);
            task_reg_download = itemView.findViewById(R.id.task_reg_download);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }

}
