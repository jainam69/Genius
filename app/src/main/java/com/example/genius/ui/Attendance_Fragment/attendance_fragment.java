package com.example.genius.ui.Attendance_Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.AttendanceCheck_Adapter;
import com.example.genius.Adapter.AttendanceMaster_Adapter;
import com.example.genius.Model.AttendanceModel;
import com.example.genius.Model.BranchClassModel;
import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.StudentData;
import com.example.genius.Model.StudentModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.databinding.AttendanceFragmentFragmentBinding;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint({"SetTextI18n", "SimpleDateFormat"})
public class attendance_fragment extends Fragment {

    AttendanceFragmentFragmentBinding binding;
    AttendanceMaster_Adapter attendanceMaster_adapter;
    AttendanceCheck_Adapter attendanceCheck_adapter;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    private int year;
    private int month;
    private int day;
    List<String> standarditem = new ArrayList<>(), batchitem = new ArrayList<>(), batchid = new ArrayList<>(),courseitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), courseid = new ArrayList<>();
    String[] STANDARDITEM, BATCHITEM,COURSEITEM;
    Integer[] STANDARDID, COURSEID;
    String BatchTime, BranchID, BatchId,indate,attendanceremark;
    OnBackPressedCallback callback;
    Long StandardId,courseID;
    List<AttendanceModel.AttendanceDetailEntity> attandance;
    List<AttendanceModel.AttendanceDetailEntity> attandance_edit;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");
    Bundle bundle;
    List<AttendanceModel.AttendanceDetailEntity> attendanceDetailEntityList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Attendance Entry");
        binding = AttendanceFragmentFragmentBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        bundle = getArguments();
        if (bundle != null) {
            binding.linearCreateAttendance.setVisibility(View.GONE);
            binding.linearEditAttendance.setVisibility(View.VISIBLE);
            if (bundle.containsKey("AttendanceID")) {
                binding.attendanceId.setText("" + bundle.getLong("AttendanceID"));
                if (Function.isNetworkAvailable(context)) {
                    progressBarHelper.showProgressDialog();
                    Call<AttendanceModel.AttendanceData1> call = apiCalling.GetAttendanceByID(Long.parseLong(binding.attendanceId.getText().toString()));
                    call.enqueue(new Callback<AttendanceModel.AttendanceData1>() {
                        @Override
                        public void onResponse(@NotNull Call<AttendanceModel.AttendanceData1> call, @NotNull Response<AttendanceModel.AttendanceData1> response) {
                            if (response.isSuccessful()) {
                                AttendanceModel.AttendanceData1 data = response.body();
                                if (data != null && data.isCompleted()) {
                                    AttendanceModel notimodel = data.getData();
                                    attendanceDetailEntityList = notimodel.getAttendanceDetail();
                                    if (attendanceDetailEntityList != null) {
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                        binding.attendanceEditRv.setLayoutManager(linearLayoutManager);
                                        attendanceCheck_adapter = new AttendanceCheck_Adapter(context, attendanceDetailEntityList);
                                        attendanceCheck_adapter.notifyDataSetChanged();
                                        binding.attendanceEditRv.setAdapter(attendanceCheck_adapter);
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<AttendanceModel.AttendanceData1> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });

                } else {
                    Toast.makeText(context, "Please check your internet connectivity", Toast.LENGTH_SHORT).show();
                }
            }
            if (bundle.containsKey("Date")) {
                indate = bundle.getString("Date").replace("T00:00:00", "");
            }
            if (bundle.containsKey("BatchId")) {
                BatchId = String.valueOf(bundle.getInt("BatchId"));
            }
            if (bundle.containsKey("BatchName")) {
                BatchTime = bundle.getString("BatchName");
            }
            if (bundle.containsKey("BranchID")) {
                BranchID = String.valueOf(bundle.getLong("BranchID"));
            }
            if (bundle.containsKey("StandardID")) {
                StandardId = bundle.getLong("StandardID");
            }
            if (bundle.containsKey("courseid")) {
                courseID = bundle.getLong("courseid");
            }
            if (bundle.containsKey("TransactionId")) {
                binding.transactionid.setText("" + bundle.getLong("TransactionId"));
            }
            if (bundle.containsKey("AttendanceRemark")){
                attendanceremark = bundle.getString("AttendanceRemark");
            }
        } else {
            indate = actualdate.format(Calendar.getInstance().getTime());
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            selectbatch_time();
            GetAllCourse();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        selectStandard();
        binding.attendanceDate.setText(yesterday());

        binding.attendanceDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(getActivity(),
                    (view, year2, monthOfYear, dayOfMonth) -> {
                        year = year2;
                        month = monthOfYear;
                        day = dayOfMonth;
                        binding.attendanceDate.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                        String as = binding.attendanceDate.getText().toString();
                        Date dt;
                        try {
                            dt = displaydate.parse(as);
                            assert dt != null;
                            indate = actualdate.format(dt);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }, year, month, day);
            picker.show();
        });

        binding.submitAttendance.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (binding.courseName.getSelectedItemId() == 0){
                    Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                }else if (binding.standard.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                else if (binding.batchTime.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                else if (binding.attendanceDate.getText().toString().equals(""))
                    Toast.makeText(context, "Please enter attendance date.", Toast.LENGTH_SHORT).show();
                else if (binding.attendanceRemark.getText().toString().isEmpty())
                    Toast.makeText(context, "Please enter attendance remark.", Toast.LENGTH_SHORT).show();
                else {
                    progressBarHelper.showProgressDialog();
                    BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    Call<StudentData> call = apiCalling.GetAllStudentForAttendance(Long.parseLong(BranchID), StandardId, courseID,Integer.parseInt(BatchId),indate,
                            binding.attendanceRemark.getText().toString());
                    call.enqueue(new Callback<StudentData>() {
                        @Override
                        public void onResponse(@NotNull Call<StudentData> call, @NotNull Response<StudentData> response) {
                            if (response.isSuccessful()) {
                                StudentData data = response.body();
                                if (data.isCompleted()) {
                                    List<StudentModel> studentModelList = data.getData();
                                    if (studentModelList != null) {
                                        if (studentModelList.size() > 0) {
                                            binding.saveAttendance.setVisibility(View.VISIBLE);
                                            binding.attendanceRv.setVisibility(View.VISIBLE);
                                            binding.noContent.setVisibility(View.GONE);
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                            binding.attendanceRv.setLayoutManager(linearLayoutManager);
                                            attendanceMaster_adapter = new AttendanceMaster_Adapter(context, studentModelList);
                                            attendanceMaster_adapter.notifyDataSetChanged();
                                            binding.attendanceRv.setAdapter(attendanceMaster_adapter);
                                        } else {
                                            binding.saveAttendance.setVisibility(View.GONE);
                                            binding.attendanceRv.setVisibility(View.GONE);
                                            binding.noContent.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }else {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<StudentData> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.saveAttendance.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (binding.courseName.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Course.", Toast.LENGTH_SHORT).show();
                } else if (binding.standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (binding.batchTime.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                } else if (binding.attendanceDate.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter attendance date.", Toast.LENGTH_SHORT).show();
                } else if (binding.attendanceRemark.getText().toString().isEmpty()){
                    Toast.makeText(context, "Please enter attendance remark.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    attandance = new ArrayList<>();
                    BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                    BranchClassSingleModel.BranchClassData branchclass = new BranchClassSingleModel.BranchClassData(StandardId);
                    BranchModel branchModel = new BranchModel(Long.parseLong(BranchID));
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    if (AttendanceMaster_Adapter.testlist.size() > 0) {
                        for (int i = 0; i < AttendanceMaster_Adapter.testlist.size(); i++) {
                            AttendanceModel.AttendanceDetailEntity at = new AttendanceModel.AttendanceDetailEntity(AttendanceMaster_Adapter.testlist.get(i).getStudent(),
                                    AttendanceMaster_Adapter.testlist.get(i).isAbsent(), AttendanceMaster_Adapter.testlist.get(i).isPresent(),
                                    AttendanceMaster_Adapter.testlist.get(i).getRemarks());
                            attandance.add(at);
                        }
                    }
                    AttendanceMaster_Adapter.testlist.clear();
                    AttendanceModel attendanceModel = new AttendanceModel(branchModel, Integer.parseInt(BatchId), BatchTime, indate, transactionModel, rowStatusModel, attandance,course,branchclass,
                            binding.attendanceRemark.getText().toString());
                    Call<AttendanceModel.AttendanceData1> call = apiCalling.AttendanceMaintenance(attendanceModel);
                    call.enqueue(new Callback<AttendanceModel.AttendanceData1>() {
                        @Override
                        public void onResponse(@NotNull Call<AttendanceModel.AttendanceData1> call, @NotNull Response<AttendanceModel.AttendanceData1> response) {
                            if (response.isSuccessful()) {
                                AttendanceModel.AttendanceData1 data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    attendance_Listfragment orderplace = new attendance_Listfragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }else {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<AttendanceModel.AttendanceData1> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.editAttendance.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                progressBarHelper.showProgressDialog();
                attandance_edit = new ArrayList<>();
                BranchModel branchModel = new BranchModel(Long.parseLong(BranchID));
                BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                BranchClassSingleModel.BranchClassData branchclass = new BranchClassSingleModel.BranchClassData(StandardId);
                TransactionModel transactionModel = new TransactionModel(Long.parseLong(binding.transactionid.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                RowStatusModel rowStatusModel = new RowStatusModel(1);
                if (AttendanceCheck_Adapter.testlist_edit.size() > 0) {
                    for (int i = 0; i < AttendanceCheck_Adapter.testlist_edit.size(); i++) {
                        AttendanceModel.AttendanceDetailEntity at = new AttendanceModel.AttendanceDetailEntity(AttendanceCheck_Adapter.testlist_edit.get(i).getDetailID(),
                                AttendanceCheck_Adapter.testlist_edit.get(i).getHeaderID(), AttendanceCheck_Adapter.testlist_edit.get(i).getStudent(),
                                AttendanceCheck_Adapter.testlist_edit.get(i).isAbsent(), AttendanceCheck_Adapter.testlist_edit.get(i).isPresent(),
                                AttendanceCheck_Adapter.testlist_edit.get(i).getRemarks());
                        attandance_edit.add(at);
                    }
                }
                AttendanceCheck_Adapter.testlist_edit.clear();
                AttendanceModel attendanceModel = new AttendanceModel(Long.parseLong(binding.attendanceId.getText().toString()), branchModel, Integer.parseInt(BatchId), BatchTime, indate, transactionModel, rowStatusModel, attandance_edit,course,branchclass,
                        attendanceremark);
                Call<AttendanceModel.AttendanceData1> call = apiCalling.AttendanceMaintenance(attendanceModel);
                call.enqueue(new Callback<AttendanceModel.AttendanceData1>() {
                    @Override
                    public void onResponse(@NotNull Call<AttendanceModel.AttendanceData1> call, @NotNull Response<AttendanceModel.AttendanceData1> response) {
                        if (response.isSuccessful()) {
                            AttendanceModel.AttendanceData1 data = response.body();
                            if (data.isCompleted()) {
                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                attendance_Listfragment orderplace = new attendance_Listfragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                                fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }else {
                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressBarHelper.hideProgressDialog();
                    }

                    @Override
                    public void onFailure(@NotNull Call<AttendanceModel.AttendanceData1> call, @NotNull Throwable t) {
                        Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                        progressBarHelper.hideProgressDialog();
                    }
                });
            } else {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                attendance_Listfragment profileFragment = new attendance_Listfragment();
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.nav_host_fragment, profileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);
        return binding.getRoot();
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }

    public void GetAllCourse()
    {
        progressBarHelper.showProgressDialog();
        courseitem.clear();
        courseid.clear();
        courseitem.add("Select Course");
        courseid.add(0);

        Call<BranchCourseModel> call = apiCalling.GetAllCourseDDL(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<BranchCourseModel>() {
            @Override
            public void onResponse(Call<BranchCourseModel> call, Response<BranchCourseModel> response) {
                if (response.isSuccessful()){
                    BranchCourseModel data = response.body();
                    if (data.isCompleted()){
                        List<BranchCourseModel.BranchCourceData> list = data.getData();
                        if (list != null && list.size() >0){
                            for (BranchCourseModel.BranchCourceData model : list) {
                                String course = model.getCourse().getCourseName();
                                courseitem.add(course);
                                int id = (int) model.getCourse_dtl_id();
                                courseid.add(id);
                            }
                            COURSEITEM = new String[courseitem.size()];
                            COURSEITEM = courseitem.toArray(COURSEITEM);

                            COURSEID = new Integer[courseid.size()];
                            COURSEID = courseid.toArray(COURSEID);

                            bindcourse();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<BranchCourseModel> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindcourse() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, COURSEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.courseName.setAdapter(adapter);
        binding.courseName.setOnItemSelectedListener(selectcourse);
    }

    AdapterView.OnItemSelectedListener selectcourse =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    courseID = Long.parseLong(courseid.get(position).toString());
                    if (binding.courseName.getSelectedItem().equals("Select Course")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    }
                    if (binding.courseName.getSelectedItemId() != 0){
                        GetAllStandard(courseID);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetAllStandard(long coursedetailid) {
        progressBarHelper.showProgressDialog();
        standarditem.clear();
        standardid.clear();
        standarditem.add("Select Standard");
        standardid.add(0);

        Call<BranchClassModel> call = apiCalling.Get_Class_Spinner(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),coursedetailid);
        call.enqueue(new Callback<BranchClassModel>() {
            @Override
            public void onResponse(@NotNull Call<BranchClassModel> call, @NotNull Response<BranchClassModel> response) {
                if (response.isSuccessful()) {
                    BranchClassModel data = response.body();
                    if (data.getCompleted()) {
                        List<BranchClassSingleModel.BranchClassData> list = data.getData();
                        if (list != null && list.size() >0)
                        {
                            for (BranchClassSingleModel.BranchClassData model : list) {

                                String std = model.getClassModel().getClassName();
                                standarditem.add(std);

                                int stdid = (int) model.getClass_dtl_id();
                                standardid.add(stdid);
                            }
                            STANDARDITEM = new String[standarditem.size()];
                            STANDARDITEM = standarditem.toArray(STANDARDITEM);

                            STANDARDID = new Integer[standardid.size()];
                            STANDARDID = standardid.toArray(STANDARDID);

                            bindstandard();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BranchClassModel> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindstandard() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, STANDARDITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.standard.setAdapter(adapter);
        binding.standard.setOnItemSelectedListener(onItemSelectedListener7);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener7 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    StandardId = Long.parseLong(standardid.get(position).toString());
                    if (binding.standard.getSelectedItem().equals("Select Standard")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void selectbatch_time() {
        batchitem.clear();
        batchitem.add("Batch Time");
        batchid.add("0");
        batchitem.add("Morning");
        batchid.add("1");
        batchitem.add("AfterNoon");
        batchid.add("2");
        batchitem.add("Evening");
        batchid.add("3");
        BATCHITEM = new String[batchitem.size()];
        BATCHITEM = batchitem.toArray(BATCHITEM);

        bindbatch_time();
    }

    public void bindbatch_time() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, BATCHITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.batchTime.setAdapter(adapter);
        binding.batchTime.setOnItemSelectedListener(onItemSelectedListener77);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener77 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    BatchTime = batchitem.get(position);
                    BatchId = batchid.get(position);
                    if (binding.batchTime.getSelectedItem().equals("Batch Time")) {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                            ((TextView) parent.getChildAt(0)).setTextSize(13);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                            ((TextView) parent.getChildAt(0)).setTextSize(14);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };

    public void selectStandard() {
        standarditem.clear();
        standardid.clear();
        standarditem.add("Select Standard");
        standardid.add(0);

        STANDARDITEM = new String[standarditem.size()];
        STANDARDITEM = standarditem.toArray(STANDARDITEM);

        bindstd();
    }

    public void bindstd() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, STANDARDITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.standard.setAdapter(adapter);
        binding.standard.setOnItemSelectedListener(onItemSelectedListener7);
    }

    public static String yesterday() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        cal.add(Calendar.DATE, 0);
        return dateFormat.format(cal.getTime());
    }

}