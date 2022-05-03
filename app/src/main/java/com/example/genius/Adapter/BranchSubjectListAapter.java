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
import com.example.genius.databinding.RowBranchSubjectListLineBinding;
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
    UserModel.PageData userpermission;

    public BranchSubjectListAapter(Context context, List<BranchSubjectModel.BranchSubjectData> courceDataList) {
        this.context = context;
        CourceDataList = courceDataList;
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowBranchSubjectListLineBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        for (UserModel.PageInfoEntity model : userpermission.Data)
        {
            if (model.getPageID() == 76){
                if (!model.Createstatus){
                    holder.binding.imgEdit.setVisibility(View.GONE);
                }
                if (!model.Deletestatus){
                    holder.binding.imgDelete.setVisibility(View.GONE);
                }
                if (!model.Createstatus && !model.Deletestatus){
                    holder.binding.linearActions.setVisibility(View.GONE);
                }
            }
        }
        holder.binding.txtBranchName.setText(CourceDataList.get(position).branch.getBranchName());
        holder.binding.txtCourseName.setText(CourceDataList.get(position).BranchCourse.getCourse().getCourseName());
        holder.binding.txtClassName.setText(CourceDataList.get(position).getClassModel().getClassName());
        holder.binding.subjectSublistRv.setLayoutManager(new LinearLayoutManager(context));
        holder.binding.subjectSublistRv.setAdapter(new BranchSubjectSublistAapter(context, CourceDataList.get(position).BranchSubjectData));
        holder.binding.imgEdit.setOnClickListener((View.OnClickListener) v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
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

        holder.binding.imgDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
            TextView btn_delete = dialogView.findViewById(R.id.btn_delete);
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

        RowBranchSubjectListLineBinding binding;

        public ViewHolder(@NonNull RowBranchSubjectListLineBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.PageData.class);
        }
    }

}
