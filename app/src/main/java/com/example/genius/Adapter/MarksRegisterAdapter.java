package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.MarksModel;
import com.example.genius.Model.StudentModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.databinding.StudentAchievemarksMasterDeatilListBinding;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarksRegisterAdapter extends RecyclerView.Adapter<MarksRegisterAdapter.ViewHolder> {

    Context context;
    List<MarksModel> marksModels;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    long ID,StudentID;
    String marks;
    UserModel.PageData userpermission;

    public MarksRegisterAdapter(Context context, List<MarksModel> studentModels) {
        this.context = context;
        this.marksModels = studentModels;
    }

    @NonNull
    @Override
    public MarksRegisterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(StudentAchievemarksMasterDeatilListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MarksRegisterAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        for (UserModel.PageInfoEntity model : userpermission.Data){
            if (model.getPageID() == 81 && !model.Createstatus){
                holder.binding.edit.setVisibility(View.GONE);
            }
        }
        holder.binding.studentName.setText(marksModels.get(position).getStudent().getName());
        holder.binding.subjectName.setText(marksModels.get(position).getBranchSubject().getSubject().getSubjectName());
        holder.binding.totalMarks.setText(""+marksModels.get(position).getTestEntityInfo().getMarks());
        holder.binding.achMarks.setText(marksModels.get(position).getAchieveMarks());
        holder.binding.edit.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to Edit AchieveMarks?");
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                        View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_test_marks_staff, null);
                        builder.setView(dialogView);
                        builder.setCancelable(true);
                        EditText achieve_marks = dialogView.findViewById(R.id.achieve_marks);
                        Button edit = dialogView.findViewById(R.id.test_marks_edit);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        achieve_marks.setText(marksModels.get(position).getAchieveMarks());
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progressBarHelper.showProgressDialog();
                                ID = marksModels.get(position).getMarksID();
                                StudentID = marksModels.get(position).getStudent().getStudentID();
                                marks = achieve_marks.getText().toString();
                                Call<MarksModel.MarksData1> call = apiCalling.Update_Achieve_Marks(ID,StudentID,marks, Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID),
                                        Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),0);
                                call.enqueue(new Callback<MarksModel.MarksData1>() {
                                    @Override
                                    public void onResponse(Call<MarksModel.MarksData1> call, Response<MarksModel.MarksData1> response) {
                                        if (response.isSuccessful()){
                                            MarksModel.MarksData1 data = response.body();
                                            if (data.isCompleted()){
                                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                                holder.binding.achMarks.setText(achieve_marks.getText().toString());
                                                marksModels.get(position).setAchieveMarks(achieve_marks.getText().toString());
                                                dialog.dismiss();
                                            }else {
                                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                            progressBarHelper.hideProgressDialog();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MarksModel.MarksData1> call, Throwable t) {
                                        progressBarHelper.hideProgressDialog();
                                        Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return marksModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        StudentAchievemarksMasterDeatilListBinding binding;

        public ViewHolder(@NonNull StudentAchievemarksMasterDeatilListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.PageData.class);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }
}
