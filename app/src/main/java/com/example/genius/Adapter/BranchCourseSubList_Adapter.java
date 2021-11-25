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

import com.example.genius.Model.BranchCourseModel;
import com.example.genius.R;

import java.util.List;

public class BranchCourseSubList_Adapter extends RecyclerView.Adapter<BranchCourseSubList_Adapter.ViewHolder> {

    Context context;
    List<BranchCourseModel.BranchCourceData> branchCourceData;

    public BranchCourseSubList_Adapter(Context context, List<BranchCourseModel.BranchCourceData> branchCourceData) {
        this.context = context;
        this.branchCourceData = branchCourceData;
    }

    @NonNull
    @Override
    public BranchCourseSubList_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_branch_course_sublist_line, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BranchCourseSubList_Adapter.ViewHolder holder, int position) {
        if (branchCourceData.get(position).getIscourse()) {
            holder.txt_course_name.setText(branchCourceData.get(position).getCourse().getCourseName());
            holder.txt_selected.setText("YES");
        } else {
            holder.linear_course_list.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = holder.linear_course_list.getLayoutParams();
            params.height = 0;
            params.width = 0;
            holder.linear_course_list.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return branchCourceData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_course_name, txt_selected;
        LinearLayout linear_course_list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_course_name = itemView.findViewById(R.id.txt_course_name);
            txt_selected = itemView.findViewById(R.id.txt_selected);
            linear_course_list = itemView.findViewById(R.id.linear_course_list);

        }
    }
}
