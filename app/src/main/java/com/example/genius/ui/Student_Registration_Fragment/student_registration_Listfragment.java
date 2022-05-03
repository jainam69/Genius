package com.example.genius.ui.Student_Registration_Fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.example.genius.Adapter.StudentMaster_Adapter;
import com.example.genius.Model.StaffModel;
import com.example.genius.Model.StudentData;
import com.example.genius.Model.StudentModel;
import com.example.genius.Model.UserModel;
import com.example.genius.databinding.StudentRegistrationListfragmentFragmentBinding;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class student_registration_Listfragment extends Fragment {

    StudentRegistrationListfragmentFragmentBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    StudentMaster_Adapter studentMaster_adapter;
    UserModel.PageData userpermission;
    List<StudentModel> model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Student Admission Form");
        binding = StudentRegistrationListfragmentFragmentBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.PageData.class);

        for (UserModel.PageInfoEntity model : userpermission.Data){
            if (model.getPageID() == 8 && !model.Createstatus){
                binding.fabContact.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllStudent();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        binding.inactive.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            GetAllInactiveStudent();
        });

        binding.active.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            GetAllActiveStudent();
        });

        binding.fabContact.setOnClickListener(v -> {
            student_registration_fragment orderplace = new student_registration_fragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getUserName(s.toString());
            }
        });

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

    public void GetAllStudent() {
        Call<StudentData> call = apiCalling.GetAllStudentWithoutContent(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<StudentData>() {
            @Override
            public void onResponse(@NotNull Call<StudentData> call, @NotNull Response<StudentData> response) {
                progressBarHelper.hideProgressDialog();
                if (response.isSuccessful()) {
                    StudentData data = response.body();
                    if (data != null && data.isCompleted()) {
                        List<StudentModel> studentModelList = data.getData();
                        if (studentModelList != null) {
                            if (studentModelList.size() > 0) {
                                binding.studentRv.setVisibility(View.VISIBLE);
                                binding.txtNodata.setVisibility(View.GONE);
                                model = studentModelList;
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                binding.studentRv.setLayoutManager(linearLayoutManager);
                                studentMaster_adapter = new StudentMaster_Adapter(context, studentModelList);
                                studentMaster_adapter.notifyDataSetChanged();
                                binding.studentRv.setAdapter(studentMaster_adapter);
                            }else {
                                binding.studentRv.setVisibility(View.GONE);
                                binding.txtNodata.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<StudentData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void GetAllActiveStudent() {
        Call<StudentData> call = apiCalling.GetAllActiveStudentWithoutContent(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<StudentData>() {
            @Override
            public void onResponse(@NotNull Call<StudentData> call, @NotNull Response<StudentData> response) {
                progressBarHelper.hideProgressDialog();
                if (response.isSuccessful()) {
                    StudentData data = response.body();
                    if (data != null && data.isCompleted()) {
                        List<StudentModel> studentModelList = data.getData();
                        if (studentModelList != null) {
                            if (studentModelList.size() > 0) {
                                binding.txtNodata.setVisibility(View.GONE);
                                binding.studentRv.setVisibility(View.VISIBLE);
                                model = studentModelList;
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                binding.studentRv.setLayoutManager(linearLayoutManager);
                                studentMaster_adapter = new StudentMaster_Adapter(context, studentModelList);
                                studentMaster_adapter.notifyDataSetChanged();
                                binding.studentRv.setAdapter(studentMaster_adapter);
                            }else {
                                binding.txtNodata.setVisibility(View.VISIBLE);
                                binding.studentRv.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<StudentData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void GetAllInactiveStudent() {
        Call<StudentData> call = apiCalling.GetAllInActiveStudentWithoutContent(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<StudentData>() {
            @Override
            public void onResponse(@NotNull Call<StudentData> call, @NotNull Response<StudentData> response) {
                progressBarHelper.hideProgressDialog();
                if (response.isSuccessful()) {
                    StudentData data = response.body();
                    if (data != null && data.isCompleted()) {
                        List<StudentModel> studentModelList = data.getData();
                        if (studentModelList != null) {
                            if (studentModelList.size() > 0) {
                                binding.txtNodata.setVisibility(View.GONE);
                                binding.studentRv.setVisibility(View.VISIBLE);
                                model = studentModelList;
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                binding.studentRv.setLayoutManager(linearLayoutManager);
                                studentMaster_adapter = new StudentMaster_Adapter(context, studentModelList);
                                studentMaster_adapter.notifyDataSetChanged();
                                binding.studentRv.setAdapter(studentMaster_adapter);
                            }else {
                                binding.txtNodata.setVisibility(View.VISIBLE);
                                binding.studentRv.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<StudentData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    private void getUserName(String text) {
        ArrayList<StudentModel> filteredList = new ArrayList<>();
        for (StudentModel item : model) {
            if (item.getFirstName().toLowerCase().contains(text.toLowerCase()) || item.getLastName().toLowerCase().contains(text.toLowerCase()) ||
            item.getBranchCourse().getCourse().getCourseName().toLowerCase().contains(text.toLowerCase()) ||
            item.getBranchClass().classModel.getClassName().toLowerCase().contains(text.toLowerCase()) ||
            item.getContactNo().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.size() > 0) {
            studentMaster_adapter.filterList(filteredList);
        } else {
            studentMaster_adapter.filterList(filteredList);
        }
    }

}