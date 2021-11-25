package com.example.genius.Adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.Model.BranchSubjectModel;
import com.example.genius.Model.ClassModel;
import com.example.genius.Model.SuperAdminSubjectModel;
import com.example.genius.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BranchSubjectAapter extends RecyclerView.Adapter<BranchSubjectAapter.ViewHolder> {

    Context context;
    public static List<SuperAdminSubjectModel.SuperAdminSubjectData> CourceDataList;
    boolean isSelectedAll;

    public BranchSubjectAapter(Context context, List<SuperAdminSubjectModel.SuperAdminSubjectData> courceDataList) {
        this.context = context;
        CourceDataList = courceDataList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_branch_subject_line, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.subject_name.setText(CourceDataList.get(position).getSubjectName());
        holder.chk_line.setChecked(CourceDataList.get(position).isSubject);
        holder.chk_line.setOnCheckedChangeListener((buttonView, isChecked) -> CourceDataList.get(position).setSubject(holder.chk_line.isChecked()));
    }

    @Override
    public int getItemCount() {
        return CourceDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView subject_name;
        CheckBox chk_line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            subject_name = itemView.findViewById(R.id.subject_name);
            chk_line = itemView.findViewById(R.id.chk_line);
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
