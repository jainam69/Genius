package com.example.genius.ui.Attendance_Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.genius.Adapter.AttendanceEntry_Adapter;
import com.example.genius.Adapter.AttendanceMaster_Adapter;
import com.example.genius.Adapter.StudentMaster_Adapter;
import com.example.genius.Model.AttendanceModel;
import com.example.genius.Model.BannerModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.StudentData;
import com.example.genius.Model.StudentModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Preferences;
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

    SearchableSpinner standard, batch_time, branch;
    EditText attendance_date;
    Button submit_attendance, save_attendance, edit_attendance;
    RecyclerView attendance_rv, attendance_edit_rv;
    AttendanceMaster_Adapter attendanceMaster_adapter;
    AttendanceCheck_Adapter attendanceCheck_adapter;
    Context context;
    TextView no_content, attendance_id, transactionid;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    private int year;
    private int month;
    private int day;
    String indate;
    List<String> standarditem = new ArrayList<>(), branchitem = new ArrayList<>(), batchitem = new ArrayList<>(), batchid = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), branchid = new ArrayList<>();
    String[] STANDARDITEM, BRANCHITEM, BATCHITEM;
    Integer[] STANDARDID, BRANCHID;
    String StandardName, BatchTime, BranchName, BranchID, BatchId;
    OnBackPressedCallback callback;
    Long StandardId;
    List<AttendanceModel.AttendanceDetailEntity> attandance;
    List<AttendanceModel.AttendanceDetailEntity> attandance_edit;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");
    Bundle bundle;
    LinearLayout linear_edit_attendance, linear_create_attendance;
    List<AttendanceModel.AttendanceDetailEntity> attendanceDetailEntityList;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Attendance");
        View root = inflater.inflate(R.layout.attendance_fragment_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        batch_time = root.findViewById(R.id.batch_time);
        standard = root.findViewById(R.id.standard);
        branch = root.findViewById(R.id.branch);
        submit_attendance = root.findViewById(R.id.submit_attendance);
        save_attendance = root.findViewById(R.id.save_attendance);
        attendance_rv = root.findViewById(R.id.attendance_rv);
        attendance_date = root.findViewById(R.id.attendance_date);
        no_content = root.findViewById(R.id.no_content);
        edit_attendance = root.findViewById(R.id.edit_attendance);
        attendance_edit_rv = root.findViewById(R.id.attendance_edit_rv);
        linear_edit_attendance = root.findViewById(R.id.linear_edit_attendance);
        linear_create_attendance = root.findViewById(R.id.linear_create_attendance);
        attendance_id = root.findViewById(R.id.attendance_id);
        transactionid = root.findViewById(R.id.transactionid);

        attendance_date.setText(yesterday());

        bundle = getArguments();
        if (bundle != null) {
            linear_create_attendance.setVisibility(View.GONE);
            linear_edit_attendance.setVisibility(View.VISIBLE);
            if (bundle.containsKey("AttendanceID")) {
                attendance_id.setText("" + bundle.getLong("AttendanceID"));
                if (Function.checkNetworkConnection(context)) {
                    progressBarHelper.showProgressDialog();
                    Call<AttendanceModel.AttendanceData1> call = apiCalling.GetAttendanceByID(Long.parseLong(attendance_id.getText().toString()));
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
                                        attendance_edit_rv.setLayoutManager(linearLayoutManager);
                                        attendanceCheck_adapter = new AttendanceCheck_Adapter(context, attendanceDetailEntityList);
                                        attendanceCheck_adapter.notifyDataSetChanged();
                                        attendance_edit_rv.setAdapter(attendanceCheck_adapter);
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
            if (bundle.containsKey("TransactionId")) {
                transactionid.setText("" + bundle.getLong("TransactionId"));
            }
        } else {
            indate = actualdate.format(Calendar.getInstance().getTime());
        }

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            //GetAllBranch();
            selectbatch_time();
            GetAllStandard();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        attendance_date.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(getActivity(),
                    (view, year2, monthOfYear, dayOfMonth) -> {
                        year = year2;
                        month = monthOfYear;
                        day = dayOfMonth;
                        attendance_date.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                        String as = attendance_date.getText().toString();
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

        submit_attendance.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                /*if (branch.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Branch.", Toast.LENGTH_SHORT).show();
                else */
                if (standard.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                else if (batch_time.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                else if (attendance_date.getText().toString().equals(""))
                    Toast.makeText(context, "Please enter attendance date.", Toast.LENGTH_SHORT).show();
                else {
                    progressBarHelper.showProgressDialog();
                    BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    Call<StudentData> call = apiCalling.GetAllStudentForAttendance(Long.parseLong(BranchID), StandardId, Integer.parseInt(BatchId));
                    call.enqueue(new Callback<StudentData>() {
                        @Override
                        public void onResponse(@NotNull Call<StudentData> call, @NotNull Response<StudentData> response) {
                            if (response.isSuccessful()) {
                                StudentData data = response.body();
                                if (data != null && data.isCompleted()) {
                                    List<StudentModel> studentModelList = data.getData();
                                    if (studentModelList != null) {
                                        if (studentModelList.size() > 0) {
                                            save_attendance.setVisibility(View.VISIBLE);
                                            attendance_rv.setVisibility(View.VISIBLE);
                                            no_content.setVisibility(View.GONE);
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                            attendance_rv.setLayoutManager(linearLayoutManager);
                                            attendanceMaster_adapter = new AttendanceMaster_Adapter(context, studentModelList);
                                            attendanceMaster_adapter.notifyDataSetChanged();
                                            attendance_rv.setAdapter(attendanceMaster_adapter);
                                        } else {
                                            attendance_rv.setVisibility(View.GONE);
                                            no_content.setVisibility(View.VISIBLE);
                                        }
                                        progressBarHelper.hideProgressDialog();
                                    }
                                }
                            } else {
                                progressBarHelper.showProgressDialog();
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

        save_attendance.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                if (branch.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Branch.", Toast.LENGTH_SHORT).show();
                else if (standard.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                else if (batch_time.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                else if (attendance_date.getText().toString().equals(""))
                    Toast.makeText(context, "Please enter attendance date.", Toast.LENGTH_SHORT).show();
                else {
                    progressBarHelper.showProgressDialog();
                    attandance = new ArrayList<>();
                    BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    BranchModel branchModel = new BranchModel(Long.parseLong(BranchID));
                    StandardModel standardModel = new StandardModel(StandardId);
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
                    AttendanceModel attendanceModel = new AttendanceModel(branchModel, standardModel, Integer.parseInt(BatchId), BatchTime, indate, transactionModel, rowStatusModel, attandance);
                    Call<AttendanceModel.AttendanceData1> call = apiCalling.AttendanceMaintenance(attendanceModel);
                    call.enqueue(new Callback<AttendanceModel.AttendanceData1>() {
                        @Override
                        public void onResponse(@NotNull Call<AttendanceModel.AttendanceData1> call, @NotNull Response<AttendanceModel.AttendanceData1> response) {
                            if (response.isSuccessful()) {
                                AttendanceModel.AttendanceData1 data = response.body();
                                if (data != null && data.isCompleted()) {
                                    AttendanceModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        Toast.makeText(context, "Attendance Inserted Successfully...", Toast.LENGTH_SHORT).show();
                                        attendance_Listfragment orderplace = new attendance_Listfragment();
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                                        fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    } else {
                                        Toast.makeText(context, "Attendance not Inserted...!", Toast.LENGTH_SHORT).show();
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
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        edit_attendance.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                progressBarHelper.showProgressDialog();
                attandance_edit = new ArrayList<>();
                BranchModel branchModel = new BranchModel(Long.parseLong(BranchID));
                StandardModel standardModel = new StandardModel(StandardId);
                TransactionModel transactionModel = new TransactionModel(Long.parseLong(transactionid.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
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
                AttendanceModel attendanceModel = new AttendanceModel(Long.parseLong(attendance_id.getText().toString()), branchModel, standardModel, Integer.parseInt(BatchId), BatchTime, indate, transactionModel, rowStatusModel, attandance_edit);
                Call<AttendanceModel.AttendanceData1> call = apiCalling.AttendanceMaintenance(attendanceModel);
                call.enqueue(new Callback<AttendanceModel.AttendanceData1>() {
                    @Override
                    public void onResponse(@NotNull Call<AttendanceModel.AttendanceData1> call, @NotNull Response<AttendanceModel.AttendanceData1> response) {
                        if (response.isSuccessful()) {
                            AttendanceModel.AttendanceData1 data = response.body();
                            if (data != null && data.isCompleted()) {
                                AttendanceModel notimodel = data.getData();
                                if (notimodel != null) {
                                    Toast.makeText(context, "Attendance Updated Successfully...", Toast.LENGTH_SHORT).show();
                                    attendance_Listfragment orderplace = new attendance_Listfragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                } else {
                                    Toast.makeText(context, "Attendance not Updated...!", Toast.LENGTH_SHORT).show();
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
        return root;
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }

    public void GetAllBranch() {
        branchitem.add("Select Branch");
        branchid.add(0);

        Call<BranchModel> call = apiCalling.GetAllBranch();
        call.enqueue(new Callback<BranchModel>() {
            @Override
            public void onResponse(@NotNull Call<BranchModel> call, @NotNull Response<BranchModel> response) {
                if (response.isSuccessful()) {
                    progressBarHelper.hideProgressDialog();
                    BranchModel branchModel = response.body();
                    if (branchModel != null) {
                        if (branchModel.isCompleted()) {
                            List<BranchModel.BranchData> respose = branchModel.getData();
                            for (BranchModel.BranchData singleResponseModel : respose) {

                                String building_name = singleResponseModel.getBranchName();
                                branchitem.add(building_name);

                                int building_id = Integer.parseInt(String.valueOf(singleResponseModel.getBranchID()));
                                branchid.add(building_id);
                            }
                            BRANCHITEM = new String[branchitem.size()];
                            BRANCHITEM = branchitem.toArray(BRANCHITEM);

                            BRANCHID = new Integer[branchid.size()];
                            BRANCHID = branchid.toArray(BRANCHID);

                            bindbranch();
                        } else {
                            progressBarHelper.hideProgressDialog();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<BranchModel> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindbranch() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, BRANCHITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(adapter);
        branch.setOnItemSelectedListener(onItemSelectedListener6);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener6 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    BranchName = branchitem.get(position);
                    BranchID = branchid.get(position).toString();
                    if (branch.getSelectedItem().equals("Select Branch")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetAllStandard() {
        standarditem.add("Select Standard");
        standardid.add(0);

        Call<StandardData> call = apiCalling.GetAllStandard(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<StandardData>() {
            @Override
            public void onResponse(@NotNull Call<StandardData> call, @NotNull Response<StandardData> response) {
                if (response.isSuccessful()) {
                    progressBarHelper.hideProgressDialog();
                    StandardData standardData = response.body();
                    if (standardData != null) {
                        if (standardData.isCompleted()) {
                            List<StandardModel> respose = standardData.getData();
                            for (StandardModel singleResponseModel : respose) {

                                String std = singleResponseModel.getStandard();
                                standarditem.add(std);

                                int stdid = (int) singleResponseModel.getStandardID();
                                standardid.add(stdid);
                            }
                            STANDARDITEM = new String[standarditem.size()];
                            STANDARDITEM = standarditem.toArray(STANDARDITEM);

                            STANDARDID = new Integer[standardid.size()];
                            STANDARDID = standardid.toArray(STANDARDID);

                            bindstandard();
                        } else {
                            progressBarHelper.hideProgressDialog();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<StandardData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindstandard() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, STANDARDITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        standard.setAdapter(adapter);
        standard.setOnItemSelectedListener(onItemSelectedListener7);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener7 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    StandardName = standarditem.get(position);
                    StandardId = Long.parseLong(standardid.get(position).toString());
                    if (standard.getSelectedItem().equals("Select Standard")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
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
        batch_time.setAdapter(adapter);
        batch_time.setOnItemSelectedListener(onItemSelectedListener77);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener77 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    BatchTime = batchitem.get(position);
                    BatchId = batchid.get(position);
                    if (batch_time.getSelectedItem().equals("Batch Time")) {
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

    public static String yesterday() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        cal.add(Calendar.DATE, 0);
        return dateFormat.format(cal.getTime());
    }

}