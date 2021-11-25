package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.BranchSubjectModel;
import com.example.genius.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BranchSubjectSublistAapter extends RecyclerView.Adapter<BranchSubjectSublistAapter.ViewHolder> {

    Context context;
    public List<BranchSubjectModel.BranchSubjectData> CourceDataList;

    public BranchSubjectSublistAapter(Context context, List<BranchSubjectModel.BranchSubjectData> courceDataList) {
        this.context = context;
        CourceDataList = courceDataList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_branch_subject_sublist_line, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (CourceDataList.get(position).isSubject) {
            holder.linear_class_sublist.setVisibility(View.VISIBLE);
            holder.txt_subject_name.setText(CourceDataList.get(position).Subject.getSubjectName());
            holder.txt_selected.setText("YES");
        } else {
            holder.linear_class_sublist.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = holder.linear_class_sublist.getLayoutParams();
            params.height = 0;
            params.width = 0;
            holder.linear_class_sublist.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return CourceDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_subject_name, txt_selected;
        LinearLayout linear_class_sublist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_subject_name = itemView.findViewById(R.id.txt_subject_name);
            txt_selected = itemView.findViewById(R.id.txt_selected);
            linear_class_sublist = itemView.findViewById(R.id.linear_class_sublist);
        }
    }

}