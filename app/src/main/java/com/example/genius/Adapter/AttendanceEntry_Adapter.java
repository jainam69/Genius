package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.MediaCodec;
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

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.AttendanceModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.UserModel;
import com.example.genius.databinding.AttendanceEntryMasterDeatilListBinding;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Attendance_Fragment.attendance_fragment;
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
public class AttendanceEntry_Adapter extends RecyclerView.Adapter<AttendanceEntry_Adapter.ViewHolder> {

    Context context;
    List<AttendanceModel> attendanceDetails;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");
    UserModel.PageData userpermission;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;

    public AttendanceEntry_Adapter(Context context, List<AttendanceModel> attendanceDetails) {
        this.context = context;
        this.attendanceDetails = attendanceDetails;
    }

    @NotNull
    @Override
    public AttendanceEntry_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AttendanceEntryMasterDeatilListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceEntry_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        for (UserModel.PageInfoEntity model : userpermission.Data){
            if (model.getPageID() == 18){
                if (!model.Createstatus){
                    holder.binding.attenEdit.setVisibility(View.GONE);
                }
                if (!model.Deletestatus){
                    holder.binding.attenDelete.setVisibility(View.GONE);
                }
                if (!model.Createstatus && !model.Deletestatus){
                    holder.binding.linearCreateDelete.setVisibility(View.GONE);
                }
            }
        }
        String date = attendanceDetails.get(position).getAttendanceDate().replace("T00:00:00","");
        try {
            Date d = actualdate.parse(date);
            holder.binding.attDate.setText(displaydate.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.binding.batchTime.setText(attendanceDetails.get(position).getBatchTypeText());
        holder.binding.std.setText(attendanceDetails.get(position).getBranchClass().getClassModel().getClassName());
        holder.binding.course.setText(attendanceDetails.get(position).getBranchCourse().getCourse().getCourseName());
        holder.binding.attendanceRemark.setText(attendanceDetails.get(position).getAttendanceRemarks());
        holder.binding.attenEdit.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to Edit Attendance?");
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
                        attendance_fragment orderplace = new attendance_fragment();
                        Bundle bundle = new Bundle();
                        bundle.putLong("AttendanceID",attendanceDetails.get(position).getAttendanceID());
                        bundle.putLong("BranchID",attendanceDetails.get(position).getBranch().getBranchID());
                        bundle.putLong("StandardID",attendanceDetails.get(position).getBranchClass().getClass_dtl_id());
                        bundle.putLong("courseid",attendanceDetails.get(position).getBranchCourse().getCourse_dtl_id());
                        bundle.putString("Date",attendanceDetails.get(position).getAttendanceDate());
                        bundle.putInt("BatchId",attendanceDetails.get(position).getBatchTypeID());
                        bundle.putString("BatchName",attendanceDetails.get(position).getBatchTypeText());
                        bundle.putString("AttendanceRemark",attendanceDetails.get(position).getAttendanceRemarks());
                        bundle.putLong("TransactionId",attendanceDetails.get(position).getTransaction().getTransactionId());
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
        holder.binding.attenDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                TextView btn_delete = dialogView.findViewById(R.id.btn_delete);
                TextView title = dialogView.findViewById(R.id.title);
                ImageView image = dialogView.findViewById(R.id.image);
                image.setImageResource(R.drawable.delete);
                title.setText("Are you sure that you want to delete this Attendance?");
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
                        dialog.dismiss();
                        if (Function.checkNetworkConnection(context))
                        {
                            progressBarHelper.showProgressDialog();
                            Call<CommonModel> call = apiCalling.RemoveAttendance(attendanceDetails.get(position).getAttendanceID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                            call.enqueue(new Callback<CommonModel>() {
                                @Override
                                public void onResponse(Call<CommonModel> call, Response<CommonModel> response) {
                                    if (response.isSuccessful()) {
                                        CommonModel model = response.body();
                                        if (model.isCompleted()) {
                                            if (model.isData()) {
                                                Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                                attendanceDetails.remove(position);
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
                                public void onFailure(Call<CommonModel> call, Throwable t) {
                                    progressBarHelper.hideProgressDialog();
                                }
                            });
                        }else {
                            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendanceDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AttendanceEntryMasterDeatilListBinding binding;

        public ViewHolder(@NonNull AttendanceEntryMasterDeatilListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.PageData.class);
        }
    }
}
