package com.example.genius.ui.Batch_Fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BatchModel;
import com.example.genius.Model.BranchClassModel;
import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.LibraryModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Homework_Fragment.homework_Listfragment;
import com.example.genius.ui.Staff_Entry_Fragment.staff_entry_listfragment;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class batch_fragment extends Fragment {

    SearchableSpinner course_name,standard,batch_time;
    EditText mon_start_time,mon_end_time,sat_start_time,sat_end_time,sun_start_time,sun_end_time;
    Button save_batch,edit_batch;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> standarditem = new ArrayList<>(), batchitem = new ArrayList<>(), batchid = new ArrayList<>(),courseitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), courseid = new ArrayList<>();
    String[] STANDARDITEM, BATCHITEM,COURSEITEM;
    Integer[] STANDARDID,COURSEID;
    Long courseID,standardID,batchID;
    String format,monday_time,saturday_time,sunday_time;
    int hour1, minute1;
    Bundle bundle;
    OnBackPressedCallback callback;
    BatchModel batchModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Batch Master Entry");
        View root = inflater.inflate(R.layout.fragment_batch_fragment, container, false);
        context = getActivity();
        course_name = root.findViewById(R.id.course_name);
        standard = root.findViewById(R.id.standard);
        batch_time = root.findViewById(R.id.batch_time);
        mon_start_time = root.findViewById(R.id.mon_start_time);
        mon_end_time = root.findViewById(R.id.mon_end_time);
        sat_start_time = root.findViewById(R.id.sat_start_time);
        sat_end_time = root.findViewById(R.id.sat_end_time);
        sun_start_time = root.findViewById(R.id.sun_start_time);
        sun_end_time = root.findViewById(R.id.sun_end_time);
        save_batch = root.findViewById(R.id.save_batch);
        edit_batch = root.findViewById(R.id.edit_batch);
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        bundle = getArguments();
        if (bundle != null){
            save_batch.setVisibility(View.GONE);
            edit_batch.setVisibility(View.VISIBLE);
            batchModel = (BatchModel) bundle.getSerializable("BATCH_DATA");
            String[] time1 = batchModel.getMonFriBatchTime().split("-");
            mon_start_time.setText(time1[0]);
            mon_end_time.setText(time1[1]);
            String[] time2 = batchModel.getSatBatchTime().split("-");
            sat_start_time.setText(time2[0]);
            sat_end_time.setText(time2[1]);
            String[] time3 = batchModel.getSunBatchTime().split("-");
            sun_start_time.setText(time3[0]);
            sun_end_time.setText(time3[1]);
        }

        if (Function.isNetworkAvailable(context)){
            GetAllCourse();
        }

        selectbatch_time();
        selectStandard();

        mon_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        (view, hourOfDay, minute2) -> {
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
                            hour1 = hourOfDay;
                            minute1 = minute2;
                            mon_start_time.setText(hourOfDay + ":" + minute2 + " " + format);
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        mon_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        (view, hourOfDay, minute2) -> {
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
                            hour1 = hourOfDay;
                            minute1 = minute2;
                            mon_end_time.setText(hourOfDay + ":" + minute2 + " " + format);
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        sat_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        (view, hourOfDay, minute2) -> {
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
                            hour1 = hourOfDay;
                            minute1 = minute2;
                            sat_start_time.setText(hourOfDay + ":" + minute2 + " " + format);
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        sat_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        (view, hourOfDay, minute2) -> {
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
                            hour1 = hourOfDay;
                            minute1 = minute2;
                            sat_end_time.setText(hourOfDay + ":" + minute2 + " " + format);
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        sun_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        (view, hourOfDay, minute2) -> {
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
                            hour1 = hourOfDay;
                            minute1 = minute2;
                            sun_start_time.setText(hourOfDay + ":" + minute2 + " " + format);
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        sun_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        (view, hourOfDay, minute2) -> {
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
                            hour1 = hourOfDay;
                            minute1 = minute2;
                            sun_end_time.setText(hourOfDay + ":" + minute2 + " " + format);
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        save_batch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)){
                    if (course_name.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please Select Course.", Toast.LENGTH_SHORT).show();
                    }else if (standard.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                    }else if (batch_time.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                    }else if (mon_start_time.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please Select Monday-Friday Start Time.", Toast.LENGTH_SHORT).show();
                    }else if (mon_end_time.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please Select Monday-Friday End Time.", Toast.LENGTH_SHORT).show();
                    }else if (sat_start_time.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please Select Saturday Start Time.", Toast.LENGTH_SHORT).show();
                    }else if (sat_end_time.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please Select Saturday End Time.", Toast.LENGTH_SHORT).show();
                    }else if (sun_start_time.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please Select Sunday Start Time.", Toast.LENGTH_SHORT).show();
                    }else if (sun_end_time.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please Select Sunday End Time.", Toast.LENGTH_SHORT).show();
                    }else {
                        progressBarHelper.showProgressDialog();
                        monday_time = mon_start_time.getText().toString() + " - " + mon_end_time.getText().toString();
                        saturday_time = sat_start_time.getText().toString() + " - " + sat_end_time.getText().toString();
                        sunday_time = sun_start_time.getText().toString() + " - " + sun_end_time.getText().toString();
                        TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                        BranchClassSingleModel.BranchClassData clname = new BranchClassSingleModel.BranchClassData(standardID);
                        BatchModel model = new BatchModel(0,Integer.parseInt(String.valueOf(batchID)),monday_time,saturday_time,sunday_time,transactionModel,rowStatusModel,branchModel,course,clname);
                        Call<BatchModel.BatchResponseModel> call = apiCalling.Save_Batch(model);
                        call.enqueue(new Callback<BatchModel.BatchResponseModel>() {
                            @Override
                            public void onResponse(Call<BatchModel.BatchResponseModel> call, Response<BatchModel.BatchResponseModel> response) {
                                if (response.isSuccessful()){
                                    BatchModel.BatchResponseModel data = response.body();
                                    if (data.isCompleted()){
                                        BatchModel list = data.getData();
                                        if (list.getBatchID() > 0){
                                            Toast.makeText(context, "Batch inserted successfully.", Toast.LENGTH_SHORT).show();
                                            batch_list_fragment profileFragment = new batch_list_fragment();
                                            FragmentManager fm = getActivity().getSupportFragmentManager();
                                            FragmentTransaction ft = fm.beginTransaction();
                                            ft.replace(R.id.nav_host_fragment, profileFragment);
                                            ft.addToBackStack(null);
                                            ft.commit();
                                        }else {
                                            Toast.makeText(context, "Batch Already Exists!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<BatchModel.BatchResponseModel> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_batch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)){
                    if (course_name.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please Select Course.", Toast.LENGTH_SHORT).show();
                    }else if (standard.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                    }else if (batch_time.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                    }else if (mon_start_time.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please Select Monday-Friday Start Time.", Toast.LENGTH_SHORT).show();
                    }else if (mon_end_time.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please Select Monday-Friday End Time.", Toast.LENGTH_SHORT).show();
                    }else if (sat_start_time.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please Select Saturday Start Time.", Toast.LENGTH_SHORT).show();
                    }else if (sat_end_time.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please Select Saturday End Time.", Toast.LENGTH_SHORT).show();
                    }else if (sun_start_time.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please Select Sunday Start Time.", Toast.LENGTH_SHORT).show();
                    }else if (sun_end_time.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please Select Sunday End Time.", Toast.LENGTH_SHORT).show();
                    }else {
                        progressBarHelper.showProgressDialog();
                        monday_time = mon_start_time.getText().toString() + " - " + mon_end_time.getText().toString();
                        saturday_time = sat_start_time.getText().toString() + " - " + sat_end_time.getText().toString();
                        sunday_time = sun_start_time.getText().toString() + " - " + sun_end_time.getText().toString();
                        TransactionModel transactionModel = new TransactionModel(batchModel.getTransaction().getTransactionId(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                        BranchClassSingleModel.BranchClassData clname = new BranchClassSingleModel.BranchClassData(standardID);
                        BatchModel model = new BatchModel(batchModel.getBatchID(),Integer.parseInt(String.valueOf(batchID)),monday_time,saturday_time,sunday_time,transactionModel,rowStatusModel,branchModel,course,clname);
                        Call<BatchModel.BatchResponseModel> call = apiCalling.Save_Batch(model);
                        call.enqueue(new Callback<BatchModel.BatchResponseModel>() {
                            @Override
                            public void onResponse(Call<BatchModel.BatchResponseModel> call, Response<BatchModel.BatchResponseModel> response) {
                                if (response.isSuccessful()){
                                    BatchModel.BatchResponseModel data = response.body();
                                    if (data.isCompleted()){
                                        BatchModel list = data.getData();
                                        if (list.getBatchID() > 0){
                                            Toast.makeText(context, "Batch Updated successfully.", Toast.LENGTH_SHORT).show();
                                            batch_list_fragment profileFragment = new batch_list_fragment();
                                            FragmentManager fm = getActivity().getSupportFragmentManager();
                                            FragmentTransaction ft = fm.beginTransaction();
                                            ft.replace(R.id.nav_host_fragment, profileFragment);
                                            ft.addToBackStack(null);
                                            ft.commit();
                                        }else {
                                            Toast.makeText(context, "Batch Already Exists!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<BatchModel.BatchResponseModel> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                batch_list_fragment profileFragment = new batch_list_fragment();
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
        course_name.setAdapter(adapter);
        if (bundle != null){
            selectSpinnerValue(course_name,batchModel.getBranchCourse().getCourse().getCourseName());
        }
        course_name.setOnItemSelectedListener(selectcourse);
    }

    AdapterView.OnItemSelectedListener selectcourse =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    courseID = Long.parseLong(courseid.get(position).toString());
                    if (course_name.getSelectedItem().equals("Select Course")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                    if (course_name.getSelectedItemId() != 0){
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
        standard.setAdapter(adapter);
        if (bundle != null){
            selectSpinnerValue(standard,batchModel.getBranchClass().getClassModel().getClassName());
        }
        standard.setOnItemSelectedListener(onItemSelectedListener7);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener7 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    standardID = Long.parseLong(standardid.get(position).toString());
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
        batch_time.setAdapter(adapter);
        if (bundle != null){
            int b = batchid.indexOf(String.valueOf(batchModel.getBatchTime()));
            batch_time.setSelection(b);
        }
        batch_time.setOnItemSelectedListener(onItemSelectedListener77);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener77 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    batchID = Long.parseLong(batchid.get(position));
                    if (batch_time.getSelectedItem().equals("Batch Time")) {
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
        standard.setAdapter(adapter);
        standard.setOnItemSelectedListener(onItemSelectedListener7);
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