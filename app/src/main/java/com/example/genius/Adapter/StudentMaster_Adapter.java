package com.example.genius.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.StudentModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Student_Registration_Fragment.student_registration_fragment;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StudentMaster_Adapter extends RecyclerView.Adapter<StudentMaster_Adapter.ViewHolder> {

    Context context;
    List<StudentModel> studentDetails;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");
    UserModel userpermission;

    public StudentMaster_Adapter(Context context, List<StudentModel> studentDetails) {
        this.context = context;
        this.studentDetails = studentDetails;
    }

    @Override
    public StudentMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StudentMaster_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.student_master_deatil_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StudentMaster_Adapter.ViewHolder holder, int position) {
        for (UserModel.UserPermission model : userpermission.getPermission()){
            if (model.getPageInfo().getPageID() == 8 && !model.getPackageRightinfo().isCreatestatus()){
                holder.student_edit.setVisibility(View.GONE);
            }
        }
        holder.student_name.setText(studentDetails.get(position).getFirstName());
        if (studentDetails.get(position).getAdmissionDate() != null) {
            String a = studentDetails.get(position).getAdmissionDate().replace("T00:00:00", "");
            try {
                Date d = actualdate.parse(a);
                holder.admission_date.setText(displaydate.format(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        holder.standard.setText("" + studentDetails.get(position).getStandardInfo().getStandard());
        int a = studentDetails.get(position).getBatchInfo().getBatchTime();
        if (a == 1) {
            holder.batch_time.setText("Morning");
        } else if (a == 2) {
            holder.batch_time.setText("Afternoon");
        } else {
            holder.batch_time.setText("Evening");
        }
        holder.contact_no.setText(studentDetails.get(position).getContactNo());
        holder.student_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Edit Student?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btn_edit_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        student_registration_fragment orderplace = new student_registration_fragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("Address", studentDetails.get(position).getAddress());
                        if (studentDetails.get(position).getAdmissionDate() != null)
                            bundle.putString("AdmissionDate", studentDetails.get(position).getAdmissionDate().replace("T00:00:00", ""));
                        bundle.putString("BatchTime", studentDetails.get(position).getBatchInfo().getBatchType());
                        if (studentDetails.get(position).getDOB() != null)
                            bundle.putString("BirthDate", studentDetails.get(position).getDOB().replace("T00:00:00", ""));
                        bundle.putLong("Branch_ID", studentDetails.get(position).getBranchInfo().getBranchID());
                        bundle.putString("ClassName", studentDetails.get(position).getLastYearClassName());
                        bundle.putString("ContactNo", studentDetails.get(position).getContactNo());
                        bundle.putString("FatherOccupation", studentDetails.get(position).getStudentMaint().getFatherOccupation());
                        bundle.putString("FirstName", studentDetails.get(position).getFirstName());
                        bundle.putString("LastName", studentDetails.get(position).getLastName());
                        bundle.putString("MiddleName", studentDetails.get(position).getMiddleName());
                        bundle.putString("MotherOccupation", studentDetails.get(position).getStudentMaint().getMotherOccupation());
                        bundle.putString("ParentContactNo", studentDetails.get(position).getStudentMaint().getContactNo());
                        bundle.putString("ParentName", studentDetails.get(position).getStudentMaint().getParentName());
                        bundle.putLong("ParentID", studentDetails.get(position).getStudentMaint().getParentID());
                        bundle.putLong("TransactionID", studentDetails.get(position).getTransaction().getTransactionId());
                        bundle.putLong("SchoolName", studentDetails.get(position).getSchoolInfo().getSchoolID());
                        bundle.putInt("SchoolTime", studentDetails.get(position).getSchoolTime());
                        bundle.putLong("Standard", studentDetails.get(position).getStandardInfo().getStandardID());
                        bundle.putInt("Status", studentDetails.get(position).getRowStatus().getRowStatusId());
                        bundle.putLong("StudentID", studentDetails.get(position).getStudentID());
                        bundle.putString("Grade", studentDetails.get(position).getGrade());
                        bundle.putInt("LastYearResult", studentDetails.get(position).getLastYearResult());
                        bundle.putString("FileName", studentDetails.get(position).getFileName());
                        bundle.putString("FilePath", studentDetails.get(position).getFilePath());
                        bundle.putString("STUDENT_PASSWORD", studentDetails.get(position).getStudentPassword());
                        bundle.putString("PARENT_PASSWORD", studentDetails.get(position).getStudentPassword2());
                        orderplace.setArguments(bundle);
                        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
                dialog.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return studentDetails.size();
    }

    public void filterList(List<StudentModel> filteredList) {
        studentDetails = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView student_name, admission_date, standard, batch_time, contact_no;
        ImageView student_edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            student_name = itemView.findViewById(R.id.student_name);
            admission_date = itemView.findViewById(R.id.admission_date);
            standard = itemView.findViewById(R.id.standard);
            batch_time = itemView.findViewById(R.id.batch_time);
            contact_no = itemView.findViewById(R.id.contact_no);
            student_edit = itemView.findViewById(R.id.student_edit);
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }
}
