package com.example.genius.ui.Marks_Entry_Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.MarksEnterAdapter;
import com.example.genius.Adapter.MarksRegisterAdapter;
import com.example.genius.Model.*;
import com.example.genius.databinding.FragmentMarksEntryListfragmentBinding;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class marks_entry_Listfragment extends Fragment {

    FragmentMarksEntryListfragmentBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> standarditem = new ArrayList<>(), subjectitem = new ArrayList<>(), batchitem = new ArrayList<>(), batchid = new ArrayList<>(),dateitem = new ArrayList<>(),courseitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), subjectid = new ArrayList<>(),dateid = new ArrayList<>(),courseid = new ArrayList<>();
    String[] STANDARDITEM, SUBJECTITEM, BATCHITEM,DATEITEM,COURSEITEM;
    Integer[] STANDARDID, SUBJECTID,COURSEID;
    String SubjectName, BatchTime,BatchId, SubjectId,TestDate,Subject_Date,TestID;
    OnBackPressedCallback callback;
    Long StandardId,courseID;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");
    MarksRegisterAdapter marksRegisterAdapter;
    UserModel userpermission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Marks Master");
        binding = FragmentMarksEntryListfragmentBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        for (UserModel.UserPermission model : userpermission.getPermission()){
            if (model.getPageInfo().getPageID() == 81 && !model.getPackageRightinfo().isCreatestatus()){
                binding.fabContact.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllCourse();
            SelectTestDate();
            SelectSubject();
            selectbatch_time();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        selectStandard();

        binding.fabContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marks_entry_fragment orderplace = new marks_entry_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.courseName.setSelection(0);
                binding.standard.setSelection(0);
                binding.batchTime.setSelection(0);
                binding.testDate.setSelection(0);
                binding.subject.setSelection(0);
            }
        });

        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)) {
                    if (binding.courseName.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                    }else if (binding.standard.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select standard.", Toast.LENGTH_SHORT).show();
                    else if (binding.batchTime.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                    else if (binding.testDate.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Test Date.", Toast.LENGTH_SHORT).show();
                    else if (binding.subject.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                    else {
                        progressBarHelper.showProgressDialog();
                        try {
                            Date d = displaydate.parse(TestDate);
                            Subject_Date = actualdate.format(d);
                        }catch (Exception e){
                        }
                        Call<MarksModel.MarksData> call = apiCalling.GetAllStudentAchieveMarks(Long.parseLong(TestID),Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),
                                Long.parseLong(BatchId),Long.parseLong(SubjectId));
                        call.enqueue(new Callback<MarksModel.MarksData>() {
                            @Override
                            public void onResponse(Call<MarksModel.MarksData> call, Response<MarksModel.MarksData> response) {
                                if (response.isSuccessful()){
                                    MarksModel.MarksData data = response.body();
                                    if (data.isCompleted()){
                                        List<MarksModel> list = data.getData();
                                        if (list != null && list.size() > 0){
                                            binding.noContent.setVisibility(View.GONE);
                                            binding.marksEntryRv.setVisibility(View.VISIBLE);
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                            binding.marksEntryRv.setLayoutManager(linearLayoutManager);
                                            marksRegisterAdapter = new MarksRegisterAdapter(context, list);
                                            marksRegisterAdapter.notifyDataSetChanged();
                                            binding.marksEntryRv.setAdapter(marksRegisterAdapter);
                                        }else {
                                            binding.noContent.setVisibility(View.VISIBLE);
                                            binding.marksEntryRv.setVisibility(View.GONE);
                                        }
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<MarksModel.MarksData> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                home_fragment profileFragment = new home_fragment();
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


    public void GetAllSubject() {
        progressBarHelper.showProgressDialog();
        subjectitem.clear();
        subjectid.clear();
        subjectitem.add("Select Subject");
        subjectid.add(0);
        try {
            Date d = displaydate.parse(TestDate);
            Subject_Date = actualdate.format(d);
        }catch (Exception e){

        }
        Call<SubjectData> call = apiCalling.GetAllSubjectByTestDate(Subject_Date,Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<SubjectData>() {
            @Override
            public void onResponse(Call<SubjectData> call, Response<SubjectData> response) {
                if (response.isSuccessful()) {
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
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<SubjectData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindsubject() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SUBJECTITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.subject.setAdapter(adapter);
        binding.subject.setOnItemSelectedListener(onItemSelectedListener8);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener8 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SubjectName = subjectitem.get(position);
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

    public void GetAllStandard(long coursedetailid) {
        progressBarHelper.showProgressDialog();
        standarditem.clear();
        standardid.clear();
        standarditem.add("Select Standard");
        standardid.add(0);

        Call<BranchClassModel> call = apiCalling.Get_Class_Spinner(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),coursedetailid);
        call.enqueue(new Callback<BranchClassModel>() {
            @Override
            public void onResponse(Call<BranchClassModel> call, Response<BranchClassModel> response) {
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
            public void onFailure(Call<BranchClassModel> call, Throwable t) {
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
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetTestDates()
    {
        progressBarHelper.showProgressDialog();
        dateitem.clear();
        dateid.clear();
        dateitem.add("Test Date");
        dateid.add(0);
        Call<MarksModel.MarksData> call = apiCalling.Get_Test_Marks(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),courseID,StandardId,Integer.parseInt(BatchId));
        call.enqueue(new Callback<MarksModel.MarksData>() {
            @Override
            public void onResponse(Call<MarksModel.MarksData> call, Response<MarksModel.MarksData> response) {
                if (response.isSuccessful()){
                    MarksModel.MarksData data = response.body();
                    if (data.isCompleted() && data != null){
                        List<MarksModel> model = data.getData();
                        for (MarksModel marksModel : model) {

                            String testdate = marksModel.getTestDate();
                            try {
                                Date d = actualdate.parse(testdate);
                                String date = displaydate.format(d);
                                dateitem.add(date);
                            }catch (Exception e){

                            }

                            int id = (int) marksModel.getTestID();
                            dateid.add(id);
                        }

                        DATEITEM = new String[dateitem.size()];
                        DATEITEM = dateitem.toArray(DATEITEM);

                        bindDate();
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<MarksModel.MarksData> call, Throwable t) {

            }
        });
    }

    public void bindDate() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, DATEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.testDate.setAdapter(adapter);
        binding.testDate.setOnItemSelectedListener(onItemSelectedListenerdate);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListenerdate =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TestDate = dateitem.get(position);
                    TestID = dateid.get(position).toString();
                    if (binding.testDate.getSelectedItem().equals("Test Date")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                    if (binding.testDate.getSelectedItemId() != 0){
                        GetAllSubject();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void selectbatch_time() {
        batchitem.clear();
        batchid.clear();
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
                        }
                    } else {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                            ((TextView) parent.getChildAt(0)).setTextSize(14);
                        } catch (Exception e) {
                        }
                    }
                    if (binding.batchTime.getSelectedItemId() != 0){
                        GetTestDates();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };

    public void SelectTestDate()
    {
        dateitem.clear();
        dateitem.add("Test Date");

        DATEITEM = new String[dateitem.size()];
        DATEITEM = dateitem.toArray(DATEITEM);

        bindTestDate();
    }

    public void bindTestDate() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, DATEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.testDate.setAdapter(adapter);
        binding.testDate.setOnItemSelectedListener(onItemSelectedListener80);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener80 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                    ((TextView) parent.getChildAt(0)).setTextSize(13);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void SelectSubject()
    {
        subjectitem.clear();
        subjectitem.add("Select Subject");

        SUBJECTITEM = new String[subjectitem.size()];
        SUBJECTITEM = subjectitem.toArray(SUBJECTITEM);

        bindselectsubject();
    }

    public void bindselectsubject() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SUBJECTITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.subject.setAdapter(adapter);
        binding.subject.setOnItemSelectedListener(onItemSelectedListener90);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener90 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                    ((TextView) parent.getChildAt(0)).setTextSize(13);
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

}