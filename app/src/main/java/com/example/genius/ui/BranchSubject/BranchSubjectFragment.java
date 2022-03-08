package com.example.genius.ui.BranchSubject;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.BranchClassAdapter;
import com.example.genius.Adapter.BranchSubjectAapter;
import com.example.genius.Model.BranchClassModel;
import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.BranchSubjectModel;
import com.example.genius.Model.BranchSubjectSingleModel;
import com.example.genius.Model.ClassModel;
import com.example.genius.Model.CourceModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.SuperAdminSubjectModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.BranchClass.BranchClassListFragment;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchSubjectFragment extends Fragment {

    View root;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    RecyclerView course_rv;
    Button save_course, delete_course;
    CheckBox checkall;
    SearchableSpinner spinner_course, spinner_class;
    List<String> couseitem = new ArrayList<>();
    List<Long> couseiditem = new ArrayList<>();
    List<String> classitem = new ArrayList<>();
    List<Long> classiditem = new ArrayList<>();
    Long[] COURSEID;
    String[] COURSEITEM;
    Long[] CLASSID;
    String[] CLASSITEM;
    String course;
    long courseid;
    String class_name;
    long classid;
    Bundle bundle = null;
    List<BranchSubjectModel.BranchSubjectData> list;
    List<BranchSubjectModel.BranchSubjectData> listForBundle;
    BranchSubjectAapter branchCourceAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Subject Master");
        root = inflater.inflate(R.layout.fragment_branch_subject, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        course_rv = root.findViewById(R.id.course_rv);
        checkall = root.findViewById(R.id.chk_all);
        spinner_course = root.findViewById(R.id.spinner_course);
        spinner_class = root.findViewById(R.id.spinner_class);

        bundle = getArguments();
        if (bundle != null) {
            if (Function.isNetworkAvailable(context)) {
                progressBarHelper.showProgressDialog();
                GetAllCourse();
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
            listForBundle = (List<BranchSubjectModel.BranchSubjectData>) bundle.getSerializable("COURSE_DTL");
            if (listForBundle.size() > 0) {
                List<BranchSubjectModel.BranchSubjectData> data = new ArrayList<>();
                for (int i = 0; i < listForBundle.size(); i++) {
                    listForBundle.get(i).setSubject(new SuperAdminSubjectModel.SuperAdminSubjectData(listForBundle.get(i).Subject.getSubjectID()
                            , listForBundle.get(i).Subject.getSubjectName()));
                    listForBundle.get(i).setSubject(listForBundle.get(i).isSubject);
                    data.add(listForBundle.get(i));
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                course_rv.setLayoutManager(linearLayoutManager);
                branchCourceAdapter = new BranchSubjectAapter(context, data);
                branchCourceAdapter.notifyDataSetChanged();
                course_rv.setAdapter(branchCourceAdapter);
            }
        } else {
            if (Function.isNetworkAvailable(context)) {
                progressBarHelper.showProgressDialog();
                GetAllCourse();
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        }

        GetAllSimpleClass();

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
            BranchSubjectListFragment profileFragment = new BranchSubjectListFragment();
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.nav_host_fragment, profileFragment);
            ft.addToBackStack(null);
            ft.commit();
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                BranchSubjectListFragment profileFragment = new BranchSubjectListFragment();
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

    public void GetAllCourse() {
        couseitem.clear();
        couseiditem.clear();
        couseitem.add("Select Course");
        couseiditem.add((long) 0);
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
                    if (spinner_course.getSelectedItemId() != 0){
                        GetAllClass();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetAllSimpleClass()
    {
        classitem.clear();
        classitem.add("Select Class");

        CLASSITEM = new String[classitem.size()];
        CLASSITEM = classitem.toArray(CLASSITEM);

        bindselectclass();
    }

    public void bindselectclass() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, CLASSITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_class.setAdapter(adapter);
        spinner_class.setOnItemSelectedListener(onItemSelectedListener80);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener80 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                    ((TextView) parent.getChildAt(0)).setTextSize(16);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetAllClass() {
        progressBarHelper.showProgressDialog();
        classitem.clear();
        classiditem.clear();
        classitem.add("Select Class");
        classiditem.add((long) 0);
        Call<BranchClassModel> call = apiCalling.Get_Class_Spinner(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),courseid);
        call.enqueue(new Callback<BranchClassModel>() {
            @Override
            public void onResponse(@NotNull Call<BranchClassModel> call, @NotNull Response<BranchClassModel> response) {
                if (response.isSuccessful()) {
                    BranchClassModel data = response.body();
                    if (data != null && data.getCompleted()) {
                        List<BranchClassSingleModel.BranchClassData> studentModelList = data.getData();
                        if (studentModelList != null) {
                            for (BranchClassSingleModel.BranchClassData singleResponseModel : studentModelList) {
                                if (singleResponseModel.getClass_dtl_id() != 0 && singleResponseModel.getClassModel().getClassName() != null){
                                    long code = singleResponseModel.getClass_dtl_id();
                                    String desc = singleResponseModel.getClassModel().getClassName();
                                    classiditem.add(code);
                                    classitem.add(desc);
                                }
                            }
                            CLASSITEM = new String[classitem.size()];
                            CLASSITEM = classitem.toArray(CLASSITEM);

                            CLASSID = new Long[classiditem.size()];
                            CLASSID = classiditem.toArray(CLASSID);
                            bindClass();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BranchClassModel> call, @NotNull Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void bindClass() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, CLASSITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_class.setAdapter(adapter);
        spinner_class.setOnItemSelectedListener(reportareaListener1);
        if (bundle != null) {
            selectSpinnerValue(spinner_class, bundle.getString("CLASS_NAME"));
        }
    }

    AdapterView.OnItemSelectedListener reportareaListener1 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (spinner_class.getSelectedItem().toString().equals("Select Class")) {
                        class_name = "";
                        classid = 0;
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(16);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(16);
                        class_name = classitem.get(position);
                        classid = classiditem.get(position);
                    }
                    if (spinner_class.getSelectedItemId() != 0){
                        GetAllSubject(courseid,classid);
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

    public void GetAllSubject(long coursedetailid,long classid) {
        progressBarHelper.showProgressDialog();
        Call<BranchSubjectModel> call = apiCalling.Get_All_Subject_By_Course(coursedetailid,classid);
        call.enqueue(new Callback<BranchSubjectModel>() {
            @Override
            public void onResponse(@NotNull Call<BranchSubjectModel> call, @NotNull Response<BranchSubjectModel> response) {
                if (response.isSuccessful()) {
                    BranchSubjectModel data = response.body();
                    if (data != null && data.isCompleted()) {
                        List<BranchSubjectModel.BranchSubjectData> model = data.getData();
                        if (model != null) {
                            if (model.size() > 0) {
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                course_rv.setLayoutManager(linearLayoutManager);
                                branchCourceAdapter = new BranchSubjectAapter(context,model);
                                branchCourceAdapter.notifyDataSetChanged();
                                course_rv.setAdapter(branchCourceAdapter);
                            }
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BranchSubjectModel> call, @NotNull Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Save() {
        if (spinner_course.getSelectedItemId() == 0) {
            Function.showToast(context, "Please select course");
        } else if (spinner_class.getSelectedItemId() == 0) {
            Function.showToast(context, "Please select class");
        } else {
            progressBarHelper.showProgressDialog();
            if (Function.isNetworkAvailable(context)) {
                list = new ArrayList<>();
                if (BranchSubjectAapter.CourceDataList.size() > 0) {
                    RowStatusModel rowStatusModel = new RowStatusModel(1, "Active");
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    if (bundle == null) {
                        TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),
                                Preferences.getInstance(context).getString(Preferences.KEY_FINANCIAL_YEAR));
                        for (int i = 0; i < BranchSubjectAapter.CourceDataList.size(); i++) {
                            BranchSubjectModel.BranchSubjectData model = new BranchSubjectModel.BranchSubjectData(
                                    0, branchModel
                                    , new BranchCourseModel.BranchCourceData(courseid)
                                    , new BranchClassSingleModel.BranchClassData(classid), transactionModel
                                    , rowStatusModel, BranchSubjectAapter.CourceDataList.get(i).getSubject(), BranchSubjectAapter.CourceDataList.get(i).isSubject);
                            list.add(model);
                        }
                    } else {
                        TransactionModel transactionModel = new TransactionModel(0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0,
                                Preferences.getInstance(context).getString(Preferences.KEY_FINANCIAL_YEAR));
                        for (int i = 0; i < BranchSubjectAapter.CourceDataList.size(); i++) {
                            BranchSubjectModel.BranchSubjectData model = new BranchSubjectModel.BranchSubjectData(
                                    listForBundle.get(i).Subject_dtl_id, branchModel
                                    , new BranchCourseModel.BranchCourceData(courseid)
                                    , new BranchClassSingleModel.BranchClassData(classid), transactionModel
                                    , rowStatusModel, listForBundle.get(i).getSubject(), BranchSubjectAapter.CourceDataList.get(i).isSubject);
                            list.add(model);
                        }
                    }
                    BranchSubjectModel.BranchSubjectData branch = new BranchSubjectModel.BranchSubjectData(list);
                    Call<BranchSubjectSingleModel> call = apiCalling.BranchSubjectMaintenance(branch);
                    call.enqueue(new Callback<BranchSubjectSingleModel>() {
                        @Override
                        public void onResponse(@NotNull Call<BranchSubjectSingleModel> call, @NotNull Response<BranchSubjectSingleModel> response) {
                            if (response.isSuccessful()) {
                                BranchSubjectSingleModel data = response.body();
                                if (data != null) {
                                    if (data.getCompleted()) {
                                        Function.showToast(context, data.getMessage());
                                        BranchSubjectListFragment profileFragment = new BranchSubjectListFragment();
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
                        public void onFailure(@NotNull Call<BranchSubjectSingleModel> call, @NotNull Throwable t) {
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