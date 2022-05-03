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
import com.example.genius.Model.HomeworkModel;
import com.example.genius.Model.UserModel;
import com.example.genius.databinding.HomeworkMasterDeatilListBinding;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Homework_Fragment.HomeWorkCheckingFragment;
import com.example.genius.ui.Homework_Fragment.homework_fragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

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
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    long downloadID;
    String Name;
    UserModel.PageData userpermission;

    public HomeworkMaster_Adapter(Context context, List<HomeworkModel> homeworkDetails) {
        this.context = context;
        this.homeworkDetails = homeworkDetails;
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
    }

    @NotNull
    @Override
    public HomeworkMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(HomeworkMasterDeatilListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HomeworkMaster_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        for (UserModel.PageInfoEntity model : userpermission.Data)
        {
            if (model.getPageID() == 43){
                if (!model.Createstatus){
                    holder.binding.homeworkEdit.setVisibility(View.GONE);
                }
                if (!model.Deletestatus){
                    holder.binding.homeworkDelete.setVisibility(View.GONE);
                }
            }
        }
        String a = homeworkDetails.get(position).getHomeworkDate().replace("T00:00:00", "");
        try {
            Date d = actualdate.parse(a);
            if (d != null) {
                holder.binding.date.setText(displaydate.format(d));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.binding.course.setText(homeworkDetails.get(position).getBranchCourse().getCourse().getCourseName());
        holder.binding.standard.setText(homeworkDetails.get(position).getBranchClass().getClassModel().getClassName());
        holder.binding.subject.setText(homeworkDetails.get(position).getBranchSubject().getSubject().getSubjectName());
        holder.binding.batchTime.setText(homeworkDetails.get(position).getBatchTimeText());

        holder.binding.homeworkEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
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
                bundle.putString("HomeworkDate", homeworkDetails.get(position).getHomeworkDate());
                bundle.putLong("CourseID", homeworkDetails.get(position).getBranchCourse().getCourse_dtl_id());
                bundle.putLong("StandardID", homeworkDetails.get(position).getBranchClass().getClass_dtl_id());
                bundle.putLong("SubjectID", homeworkDetails.get(position).getBranchSubject().getSubject_dtl_id());
                bundle.putString("BatchTimeText", homeworkDetails.get(position).getBatchTimeText());
                bundle.putString("Remarks", homeworkDetails.get(position).getRemarks());
                bundle.putLong("TransactionId", homeworkDetails.get(position).getTransaction().getTransactionId());
                bundle.putString("FilePath", homeworkDetails.get(position).getFilePath());
                bundle.putString("FileName", homeworkDetails.get(position).getHomeworkContentFileName());
                orderplace.setArguments(bundle);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            });
            dialog.show();
        });
        holder.binding.homeworkDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
            TextView btn_delete = dialogView.findViewById(R.id.btn_delete);
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
                                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                        homeworkDetails.remove(position);
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
                        }
                    });
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        });
        holder.binding.homeworkChecking.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
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
                bundle.putLong("ID", homeworkDetails.get(position).getHomeworkID());
                orderplace.setArguments(bundle);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            });
            dialog.show();
        });
        holder.binding.homeworkDownload.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to Download Document?");
            image.setImageResource(R.drawable.download);
            AlertDialog dialog = builder.create();

            btn_edit_no.setOnClickListener(v18 -> dialog.dismiss());

            btn_edit_yes.setOnClickListener(v17 -> {
                dialog.dismiss();
                if (homeworkDetails.get(position).getHomeworkContentFileName() != null){
                    String filetype = homeworkDetails.get(position).getFilePath();
                    Toast.makeText(context, "Download Started..", Toast.LENGTH_SHORT).show();
                    DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(filetype);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    Name = homeworkDetails.get(position).getHomeworkContentFileName();
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/AshirvadStudyCircle/" + Name);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    downloadID = dm.enqueue(request);
                    context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                }else {
                    Toast.makeText(context, "Document is not Available.", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return homeworkDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        HomeworkMasterDeatilListBinding binding;

        public ViewHolder(@NonNull HomeworkMasterDeatilListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.PageData.class);
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

    public void filterList(List<HomeworkModel> filteredList) {
        homeworkDetails = filteredList;
        notifyDataSetChanged();
    }
}
