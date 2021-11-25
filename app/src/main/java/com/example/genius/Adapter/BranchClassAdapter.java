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

import com.example.genius.Model.ClassModel;
import com.example.genius.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BranchClassAdapter  extends RecyclerView.Adapter<BranchClassAdapter.ViewHolder> {

    Context context;
    public static List<ClassModel.ClassData> CourceDataList;
    boolean isSelectedAll;

    public BranchClassAdapter(Context context, List<ClassModel.ClassData> courceDataList) {
        this.context = context;
        CourceDataList = courceDataList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_branch_class_line, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.class_name.setText(CourceDataList.get(position).getClassName());
        holder.chk_line.setChecked(CourceDataList.get(position).getIscourse());
        holder.chk_line.setOnCheckedChangeListener((buttonView, isChecked) -> CourceDataList.get(position).setIscourse(holder.chk_line.isChecked()));
    }

    @Override
    public int getItemCount() {
        return CourceDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView class_name;
        CheckBox chk_line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            class_name = itemView.findViewById(R.id.class_name);
            chk_line = itemView.findViewById(R.id.chk_line);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selectAll() {
        CourceDataList.forEach((u) -> u.setIscourse(true));
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void unselectall() {
        CourceDataList.forEach((u) -> u.setIscourse(false));
        notifyDataSetChanged();
    }

}
