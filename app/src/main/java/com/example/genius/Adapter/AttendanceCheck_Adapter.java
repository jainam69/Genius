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

import java.util.ArrayList;
import java.util.List;

public class AttendanceCheck_Adapter extends RecyclerView.Adapter<AttendanceCheck_Adapter.ViewHolder> {

    Context context;
    List<AttendanceModel.AttendanceDetailEntity> attendanceDetailEntities;
    public static List<AttendanceModel.AttendanceDetailEntity> testlist_edit = new ArrayList<>();

    public AttendanceCheck_Adapter(Context context, List<AttendanceModel.AttendanceDetailEntity> attendanceDetailEntities) {
        this.context = context;
        this.attendanceDetailEntities = attendanceDetailEntities;
    }

    @NonNull
    @Override
    public AttendanceCheck_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AttendanceCheck_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_master_deatil_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceCheck_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.gr_no.setText(""+attendanceDetailEntities.get(position).getStudent().getGrNo());
        holder.stu_name.setText(attendanceDetailEntities.get(position).getStudent().getFirstName() + " " + attendanceDetailEntities.get(position).getStudent().getLastName());
        holder.present_absent.setChecked(attendanceDetailEntities.get(position).isAbsent());
        holder.present_absent.setTag(position);
        holder.remarks.setText(attendanceDetailEntities.get(position).getRemarks());

        AttendanceModel.AttendanceDetailEntity att = new AttendanceModel.AttendanceDetailEntity();
        StudentModel mod = new StudentModel();
        mod.setStudentID(attendanceDetailEntities.get(position).getStudent().getStudentID());
        att.setStudent(mod);
        testlist_edit.add(att);
        testlist_edit.get(position).setDetailID(attendanceDetailEntities.get(position).getDetailID());
        testlist_edit.get(position).setHeaderID(attendanceDetailEntities.get(position).getHeaderID());
        testlist_edit.get(position).setRemarks(attendanceDetailEntities.get(position).getRemarks());
        testlist_edit.get(position).setAbsent(attendanceDetailEntities.get(position).isAbsent());
        testlist_edit.get(position).setPresent(attendanceDetailEntities.get(position).isPresent());
        /*att.setDetailID(attendanceDetailEntities.get(position).getDetailID());
        att.setHeaderID(attendanceDetailEntities.get(position).getHeaderID());
        att.setRemarks(attendanceDetailEntities.get(position).getRemarks());
        att.setAbsent(attendanceDetailEntities.get(position).isAbsent());
        att.setPresent(attendanceDetailEntities.get(position).isPresent());
        testlist_edit.add(att);*/
        holder.present_absent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Integer pos = (Integer) holder.present_absent.getTag();
                if (holder.present_absent.isChecked())
                {
                    attendanceDetailEntities.get(pos).setAbsent(true);
                    testlist_edit.get(pos).setAbsent(true);
                    attendanceDetailEntities.get(pos).setPresent(false);
                    testlist_edit.get(pos).setPresent(false);
                }else {
                    attendanceDetailEntities.get(pos).setPresent(true);
                    testlist_edit.get(pos).setPresent(true);
                    attendanceDetailEntities.get(pos).setAbsent(false);
                    testlist_edit.get(pos).setAbsent(false);
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
                    attendanceDetailEntities.get(position).setRemarks(rm);
                    testlist_edit.get(position).setRemarks(rm);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendanceDetailEntities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView gr_no,stu_name,remarks,std_id,header_id,detail_id;
        CheckBox present_absent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gr_no = itemView.findViewById(R.id.gr_no);
            stu_name = itemView.findViewById(R.id.stu_name);
            present_absent = itemView.findViewById(R.id.present_absent);
            remarks = itemView.findViewById(R.id.remarks);
            std_id = itemView.findViewById(R.id.stu_id);
            header_id = itemView.findViewById(R.id.header_id);
            detail_id = itemView.findViewById(R.id.detail_id);
        }
    }
}
