package com.example.genius.ui.BranchCource;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.BranchCourseAdapter;
import com.example.genius.Adapter.MarksEnterAdapter;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.BranchCourseSingleModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CourceModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchCourseFragment extends Fragment {

    View root;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    RecyclerView course_rv;
    Button save_course, delete_course;
    BranchCourseAdapter branchCourceAdapter;
    CheckBox checkall;
    List<BranchCourseModel.BranchCourceData> list;
    Bundle bundle;
    BranchCourseModel.BranchCourceData model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Branch Course Master");
        root = inflater.inflate(R.layout.fragment_branch_cource, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        course_rv = root.findViewById(R.id.course_rv);
        checkall = root.findViewById(R.id.chk_all);

        bundle = getArguments();
        if (bundle != null){
            list = (List<BranchCourseModel.BranchCourceData>) bundle.getSerializable("COURSE_DTL");
            /*if (list.size() > 0){
                List<CourceModel.CourceData> data = new ArrayList<>();
                for (int i = 0; i< list.size();i++){
                    list.get(i).setCourse(new CourceModel.CourceData(true));
                    data.add(list.get(i).getCourse());
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                course_rv.setLayoutManager(linearLayoutManager);
                branchCourceAdapter = new BranchCourseAdapter(context, data);
                branchCourceAdapter.notifyDataSetChanged();
                course_rv.setAdapter(branchCourceAdapter);
            }*/
        }

        if (Function.checkNetworkConnection(context)) {
            GetAllCourse();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        checkall.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (checkall.isChecked()) {
                branchCourceAdapter.selectAll();
            } else {
                branchCourceAdapter.unselectall();
            }
        });

        save_course = root.findViewById(R.id.save_course);
        save_course.setOnClickListener(view -> {
            progressBarHelper.showProgressDialog();
            list = new ArrayList<>();
            if (BranchCourseAdapter.branchCourceData.size() > 0) {
                TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                RowStatusModel rowStatusModel = new RowStatusModel(1);
                BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                long[] longs = {4, 5, 6};
                for (int i = 0; i < BranchCourseAdapter.branchCourceData.size(); i++) {
                    BranchCourseModel.BranchCourceData model = new BranchCourseModel.BranchCourceData(longs[i], branchModel, BranchCourseAdapter.branchCourceData.get(i).getCourse(),
                            transactionModel, rowStatusModel, BranchCourseAdapter.branchCourceData.get(i).getIscourse());
                    list.add(model);
                }
                BranchCourseModel.BranchCourceData branch = new BranchCourseModel.BranchCourceData(list);
                Call<BranchCourseSingleModel> call = apiCalling.BranchCourseMaintenance(branch);
                call.enqueue(new Callback<BranchCourseSingleModel>() {
                    @Override
                    public void onResponse(Call<BranchCourseSingleModel> call, Response<BranchCourseSingleModel> response) {
                        if (response.isSuccessful()) {
                            BranchCourseSingleModel data = response.body();
                            if (data.getCompleted()) {
                                BranchCourseModel.BranchCourceData model = data.getData();
                                if (model.getCourse_dtl_id() >= 0) {
                                    Toast.makeText(context, "Course Created Successfully.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Course Already Exists!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<BranchCourseSingleModel> call, Throwable t) {
                        progressBarHelper.hideProgressDialog();
                        Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        delete_course = root.findViewById(R.id.delete_course);
        delete_course.setOnClickListener(view -> {

        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                BranchCourseListFragment profileFragment = new BranchCourseListFragment();
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
        progressBarHelper.showProgressDialog();
        Call<CourceModel> call = apiCalling.GetAllCourse();
        call.enqueue(new Callback<CourceModel>() {
            @Override
            public void onResponse(@NotNull Call<CourceModel> call, @NotNull Response<CourceModel> response) {
                if (response.isSuccessful()) {
                    CourceModel data = response.body();
                    if (data != null && data.getCompleted()) {
                        List<CourceModel.CourceData> studentModelList = data.getData();
                        if (studentModelList != null) {
                            if (studentModelList.size() > 0) {
                                if (bundle != null && list.size() >0)
                                {
                                    for (int i = 0; i < list.size(); i++){
                                        list.get(i).setCourse(new CourceModel.CourceData(true));
                                        studentModelList.add(list.get(i).getCourse());
                                    }
                                }
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                course_rv.setLayoutManager(linearLayoutManager);
                                branchCourceAdapter = new BranchCourseAdapter(context, studentModelList);
                                branchCourceAdapter.notifyDataSetChanged();
                                course_rv.setAdapter(branchCourceAdapter);
                            }
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<CourceModel> call, @NotNull Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

}