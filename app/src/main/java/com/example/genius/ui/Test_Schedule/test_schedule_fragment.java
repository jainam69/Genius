package com.example.genius.ui.Test_Schedule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.TestScheduleMaster_Adapter;
import com.example.genius.Model.AttendanceModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.SubjectData;
import com.example.genius.Model.SubjectModel;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.TestScheduleData;
import com.example.genius.Model.TestScheduleModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Model.UploadPaperData;
import com.example.genius.Model.UploadPaperModel;
import com.example.genius.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Attendance_Fragment.attendance_Listfragment;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@SuppressLint({"SimpleDateFormat", "SetTextI18n"})
public class test_schedule_fragment extends Fragment {

    SearchableSpinner standard, batch_time, subject, paper_type;
    Context context;
    EditText marks, start_time, end_time, remarks, test_date, paper_remarks, upload_link;
    TextView id, upload_test_paper, test_id, paper_id;
    Button save_testschedule, edit_testschedule, save_test_paper, edit_test_paper;
    List<String> standarditem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>();
    String[] STANDARDITEM;
    Integer[] STANDARDID;
    List<String> batchitem = new ArrayList<>(), batchid = new ArrayList<>();
    String[] BATCHITEM;
    List<String> subjectitem = new ArrayList<>();
    List<Integer> subjectid = new ArrayList<>();
    String[] SUBJECTITEM;
    Integer[] SUBJECTID;
    List<String> typeitem = new ArrayList<>(), typeid = new ArrayList<>();
    String[] TYPEITEM;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    String StandardName, SubjectName, format, BatchTime, SubjectId, BatchId, indate, PaperType_Id, PaperType_Name, upload_paper, paper_ext, papername;
    private int year;
    private int month;
    private int day;
    Bundle bundle;
    long a, b, c;
    Long StandardId;
    OnBackPressedCallback callback;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");
    RelativeLayout linear_doc, linear_link, linear_remarks, linear_edit, linear_save;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    File instrumentFileDestination;
    int flag = 0, type;
    UploadPaperModel uploadPaperModel;
    UploadPaperModel uploadPaperModel_update;
    List<UploadPaperModel> studentModelList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Test Schedule");
        View root = inflater.inflate(R.layout.test_schedule_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        standard = root.findViewById(R.id.standard);
        batch_time = root.findViewById(R.id.batch_time);
        subject = root.findViewById(R.id.subject);
        marks = root.findViewById(R.id.marks);
        start_time = root.findViewById(R.id.start_time);
        end_time = root.findViewById(R.id.end_time);
        remarks = root.findViewById(R.id.remarks);
        save_testschedule = root.findViewById(R.id.save_testschedule);
        edit_testschedule = root.findViewById(R.id.edit_testschedule);
        test_date = root.findViewById(R.id.test_date);
        id = root.findViewById(R.id.id);
        paper_type = root.findViewById(R.id.paper_type);
        paper_remarks = root.findViewById(R.id.paper_remarks);
        upload_test_paper = root.findViewById(R.id.upload_test_paper);
        upload_link = root.findViewById(R.id.upload_link);
        save_test_paper = root.findViewById(R.id.save_test_paper);
        linear_doc = root.findViewById(R.id.linear_doc);
        linear_link = root.findViewById(R.id.linear_link);
        linear_remarks = root.findViewById(R.id.linear_remarks);
        test_id = root.findViewById(R.id.test_id);
        edit_test_paper = root.findViewById(R.id.edit_test_paper);
        paper_id = root.findViewById(R.id.paper_id);
        linear_edit = root.findViewById(R.id.linear_edit);
        linear_save = root.findViewById(R.id.linear_save);

        bundle = getArguments();
        if (bundle != null) {
            linear_save.setVisibility(View.GONE);
            linear_edit.setVisibility(View.VISIBLE);
            if (bundle.containsKey("TestDate")) {
                try {
                    Date d = actualdate.parse(bundle.getString("TestDate").replace("T00:00:00", ""));
                    test_date.setText("" + displaydate.format(d));
                    indate = bundle.getString("TestDate").replace("T00:00:00", "");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (bundle.containsKey("StartTime")) {
                start_time.setText(bundle.getString("StartTime"));
            }
            if (bundle.containsKey("EndTime")) {
                end_time.setText(bundle.getString("EndTime"));
            }
            if (bundle.containsKey("TestMarks")) {
                marks.setText("" + bundle.getDouble("TestMarks"));
            }
            if (bundle.containsKey("TestRemarks")) {
                remarks.setText(bundle.getString("TestRemarks"));
            }
            if (bundle.containsKey("TestID")) {
                id.setText("" + bundle.getLong("TestID"));
                if (Function.checkNetworkConnection(context)) {
                    progressBarHelper.showProgressDialog();
                    Call<UploadPaperData> call = apiCalling.GetAllTestPapaerByTest(bundle.getLong("TestID"));
                    call.enqueue(new Callback<UploadPaperData>() {
                        @Override
                        public void onResponse(@NotNull Call<UploadPaperData> call, @NotNull Response<UploadPaperData> response) {
                            if (response.isSuccessful()) {
                                UploadPaperData data = response.body();
                                if (data.isCompleted()) {
                                    studentModelList = data.getData();
                                    if (studentModelList != null) {
                                        if (studentModelList.size() > 0) {
                                            b = studentModelList.get(0).getTestPaperID();
                                            c = studentModelList.get(0).getTestID();
                                            type = studentModelList.get(0).getPaperTypeID();
                                            if (type == 1) {
                                                paper_type.setSelection(1);
                                                upload_test_paper.setText("Attached");
                                                upload_test_paper.setTextColor(context.getResources().getColor(R.color.black));
                                                linear_doc.setVisibility(View.VISIBLE);
                                            } else {
                                                paper_type.setSelection(2);
                                                upload_link.setText(studentModelList.get(0).getDocLink());
                                                linear_link.setVisibility(View.VISIBLE);
                                            }
                                            linear_remarks.setVisibility(View.VISIBLE);
                                            edit_test_paper.setVisibility(View.VISIBLE);
                                            save_test_paper.setVisibility(View.GONE);
                                            paper_remarks.setText(studentModelList.get(0).getRemarks());
                                        }
                                        GetPaperType();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<UploadPaperData> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        }

        test_date.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year2, int monthOfYear, int dayOfMonth) {
                            year = year2;
                            month = monthOfYear;
                            day = dayOfMonth;
                            test_date.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                            String as = test_date.getText().toString();
                            Date dt = null;
                            try {
                                dt = displaydate.parse(as);
                                indate = actualdate.format(dt);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }, year, month, day);
            picker.show();
        });

        start_time.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            if (hourOfDay == 0) {
                                hourOfDay += 12;
                                format = "AM";
                            } else if (hourOfDay == 12) {
                                format = "PM";
                            } else if (hourOfDay > 12) {
                                hourOfDay -= 12;
                                format = "PM";
                            } else {
                                format = "AM";
                            }
                            start_time.setText(hourOfDay + ":" + minute + " " + format);
                        }
                    }, hour, minute, false);
            timePickerDialog.show();
        });

        end_time.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            if (hourOfDay == 0) {
                                hourOfDay += 12;
                                format = "AM";
                            } else if (hourOfDay == 12) {
                                format = "PM";
                            } else if (hourOfDay > 12) {
                                hourOfDay -= 12;
                                format = "PM";
                            } else {
                                format = "AM";
                            }
                            end_time.setText(hourOfDay + ":" + minute + " " + format);
                        }
                    }, hour, minute, false);
            timePickerDialog.show();
        });

        save_testschedule.setOnClickListener((View.OnClickListener) v -> {
            progressBarHelper.showProgressDialog();
            if (Function.checkNetworkConnection(context)) {
                if (standard.getSelectedItemId() == 0) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (batch_time.getSelectedItemId() == 0) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                } else if (subject.getSelectedItemId() == 0) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                } else if (marks.getText().toString().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please enter Total Marks.", Toast.LENGTH_SHORT).show();
                } else if (test_date.getText().toString().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please enter Test Date.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    StandardModel standardModel = new StandardModel(StandardId);
                    SubjectModel subjectModel = new SubjectModel(Long.parseLong(SubjectId));
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    TestScheduleModel testScheduleModel = new TestScheduleModel("Demo", branchModel, standardModel, subjectModel, Integer.parseInt(BatchId), BatchTime,
                            Double.parseDouble(marks.getText().toString()), indate, start_time.getText().toString(), end_time.getText().toString(),
                            remarks.getText().toString(), rowStatusModel, transactionModel);
                    Call<TestScheduleModel.TestScheduleData1> call = apiCalling.TestMaintenance(testScheduleModel);
                    call.enqueue(new Callback<TestScheduleModel.TestScheduleData1>() {
                        @Override
                        public void onResponse(@NotNull Call<TestScheduleModel.TestScheduleData1> call, @NotNull Response<TestScheduleModel.TestScheduleData1> response) {
                            if (response.isSuccessful()) {
                                TestScheduleModel.TestScheduleData1 data = response.body();
                                if (data.isCompleted()) {
                                    TestScheduleModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        if (paper_type.getSelectedItemId() == 0) {
                                            Toast.makeText(context, "Test Schedule Inserted Successfully...", Toast.LENGTH_SHORT).show();
                                            test_Listfragment orderplace = new test_Listfragment();
                                            FragmentManager fragmentManager = getFragmentManager();
                                            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                                            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();
                                        } else {
                                            Toast.makeText(context, "Test Schedule Inserted Successfully...", Toast.LENGTH_SHORT).show();
                                            save_testschedule.setVisibility(View.GONE);
                                            a = notimodel.getTestID();
                                            if (PaperType_Name.equalsIgnoreCase("UploadDocument")) {
                                                linear_doc.setVisibility(View.VISIBLE);
                                            } else {
                                                linear_link.setVisibility(View.VISIBLE);
                                            }
                                            linear_remarks.setVisibility(View.VISIBLE);
                                            save_test_paper.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        Toast.makeText(context, "Test Schedule not Inserted...!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<TestScheduleModel.TestScheduleData1> call, @NotNull Throwable t) {
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

        edit_testschedule.setOnClickListener((View.OnClickListener) v -> {
            progressBarHelper.showProgressDialog();
            if (Function.checkNetworkConnection(context)) {
                if (standard.getSelectedItemId() == 0) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (batch_time.getSelectedItemId() == 0) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                } else if (subject.getSelectedItemId() == 0) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                } else if (marks.getText().toString().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please enter Total Marks.", Toast.LENGTH_SHORT).show();
                } else if (test_date.getText().toString().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please enter Test Date.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    StandardModel standardModel = new StandardModel(StandardId);
                    SubjectModel subjectModel = new SubjectModel(Long.parseLong(SubjectId));
                    TransactionModel transactionModel = new TransactionModel(bundle.getLong("TransactionId"), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    TestScheduleModel testScheduleModel = new TestScheduleModel(Long.parseLong(id.getText().toString()), "Demo", branchModel, standardModel, subjectModel, Integer.parseInt(BatchId), BatchTime,
                            Double.parseDouble(marks.getText().toString()), indate, start_time.getText().toString(), end_time.getText().toString(),
                            remarks.getText().toString(), rowStatusModel, transactionModel);
                    Call<TestScheduleModel.TestScheduleData1> call = apiCalling.TestMaintenance(testScheduleModel);
                    call.enqueue(new Callback<TestScheduleModel.TestScheduleData1>() {
                        @Override
                        public void onResponse(@NotNull Call<TestScheduleModel.TestScheduleData1> call, @NotNull Response<TestScheduleModel.TestScheduleData1> response) {
                            if (response.isSuccessful()) {
                                TestScheduleModel.TestScheduleData1 data = response.body();
                                if (data.isCompleted()) {
                                    TestScheduleModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        Toast.makeText(context, "Test Schedule Updated Successfully...", Toast.LENGTH_SHORT).show();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                                        View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                                        builder.setView(dialogView);
                                        builder.setCancelable(true);
                                        Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                                        Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                                        ImageView image = dialogView.findViewById(R.id.image);
                                        TextView title = dialogView.findViewById(R.id.title);
                                        TextView header_title = dialogView.findViewById(R.id.header_title);
                                        header_title.setVisibility(View.GONE);
                                        title.setText("Do you want to changes continue?");
                                        image.setImageResource(R.drawable.ic_edit);
                                        AlertDialog dialog = builder.create();
                                        btn_edit_no.setOnClickListener(v1 -> {
                                            dialog.dismiss();
                                            test_Listfragment orderplace = new test_Listfragment();
                                            FragmentManager fragmentManager = getFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();
                                        });
                                        btn_edit_yes.setOnClickListener(v12 -> {
                                            dialog.dismiss();
                                            edit_testschedule.setVisibility(View.GONE);
                                        });
                                        dialog.show();
                                    } else {
                                        Toast.makeText(context, "Test Schedule not Updated...!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<TestScheduleModel.TestScheduleData1> call, @NotNull Throwable t) {
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

        save_test_paper.setOnClickListener((View.OnClickListener) v -> {
            progressBarHelper.showProgressDialog();
            if (Function.checkNetworkConnection(context)) {
                if (PaperType_Name.equals("UploadDocument") && upload_test_paper.getText().toString().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please upload document.", Toast.LENGTH_SHORT).show();
                } else if (PaperType_Name.equals("UploadLink") && upload_link.getText().toString().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please upload link.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    if (bundle != null) {
                        if (PaperType_Name.equalsIgnoreCase("UploadDocument")) {
                            uploadPaperModel = new UploadPaperModel(Long.parseLong(id.getText().toString()), "Demo", indate, Integer.parseInt(PaperType_Id), PaperType_Name,
                                    upload_paper, papername, " ", paper_remarks.getText().toString(), rowStatusModel, transactionModel);
                        } else {
                            uploadPaperModel = new UploadPaperModel(Long.parseLong(id.getText().toString()), "Demo", indate, Integer.parseInt(PaperType_Id), PaperType_Name,
                                    " ", " ", upload_link.getText().toString(), paper_remarks.getText().toString(), rowStatusModel, transactionModel);
                        }
                    } else {
                        if (PaperType_Name.equalsIgnoreCase("UploadDocument")) {
                            uploadPaperModel = new UploadPaperModel(a, "Demo", indate, Integer.parseInt(PaperType_Id), PaperType_Name,
                                    upload_paper, papername, " ", paper_remarks.getText().toString(), rowStatusModel, transactionModel);
                        } else {
                            uploadPaperModel = new UploadPaperModel(a, "Demo", indate, Integer.parseInt(PaperType_Id), PaperType_Name,
                                    " ", " ", upload_link.getText().toString(), paper_remarks.getText().toString(), rowStatusModel, transactionModel);
                        }
                    }
                    Call<UploadPaperModel.UploadPaperData1> call = apiCalling.TestPaperMaintenance(uploadPaperModel);
                    call.enqueue(new Callback<UploadPaperModel.UploadPaperData1>() {
                        @Override
                        public void onResponse(@NotNull Call<UploadPaperModel.UploadPaperData1> call, @NotNull Response<UploadPaperModel.UploadPaperData1> response) {
                            if (response.isSuccessful()) {
                                UploadPaperModel.UploadPaperData1 data = response.body();
                                if (data.isCompleted()) {
                                    UploadPaperModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        Toast.makeText(context, "Paper Inserted Successfully...", Toast.LENGTH_SHORT).show();
                                        test_Listfragment orderplace = new test_Listfragment();
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                                        fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    } else {
                                        Toast.makeText(context, "Paper not Inserted...!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<UploadPaperModel.UploadPaperData1> call, @NotNull Throwable t) {
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

        edit_test_paper.setOnClickListener((View.OnClickListener) v -> {
            progressBarHelper.showProgressDialog();
            if (Function.checkNetworkConnection(context)) {
                if (PaperType_Name.equals("UploadDocument") && upload_test_paper.getText().toString().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please upload document.", Toast.LENGTH_SHORT).show();
                } else if (PaperType_Name.equals("UploadLink") && upload_link.getText().toString().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please upload link.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(bundle.getLong("TransactionId"), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    if (PaperType_Name.equalsIgnoreCase("UploadDocument")) {
                        uploadPaperModel_update = new UploadPaperModel(b, c, "Demo", indate, Integer.parseInt(PaperType_Id), PaperType_Name,
                                upload_paper, papername, " ", paper_remarks.getText().toString(), rowStatusModel, transactionModel);
                    } else {
                        uploadPaperModel_update = new UploadPaperModel(b, c, "Demo", indate, Integer.parseInt(PaperType_Id), PaperType_Name,
                                " ", " ", upload_link.getText().toString(), paper_remarks.getText().toString(), rowStatusModel, transactionModel);
                    }
                    Call<UploadPaperModel.UploadPaperData1> call = apiCalling.TestPaperMaintenance(uploadPaperModel_update);
                    call.enqueue(new Callback<UploadPaperModel.UploadPaperData1>() {
                        @Override
                        public void onResponse(@NotNull Call<UploadPaperModel.UploadPaperData1> call, @NotNull Response<UploadPaperModel.UploadPaperData1> response) {
                            if (response.isSuccessful()) {
                                UploadPaperModel.UploadPaperData1 data = response.body();
                                if (data.isCompleted()) {
                                    UploadPaperModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        Toast.makeText(context, "Paper Updated Successfully...", Toast.LENGTH_SHORT).show();
                                        test_Listfragment orderplace = new test_Listfragment();
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                                        fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    } else {
                                        Toast.makeText(context, "Paper not Updated...!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<UploadPaperModel.UploadPaperData1> call, @NotNull Throwable t) {
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

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            selectbatch_time();
            GetAllStandard();
            GetAllSubject();
            if (bundle == null) {
                GetPaperType();
            }
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        upload_test_paper.setOnClickListener(v -> requestPermissionForAll());

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                test_Listfragment profileFragment = new test_Listfragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.nav_host_fragment, profileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        return root;
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }

    public void GetAllSubject() {

        subjectitem.add("Select Subject");
        subjectid.add(0);
        Call<SubjectData> call = apiCalling.GetAllSubject(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<SubjectData>() {
            @Override
            public void onResponse(@NotNull Call<SubjectData> call, @NotNull Response<SubjectData> response) {
                if (response.isSuccessful()) {
                    progressBarHelper.hideProgressDialog();
                    SubjectData standardData = response.body();
                    if (standardData != null) {
                        if (standardData.isCompleted()) {
                            List<SubjectModel> respose = standardData.getData();
                            if (respose.size() > 0) {
                                List<SubjectModel> list = new ArrayList<>();
                                for (SubjectModel singleResponseModel : respose) {

                                    String std = singleResponseModel.getSubject();
                                    subjectitem.add(std);

                                    int stdid = (int) singleResponseModel.getSubjectID();
                                    subjectid.add(stdid);
                                }
                                SUBJECTITEM = new String[subjectitem.size()];
                                SUBJECTITEM = subjectitem.toArray(SUBJECTITEM);

                                SUBJECTID = new Integer[subjectid.size()];
                                SUBJECTID = subjectid.toArray(SUBJECTID);

                                bindsubject();
                            }

                        } else {
                            progressBarHelper.hideProgressDialog();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<SubjectData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindsubject() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SUBJECTITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject.setAdapter(adapter);
        if (bundle != null) {
            int c = subjectid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("TestSubject"))));
            subject.setSelection(c);
        }
        subject.setOnItemSelectedListener(onItemSelectedListener8);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener8 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SubjectName = subjectitem.get(position);
                    SubjectId = subjectid.get(position).toString();
                    if (subject.getSelectedItem().equals("Select Subject")) {
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
        if (bundle != null) {
            int b = standardid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("Standard"))));
            standard.setSelection(b);
        }
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
        if (bundle != null) {
            int a = batchid.indexOf(String.valueOf(bundle.getInt("BatchTime")));
            batch_time.setSelection(a);
        }
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
                        } catch (Exception ignored) {
                        }
                    } else {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                            ((TextView) parent.getChildAt(0)).setTextSize(14);
                        } catch (Exception ignored) {
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };

    public void GetPaperType() {
        typeitem.clear();
        typeid.clear();
        typeitem.add("Select Type");
        typeid.add("0");
        typeitem.add("UploadDocument");
        typeid.add("1");
        typeitem.add("UploadLink");
        typeid.add("2");

        TYPEITEM = new String[typeitem.size()];
        TYPEITEM = typeitem.toArray(TYPEITEM);

        bindtype();
    }

    public void bindtype() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, TYPEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paper_type.setAdapter(adapter);
        if (bundle != null && type == 1) {
            paper_type.setSelection(1);
        } else if (bundle != null && type == 2) {
            paper_type.setSelection(2);
        }
        paper_type.setOnItemSelectedListener(onItemSelectedListener78);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener78 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    PaperType_Name = typeitem.get(position);
                    PaperType_Id = typeid.get(position);
                    if (paper_type.getSelectedItem().equals("Select Type")) {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                            ((TextView) parent.getChildAt(0)).setTextSize(13);
                        } catch (Exception ignored) {
                        }
                    } else {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                            ((TextView) parent.getChildAt(0)).setTextSize(14);
                        } catch (Exception ignored) {
                        }
                    }
                    if (bundle != null && paper_type.getSelectedItemId() != 0 && studentModelList.size() == 0) {
                        if (PaperType_Name.equalsIgnoreCase("UploadDocument")) {
                            linear_doc.setVisibility(View.VISIBLE);
                            linear_link.setVisibility(View.GONE);
                        } else if (PaperType_Name.equalsIgnoreCase("UploadLink")) {
                            linear_link.setVisibility(View.VISIBLE);
                            linear_doc.setVisibility(View.GONE);
                        }
                        linear_remarks.setVisibility(View.VISIBLE);
                        save_test_paper.setVisibility(View.VISIBLE);
                        edit_test_paper.setVisibility(View.GONE);
                    } else if (bundle != null && paper_type.getSelectedItemId() != 0 && studentModelList.size() > 0) {
                        if (PaperType_Name.equalsIgnoreCase("UploadDocument")) {
                            linear_doc.setVisibility(View.VISIBLE);
                            linear_link.setVisibility(View.GONE);
                        } else if (PaperType_Name.equalsIgnoreCase("UploadLink")) {
                            linear_link.setVisibility(View.VISIBLE);
                            linear_doc.setVisibility(View.GONE);
                        }
                        linear_remarks.setVisibility(View.VISIBLE);
                        save_test_paper.setVisibility(View.GONE);
                        edit_test_paper.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };

    private void selectSpinnerValue(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void requestPermissionForAll() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

            if (!hasPermission) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, 1);
            } else {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), 4);
            }
        } else {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select File"), 4);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == 4) {
            if (resultCode == RESULT_CANCELED) {
                userCancelled();
            } else if (resultCode == RESULT_OK) {
                Uri image = result.getData();
                try {
                    flag = 1;
                    upload_paper = "";
                    InputStream imageStream;
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireContext(), uri);
                    instrumentFileDestination = new File(Path);
                    imageStream = requireActivity().getContentResolver().openInputStream(image);
                    upload_test_paper.setText("Attached");
                    upload_test_paper.setTextColor(context.getResources().getColor(R.color.black));
                    String name = instrumentFileDestination.getName();
                    paper_ext = name.substring(name.lastIndexOf("."));
                    papername = instrumentFileDestination.getName();
                    upload_paper = encodeFileToBase64Binary(instrumentFileDestination);
                } catch (Exception e) {
                    errored();
                }
            } else {
                errored();
            }
        }
    }

    private String encodeFileToBase64Binary(File yourFile) {
        int size = (int) yourFile.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(yourFile));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public void userCancelled() {

    }

    public void errored() {
        Intent intent = new Intent();
        intent.putExtra(ERROR, true);
        intent.putExtra(ERROR_MSG, "Error while opening the image file. Please try again.");
    }
}