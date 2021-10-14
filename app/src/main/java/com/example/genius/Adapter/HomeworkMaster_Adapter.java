package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
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
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.HomeworkByIdData;
import com.example.genius.Model.HomeworkModel;
import com.example.genius.Model.PaperByIdData;
import com.example.genius.Model.PaperModel;
import com.example.genius.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Homework_Fragment.HomeWorkCheckingFragment;
import com.example.genius.ui.Homework_Fragment.homework_fragment;

import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
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

@SuppressLint("SimpleDateFormat")
public class HomeworkMaster_Adapter extends RecyclerView.Adapter<HomeworkMaster_Adapter.ViewHolder> {

    Context context;
    List<HomeworkModel> homeworkDetails;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");
    int id;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    long downloadID;
    String Name;
    byte[] imageVal;

    public HomeworkMaster_Adapter(Context context, List<HomeworkModel> homeworkDetails) {
        this.context = context;
        this.homeworkDetails = homeworkDetails;
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
    }

    @NotNull
    @Override
    public HomeworkMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeworkMaster_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.homework_master_deatil_list, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HomeworkMaster_Adapter.ViewHolder holder, int position) {
        String a = homeworkDetails.get(position).getHomeworkDate().replace("T00:00:00", "");
        try {
            Date d = actualdate.parse(a);
            if (d != null) {
                holder.date.setText(displaydate.format(d));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.standard.setText(homeworkDetails.get(position).getStandardInfo().getStandard());
        holder.subject.setText(homeworkDetails.get(position).getSubjectInfo().getSubject());
        holder.batch_time.setText(homeworkDetails.get(position).getBatchTimeText());

        holder.homework_edit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to Edit Homework?");
            image.setImageResource(R.drawable.ic_edit);
            AlertDialog dialog = builder.create();

            btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

            btn_edit_yes.setOnClickListener(v12 -> {
                dialog.dismiss();
                homework_fragment orderplace = new homework_fragment();
                Bundle bundle = new Bundle();
                bundle.putLong("HomeworkID", homeworkDetails.get(position).getHomeworkID());
                bundle.putLong("BranchID", homeworkDetails.get(position).getBranchInfo().getBranchID());
                bundle.putString("HomeworkDate", homeworkDetails.get(position).getHomeworkDate());
                bundle.putLong("StandardID", homeworkDetails.get(position).getStandardInfo().getStandardID());
                bundle.putLong("SubjectID", homeworkDetails.get(position).getSubjectInfo().getSubjectID());
                bundle.putString("BatchTimeText", homeworkDetails.get(position).getBatchTimeText());
                bundle.putString("Remarks", homeworkDetails.get(position).getRemarks());
                bundle.putString("HomeworkContentText", homeworkDetails.get(position).getHomeworkContentText());
                bundle.putString("HomeworkContentFileName", homeworkDetails.get(position).getHomeworkContentFileName());
                bundle.putLong("TransactionId", homeworkDetails.get(position).getTransaction().getTransactionId());
                orderplace.setArguments(bundle);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            });
            dialog.show();
        });
        holder.homework_delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
            Button btn_delete = dialogView.findViewById(R.id.btn_delete);
            TextView title = dialogView.findViewById(R.id.title);
            ImageView image = dialogView.findViewById(R.id.image);
            image.setImageResource(R.drawable.delete);
            title.setText("Are you sure that you want to delete this Homework?");
            AlertDialog dialog = builder.create();

            btn_cancel.setOnClickListener(v13 -> dialog.dismiss());

            btn_delete.setOnClickListener(v14 -> {
                dialog.dismiss();
                if (Function.checkNetworkConnection(context)) {
                    progressBarHelper.showProgressDialog();
                    Call<CommonModel> call = apiCalling.RemoveHomework(homeworkDetails.get(position).getHomeworkID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    call.enqueue(new Callback<CommonModel>() {
                        @Override
                        public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                            if (response.isSuccessful()) {
                                CommonModel model = response.body();
                                if (model != null && model.isCompleted()) {
                                    if (model.isData()) {
                                        homeworkDetails.remove(position);
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
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        });
        holder.homework_checking.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to Check Homework?");
            image.setImageResource(R.drawable.check);
            AlertDialog dialog = builder.create();

            btn_edit_no.setOnClickListener(v15 -> dialog.dismiss());

            btn_edit_yes.setOnClickListener(v16 -> {
                dialog.dismiss();
                HomeWorkCheckingFragment orderplace = new HomeWorkCheckingFragment();
                Bundle bundle = new Bundle();
                orderplace.setArguments(bundle);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            });
            dialog.show();
        });
        holder.homework_download.setOnClickListener(v -> {
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

            btn_edit_no.setOnClickListener(v18 -> dialog.dismiss());

            btn_edit_yes.setOnClickListener(v17 -> {
                progressBarHelper.showProgressDialog();
                Call<HomeworkByIdData> call = apiCalling.GetHomeworkByHWID(homeworkDetails.get(position).getHomeworkID());
                call.enqueue(new Callback<HomeworkByIdData>() {
                    @Override
                    public void onResponse(@NotNull Call<HomeworkByIdData> call, @NotNull Response<HomeworkByIdData> response) {
                        if (response.isSuccessful()) {
                            progressBarHelper.hideProgressDialog();
                            HomeworkByIdData paperData = response.body();
                            if (paperData != null && paperData.Completed) {
                                HomeworkModel paperModelList = paperData.Data;
                                if (paperModelList != null) {
                                    Toast.makeText(context, "Download Start", Toast.LENGTH_SHORT).show();
                                    String a1 = paperModelList.getHomeworkContentText();
                                    imageVal = Base64.decode(a1, Base64.DEFAULT);
                                    try {
                                        FileOutputStream out = new FileOutputStream(
                                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                                        + "/" + paperModelList.getHomeworkContentFileName());
                                        out.write(imageVal);
                                        out.close();
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                        Log.e("Error", e.toString());
                                    }
                                    String z = paperModelList.getHomeworkContentFileName();
                                    String FileName;
                                    if (z.contains("/")) {
                                        FileName = z.substring(z.lastIndexOf("/"));
                                    } else {
                                        FileName = z;
                                    }
                                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + FileName;
                                    Toast.makeText(context, "File Stored in " + path, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<HomeworkByIdData> call, @NotNull Throwable t) {
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
        return homeworkDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date, standard, subject, batch_time;
        ImageView homework_edit, homework_delete, homework_checking, homework_download;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            standard = itemView.findViewById(R.id.standard);
            subject = itemView.findViewById(R.id.subject);
            batch_time = itemView.findViewById(R.id.batch_time);
            homework_edit = itemView.findViewById(R.id.homework_edit);
            homework_delete = itemView.findViewById(R.id.homework_delete);
            homework_checking = itemView.findViewById(R.id.homework_checking);
            homework_download = itemView.findViewById(R.id.homework_download);
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
        return matcher.replaceAll("");
    }

    public boolean accept(String pathname) {
        String ext = null;
        int i = pathname.lastIndexOf('.');


        if (i > 0 && i < pathname.length() - 1) {
            ext = pathname.substring(i + 1).toLowerCase();
        }

        if (ext == null)
            return false;
        else
            return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif");
    }

    public void filterList(List<HomeworkModel> filteredList) {
        homeworkDetails = filteredList;
        notifyDataSetChanged();
    }
}
