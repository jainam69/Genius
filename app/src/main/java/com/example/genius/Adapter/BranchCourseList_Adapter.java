package com.example.genius.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.BranchCource.BranchCourseFragment;
import com.example.genius.ui.Staff_Entry_Fragment.staff_entry_fragment;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchCourseList_Adapter extends RecyclerView.Adapter<BranchCourseList_Adapter.ViewHolder> {

    Context context;
    List<BranchCourseModel.BranchCourceData> branchCourceData;
    BranchCourseSubList_Adapter branchCourseSubList_adapter;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;

    public BranchCourseList_Adapter(Context context, List<BranchCourseModel.BranchCourceData> branchCourceData) {
        this.context = context;
        this.branchCourceData = branchCourceData;
    }

    @NonNull
    @Override
    public BranchCourseList_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BranchCourseList_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_branch_course_list_line, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BranchCourseList_Adapter.ViewHolder holder, int position) {
        holder.branch_name.setText("Branch Name :  "+ Preferences.getInstance(context).getString(Preferences.KEY_BRANCH_NAME));
        LinearLayoutManager lln = new LinearLayoutManager(context);
        holder.course_sublist_rv.setLayoutManager(lln);
        branchCourseSubList_adapter = new BranchCourseSubList_Adapter(context,branchCourceData);
        holder.course_sublist_rv.setAdapter(branchCourseSubList_adapter);
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
                title.setText("Are you sure that you want to Edit Course?");
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
                        BranchCourseFragment orderplace = new BranchCourseFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("COURSE_DTL", (Serializable) branchCourceData);
                        //bundle.putSerializable("COURSE_DTL", new Gson().toJson(branchCourceData));
                        //bundle.putParcelable("COURSE_DTL_PARSE", (Parcelable) branchCourceData.get(position));
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
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to delete this Course?");
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
                        Call<CommonModel> call = apiCalling.RemoveBranchCourse(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        call.enqueue(new Callback<CommonModel>() {
                            @Override
                            public void onResponse(Call<CommonModel> call, Response<CommonModel> response) {
                                if (response.isSuccessful()) {
                                    CommonModel model = response.body();
                                    if (model.isCompleted()) {
                                        if (model.isData()) {
                                            Toast.makeText(context, "Course deleted successfully.", Toast.LENGTH_SHORT).show();
                                            branchCourceData.remove(position);
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
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView branch_name;
        ImageView img_edit,img_delete;
        RecyclerView course_sublist_rv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            branch_name = itemView.findViewById(R.id.branch_name);
            course_sublist_rv = itemView.findViewById(R.id.course_sublist_rv);
            img_edit = itemView.findViewById(R.id.img_edit);
            img_delete = itemView.findViewById(R.id.img_delete);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }
}
