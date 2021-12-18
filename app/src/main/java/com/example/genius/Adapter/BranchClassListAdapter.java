package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.ClassModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.BranchClass.BranchClassFragment;
import com.example.genius.ui.BranchCource.BranchCourseFragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchClassListAdapter extends RecyclerView.Adapter<BranchClassListAdapter.ViewHolder> {

    Context context;
    public List<BranchClassSingleModel.BranchClassData> CourceDataList;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    UserModel userpermission;

    public BranchClassListAdapter(Context context, List<BranchClassSingleModel.BranchClassData> courceDataList) {
        this.context = context;
        CourceDataList = courceDataList;
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_branch_class_list_line, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (userpermission.getPermission().get(8).getPageInfo().getPageID() == 74){
            if (!userpermission.getPermission().get(8).getPackageRightinfo().isCreatestatus()){
                holder.img_edit.setVisibility(View.GONE);
            }
            if (!userpermission.getPermission().get(8).getPackageRightinfo().isDeletestatus()){
                holder.img_delete.setVisibility(View.GONE);
            }
            if (!userpermission.getPermission().get(8).getPackageRightinfo().isCreatestatus() && !userpermission.getPermission().get(8).getPackageRightinfo().isDeletestatus()){
                holder.linear_actions.setVisibility(View.GONE);
            }
        }
        holder.txt_branch_name.setText(CourceDataList.get(position).branch.getBranchName());
        holder.txt_course_name.setText(CourceDataList.get(position).BranchCourse.getCourse().getCourseName());
        BranchClassSublistAdapter branchClassSublistAdapter = new BranchClassSublistAdapter(context, CourceDataList.get(position).BranchClassData);
        holder.class_sublist_rv.setLayoutManager(new LinearLayoutManager(context));
        holder.class_sublist_rv.setAdapter(branchClassSublistAdapter);
        holder.img_edit.setOnClickListener((View.OnClickListener) v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to Edit Branch Class?");
            image.setImageResource(R.drawable.ic_edit);
            AlertDialog dialog = builder.create();

            btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

            btn_edit_yes.setOnClickListener((View.OnClickListener) v12 -> {
                dialog.dismiss();
                BranchClassFragment orderplace = new BranchClassFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("COURSE_DTL", (Serializable) CourceDataList.get(position).BranchClassData);
                bundle.putSerializable("COURSE_NAME", CourceDataList.get(position).BranchCourse.getCourse().getCourseName());
                //bundle.putParcelable("COURSE_DTL_PARSE", (Parcelable) branchCourceData.get(position));
                orderplace.setArguments(bundle);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            });
            dialog.show();
        });
        holder.img_delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
            Button btn_delete = dialogView.findViewById(R.id.btn_delete);
            TextView title = dialogView.findViewById(R.id.title);
            ImageView image = dialogView.findViewById(R.id.image);
            image.setImageResource(R.drawable.delete);
            title.setText("Are you sure that you want to delete this Class?");
            AlertDialog dialog = builder.create();

            btn_cancel.setOnClickListener(v13 -> dialog.dismiss());

            btn_delete.setOnClickListener(v14 -> {
                progressBarHelper.showProgressDialog();
                Call<CommonModel> call = apiCalling.RemoveClassDetail(CourceDataList.get(position).BranchCourse.getCourse_dtl_id()
                        , Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)
                        , Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID));
                call.enqueue(new Callback<CommonModel>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                        if (response.isSuccessful()) {
                            CommonModel model = response.body();
                            if (model != null && model.isCompleted()) {
                                if (model.isData()) {
                                    Toast.makeText(context, "Class deleted successfully.", Toast.LENGTH_SHORT).show();
                                    CourceDataList.remove(position);
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
            });
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return CourceDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_branch_name, txt_course_name;
        RecyclerView class_sublist_rv;
        ImageView img_edit, img_delete;
        LinearLayout linear_actions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_branch_name = itemView.findViewById(R.id.txt_branch_name);
            txt_course_name = itemView.findViewById(R.id.txt_course_name);
            class_sublist_rv = itemView.findViewById(R.id.class_sublist_rv);
            img_edit = itemView.findViewById(R.id.img_edit);
            img_delete = itemView.findViewById(R.id.img_delete);
            linear_actions = itemView.findViewById(R.id.linear_actions);
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);
        }
    }

}
