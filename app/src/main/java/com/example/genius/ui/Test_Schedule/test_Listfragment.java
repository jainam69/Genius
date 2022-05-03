package com.example.genius.ui.Test_Schedule;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.TestScheduleMaster_Adapter;
import com.example.genius.Model.TestScheduleData;
import com.example.genius.Model.TestScheduleModel;
import com.example.genius.Model.UserModel;
import com.example.genius.databinding.TestListFragmentBinding;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

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

@SuppressLint({"SimpleDateFormat", "SetTextI18n"})
public class test_Listfragment extends Fragment {

    TestListFragmentBinding binding;
    Context context;
    TestScheduleMaster_Adapter testScheduleMaster_adapter;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    List<TestScheduleModel> testScheduleDetails2;
    UserModel.PageData userpermission;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Test Paper Master");
        binding = TestListFragmentBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.PageData.class);

        for (UserModel.PageInfoEntity model : userpermission.Data){
            if (model.getPageID() == 84 && !model.Createstatus){
                binding.fabContact.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetTestScheduleDetails();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getStandard(s.toString());
            }
        });

        binding.fabContact.setOnClickListener((View.OnClickListener) v -> {
            test_schedule_fragment orderplace = new test_schedule_fragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
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

    public void GetTestScheduleDetails() {
        Call<TestScheduleData> call = apiCalling.GetAllTestByBranchAPI(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<TestScheduleData>() {
            @Override
            public void onResponse(@NotNull Call<TestScheduleData> call, @NotNull Response<TestScheduleData> response) {
                progressBarHelper.hideProgressDialog();
                if (response.isSuccessful()) {
                    TestScheduleData data = response.body();
                    if (data.isCompleted()) {
                        List<TestScheduleModel> studentModelList = data.getData();
                        if (studentModelList != null) {
                            if (studentModelList.size() > 0) {
                                binding.txtNodata.setVisibility(View.GONE);
                                binding.testscheduleRv.setVisibility(View.VISIBLE);
                                testScheduleDetails2 = studentModelList;
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                binding.testscheduleRv.setLayoutManager(linearLayoutManager);
                                testScheduleMaster_adapter = new TestScheduleMaster_Adapter(context, studentModelList);
                                testScheduleMaster_adapter.notifyDataSetChanged();
                                binding.testscheduleRv.setAdapter(testScheduleMaster_adapter);
                            }else {
                                binding.txtNodata.setVisibility(View.VISIBLE);
                                binding.testscheduleRv.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<TestScheduleData> call, @NotNull Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    private void getStandard(String text) {
        ArrayList<TestScheduleModel> filteredList = new ArrayList<>();
        for (TestScheduleModel item : testScheduleDetails2) {
            if (item.getBranchCourse().getCourse().getCourseName().toLowerCase().contains(text.toLowerCase()) || item.getBranchClass().getClassModel().getClassName().toLowerCase().contains(text.toLowerCase()) || item.getBatchTimeText().contains(text.toLowerCase()) || item.getTestEndTime().toLowerCase().contains(text.toLowerCase()) ||
            item.getTestStartTime().toLowerCase().contains(text.toLowerCase()) || item.getBranchSubject().getSubject().getSubjectName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.size() > 0) {
            testScheduleMaster_adapter.filterList(filteredList);
        } else {
            testScheduleMaster_adapter.filterList(filteredList);
        }
    }

}