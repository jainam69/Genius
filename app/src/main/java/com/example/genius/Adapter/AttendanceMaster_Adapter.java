package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.Model.AttendanceModel;
import com.example.genius.Model.StudentModel;
import com.example.genius.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("SetTextI18n")
public class AttendanceMaster_Adapter extends RecyclerView.Adapter<AttendanceMaster_Adapter.ViewHolder> {

    Context context;
    List<StudentModel> studentModels;
    public static List<AttendanceModel.AttendanceDetailEntity> testlist = new ArrayList<>();

    public AttendanceMaster_Adapter(Context context, List<StudentModel> studentModels) {
        this.context = context;
        this.studentModels = studentModels;
    }

    @NotNull
    @Override
    public AttendanceMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_master_deatil_list, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull AttendanceMaster_Adapter.ViewHolder holder, int position) {
        holder.gr_no.setText(""+studentModels.get(position).getGrNo());
        holder.stu_name.setText(studentModels.get(position).getFirstName() + " " + studentModels.get(position).getLastName());
        holder.std_id.setText(""+studentModels.get(position).getStudentID());
        AttendanceModel.AttendanceDetailEntity att = new AttendanceModel.AttendanceDetailEntity();
        StudentModel mod = new StudentModel();
        mod.setStudentID(studentModels.get(position).getStudentID());
        att.setStudent(mod);
        testlist.add(att);
        testlist.get(position).setRemarks("");
        testlist.get(position).setPresent(true);
        testlist.get(position).setAbsent(false);
//        AttendanceModel.AttendanceDetailEntity att = new AttendanceModel.AttendanceDetailEntity();
//        att.setPresent(false);
//        att.setAbsent(true);
//        att.setRemarks(" ");
//        StudentModel mod = new StudentModel();
//        mod.setStudentID(studentModels.get(position).getStudentID());
//        att.setStudent(mod);
//        testlist.add(att);
        holder.present_absent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (holder.present_absent.isChecked())
                {
                    testlist.get(position).setAbsent(true);
                    testlist.get(position).setPresent(false);
                }else {
                    testlist.get(position).setAbsent(false);
                    testlist.get(position).setPresent(true);
                }
            }
        });
        holder.remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    String rm = holder.remarks.getText().toString();
                    testlist.get(position).setRemarks(rm);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView gr_no,stu_name,remarks,std_id;
        CheckBox present_absent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gr_no = itemView.findViewById(R.id.gr_no);
            stu_name = itemView.findViewById(R.id.stu_name);
            present_absent = itemView.findViewById(R.id.present_absent);
            remarks = itemView.findViewById(R.id.remarks);
            std_id = itemView.findViewById(R.id.stu_id);
        }
    }
}
