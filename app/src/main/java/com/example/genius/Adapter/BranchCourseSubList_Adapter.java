package com.example.genius.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        return new BranchCourseSubList_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_branch_course_sublist_line, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BranchCourseSubList_Adapter.ViewHolder holder, int position) {
        holder.txt_course_name.setText(branchCourceData.get(position).getCourse().getCourseName());
        boolean select = branchCourceData.get(position).getIscourse();
        if (select){
            holder.txt_selected.setText("YES");
        }
    }

    @Override
    public int getItemCount() {
        return branchCourceData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_course_name,txt_selected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_course_name = itemView.findViewById(R.id.txt_course_name);
            txt_selected = itemView.findViewById(R.id.txt_selected);
        }
    }
}
