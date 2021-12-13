package com.example.genius.ui.BranchClass;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.BranchClassAdapter;
import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.ClassModel;
import com.example.genius.Model.CourceModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchClassFragment extends Fragment {

    View root;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    RecyclerView course_rv;
    Button save_course, delete_course;
    BranchClassAdapter branchCourceAdapter;
    CheckBox checkall;
    SearchableSpinner spinner_course;
    List<String> couseitem = new ArrayList<>();
    List<Long> couseiditem = new ArrayList<>();
    Long[] COURSEID;
    String[] COURSEITEM;
    String course;
    long courseid;
    Bundle bundle = null;
    List<BranchClassSingleModel.BranchClassData> list;
    List<BranchClassSingleModel.BranchClassData> listForBundle;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Branch Class Master");
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_branch_class, container, false);

        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        course_rv = root.findViewById(R.id.course_rv);
        checkall = root.findViewById(R.id.chk_all);
        spinner_course = root.findViewById(R.id.spinner_course);

        bundle = getArguments();
        if (bundle != null) {
            if (Function.checkNetworkConnection(context)) {
                progressBarHelper.showProgressDialog();
                GetAllCourse();
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
            listForBundle = (List<BranchClassSingleModel.BranchClassData>) bundle.getSerializable("COURSE_DTL");
            if (listForBundle.size() > 0) {
                List<ClassModel.ClassData> data = new ArrayList<>();
                for (int i = 0; i < listForBundle.size(); i++) {
                    listForBundle.get(i).setClassModel(new ClassModel.ClassData(listForBundle.get(i).classModel.getClassID()
                            , listForBundle.get(i).classModel.getClassName()
                            , listForBundle.get(i).isClass));
                    data.add(listForBundle.get(i).classModel);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                course_rv.setLayoutManager(linearLayoutManager);
                branchCourceAdapter = new BranchClassAdapter(context, data);
                branchCourceAdapter.notifyDataSetChanged();
                course_rv.setAdapter(branchCourceAdapter);
            }
        } else {
            if (Function.checkNetworkConnection(context)) {
                progressBarHelper.showProgressDialog();
                GetAllClass();
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        }

        checkall.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (branchCourceAdapter != null) {
                if (checkall.isChecked()) {
                    branchCourceAdapter.selectAll();
                } else {
                    branchCourceAdapter.unselectall();
                }
            }
        });

        save_course = root.findViewById(R.id.save_course);
        save_course.setOnClickListener(view -> {
            Save();
        });

        delete_course = root.findViewById(R.id.delete_course);
        delete_course.setOnClickListener(view -> {
            BranchClassListFragment profileFragment = new BranchClassListFragment();
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.nav_host_fragment, profileFragment);
            ft.addToBackStack(null);
            ft.commit();
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                BranchClassListFragment profileFragment = new BranchClassListFragment();
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

    public void GetAllClass() {
        Call<ClassModel> call = apiCalling.GetAllClass();
        call.enqueue(new Callback<ClassModel>() {
            @Override
            public void onResponse(@NotNull Call<ClassModel> call, @NotNull Response<ClassModel> response) {
                if (response.isSuccessful()) {
                    ClassModel data = response.body();
                    if (data != null && data.getCompleted()) {
                        List<ClassModel.ClassData> studentModelList = data.getData();
                        if (studentModelList != null) {
                            if (studentModelList.size() > 0) {
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                course_rv.setLayoutManager(linearLayoutManager);
                                branchCourceAdapter = new BranchClassAdapter(context, studentModelList);
                                branchCourceAdapter.notifyDataSetChanged();
                                course_rv.setAdapter(branchCourceAdapter);
                            }
                        }
                    }
                    GetAllCourse();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ClassModel> call, @NotNull Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void GetAllCourse() {
        couseitem.clear();
        couseiditem.clear();
        couseitem.add("Select Course");
        couseiditem.add(0L);
        Call<BranchCourseModel> call = apiCalling.Get_Course_Spinner(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<BranchCourseModel>() {
            @Override
            public void onResponse(@NotNull Call<BranchCourseModel> call, @NotNull Response<BranchCourseModel> response) {
                if (response.isSuccessful()) {
                    BranchCourseModel data = response.body();
                    if (data != null && data.isCompleted()) {
                        List<BranchCourseModel.BranchCourceData> studentModelList = data.getData();
                        if (studentModelList != null) {
                            for (BranchCourseModel.BranchCourceData singleResponseModel : studentModelList) {
                                if (singleResponseModel.getCourse_dtl_id() != 0 && singleResponseModel.getCourse().getCourseName() != null){
                                    long code = singleResponseModel.getCourse_dtl_id();
                                    String desc = singleResponseModel.getCourse().getCourseName();
                                    couseiditem.add(code);
                                    couseitem.add(desc);
                                }
                            }
                            COURSEITEM = new String[couseitem.size()];
                            COURSEITEM = couseitem.toArray(COURSEITEM);

                            COURSEID = new Long[couseiditem.size()];
                            COURSEID = couseiditem.toArray(COURSEID);
                            bindCourse();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BranchCourseModel> call, @NotNull Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void bindCourse() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, COURSEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_course.setAdapter(adapter);
        spinner_course.setOnItemSelectedListener(reportareaListener);
        if (bundle != null) {
            selectSpinnerValue(spinner_course, bundle.getString("COURSE_NAME"));
        }
    }

    private void selectSpinnerValue(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    AdapterView.OnItemSelectedListener reportareaListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (spinner_course.getSelectedItem().toString().equals("Select Course")) {
                        course = "";
                        courseid = 0;
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(16);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(16);
                        course = couseitem.get(position);
                        courseid = couseiditem.get(position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void Save() {
        if (spinner_course.getSelectedItemId() == 0) {
            Function.showToast(context, "Please select course");
        } else {
            progressBarHelper.showProgressDialog();
            if (Function.checkNetworkConnection(context)) {
                list = new ArrayList<>();
                if (BranchClassAdapter.CourceDataList.size() > 0) {
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    RowStatusModel rowStatusModel = new RowStatusModel(1, "Active");
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    if (bundle == null) {
                        for (int i = 0; i < BranchClassAdapter.CourceDataList.size(); i++) {
                            BranchClassSingleModel.BranchClassData model = new BranchClassSingleModel.BranchClassData(0
                                    , branchModel, BranchClassAdapter.CourceDataList.get(i)
                                    , transactionModel, rowStatusModel, new BranchCourseModel.BranchCourceData(courseid)
                                    , BranchClassAdapter.CourceDataList.get(i).getIscourse());
                            list.add(model);
                        }
                    } else {
                        for (int i = 0; i < BranchClassAdapter.CourceDataList.size(); i++) {
                            BranchClassSingleModel.BranchClassData model = new BranchClassSingleModel.BranchClassData(listForBundle.get(i).Class_dtl_id
                                    , branchModel, BranchClassAdapter.CourceDataList.get(i)
                                    , transactionModel, rowStatusModel, new BranchCourseModel.BranchCourceData(courseid)
                                    , BranchClassAdapter.CourceDataList.get(i).getIscourse());
                            list.add(model);
                        }
                    }
                    BranchClassSingleModel.BranchClassData branch = new BranchClassSingleModel.BranchClassData(list);
                    Call<BranchClassSingleModel> call = apiCalling.BranchClassMaintenance(branch);
                    call.enqueue(new Callback<BranchClassSingleModel>() {
                        @Override
                        public void onResponse(@NotNull Call<BranchClassSingleModel> call, @NotNull Response<BranchClassSingleModel> response) {
                            if (response.isSuccessful()) {
                                BranchClassSingleModel data = response.body();
                                if (data != null) {
                                    if (data.getCompleted()) {
                                        Function.showToast(context, data.getMessage());
                                        BranchClassListFragment profileFragment = new BranchClassListFragment();
                                        FragmentManager fm = requireActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        ft.replace(R.id.nav_host_fragment, profileFragment);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    } else {
                                        Function.showToast(context, data.getMessage());
                                    }
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<BranchClassSingleModel> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        }
    }
}