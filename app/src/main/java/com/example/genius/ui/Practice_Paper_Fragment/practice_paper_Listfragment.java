package com.example.genius.ui.Practice_Paper_Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.*;
import com.example.genius.Model.SubjectData;
import com.example.genius.databinding.PracticePaperListfragmentFragmentBinding;
import com.example.genius.databinding.PracticepaperMasterDeatilListBinding;
import com.example.genius.helper.FileUtils;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.google.gson.Gson;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

@SuppressLint("SetTextI18n")
public class practice_paper_Listfragment extends Fragment {

    PracticePaperListfragmentFragmentBinding binding;
    Context context;
    PracticePaperMaster_Adapter practicePaperMaster_adapter;
    List<String> standarditem = new ArrayList<>(), subjectitem = new ArrayList<>(), batchitem = new ArrayList<>(), batchid = new ArrayList<>(),courseitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), subjectid = new ArrayList<>(),courseid = new ArrayList<>();
    String[] STANDARDITEM, SUBJECTITEM, BATCHITEM,COURSEITEM;
    Integer[] STANDARDID, SUBJECTID, COURESID;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    String BatchTime, BranchID, BatchId, SubjectId,OriginFileName,stdname = "",subname = "",FilePath;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    File instrumentFileDestination;
    int flag = 0;
    Long StandardId,courseID;
    OnBackPressedCallback callback;
    UserModel.PageData userpermission;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Practice Paper Master");
        binding = PracticePaperListfragmentFragmentBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.PageData.class);

        for (UserModel.PageInfoEntity model : userpermission.Data){
            if (model.getPageID() == 36 && !model.Createstatus){
                binding.linearCreatePaper.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllCourse();
            selectbatch_time();
            GetPracticePaperDetails();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        selectStandard();
        selectSubject();

        binding.savePracticePaper.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (binding.courseName.getSelectedItemId() == 0){
                    Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                }else if (binding.standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (binding.subject.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                } else if (binding.batchTime.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                } else if (binding.attachPaper.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Upload Practice Paper.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    BranchModel branch = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                    BranchClassSingleModel.BranchClassData classmodel = new BranchClassSingleModel.BranchClassData(StandardId);
                    BranchSubjectModel.BranchSubjectData branchsubject = new BranchSubjectModel.BranchSubjectData(Long.parseLong(SubjectId));
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, "");
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    PaperData paperData = new PaperData(0,OriginFileName,FilePath);
                    PaperModel model = new PaperModel(0,branch,Integer.parseInt(BatchId),binding.remarks.getText().toString(),rowStatusModel,transactionModel,
                            paperData,course,classmodel,branchsubject);
                    String data = new Gson().toJson(model);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                    MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                    Call<PaperModel.PaperData1> call = apiCalling.PaperMaintenance(data,true,uploadfile);
                    call.enqueue(new Callback<PaperModel.PaperData1>() {
                        @Override
                        public void onResponse(@NotNull Call<PaperModel.PaperData1> call, @NotNull Response<PaperModel.PaperData1> response) {
                            if (response.isSuccessful()) {
                                PaperModel.PaperData1 data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    GetPracticePaperDetails();
                                    binding.courseName.setSelection(0);
                                    binding.subject.setSelection(0);
                                    binding.standard.setSelection(0);
                                    binding.batchTime.setSelection(0);
                                    binding.attachPaper.setText("");
                                    binding.remarks.setText("");
                                }else {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<PaperModel.PaperData1> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.editPracticePaper.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (binding.courseName.getSelectedItemId() == 0){
                    Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                } else if (binding.standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (binding.subject.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                } else if (binding.batchTime.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                } else if (binding.attachPaper.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Upload Practice Paper.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    BranchModel branch = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                    BranchClassSingleModel.BranchClassData classmodel = new BranchClassSingleModel.BranchClassData(StandardId);
                    BranchSubjectModel.BranchSubjectData branchsubject = new BranchSubjectModel.BranchSubjectData(Long.parseLong(SubjectId));
                    TransactionModel transactionModel = new TransactionModel(Long.parseLong(binding.id.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    PaperData paperData = new PaperData(Long.parseLong(binding.uniqId.getText().toString()),OriginFileName,FilePath);
                    PaperModel model = new PaperModel(Long.parseLong(binding.paperId.getText().toString()),branch,Integer.parseInt(BatchId),binding.remarks.getText().toString(),rowStatusModel,transactionModel,
                            paperData,course,classmodel,branchsubject);
                    String data = new Gson().toJson(model);
                    Call<PaperModel.PaperData1> call;
                    if (instrumentFileDestination != null) {
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                        call = apiCalling.PaperMaintenance(data,true,uploadfile);
                    }else {
                        RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
                        call = apiCalling.PaperMaintenance(data,false,uploadfile);
                    }
                    call.enqueue(new Callback<PaperModel.PaperData1>() {
                        @Override
                        public void onResponse(@NotNull Call<PaperModel.PaperData1> call, @NotNull Response<PaperModel.PaperData1> response) {
                            if (response.isSuccessful()) {
                                PaperModel.PaperData1 data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    GetPracticePaperDetails();
                                    stdname = "";
                                    subname = "";
                                    binding.courseName.setSelection(0);
                                    binding.subject.setSelection(0);
                                    binding.standard.setSelection(0);
                                    binding.batchTime.setSelection(0);
                                    binding.attachPaper.setText("");
                                    binding.remarks.setText("");
                                    binding.savePracticePaper.setVisibility(View.VISIBLE);
                                    binding.editPracticePaper.setVisibility(View.GONE);
                                }else {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<PaperModel.PaperData1> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.attachPaper.setOnClickListener(v -> requestPermissionForAll());

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                home_fragment profileFragment = new home_fragment();
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

    private void requestPermissionForAll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(requireActivity(),
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
                try {
                    flag = 1;
                    Uri uri = result.getData();
                    String Path = FileUtils.getReadablePathFromUri(requireContext(), uri);
                    if (Path != null) {
                        instrumentFileDestination = new File(Path);
                    }
                    binding.attachPaper.setText("Attached");
                    binding.attachPaper.setTextColor(context.getResources().getColor(R.color.black));
                } catch (Exception e) {
                    errored();
                }
            } else {
                errored();
            }
        }
    }

    public void userCancelled() {

    }

    public void errored() {
        Intent intent = new Intent();
        intent.putExtra(ERROR, true);
        intent.putExtra(ERROR_MSG, "Error while opening the image file. Please try again.");
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
                                int id = Integer.parseInt(String.valueOf(model.getCourse_dtl_id()));
                                courseid.add(id);
                            }
                            COURSEITEM = new String[courseitem.size()];
                            COURSEITEM = courseitem.toArray(COURSEITEM);

                            COURESID = new Integer[courseid.size()];
                            COURESID = courseid.toArray(COURESID);

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
                        if (list != null && list.size() > 0){
                            for (BranchClassSingleModel.BranchClassData model : list) {
                                String std = model.getClassModel().getClassName();
                                standarditem.add(std);
                                int id = Integer.parseInt(String.valueOf(model.getClass_dtl_id()));
                                standardid.add(id);
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
        if (stdname != ""){
            selectSpinnerValue(binding.standard,stdname);
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
                                String subject = model.getSubject().getSubjectName();
                                subjectitem.add(subject);
                                int id = Integer.parseInt(String.valueOf(model.getSubject_dtl_id()));
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
        if (subname != "") {
            selectSpinnerValue(binding.subject,subname);
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
        batchid.clear();
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

    public void GetPracticePaperDetails() {
        Call<PaperData> call = apiCalling.GetAllPaperWithoutContent(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<PaperData>() {
            @Override
            public void onResponse(@NotNull Call<PaperData> call, @NotNull Response<PaperData> response) {
                if (response.isSuccessful()) {
                    progressBarHelper.hideProgressDialog();
                    PaperData paperData = response.body();
                    if (paperData.isCompleted()) {
                        List<PaperModel> paperModelList = paperData.getData();
                        if (paperModelList != null) {
                            if (paperModelList.size() > 0) {
                                binding.text.setVisibility(View.VISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                binding.practicePaperRv.setLayoutManager(linearLayoutManager);
                                practicePaperMaster_adapter = new PracticePaperMaster_Adapter(context, paperModelList);
                                practicePaperMaster_adapter.notifyDataSetChanged();
                                binding.practicePaperRv.setAdapter(practicePaperMaster_adapter);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<PaperData> call, @NotNull Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public class PracticePaperMaster_Adapter extends RecyclerView.Adapter<PracticePaperMaster_Adapter.ViewHolder> {

        Context context;
        List<PaperModel> paperModels;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        UserModel.PageData userpermission;
        long downloadID;
        String Name;

        public PracticePaperMaster_Adapter(Context context, List<PaperModel> paperModels) {
            this.context = context;
            this.paperModels = paperModels;
        }

        @NonNull
        @Override
        public PracticePaperMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(PracticepaperMasterDeatilListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull PracticePaperMaster_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            for (UserModel.PageInfoEntity model : userpermission.Data){
                if (model.getPageID() == 36){
                    if (!model.Createstatus){
                        holder.binding.paperEdit.setVisibility(View.GONE);
                    }
                    if (!model.Deletestatus){
                        holder.binding.paperDelete.setVisibility(View.GONE);
                    }
                    if (!model.Createstatus && !model.Deletestatus){
                        holder.binding.paperEdit.setVisibility(View.GONE);
                        holder.binding.paperDelete.setVisibility(View.GONE);
                    }
                }
            }
            holder.binding.course.setText(paperModels.get(position).getBranchCourse().getCourse().getCourseName());
            holder.binding.standard.setText(paperModels.get(position).getBranchClass().getClassModel().getClassName());
            holder.binding.subject.setText(paperModels.get(position).getBranchSubject().getSubject().getSubjectName());
            int a = paperModels.get(position).getBatchTypeID();
            if (a == 1) {
                holder.binding.batchTime.setText("Morning");
            } else if (a == 2) {
                holder.binding.batchTime.setText("AfterNoon");
            } else {
                holder.binding.batchTime.setText("Evening");
            }

            holder.binding.paperEdit.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Edit Practice Paper?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v13 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v14 -> {
                    dialog.dismiss();
                    progressBarHelper.showProgressDialog();
                    Call<PaperByIdData> call = apiCalling.GetPaperByPaperID(paperModels.get(position).getPaperID());
                    call.enqueue(new Callback<PaperByIdData>() {
                        @Override
                        public void onResponse(@NotNull Call<PaperByIdData> call, @NotNull Response<PaperByIdData> response) {
                            if (response.isSuccessful()) {
                                PaperByIdData paperData = response.body();
                                if (paperData.Completed) {
                                    PaperModel paperModelList = paperData.Data;
                                    if (paperModelList != null) {
                                        binding.savePracticePaper.setVisibility(View.GONE);
                                        binding.editPracticePaper.setVisibility(View.VISIBLE);
                                        courseID = paperModels.get(position).getBranchCourse().getCourse_dtl_id();
                                        StandardId = paperModels.get(position).getBranchClass().getClass_dtl_id();
                                        SubjectId = String.valueOf(paperModels.get(position).getBranchSubject().getSubject_dtl_id());
                                        selectSpinnerValue(binding.courseName, paperModels.get(position).getBranchCourse().getCourse().getCourseName());
                                        stdname = paperModels.get(position).getBranchClass().getClassModel().getClassName();
                                        subname = paperModels.get(position).getBranchSubject().getSubject().getSubjectName();
                                        int de = batchid.indexOf(String.valueOf(paperModels.get(position).getBatchTypeID()));
                                        binding.batchTime.setSelection(de);
                                        FilePath = paperModelList.getPaperData().getFilePath().replace("https://mastermind.org.in","");
                                        OriginFileName = paperModels.get(position).getPaperData().getPaperPath();
                                        binding.attachPaper.setText("Attached");
                                        binding.attachPaper.setTextColor(context.getResources().getColor(R.color.black));
                                        binding.uniqId.setText("" + paperModels.get(position).getPaperData().getUniqueID());
                                        binding.paperId.setText("" + paperModels.get(position).getPaperID());
                                        binding.id.setText("" + paperModels.get(position).getTransaction().getTransactionId());
                                        binding.remarks.setText(paperModels.get(position).getRemarks());
                                        binding.paperScroll.scrollTo(0, 0);
                                        binding.paperScroll.fullScroll(View.FOCUS_UP);
                                    }
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<PaperByIdData> call, @NotNull Throwable t) {
                            Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                });
                dialog.show();
            });

            holder.binding.paperDelete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                TextView btn_delete = dialogView.findViewById(R.id.btn_delete);
                TextView title = dialogView.findViewById(R.id.title);
                ImageView image = dialogView.findViewById(R.id.image);
                image.setImageResource(R.drawable.delete);
                title.setText("Are you sure that you want to delete this Practice Paper?");
                AlertDialog dialog = builder.create();

                btn_cancel.setOnClickListener(v15 -> dialog.dismiss());

                btn_delete.setOnClickListener(v16 -> {
                    dialog.dismiss();
                    if (Function.checkNetworkConnection(context)) {
                        progressBarHelper.showProgressDialog();
                        Call<CommonModel> call = apiCalling.RemovePaper(paperModels.get(position).getPaperID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        call.enqueue(new Callback<CommonModel>() {
                            @Override
                            public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                                if (response.isSuccessful()) {
                                    CommonModel model = response.body();
                                    if (model.isCompleted()) {
                                        if (model.isData()) {
                                            Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                            paperModels.remove(position);
                                            notifyItemRemoved(position);
                                            notifyDataSetChanged();
                                        }else {
                                            Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                progressBarHelper.hideProgressDialog();
                            }

                            @Override
                            public void onFailure(@NotNull Call<CommonModel> call, @NotNull Throwable t) {
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                                progressBarHelper.hideProgressDialog();
                            }
                        });
                    } else {
                        Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            });

            holder.binding.paperDownload.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Download Document?");
                image.setImageResource(R.drawable.download);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v12 -> {
                    dialog.dismiss();
                    String filetype = paperModels.get(position).getPaperData().getFilePath();
                    Toast.makeText(context, "Download Started..", Toast.LENGTH_SHORT).show();
                    DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(filetype);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    Name = paperModels.get(position).getPaperData().getPaperPath();
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/AshirvadStudyCircle/" + Name);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    downloadID = dm.enqueue(request);
                    context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                });
                dialog.show();
            });
        }

        @Override
        public int getItemCount() {
            return paperModels.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            PracticepaperMasterDeatilListBinding binding;

            public ViewHolder(@NonNull PracticepaperMasterDeatilListBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;
                userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.PageData.class);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }

        private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context1, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadID == id) {
                    Toast.makeText(context, "Download " + Name + " Completed And Stored In AshirvadStudyCircle Folder...", Toast.LENGTH_LONG).show();
                }
            }
        };
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

    private void selectSpinnerValue(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}