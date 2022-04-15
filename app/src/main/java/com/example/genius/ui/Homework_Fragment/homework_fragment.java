package com.example.genius.ui.Homework_Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.genius.Model.HomeworkByIdData;
import com.example.genius.Model.HomeworkModel;
import com.example.genius.Model.MarksModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.SubjectData;
import com.example.genius.Model.SubjectModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.databinding.FragmentHomeworkFragmentBinding;
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
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@SuppressLint({"SimpleDateFormat", "SetTextI18n"})
public class homework_fragment extends Fragment {

    FragmentHomeworkFragmentBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> standarditem = new ArrayList<>(), subjectitem = new ArrayList<>(), batchitem = new ArrayList<>(), batchid = new ArrayList<>(),courseitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), subjectid = new ArrayList<>(), courseid = new ArrayList<>();
    String[] STANDARDITEM, SUBJECTITEM, BATCHITEM,COURSEITEM;
    Integer[] STANDARDID, SUBJECTID,COURSEID;
    private int year;
    private int month;
    private int day;
    String BatchTime, BranchID, BatchId,indate, filename = "";
    Bundle bundle;
    OnBackPressedCallback callback;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    int flag = 0;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    File instrumentFileDestination;
    Long StandardId,courseID,subjectID;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");
    String FilePath, OriginFileName;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Homework Master Entry");
        binding = FragmentHomeworkFragmentBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));

        Calendar cal2 = Calendar.getInstance();
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        cal2.add(Calendar.DATE, 0);
        indate = dateFormat1.format(cal2.getTime());

        binding.homeworkDate.setText(yesterday());

        bundle = getArguments();
        if (bundle != null) {
            binding.saveHomework.setVisibility(View.GONE);
            binding.editHomework.setVisibility(View.VISIBLE);
            if (bundle.containsKey("HomeworkDate")) {
                try {
                    String date = bundle.getString("HomeworkDate").replace("T00:00:00", "");
                    Date d = actualdate.parse(date);
                    if (d != null) {
                        binding.homeworkDate.setText("" + displaydate.format(d));
                    }
                    indate = date;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (!(bundle.getString("Remarks") == null)) {
                binding.remarks.setText(bundle.getString("Remarks"));
            }
            if (bundle.getString("FileName") != null) {
                binding.attachmentHomework.setText("Attached");
                binding.attachmentHomework.setTextColor(context.getResources().getColor(R.color.black));
                OriginFileName = bundle.getString("FileName");
                FilePath = bundle.getString("FilePath").replace("https://mastermind.org.in","");
            }
            if (bundle.containsKey("TransactionId")) {
                binding.transactionid.setText("" + bundle.getLong("TransactionId"));
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllCourse();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        selectStandard();
        selectSubject();
        selectbatch_time();

        binding.attachmentHomework.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
                    pickImage();
                }
            } else {
                pickImage();
            }
        });

        binding.saveHomework.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (binding.homeworkDate.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Select Homework Date.", Toast.LENGTH_SHORT).show();
                } else if (binding.courseName.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Course.", Toast.LENGTH_SHORT).show();
                } else if (binding.standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (binding.subject.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    Call<HomeworkModel.HomeworkData1> call;
                    BranchModel branch = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                    BranchClassSingleModel.BranchClassData classmodel = new BranchClassSingleModel.BranchClassData(StandardId);
                    BranchSubjectModel.BranchSubjectData subject = new BranchSubjectModel.BranchSubjectData(subjectID);
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, "");
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    HomeworkModel model = new HomeworkModel(0,branch,indate,Integer.parseInt(BatchId),binding.remarks.getText().toString(),
                            OriginFileName,transactionModel,rowStatusModel,FilePath,course,classmodel,subject);
                    String data = new Gson().toJson(model);
                    if (instrumentFileDestination == null) {
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("attachment", "", requestBody);
                        call = apiCalling.HomeworkMaintenance(data,false, uploadfile);
                    } else {
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                        call = apiCalling.HomeworkMaintenance(data,true, uploadfile);
                    }
                    call.enqueue(new Callback<HomeworkModel.HomeworkData1>() {
                        @Override
                        public void onResponse(@NotNull Call<HomeworkModel.HomeworkData1> call, @NotNull Response<HomeworkModel.HomeworkData1> response) {
                            if (response.isSuccessful()) {
                                HomeworkModel.HomeworkData1 data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    homework_Listfragment orderplace = new homework_Listfragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }else {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<HomeworkModel.HomeworkData1> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.editHomework.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (binding.homeworkDate.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Select Homework Date.", Toast.LENGTH_SHORT).show();
                } else if (binding.courseName.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Course.", Toast.LENGTH_SHORT).show();
                } else if (binding.standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (binding.subject.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    Call<HomeworkModel.HomeworkData1> call;
                    BranchModel branch = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                    BranchClassSingleModel.BranchClassData classmodel = new BranchClassSingleModel.BranchClassData(StandardId);
                    BranchSubjectModel.BranchSubjectData subject = new BranchSubjectModel.BranchSubjectData(subjectID);
                    TransactionModel transactionModel = new TransactionModel(Long.parseLong(binding.transactionid.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    HomeworkModel model = new HomeworkModel(bundle.getLong("HomeworkID"),branch,indate,Integer.parseInt(BatchId),binding.remarks.getText().toString(),
                            OriginFileName,transactionModel,rowStatusModel,FilePath,course,classmodel,subject);
                    String data = new Gson().toJson(model);
                    if (instrumentFileDestination != null) {
                        call = apiCalling.HomeworkMaintenance(data,true, MultipartBody.Part.createFormData("", instrumentFileDestination.getName()
                                        , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination)));
                    } else {
                        call = apiCalling.HomeworkMaintenance(data,false, MultipartBody.Part.createFormData("attachment", ""
                                        , RequestBody.create(MediaType.parse("multipart/form-data"), "")));
                    }
                    call.enqueue(new Callback<HomeworkModel.HomeworkData1>() {
                        @Override
                        public void onResponse(@NotNull Call<HomeworkModel.HomeworkData1> call, @NotNull Response<HomeworkModel.HomeworkData1> response) {
                            if (response.isSuccessful()) {
                                HomeworkModel.HomeworkData1 data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    homework_Listfragment orderplace = new homework_Listfragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                } else {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<HomeworkModel.HomeworkData1> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.homeworkDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog picker = new DatePickerDialog(getActivity(),
                    (view, year2, monthOfYear, dayOfMonth) -> {
                        year = year2;
                        month = monthOfYear;
                        day = dayOfMonth;
                        binding.homeworkDate.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                        String as = binding.homeworkDate.getText().toString();
                        Date dt;
                        try {
                            dt = displaydate.parse(as);
                            indate = actualdate.format(Objects.requireNonNull(dt));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }, year, month, day);
            picker.show();
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                homework_Listfragment profileFragment = new homework_Listfragment();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == REQUEST_CODE_PICK_GALLERY) {
            if (resultCode == RESULT_CANCELED) {
                userCancelled();
            } else if (resultCode == RESULT_OK) {
                Uri image = result.getData();
                try {
                    flag = 1;
                    Uri uri = result.getData();
                    String Path = FileUtils.getReadablePathFromUri(requireActivity(), uri);
                    instrumentFileDestination = new File(Objects.requireNonNull(Path));
                    binding.attachmentHomework.setText("Attached");
                    binding.attachmentHomework.setTextColor(context.getResources().getColor(R.color.black));
                    filename = instrumentFileDestination.getName();
                } catch (Exception e) {
                    binding.attachmentHomework.setText("");
                    errored();
                }
            } else {
                errored();
            }
        }
    }

    private void pickImage() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_GALLERY);
        } catch (ActivityNotFoundException ignored) {
        }
    }

    public void userCancelled() {

    }

    public void errored() {
        Intent intent = new Intent();
        intent.putExtra(ERROR, true);
        intent.putExtra(ERROR_MSG, "Error while opening the image file. Please try again.");
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
            int co = courseid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("CourseID"))));
            binding.courseName.setSelection(co);
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
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
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
                        if (list != null && list.size() > 0){
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
        if (bundle != null) {
            int c = standardid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("StandardID"))));
            binding.standard.setSelection(c);
        }
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
                                String std = model.getSubject().getSubjectName();
                                subjectitem.add(std);
                                int stdid = (int) model.getSubject_dtl_id();
                                subjectid.add(stdid);
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
            int b = subjectid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("SubjectID"))));
            binding.subject.setSelection(b);
        }
        binding.subject.setOnItemSelectedListener(onItemSelectedListener8);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener8 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    subjectID = Long.parseLong(subjectid.get(position).toString());
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
        if (bundle != null) {
            if (bundle.containsKey("BatchTimeText")) {
                selectSpinnerValue(binding.batchTime, bundle.getString("BatchTimeText"));
            }
        }
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

    private void selectSpinnerValue(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                spinner.setSelection(i);
                break;
            }
        }
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