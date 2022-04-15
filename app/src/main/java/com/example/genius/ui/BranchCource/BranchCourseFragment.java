package com.example.genius.ui.BranchCource;

import android.content.Context;
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
import com.example.genius.databinding.FragmentBranchCourceBinding;
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

    FragmentBranchCourceBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    BranchCourseAdapter branchCourceAdapter;
    List<BranchCourseModel.BranchCourceData> list;
    List<BranchCourseModel.BranchCourceData> listForBundle;
    Bundle bundle = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Course Master Entry");
        binding = FragmentBranchCourceBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        bundle = getArguments();
        if (bundle != null) {
            listForBundle = (List<BranchCourseModel.BranchCourceData>) bundle.getSerializable("COURSE_DTL");
            if (listForBundle.size() > 0) {
                List<CourceModel.CourceData> data = new ArrayList<>();
                for (int i = 0; i < listForBundle.size(); i++) {
                    listForBundle.get(i).setCourse(new CourceModel.CourceData(listForBundle.get(i).getCourse().getCourseID()
                            , listForBundle.get(i).getCourse().getCourseName()
                            , listForBundle.get(i).getIscourse()));
                    data.add(listForBundle.get(i).getCourse());
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                binding.courseRv.setLayoutManager(linearLayoutManager);
                branchCourceAdapter = new BranchCourseAdapter(context, data);
                branchCourceAdapter.notifyDataSetChanged();
                binding.courseRv.setAdapter(branchCourceAdapter);
            }
        } else {
            if (Function.checkNetworkConnection(context)) {
                GetAllCourse();
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        }

        binding.chkAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (binding.chkAll.isChecked()) {
                branchCourceAdapter.selectAll();
            } else {
                branchCourceAdapter.unselectall();
            }
        });

        binding.saveCourse.setOnClickListener(view -> {
            progressBarHelper.showProgressDialog();
            list = new ArrayList<>();
            if (BranchCourseAdapter.CourceDataList.size() > 0) {
                TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                RowStatusModel rowStatusModel = new RowStatusModel(1, "Active");
                BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                if (bundle == null) {
                    for (int i = 0; i < BranchCourseAdapter.CourceDataList.size(); i++) {
                        BranchCourseModel.BranchCourceData model = new BranchCourseModel.BranchCourceData(0, branchModel, BranchCourseAdapter.CourceDataList.get(i),
                                transactionModel, rowStatusModel, BranchCourseAdapter.CourceDataList.get(i).getIscourse());
                        list.add(model);
                    }
                } else {
                    for (int i = 0; i < BranchCourseAdapter.CourceDataList.size(); i++) {
                        BranchCourseModel.BranchCourceData model = new BranchCourseModel.BranchCourceData(listForBundle.get(i).getCourse_dtl_id(), branchModel, BranchCourseAdapter.CourceDataList.get(i),
                                transactionModel, rowStatusModel, BranchCourseAdapter.CourceDataList.get(i).getIscourse());
                        list.add(model);
                    }
                }
                BranchCourseModel.BranchCourceData branch = new BranchCourseModel.BranchCourceData(list);
                Call<BranchCourseSingleModel> call = apiCalling.BranchCourseMaintenance(branch);
                call.enqueue(new Callback<BranchCourseSingleModel>() {
                    @Override
                    public void onResponse(@NotNull Call<BranchCourseSingleModel> call, @NotNull Response<BranchCourseSingleModel> response) {
                        if (response.isSuccessful()) {
                            BranchCourseSingleModel data = response.body();
                            if (data.getCompleted()) {
                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                BranchCourseListFragment profileFragment = new BranchCourseListFragment();
                                FragmentManager fm = requireActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.nav_host_fragment, profileFragment);
                                ft.addToBackStack(null);
                                ft.commit();
                            } else {
                                Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBarHelper.hideProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<BranchCourseSingleModel> call, @NotNull Throwable t) {
                        progressBarHelper.hideProgressDialog();
                        Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.deleteCourse.setOnClickListener(view -> {
            BranchCourseListFragment profileFragment = new BranchCourseListFragment();
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.nav_host_fragment, profileFragment);
            ft.addToBackStack(null);
            ft.commit();
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
        return binding.getRoot();
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
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                binding.courseRv.setLayoutManager(linearLayoutManager);
                                branchCourceAdapter = new BranchCourseAdapter(context, studentModelList);
                                branchCourceAdapter.notifyDataSetChanged();
                                binding.courseRv.setAdapter(branchCourceAdapter);
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