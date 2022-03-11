package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchSubjectModel;
import com.example.genius.Model.ClassModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.SuperAdminSubjectModel;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchSubjectAapter extends RecyclerView.Adapter<BranchSubjectAapter.ViewHolder> {

    Context context;
    public static List<BranchSubjectModel.BranchSubjectData> CourceDataList;
    boolean isSelectedAll;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;

    public BranchSubjectAapter(Context context, List<BranchSubjectModel.BranchSubjectData> courceDataList) {
        this.context = context;
        CourceDataList = courceDataList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_branch_subject_line, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.subject_name.setText(CourceDataList.get(position).getSubject().getSubjectName());
        SuperAdminSubjectModel.SuperAdminSubjectData da= new SuperAdminSubjectModel.SuperAdminSubjectData();
        da.setSubjectID(CourceDataList.get(position).getSubject().getSubjectID());
        da.setSubjectName(CourceDataList.get(position).getSubject().getSubjectName());
        CourceDataList.get(position).setSubject(da);
        holder.chk_line.setChecked(CourceDataList.get(position).isSubject);
        holder.chk_line.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CourceDataList.get(position).setSubject(holder.chk_line.isChecked());
                long subject_id = CourceDataList.get(position).getSubject_dtl_id();
                if (!holder.chk_line.isChecked())
                {
                    progressBarHelper.showProgressDialog();
                    Call<CommonModel.ResponseModel> call = apiCalling.Check_Subject_Detail(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),subject_id);
                    call.enqueue(new Callback<CommonModel.ResponseModel>() {
                        @Override
                        public void onResponse(Call<CommonModel.ResponseModel> call, Response<CommonModel.ResponseModel> response) {
                            if (response.isSuccessful()){
                                CommonModel.ResponseModel data = response.body();
                                if (data.isCompleted()){
                                    CommonModel model = data.getData();
                                    if (!model.isStatus()){
                                        holder.chk_line.setChecked(true);
                                        Toast.makeText(context,model.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(Call<CommonModel.ResponseModel> call, Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return CourceDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView subject_name;
        CheckBox chk_line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            subject_name = itemView.findViewById(R.id.subject_name);
            chk_line = itemView.findViewById(R.id.chk_line);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selectAll() {
        CourceDataList.forEach((u) -> u.setSubject(true));
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void unselectall() {
        CourceDataList.forEach((u) -> u.setSubject(false));
        notifyDataSetChanged();
    }

}
