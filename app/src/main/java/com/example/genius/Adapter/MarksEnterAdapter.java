package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.Model.MarksModel;
import com.example.genius.Model.StudentModel;
import com.example.genius.R;
import com.example.genius.databinding.AchievemarksMasterDeatilListBinding;

import java.util.List;

public class MarksEnterAdapter extends RecyclerView.Adapter<MarksEnterAdapter.ViewHolder> {

    Context context;
    public static List<StudentModel> studentModels;

    public MarksEnterAdapter(Context context, List<StudentModel> studentModels) {
        this.context = context;
        this.studentModels = studentModels;
    }

    @NonNull
    @Override
    public MarksEnterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AchievemarksMasterDeatilListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MarksEnterAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.studentName.setText(studentModels.get(position).getName());
        long stdid = studentModels.get(position).getStudentID();
        studentModels.get(position).setAchieveMarks("");
        studentModels.get(position).setStudentID(stdid);
        holder.binding.achMarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    String mks = holder.binding.achMarks.getText().toString();
                    studentModels.get(position).setAchieveMarks(mks);
                }else {
                    studentModels.get(position).setAchieveMarks("");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AchievemarksMasterDeatilListBinding binding;

        public ViewHolder(@NonNull AchievemarksMasterDeatilListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
