package com.example.genius.Adapter;

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
    UserModel userpermission;

    public MarksRegisterAdapter(Context context, List<MarksModel> studentModels) {
        this.context = context;
        this.marksModels = studentModels;
    }

    @NonNull
    @Override
    public MarksRegisterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MarksRegisterAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.student_achievemarks_master_deatil_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MarksRegisterAdapter.ViewHolder holder, int position) {
        if (userpermission.getPermission().get(19).getPageInfo().getPageID() == 81 && !userpermission.getPermission().get(19).getPackageRightinfo().isCreatestatus()){
            holder.edit.setVisibility(View.GONE);
        }
        holder.student_name.setText(marksModels.get(position).getStudent().getName());
        holder.subject_name.setText(marksModels.get(position).getSubjectInfo().getSubject());
        holder.total_marks.setText(""+marksModels.get(position).getTestEntityInfo().getMarks());
        holder.ach_marks.setText(marksModels.get(position).getAchieveMarks());
        holder.edit.setOnClickListener(new View.OnClickListener() {
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
                                Call<MarksModel.MarksData> call = apiCalling.Update_Achieve_Marks(ID,StudentID,marks, Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID),
                                        Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),0);
                                call.enqueue(new Callback<MarksModel.MarksData>() {
                                    @Override
                                    public void onResponse(Call<MarksModel.MarksData> call, Response<MarksModel.MarksData> response) {
                                        if (response.isSuccessful()){
                                            MarksModel.MarksData data = response.body();
                                            if (data.isCompleted()){
                                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                                holder.ach_marks.setText(achieve_marks.getText().toString());
                                                marksModels.get(position).setAchieveMarks(achieve_marks.getText().toString());
                                                dialog.dismiss();
                                            }else {
                                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                            progressBarHelper.hideProgressDialog();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MarksModel.MarksData> call, Throwable t) {
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

        TextView student_name,subject_name,total_marks,ach_marks;
        ImageView edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            student_name = itemView.findViewById(R.id.student_name);
            subject_name = itemView.findViewById(R.id.subject_name);
            total_marks = itemView.findViewById(R.id.total_marks);
            ach_marks = itemView.findViewById(R.id.ach_marks);
            edit = itemView.findViewById(R.id.edit);
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }
}
