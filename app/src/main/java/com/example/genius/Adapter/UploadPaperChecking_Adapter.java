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
import com.example.genius.Model.AnswerSheetModel;
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
    String Name;
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
        holder.standard.setText("");
        holder.stu_name.setText(answerSheetModels.get(position).getStudentInfo().getFirstName() + " " + answerSheetModels.get(position).getStudentInfo().getLastName());
        holder.status.setText(answerSheetModels.get(position).getStatusText());
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

                    }
                });
                dialog.show();
            }
        });
        holder.homework_checking_download.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to Download Test Paper?");
                image.setImageResource(R.drawable.download);
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

                    }
                });
                dialog.show();
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
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(context, "Download " + Name + " Completed And Stored In AshirvadStudyCircle Folder...", Toast.LENGTH_LONG).show();
            }
        }
    };

    public static String getOnlyDigits(String s) {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }

    public boolean accept(String pathname) {
        final String name = pathname;
        String ext = null;
        int i = name.lastIndexOf('.');


        if (i > 0 && i < name.length() - 1) {
            ext = name.substring(i + 1).toLowerCase();
        }

        if (ext == null)
            return false;
        else return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif");
    }
}
