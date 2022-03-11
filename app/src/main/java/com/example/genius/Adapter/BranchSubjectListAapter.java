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
import com.example.genius.Model.BranchSubjectModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.BranchClass.BranchClassFragment;
import com.example.genius.ui.BranchSubject.BranchSubjectFragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchSubjectListAapter extends RecyclerView.Adapter<BranchSubjectListAapter.ViewHolder> {

    Context context;
    public List<BranchSubjectModel.BranchSubjectData> CourceDataList;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    UserModel userpermission;

    public BranchSubjectListAapter(Context context, List<BranchSubjectModel.BranchSubjectData> courceDataList) {
        this.context = context;
        CourceDataList = courceDataList;
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_branch_subject_list_line, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        for (UserModel.UserPermission model : userpermission.getPermission())
        {
            if (model.getPageInfo().getPageID() == 76){
                if (!model.getPackageRightinfo().isCreatestatus()){
                    holder.img_edit.setVisibility(View.GONE);
                }
                if (!model.getPackageRightinfo().isDeletestatus()){
                    holder.img_delete.setVisibility(View.GONE);
                }
                if (!model.getPackageRightinfo().isCreatestatus() && !model.getPackageRightinfo().isDeletestatus()){
                    holder.linear_actions.setVisibility(View.GONE);
                }
            }
        }
        holder.txt_branch_name.setText(CourceDataList.get(position).branch.getBranchName());
        holder.txt_course_name.setText(CourceDataList.get(position).BranchCourse.getCourse().getCourseName());
        holder.txt_class_name.setText(CourceDataList.get(position).getClassModel().getClassName());
        holder.subject_sublist_rv.setLayoutManager(new LinearLayoutManager(context));
        holder.subject_sublist_rv.setAdapter(new BranchSubjectSublistAapter(context, CourceDataList.get(position).BranchSubjectData));
        holder.img_edit.setOnClickListener((View.OnClickListener) v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to Edit Subject?");
            image.setImageResource(R.drawable.ic_edit);
            AlertDialog dialog = builder.create();

            btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

            btn_edit_yes.setOnClickListener((View.OnClickListener) v12 -> {
                dialog.dismiss();
                BranchSubjectFragment orderplace = new BranchSubjectFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("COURSE_DTL", (Serializable) CourceDataList.get(position).BranchSubjectData);
                bundle.putSerializable("COURSE_NAME", CourceDataList.get(position).BranchCourse.getCourse().getCourseName());
                bundle.putSerializable("CLASS_NAME", CourceDataList.get(position).getClassModel().getClassName());
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
            title.setText("Are you sure that you want to delete this Subject?");
            AlertDialog dialog = builder.create();

            btn_cancel.setOnClickListener(v13 -> dialog.dismiss());

            btn_delete.setOnClickListener(v14 -> {
                progressBarHelper.showProgressDialog();
                Call<CommonModel.ResponseModel> call = apiCalling.RemoveSubjectDetail(CourceDataList.get(position).getBranchCourse().getCourse_dtl_id(),
                        CourceDataList.get(position).getBranchClass().getClass_dtl_id()
                        , Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)
                        , Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID));
                call.enqueue(new Callback<CommonModel.ResponseModel>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonModel.ResponseModel> call, @NotNull Response<CommonModel.ResponseModel> response) {
                        if (response.isSuccessful()) {
                            CommonModel.ResponseModel data = response.body();
                            if (data.isCompleted()) {
                                CommonModel model = data.getData();
                                if (model.isStatus()){
                                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                    CourceDataList.remove(position);
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
                    public void onFailure(@NotNull Call<CommonModel.ResponseModel> call, @NotNull Throwable t) {
                        progressBarHelper.hideProgressDialog();
                        Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
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

        TextView txt_branch_name, txt_course_name, txt_class_name;
        RecyclerView subject_sublist_rv;
        ImageView img_edit, img_delete;
        LinearLayout linear_actions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_branch_name = itemView.findViewById(R.id.txt_branch_name);
            txt_course_name = itemView.findViewById(R.id.txt_course_name);
            subject_sublist_rv = itemView.findViewById(R.id.subject_sublist_rv);
            txt_class_name = itemView.findViewById(R.id.txt_class_name);
            img_edit = itemView.findViewById(R.id.img_edit);
            img_delete = itemView.findViewById(R.id.img_delete);
            linear_actions = itemView.findViewById(R.id.linear_actions);
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);
        }
    }

}
