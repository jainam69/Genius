package com.example.genius.Adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
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
import com.example.genius.Model.AnswerSheetData;
import com.example.genius.Model.AnswerSheetModel;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadPaperChecking_Adapter extends RecyclerView.Adapter<UploadPaperChecking_Adapter.ViewHolder> {

    Context context;
    List<AnswerSheetModel> answerSheetModels;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    long downloadID;
    String pending_done,remarks,created_by,classname,homework,student,Name;
    int select,status;
    long testID,StdID,created_id;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");

    public UploadPaperChecking_Adapter(Context context, List<AnswerSheetModel> answerSheetModels) {
        this.context = context;
        this.answerSheetModels = answerSheetModels;
    }

    @Override
    public UploadPaperChecking_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UploadPaperChecking_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.homework_checking_master_deatil_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UploadPaperChecking_Adapter.ViewHolder holder, int position) {

        String a = answerSheetModels.get(position).getSubmitDate().replace("T00:00:00", "");
        try {
            Date d = actualdate.parse(a);
            holder.submit_date.setText(displaydate.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.standard.setText(answerSheetModels.get(position).getTestInfo().getStandard().getStandard());
        holder.stu_name.setText(answerSheetModels.get(position).getStudentInfo().getName());
        int st = answerSheetModels.get(position).getStatus();
        if (st == 2){
            holder.status.setText("Pending");
        }else {
            holder.status.setText("Done");
        }
        holder.remarks.setText(answerSheetModels.get(position).getRemarks());

        holder.homework_checking_edit.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to Edit Test Paper?");
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
                        int rb = answerSheetModels.get(position).getStatus();
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
                        String remark = answerSheetModels.get(position).getRemarks();
                        rm.setText(remark);

                        dialog.show();
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progressBarHelper.showProgressDialog();
                                testID = answerSheetModels.get(position).getTestInfo().getTestID();
                                StdID = answerSheetModels.get(position).getStudentInfo().getStudentID();
                                remarks = rm.getText().toString();
                                if (pending_done.equals("Pending")){
                                    status = 2;
                                }else {
                                    status = 1;
                                }
                                created_by = Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME);
                                created_id = Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID);
                                Call<AnswerSheetData> call = apiCalling.Update_Test_Paper_Checking(testID,StdID,remarks,status,created_by,created_id);
                                call.enqueue(new Callback<AnswerSheetData>() {
                                    @Override
                                    public void onResponse(Call<AnswerSheetData> call, Response<AnswerSheetData> response) {
                                        if (response.isSuccessful()){
                                            AnswerSheetData data = response.body();
                                            if (data.isCompleted()){
                                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                                holder.status.setText(pending_done);
                                                holder.remarks.setText(rm.getText().toString());
                                                if (pending_done.equals("Pending")){
                                                    answerSheetModels.get(position).setStatusText("Pending");
                                                }else {
                                                    answerSheetModels.get(position).setStatusText("Done");
                                                }
                                                answerSheetModels.get(position).setRemarks(rm.getText().toString());
                                                dialog.dismiss();
                                            }else {
                                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                            progressBarHelper.hideProgressDialog();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<AnswerSheetData> call, Throwable t) {
                                        progressBarHelper.hideProgressDialog();
                                        Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
        holder.homework_checking_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarHelper.showProgressDialog();
                homework = answerSheetModels.get(position).getSubmitDate().replace("T00:00:00", "");
                student = answerSheetModels.get(position).getStudentInfo().getName().replaceAll("\\s","");
                String name = answerSheetModels.get(position).getTestInfo().getStandard().getStandard().replaceAll("\\s","");
                classname = name.replaceAll("\\.","");
                Call<HomeworkModel.HomeworkData1> call = apiCalling.Download_Student_Test_Paper(answerSheetModels.get(position).getTestInfo().getTestID(),
                        answerSheetModels.get(position).getStudentInfo().getStudentID(),homework,student,classname);
                call.enqueue(new Callback<HomeworkModel.HomeworkData1>() {
                    @Override
                    public void onResponse(Call<HomeworkModel.HomeworkData1> call, Response<HomeworkModel.HomeworkData1> response) {
                        if (response.isSuccessful()){
                            HomeworkModel.HomeworkData1 data = response.body();
                            if (data.isCompleted()){
                                HomeworkModel model = data.getData();
                                String filetype = model.getFilePath();
                                String filetyp = filetype.substring(filetype.lastIndexOf("."));
                                Toast.makeText(context, "Download Started..", Toast.LENGTH_SHORT).show();
                                DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                Uri uri = Uri.parse(filetype);
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                Name = homework + " " + student + " " + classname + filetyp;
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/AshirvadStudyCircle/" + Name);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                downloadID = dm.enqueue(request);
                                context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                            }else {
                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBarHelper.hideProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<HomeworkModel.HomeworkData1> call, Throwable t) {
                        progressBarHelper.hideProgressDialog();
                        Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return answerSheetModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView submit_date,standard,stu_name,status,remarks;
        ImageView homework_checking_edit,homework_checking_download;
        RadioButton rb1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            submit_date = itemView.findViewById(R.id.submit_date);
            standard = itemView.findViewById(R.id.standard);
            stu_name = itemView.findViewById(R.id.stu_name);
            status = itemView.findViewById(R.id.status);
            remarks = itemView.findViewById(R.id.remarks);
            homework_checking_edit = itemView.findViewById(R.id.homework_checking_edit);
            homework_checking_download = itemView.findViewById(R.id.homework_checking_download);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context1, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                Toast.makeText(context, "Download " + Name + " Completed And Stored In AshirvadStudyCircle Folder...", Toast.LENGTH_LONG).show();
            }
        }
    };

}
