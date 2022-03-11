package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
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
import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.ClassModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchClassAdapter  extends RecyclerView.Adapter<BranchClassAdapter.ViewHolder> {

    Context context;
    public static List<BranchClassSingleModel.BranchClassData> CourceDataList;
    boolean isSelectedAll;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;

    public BranchClassAdapter(Context context, List<BranchClassSingleModel.BranchClassData> courceDataList) {
        this.context = context;
        CourceDataList = courceDataList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BranchClassAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_branch_class_line, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BranchClassSingleModel.BranchClassData data = CourceDataList.get(position);
        holder.class_name.setText(data.getClassModel().getClassName());
        ClassModel.ClassData da= new ClassModel.ClassData();
        da.setClassID(data.getClassModel().getClassID());
        da.setClassName(data.getClassModel().getClassName());
        data.setClassModel(da);
        holder.chk_line.setTag(position);
        holder.chk_line.setChecked(data.isClass);
        holder.chk_line.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setClass(holder.chk_line.isChecked());
                long class_id = data.getClass_dtl_id();
                if (!holder.chk_line.isChecked())
                {
                    progressBarHelper.showProgressDialog();
                    Call<CommonModel.ResponseModel> call = apiCalling.Check_Class_Detail(class_id, Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
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

        TextView class_name;
        CheckBox chk_line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            class_name = itemView.findViewById(R.id.class_name);
            chk_line = itemView.findViewById(R.id.chk_line);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selectAll() {
        CourceDataList.forEach((u) -> u.setClass(true));
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void unselectall() {
        CourceDataList.forEach((u) -> u.setClass(false));
        notifyDataSetChanged();
    }

}
