package com.example.genius.Adapter;

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
        return new MarksEnterAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.achievemarks_master_deatil_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MarksEnterAdapter.ViewHolder holder, int position) {
        holder.student_name.setText(studentModels.get(position).getName());
        long stdid = studentModels.get(position).getStudentID();
        studentModels.get(position).setAchieveMarks("");
        studentModels.get(position).setStudentID(stdid);
        holder.ach_marks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    String mks = holder.ach_marks.getText().toString();
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

        TextView student_name;
        EditText ach_marks;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            student_name = itemView.findViewById(R.id.student_name);
            ach_marks = itemView.findViewById(R.id.ach_marks);
        }
    }
}
