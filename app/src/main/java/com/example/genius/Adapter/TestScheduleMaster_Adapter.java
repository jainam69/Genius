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
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Test_Paper_Entry_Fragment.Test_Paper_Checking_fragment;
import com.example.genius.ui.Test_Schedule.test_schedule_fragment;

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

    public TestScheduleMaster_Adapter(Context context, List<TestScheduleModel> testScheduleDetails) {
        this.context = context;
        this.testScheduleDetails = testScheduleDetails;
    }

    @NotNull
    @Override
    public TestScheduleMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TestScheduleMaster_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.testschedule_master_deatil_list, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TestScheduleMaster_Adapter.ViewHolder holder, int position) {
        holder.standard.setText(testScheduleDetails.get(position).getStandard().getStandard());
        holder.batch_time.setText(testScheduleDetails.get(position).getBatchTimeText());
        String a = testScheduleDetails.get(position).getTestDate().replace("T00:00:00", "");
        try {
            Date d = actualdate.parse(a);
            holder.test_date.setText(displaydate.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.test_time.setText(testScheduleDetails.get(position).getTestStartTime() + "  -  " + testScheduleDetails.get(position).getTestEndTime());
        holder.subject.setText(testScheduleDetails.get(position).getSubject().getSubject());
        holder.total_marks.setText("" + testScheduleDetails.get(position).getMarks());

        holder.testschedule_edit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
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
                    bundle.putLong("Standard", testScheduleDetails.get(position).getStandard().getStandardID());
                    bundle.putInt("BatchTime", testScheduleDetails.get(position).getBatchTimeID());
                    bundle.putLong("TestSubject", testScheduleDetails.get(position).getSubject().getSubjectID());
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

        holder.testschedule_delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
            Button btn_delete = dialogView.findViewById(R.id.btn_delete);
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
                                        Toast.makeText(context, "Test Schedule Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                        testScheduleDetails.remove(position);
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
                    dialog.dismiss();
                }
            });
            dialog.show();
        });

        holder.test_paper.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to View Test Paper?");
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
                    }
                });
                dialog.show();
            }
        });

        holder.paper_view.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
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

        TextView standard, batch_time, test_date, test_time, subject, total_marks;
        ImageView testschedule_edit, testschedule_delete, paper_view,test_paper;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            standard = itemView.findViewById(R.id.standard);
            batch_time = itemView.findViewById(R.id.batch_time);
            test_date = itemView.findViewById(R.id.test_date);
            test_time = itemView.findViewById(R.id.test_time);
            subject = itemView.findViewById(R.id.subject);
            total_marks = itemView.findViewById(R.id.total_marks);
            testschedule_edit = itemView.findViewById(R.id.testschedule_edit);
            testschedule_delete = itemView.findViewById(R.id.testschedule_delete);
            paper_view = itemView.findViewById(R.id.paper_view);
            test_paper = itemView.findViewById(R.id.test_paper);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }

    public void filterList(List<TestScheduleModel> filteredList) {
        testScheduleDetails = filteredList;
        notifyDataSetChanged();
    }
}
