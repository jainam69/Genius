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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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


import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchClassModel;
import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.BranchSubjectModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.SubjectData;
import com.example.genius.Model.SubjectModel;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.TestScheduleModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Model.UploadPaperData;
import com.example.genius.Model.UploadPaperModel;
import com.example.genius.databinding.TestScheduleFragmentBinding;
import com.example.genius.helper.FileUtils;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.google.gson.Gson;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@SuppressLint({"SimpleDateFormat", "SetTextI18n"})
public class test_schedule_fragment extends Fragment {

    TestScheduleFragmentBinding binding;
    Context context;
    List<String> subjectitem = new ArrayList<>(),typeitem = new ArrayList<>(), typeid = new ArrayList<>(),batchitem = new ArrayList<>(), batchid = new ArrayList<>(),
            standarditem = new ArrayList<>(),courseitem = new ArrayList<>();
    List<Integer> subjectid = new ArrayList<>(),standardid = new ArrayList<>(),courseid = new ArrayList<>();
    String[] SUBJECTITEM,TYPEITEM,BATCHITEM,STANDARDITEM,COURSEITEM;
    Integer[] SUBJECTID,STANDARDID,COURSEID;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    String format, BatchTime, SubjectId, BatchId, indate, PaperType_Id, PaperType_Name, upload_paper, paper_ext, papername,OriginFileName,status,FilePath;
    private int year;
    private int month;
    private int day;
    Bundle bundle;
    long StandardId,courseID,a, b, c,testid = 0;
    OnBackPressedCallback callback;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    File instrumentFileDestination;
    int flag = 0, type,select,statusid;
    RadioButton rb2;
    List<UploadPaperModel> studentModelList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Test Paper Master Entry");
        binding = TestScheduleFragmentBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        Calendar cal2 = Calendar.getInstance();
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        cal2.add(Calendar.DATE, 0);
        indate = dateFormat1.format(cal2.getTime());

        binding.testDate.setText(yesterday());

        bundle = getArguments();
        if (bundle != null) {
            binding.linearSave.setVisibility(View.GONE);
            binding.linearEdit.setVisibility(View.VISIBLE);
            if (bundle.containsKey("TestDate")) {
                try {
                    Date d = actualdate.parse(bundle.getString("TestDate").replace("T00:00:00", ""));
                    binding.testDate.setText("" + displaydate.format(d));
                    indate = bundle.getString("TestDate").replace("T00:00:00", "");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (bundle.containsKey("StartTime")) {
                binding.startTime.setText(bundle.getString("StartTime"));
            }
            if (bundle.containsKey("EndTime")) {
                binding.endTime.setText(bundle.getString("EndTime"));
            }
            if (bundle.containsKey("TestMarks")) {
                binding.marks.setText("" + bundle.getDouble("TestMarks"));
            }
            if (bundle.containsKey("TestRemarks")) {
                binding.remarks.setText(bundle.getString("TestRemarks"));
            }
            if (bundle.containsKey("TestID")) {
                testid = bundle.getLong("TestID");
                binding.id.setText("" + bundle.getLong("TestID"));
                if (Function.isNetworkAvailable(context)) {
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
                                            statusid = studentModelList.get(0).getRowStatus().getRowStatusId();
                                            if (type == 1) {
                                                binding.paperType.setSelection(1);
                                                binding.uploadTestPaper.setText("Attached");
                                                binding.uploadTestPaper.setTextColor(context.getResources().getColor(R.color.black));
                                                binding.linearDoc.setVisibility(View.VISIBLE);
                                            } else {
                                                binding.paperType.setSelection(2);
                                                binding.uploadLink.setText(studentModelList.get(0).getDocLink());
                                                binding.linearLink.setVisibility(View.VISIBLE);
                                            }
                                            binding.linearRemarks.setVisibility(View.VISIBLE);
                                            binding.editTestPaper.setVisibility(View.VISIBLE);
                                            binding.saveTestPaper.setVisibility(View.GONE);
                                            if (studentModelList.get(0).getRemarks() != null){
                                                binding.paperRemarks.setText(studentModelList.get(0).getRemarks());
                                            }
                                            if (!studentModelList.get(0).getFilePath().equals("") && studentModelList.get(0).getFilePath() != null) {
                                                OriginFileName = studentModelList.get(0).getFileName();
                                                FilePath = studentModelList.get(0).getFilePath().replace("https://mastermind.org.in","");
                                            }else {
                                                binding.uploadLink.setText(studentModelList.get(0).getDocLink());
                                            }
                                            if (statusid == 1){
                                                binding.active.setChecked(true);
                                                binding.inactive.setChecked(false);
                                            }
                                            if (statusid == 2){
                                                binding.active.setChecked(false);
                                                binding.inactive.setChecked(true);
                                            }
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

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            selectbatch_time();
            GetAllCourse();
            if (bundle == null) {
                GetPaperType();
            }
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        binding.statusRg.setOnCheckedChangeListener((group, checkedId) -> {
            rb2 = binding.getRoot().findViewById(checkedId);
            status = rb2.getText().toString();
        });
        select = binding.statusRg.getCheckedRadioButtonId();
        rb2 = binding.getRoot().findViewById(select);
        status = rb2.getText().toString();

        selectStandard();
        selectSubject();

        binding.testDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog picker = new DatePickerDialog(getActivity(),
                    (view, year2, monthOfYear, dayOfMonth) -> {
                        year = year2;
                        month = monthOfYear;
                        day = dayOfMonth;
                        binding.testDate.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                        String as = binding.testDate.getText().toString();
                        Date dt = null;
                        try {
                            dt = displaydate.parse(as);
                            indate = actualdate.format(dt);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }, year, month, day);
            picker.show();
        });

        binding.startTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, hourOfDay, minute1) -> {
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
                        binding.startTime.setText(hourOfDay + ":" + minute1 + " " + format);
                    }, hour, minute, false);
            timePickerDialog.show();
        });

        binding.endTime.setOnClickListener(v -> {
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
                            binding.endTime.setText(hourOfDay + ":" + minute + " " + format);
                        }
                    }, hour, minute, false);
            timePickerDialog.show();
        });

        binding.saveTestschedule.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (binding.courseName.getSelectedItemId() == 0){
                    Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                }else if (binding.standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (binding.batchTime.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                } else if (binding.subject.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                } else if (binding.marks.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter Total Marks.", Toast.LENGTH_SHORT).show();
                } else if (binding.testDate.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter Test Date.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                    BranchClassSingleModel.BranchClassData branchclass = new BranchClassSingleModel.BranchClassData(StandardId);
                    BranchSubjectModel.BranchSubjectData subject = new BranchSubjectModel.BranchSubjectData(Long.parseLong(SubjectId));
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    TestScheduleModel testScheduleModel = new TestScheduleModel("Demo", branchModel,Integer.parseInt(BatchId), BatchTime,
                            Double.parseDouble(binding.marks.getText().toString()), indate, binding.startTime.getText().toString(), binding.endTime.getText().toString(),
                            binding.remarks.getText().toString(), rowStatusModel, transactionModel,course,branchclass,subject);
                    Call<TestScheduleModel.TestScheduleData1> call = apiCalling.TestMaintenance(testScheduleModel);
                    call.enqueue(new Callback<TestScheduleModel.TestScheduleData1>() {
                        @Override
                        public void onResponse(@NotNull Call<TestScheduleModel.TestScheduleData1> call, @NotNull Response<TestScheduleModel.TestScheduleData1> response) {
                            if (response.isSuccessful()) {
                                TestScheduleModel.TestScheduleData1 data = response.body();
                                if (data.isCompleted()) {
                                    TestScheduleModel notimodel = data.getData();
                                    if (notimodel.getTestID() > 0) {
                                        if (binding.paperType.getSelectedItemId() == 0) {
                                            Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                            test_Listfragment orderplace = new test_Listfragment();
                                            FragmentManager fragmentManager = getFragmentManager();
                                            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                                            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();
                                        } else {
                                            Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                            binding.saveTestschedule.setVisibility(View.GONE);
                                            a = notimodel.getTestID();
                                            if (PaperType_Name.equalsIgnoreCase("UploadDocument")) {
                                                binding.linearDoc.setVisibility(View.VISIBLE);
                                            } else {
                                                binding.linearLink.setVisibility(View.VISIBLE);
                                            }
                                            binding.linearRemarks.setVisibility(View.VISIBLE);
                                            binding.linearStatus.setVisibility(View.VISIBLE);
                                            binding.saveTestPaper.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.editTestschedule.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (binding.courseName.getSelectedItemId() == 0){
                    Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                }else if (binding.standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (binding.batchTime.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                } else if (binding.subject.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                } else if (binding.marks.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter Total Marks.", Toast.LENGTH_SHORT).show();
                } else if (binding.testDate.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter Test Date.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                    BranchClassSingleModel.BranchClassData branchclass = new BranchClassSingleModel.BranchClassData(StandardId);
                    BranchSubjectModel.BranchSubjectData subject = new BranchSubjectModel.BranchSubjectData(Long.parseLong(SubjectId));
                    TransactionModel transactionModel = new TransactionModel(bundle.getLong("TransactionId"), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    TestScheduleModel testScheduleModel = new TestScheduleModel(Long.parseLong(binding.id.getText().toString()), "Demo", branchModel, Integer.parseInt(BatchId), BatchTime,
                            Double.parseDouble(binding.marks.getText().toString()), indate, binding.startTime.getText().toString(), binding.endTime.getText().toString(),
                            binding.remarks.getText().toString(), rowStatusModel, transactionModel,course,branchclass,subject);
                    Call<TestScheduleModel.TestScheduleData1> call = apiCalling.TestMaintenance(testScheduleModel);
                    call.enqueue(new Callback<TestScheduleModel.TestScheduleData1>() {
                        @Override
                        public void onResponse(@NotNull Call<TestScheduleModel.TestScheduleData1> call, @NotNull Response<TestScheduleModel.TestScheduleData1> response) {
                            if (response.isSuccessful()) {
                                TestScheduleModel.TestScheduleData1 data = response.body();
                                if (data.isCompleted()) {
                                    TestScheduleModel notimodel = data.getData();
                                    if (notimodel.getTestID() > 0){
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        test_Listfragment orderplace = new test_Listfragment();
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    }else {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.saveTestPaper.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (PaperType_Name.equals("UploadDocument") && instrumentFileDestination == null) {
                    Toast.makeText(context, "Please upload document.", Toast.LENGTH_SHORT).show();
                } else if (PaperType_Name.equals("UploadLink") && binding.uploadLink.getText().toString().equals("")) {
                    Toast.makeText(context, "Please upload link.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    if (status.equals("Active")){
                        statusid = 1;
                    }
                    if (status.equals("InActive")){
                        statusid = 2;
                    }
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, "");
                    RowStatusModel rowStatusModel = new RowStatusModel(statusid);
                    Call<UploadPaperModel.UploadPaperData1> call;
                    if (bundle != null) {
                        UploadPaperModel model = new UploadPaperModel(0,Long.parseLong(binding.id.getText().toString()),Integer.parseInt(PaperType_Id),OriginFileName,FilePath,
                                binding.uploadLink.getText().toString(),binding.paperRemarks.getText().toString(),rowStatusModel,transactionModel);
                        String data = new Gson().toJson(model);
                        if (PaperType_Name.equalsIgnoreCase("UploadDocument")) {
                            call = apiCalling.TestPaperMaintenance(data, true
                                    , MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination)));
                        } else {
                            call = apiCalling.TestPaperMaintenance(data, false
                                    , MultipartBody.Part.createFormData("attachment", "", RequestBody.create(MediaType.parse("multipart/form-data"), "")));
                        }
                    } else {
                        UploadPaperModel model = new UploadPaperModel(0,a,Integer.parseInt(PaperType_Id),OriginFileName,FilePath,
                                binding.uploadLink.getText().toString(),binding.paperRemarks.getText().toString(),rowStatusModel,transactionModel);
                        String data = new Gson().toJson(model);
                        if (PaperType_Name.equalsIgnoreCase("UploadDocument")) {
                            call = apiCalling.TestPaperMaintenance(data, true
                                    , MultipartBody.Part.createFormData("", instrumentFileDestination.getName()
                                            , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination)));
                        } else {
                            call = apiCalling.TestPaperMaintenance(data, false
                                    , MultipartBody.Part.createFormData("attachment", ""
                                            , RequestBody.create(MediaType.parse("multipart/form-data"), "")));
                        }
                    }
                    call.enqueue(new Callback<UploadPaperModel.UploadPaperData1>() {
                        @Override
                        public void onResponse(@NotNull Call<UploadPaperModel.UploadPaperData1> call, @NotNull Response<UploadPaperModel.UploadPaperData1> response) {
                            if (response.isSuccessful()) {
                                UploadPaperModel.UploadPaperData1 data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    test_Listfragment orderplace = new test_Listfragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
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
                        public void onFailure(@NotNull Call<UploadPaperModel.UploadPaperData1> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.editTestPaper.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (PaperType_Name.equals("UploadDocument") && binding.uploadTestPaper.getText().toString().equals("")) {
                    Toast.makeText(context, "Please upload document.", Toast.LENGTH_SHORT).show();
                } else if (PaperType_Name.equals("UploadLink") && binding.uploadLink.getText().toString().equals("")) {
                    Toast.makeText(context, "Please upload link.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    if (status.equals("Active")){
                        statusid = 1;
                    }
                    if (status.equals("InActive")){
                        statusid = 2;
                    }
                    Call<UploadPaperModel.UploadPaperData1> call;
                    TransactionModel transactionModel = new TransactionModel(bundle.getLong("TransactionId"), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    RowStatusModel rowStatusModel = new RowStatusModel(statusid);
                    UploadPaperModel model = new UploadPaperModel(b,c,Integer.parseInt(PaperType_Id),OriginFileName,FilePath,
                            binding.uploadLink.getText().toString(),binding.paperRemarks.getText().toString(),rowStatusModel,transactionModel);
                    String data = new Gson().toJson(model);
                    if (PaperType_Name.equalsIgnoreCase("UploadDocument")) {
                        if (instrumentFileDestination != null) {
                            call = apiCalling.TestPaperMaintenance(data, true
                                    , MultipartBody.Part.createFormData("", instrumentFileDestination.getName()
                                            , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination)));
                        } else {
                            call = apiCalling.TestPaperMaintenance(data, false
                                    , MultipartBody.Part.createFormData("attachment", ""
                                            , RequestBody.create(MediaType.parse("multipart/form-data"), "")));
                        }
                    } else {
                        call = apiCalling.TestPaperMaintenance(data, false
                                , MultipartBody.Part.createFormData("attachment", ""
                                        , RequestBody.create(MediaType.parse("multipart/form-data"), "")));
                    }
                    call.enqueue(new Callback<UploadPaperModel.UploadPaperData1>() {
                        @Override
                        public void onResponse(@NotNull Call<UploadPaperModel.UploadPaperData1> call, @NotNull Response<UploadPaperModel.UploadPaperData1> response) {
                            if (response.isSuccessful()) {
                                UploadPaperModel.UploadPaperData1 data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    test_Listfragment orderplace = new test_Listfragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
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
                        public void onFailure(@NotNull Call<UploadPaperModel.UploadPaperData1> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.uploadTestPaper.setOnClickListener(v -> requestPermissionForAll());

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
                        if (list != null && list.size() > 0){
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
        if (bundle != null) {
            int b = courseid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("Course"))));
            binding.courseName.setSelection(b);
        }
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
        if (bundle != null) {
            int b = standardid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("Standard"))));
            binding.standard.setSelection(b);
        }
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
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                    if (binding.standard.getSelectedItemId() != 0){
                        GetAllSubject(StandardId,courseID);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetAllSubject(long classdetailid,long coursedetailid) {
        progressBarHelper.showProgressDialog();
        subjectitem.clear();
        subjectid.clear();
        subjectitem.add("Select Subject");
        subjectid.add(0);

        Call<BranchSubjectModel> call = apiCalling.Get_All_Subject_DDL(classdetailid,Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),coursedetailid);
        call.enqueue(new Callback<BranchSubjectModel>() {
            @Override
            public void onResponse(@NotNull Call<BranchSubjectModel> call, @NotNull Response<BranchSubjectModel> response) {
                if (response.isSuccessful()) {
                    BranchSubjectModel data = response.body();
                    if (data.isCompleted()) {
                        List<BranchSubjectModel.BranchSubjectData> list = data.getData();
                        if (list != null && list.size() > 0){
                            for (BranchSubjectModel.BranchSubjectData model : list) {
                                String subject = model.getSubject().getSubjectName();
                                subjectitem.add(subject);
                                int id = (int) model.getSubject_dtl_id();
                                subjectid.add(id);
                            }
                            SUBJECTITEM = new String[subjectitem.size()];
                            SUBJECTITEM = subjectitem.toArray(SUBJECTITEM);

                            SUBJECTID = new Integer[subjectid.size()];
                            SUBJECTID = subjectid.toArray(SUBJECTID);

                            bindsubject();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BranchSubjectModel> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindsubject() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SUBJECTITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.subject.setAdapter(adapter);
        if (bundle != null) {
            int c = subjectid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("TestSubject"))));
            binding.subject.setSelection(c);
        }
        binding.subject.setOnItemSelectedListener(onItemSelectedListener8);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener8 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SubjectId = subjectid.get(position).toString();
                    if (binding.subject.getSelectedItem().equals("Select Subject")) {
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
        binding.batchTime.setAdapter(adapter);
        binding.batchTime.setOnItemSelectedListener(onItemSelectedListener77);
        if (bundle != null) {
            int a = batchid.indexOf(String.valueOf(bundle.getInt("BatchTime")));
            binding.batchTime.setSelection(a);
        }
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
        binding.paperType.setAdapter(adapter);
        if (bundle != null && type == 1) {
            binding.paperType.setSelection(1);
        } else if (bundle != null && type == 2) {
            binding.paperType.setSelection(2);
        }
        binding.paperType.setOnItemSelectedListener(onItemSelectedListener78);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener78 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    PaperType_Name = typeitem.get(position);
                    PaperType_Id = typeid.get(position);
                    if (binding.paperType.getSelectedItem().equals("Select Type")) {
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
                    if (binding.paperType.getSelectedItemId() != 0){
                        if(testid == 0){
                            Toast.makeText(context, "Please Create Test Schedule First.", Toast.LENGTH_LONG).show();
                        }
                    }
                    if (bundle != null && binding.paperType.getSelectedItemId() != 0 && studentModelList.size() == 0) {
                        if (PaperType_Name.equalsIgnoreCase("UploadDocument")) {
                            binding.linearDoc.setVisibility(View.VISIBLE);
                            binding.linearLink.setVisibility(View.GONE);
                        } else if (PaperType_Name.equalsIgnoreCase("UploadLink")) {
                            binding.linearLink.setVisibility(View.VISIBLE);
                            binding.linearDoc.setVisibility(View.GONE);
                        }
                        binding.linearRemarks.setVisibility(View.VISIBLE);
                        binding.linearStatus.setVisibility(View.VISIBLE);
                        binding.saveTestPaper.setVisibility(View.VISIBLE);
                        binding.editTestPaper.setVisibility(View.GONE);
                    } else if (bundle != null && binding.paperType.getSelectedItemId() != 0 && studentModelList.size() > 0) {
                        if (PaperType_Name.equalsIgnoreCase("UploadDocument")) {
                            binding.linearDoc.setVisibility(View.VISIBLE);
                            binding.linearLink.setVisibility(View.GONE);
                        } else if (PaperType_Name.equalsIgnoreCase("UploadLink")) {
                            binding.linearLink.setVisibility(View.VISIBLE);
                            binding.linearDoc.setVisibility(View.GONE);
                        }
                        binding.linearRemarks.setVisibility(View.VISIBLE);
                        binding.linearStatus.setVisibility(View.VISIBLE);
                        binding.saveTestPaper.setVisibility(View.GONE);
                        binding.editTestPaper.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };

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
                    Uri uri = result.getData();
                    String Path = FileUtils.getReadablePathFromUri(requireContext(), uri);
                    instrumentFileDestination = new File(Path);
                    binding.uploadTestPaper.setText("Attached");
                    binding.uploadTestPaper.setTextColor(context.getResources().getColor(R.color.black));
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

    public String encodeDecode(String text) {
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replace("\n", "");
    }

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

    public void selectSubject() {
        subjectitem.clear();
        subjectid.clear();
        subjectitem.add("Select Subject");
        subjectid.add(0);

        SUBJECTITEM = new String[subjectitem.size()];
        SUBJECTITEM = subjectitem.toArray(SUBJECTITEM);

        bindsub();
    }

    public void bindsub() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SUBJECTITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.subject.setAdapter(adapter);
        binding.subject.setOnItemSelectedListener(onItemSelectedListener8);
    }

    public static String yesterday() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        cal.add(Calendar.DATE, 0);
        return dateFormat.format(cal.getTime());
    }
}