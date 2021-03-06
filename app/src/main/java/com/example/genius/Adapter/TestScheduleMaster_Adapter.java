package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
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
import com.example.genius.Model.TestScheduleModel;
import com.example.genius.Model.UploadPaperData;
import com.example.genius.Model.UploadPaperModel;
import com.example.genius.Model.UserModel;
import com.example.genius.databinding.TestscheduleMasterDeatilListBinding;
import com.example.genius.helper.Function;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Test_Paper_Entry_Fragment.Test_Paper_Checking_fragment;
import com.example.genius.ui.Test_Schedule.test_schedule_fragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SimpleDateFormat")
public class TestScheduleMaster_Adapter extends RecyclerView.Adapter<TestScheduleMaster_Adapter.ViewHolder> {

    Context context;
    List<TestScheduleModel> testScheduleDetails;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");
    UserModel.PageData userpermission;
    long downloadID;
    String Name;

    public TestScheduleMaster_Adapter(Context context, List<TestScheduleModel> testScheduleDetails) {
        this.context = context;
        this.testScheduleDetails = testScheduleDetails;
    }

    @NotNull
    @Override
    public TestScheduleMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(TestscheduleMasterDeatilListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TestScheduleMaster_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        for (UserModel.PageInfoEntity model : userpermission.Data){
            if (model.getPageID() == 84){
                if (!model.Createstatus){
                    holder.binding.testscheduleEdit.setVisibility(View.GONE);
                }
                if (!model.Deletestatus){
                    holder.binding.testscheduleDelete.setVisibility(View.GONE);
                }
                if (!model.Createstatus && !model.Deletestatus){
                    holder.binding.testscheduleEdit.setVisibility(View.GONE);
                    holder.binding.testscheduleDelete.setVisibility(View.GONE);
                }
            }
        }
        holder.binding.course.setText(testScheduleDetails.get(position).getBranchCourse().getCourse().getCourseName());
        holder.binding.standard.setText(testScheduleDetails.get(position).getBranchClass().getClassModel().getClassName());
        holder.binding.batchTime.setText(testScheduleDetails.get(position).getBatchTimeText());
        String a = testScheduleDetails.get(position).getTestDate().replace("T00:00:00", "");
        try {
            Date d = actualdate.parse(a);
            holder.binding.testDate.setText(displaydate.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.binding.testTime.setText(testScheduleDetails.get(position).getTestStartTime() + "  -  " + testScheduleDetails.get(position).getTestEndTime());
        holder.binding.subject.setText(testScheduleDetails.get(position).getBranchSubject().getSubject().getSubjectName());
        holder.binding.totalMarks.setText("" + testScheduleDetails.get(position).getMarks());

        holder.binding.testscheduleEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to Edit Test Schedule?");
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
                    test_schedule_fragment orderplace = new test_schedule_fragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong("Standard", testScheduleDetails.get(position).getBranchClass().getClass_dtl_id());
                    bundle.putInt("BatchTime", testScheduleDetails.get(position).getBatchTimeID());
                    bundle.putLong("TestSubject", testScheduleDetails.get(position).getBranchSubject().getSubject_dtl_id());
                    bundle.putLong("Course", testScheduleDetails.get(position).getBranchCourse().getCourse_dtl_id());
                    bundle.putString("TestDate", testScheduleDetails.get(position).getTestDate());
                    bundle.putString("StartTime", testScheduleDetails.get(position).getTestStartTime());
                    bundle.putString("EndTime", testScheduleDetails.get(position).getTestEndTime());
                    bundle.putDouble("TestMarks", testScheduleDetails.get(position).getMarks());
                    bundle.putString("TestRemarks", testScheduleDetails.get(position).getRemarks());
                    bundle.putLong("TestID", testScheduleDetails.get(position).getTestID());
                    bundle.putLong("TransactionId", testScheduleDetails.get(position).getTransaction().getTransactionId());
                    orderplace.setArguments(bundle);
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            dialog.show();
        });

        holder.binding.testscheduleDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
            TextView btn_delete = dialogView.findViewById(R.id.btn_delete);
            TextView title = dialogView.findViewById(R.id.title);
            ImageView image = dialogView.findViewById(R.id.image);
            image.setImageResource(R.drawable.delete);
            title.setText("Are you sure that you want to delete this Test Schedule?");
            AlertDialog dialog = builder.create();

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBarHelper.showProgressDialog();
                    Call<CommonModel> call = apiCalling.RemoveTest(testScheduleDetails.get(position).getTestID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), true);
                    call.enqueue(new Callback<CommonModel>() {
                        @Override
                        public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                            if (response.isSuccessful()) {
                                CommonModel model = response.body();
                                if (model.isCompleted()) {
                                    if (model.isData()) {
                                        Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                        testScheduleDetails.remove(position);
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
                        }
                    });
                    dialog.dismiss();
                }
            });
            dialog.show();
        });

        holder.binding.testPaper.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to Download Test Paper?");
                image.setImageResource(R.drawable.document);
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
                        if (Function.isNetworkAvailable(context)) {
                            progressBarHelper.showProgressDialog();
                            Call<UploadPaperData> call = apiCalling.GetAllTestPapaerByTest(testScheduleDetails.get(position).getTestID());
                            call.enqueue(new Callback<UploadPaperData>() {
                                @Override
                                public void onResponse(@NotNull Call<UploadPaperData> call, @NotNull Response<UploadPaperData> response) {
                                    if (response.isSuccessful()) {
                                        UploadPaperData data = response.body();
                                        if (data.isCompleted()) {
                                            List<UploadPaperModel> models = data.getData();
                                            if (models != null && models.size() > 0) {
                                                if (!models.get(0).getFileName().equals("")){
                                                    String path = models.get(0).getFilePath();
                                                    Toast.makeText(context, "Download Started..", Toast.LENGTH_SHORT).show();
                                                    DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                                    Uri uri = Uri.parse(path);
                                                    DownloadManager.Request request = new DownloadManager.Request(uri);
                                                    Name = models.get(0).getFileName();
                                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/AshirvadStudyCircle/" + Name);
                                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                                    downloadID = dm.enqueue(request);
                                                    context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                                }else {
                                                    try {
                                                        String url = models.get(0).getDocLink();
                                                        if (!models.get(0).getDocLink().startsWith("http://") && !models.get(0).getDocLink().startsWith("https://")){
                                                            url = "http://" + models.get(0).getDocLink();
                                                        }
                                                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                        webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        context.startActivity(webIntent);
                                                    } catch (ActivityNotFoundException ex) {
                                                        ex.printStackTrace();
                                                    }
                                                }
                                            }else {
                                                Toast.makeText(context, "You have not uploaded Test Paper.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }

                                @Override
                                public void onFailure(@NotNull Call<UploadPaperData> call, @NotNull Throwable t) {
                                    Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                                    progressBarHelper.hideProgressDialog();
                                }
                            });
                        } else {
                            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });

        holder.binding.paperView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to View Student AnswerSheet?");
            image.setImageResource(R.drawable.view);
            AlertDialog dialog = builder.create();

            btn_edit_no.setOnClickListener(v15 -> dialog.dismiss());

            btn_edit_yes.setOnClickListener(v16 -> {
                dialog.dismiss();
                Test_Paper_Checking_fragment orderplace = new Test_Paper_Checking_fragment();
                Bundle bundle = new Bundle();
                bundle.putLong("TestID", testScheduleDetails.get(position).getTestID());
                orderplace.setArguments(bundle);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            });
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return testScheduleDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TestscheduleMasterDeatilListBinding binding;

        public ViewHolder(@NonNull TestscheduleMasterDeatilListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.PageData.class);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }

    public void filterList(List<TestScheduleModel> filteredList) {
        testScheduleDetails = filteredList;
        notifyDataSetChanged();
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
