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
    UserModel userpermission;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;

    public AttendanceEntry_Adapter(Context context, List<AttendanceModel> attendanceDetails) {
        this.context = context;
        this.attendanceDetails = attendanceDetails;
    }

    @NotNull
    @Override
    public AttendanceEntry_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AttendanceEntry_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_entry_master_deatil_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceEntry_Adapter.ViewHolder holder, int position) {
        for (UserModel.UserPermission model : userpermission.getPermission()){
            if (model.getPageInfo().getPageID() == 18){
                if (!model.getPackageRightinfo().isCreatestatus()){
                    holder.atten_edit.setVisibility(View.GONE);
                }
                if (!model.getPackageRightinfo().isDeletestatus()){
                    holder.atten_delete.setVisibility(View.GONE);
                }
                if (!model.getPackageRightinfo().isCreatestatus() && !model.getPackageRightinfo().isDeletestatus()){
                    holder.linear_create_delete.setVisibility(View.GONE);
                }
            }
        }
        String date = attendanceDetails.get(position).getAttendanceDate().replace("T00:00:00","");
        try {
            Date d = actualdate.parse(date);
            holder.att_date.setText(displaydate.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.batch_time.setText(attendanceDetails.get(position).getBatchTypeText());
        holder.std.setText(attendanceDetails.get(position).getStandard().getStandard());

        holder.atten_edit.setOnClickListener(new View.OnClickListener() {
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
                        bundle.putLong("StandardID",attendanceDetails.get(position).getStandard().getStandardID());
                        bundle.putString("Date",attendanceDetails.get(position).getAttendanceDate());
                        bundle.putInt("BatchId",attendanceDetails.get(position).getBatchTypeID());
                        bundle.putString("BatchName",attendanceDetails.get(position).getBatchTypeText());
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
        holder.atten_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                Button btn_delete = dialogView.findViewById(R.id.btn_delete);
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
                                                Toast.makeText(context, "Attendance deleted successfully.", Toast.LENGTH_SHORT).show();
                                                attendanceDetails.remove(position);
                                                notifyItemRemoved(position);
                                                notifyDataSetChanged();
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

        TextView att_date,batch_time,std;
        ImageView atten_edit,atten_delete;
        LinearLayout linear_create_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            att_date = itemView.findViewById(R.id.att_date);
            batch_time = itemView.findViewById(R.id.batch_time);
            std = itemView.findViewById(R.id.std);
            atten_edit = itemView.findViewById(R.id.atten_edit);
            atten_delete = itemView.findViewById(R.id.atten_delete);
            linear_create_delete = itemView.findViewById(R.id.linear_create_delete);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);
        }
    }
}
