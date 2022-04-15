package com.example.genius.ui.Homework_Fragment;

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
import com.example.genius.Adapter.HomeworkMaster_Adapter;
import com.example.genius.Model.HomeworkData;
import com.example.genius.Model.HomeworkModel;
import com.example.genius.Model.UserModel;
import com.example.genius.databinding.HomeworkListfragmentFragmentBinding;
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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint({"SimpleDateFormat", "SetTextI18n"})
public class homework_Listfragment extends Fragment {

    HomeworkListfragmentFragmentBinding binding;
    Context context;
    HomeworkMaster_Adapter homeworkMaster_adapter;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    List<HomeworkModel> homeworkfilter;
    UserModel userpermission;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Homework Master");
        binding = HomeworkListfragmentFragmentBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        for (UserModel.UserPermission model : userpermission.getPermission())
        {
            if (model.getPageInfo().getPageID() == 43 && !model.getPackageRightinfo().isCreatestatus()){
                binding.fabContact.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllHomework();
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
                getstandard(s.toString());
            }
        });

        binding.fabContact.setOnClickListener(v -> {
            homework_fragment orderplace = new homework_fragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
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

    public void GetAllHomework() {
        Call<HomeworkData> call = apiCalling.GetAllHomeworkWithoutContentByBranch(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<HomeworkData>() {
            @Override
            public void onResponse(@NotNull Call<HomeworkData> call, @NotNull Response<HomeworkData> response) {
                if (response.isSuccessful()) {
                    HomeworkData data = response.body();
                    if (data != null && data.isCompleted()) {
                        List<HomeworkModel> studentModelList = data.getData();
                        if (studentModelList != null) {
                            if (studentModelList.size() > 0) {
                                binding.txtNodata.setVisibility(View.GONE);
                                binding.homeworkRv.setVisibility(View.VISIBLE);
                                homeworkfilter = studentModelList;
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                binding.homeworkRv.setLayoutManager(linearLayoutManager);
                                homeworkMaster_adapter = new HomeworkMaster_Adapter(context, studentModelList);
                                homeworkMaster_adapter.notifyDataSetChanged();
                                binding.homeworkRv.setAdapter(homeworkMaster_adapter);
                            }else {
                                binding.txtNodata.setVisibility(View.VISIBLE);
                                binding.homeworkRv.setVisibility(View.GONE);
                            }
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<HomeworkData> call, @NotNull Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    private void getstandard(String text) {
        ArrayList<HomeworkModel> filteredList = new ArrayList<>();
        for (HomeworkModel item : homeworkfilter) {
            if (item.getBranchSubject().getSubject().getSubjectName().toLowerCase().contains(text.toLowerCase()) || item.getBranchClass().getClassModel().getClassName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getBatchTimeText().toLowerCase().contains(text.toLowerCase()) || item.getBranchCourse().getCourse().getCourseName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.size() > 0) {
            homeworkMaster_adapter.filterList(filteredList);
        } else {
            homeworkMaster_adapter.filterList(filteredList);
        }

    }

}