package com.example.genius.ui.Student_Registration_Fragment;

import android.content.Context;
import android.os.Bundle;
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
import com.example.genius.Model.StudentData;
import com.example.genius.Model.StudentModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class student_registration_Listfragment extends Fragment {

    FloatingActionButton fab_contact;
    Context context;
    RecyclerView student_rv;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    EditText stu_name, con_no;
    Button clear, search, active, inactive;
    OnBackPressedCallback callback;
    StudentMaster_Adapter studentMaster_adapter;
    LinearLayout txt_nodata;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Student Registration");
        View root = inflater.inflate(R.layout.student_registration__listfragment_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        fab_contact = root.findViewById(R.id.fab_contact);
        student_rv = root.findViewById(R.id.student_rv);
        stu_name = root.findViewById(R.id.stu_name);
        con_no = root.findViewById(R.id.con_no);
        clear = root.findViewById(R.id.clear);
        active = root.findViewById(R.id.active);
        inactive = root.findViewById(R.id.inactive);
        txt_nodata = root.findViewById(R.id.txt_nodata);

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetAllStudent();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        clear.setOnClickListener(v -> {
            stu_name.setText("");
            con_no.setText("");
        });

        inactive.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            GetAllInactiveStudent();
        });

        active.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            GetAllActiveStudent();
        });

        fab_contact.setOnClickListener(v -> {
            student_registration_fragment orderplace = new student_registration_fragment();
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
        return root;
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
                                student_rv.setVisibility(View.VISIBLE);
                                txt_nodata.setVisibility(View.GONE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                student_rv.setLayoutManager(linearLayoutManager);
                                studentMaster_adapter = new StudentMaster_Adapter(context, studentModelList);
                                studentMaster_adapter.notifyDataSetChanged();
                                student_rv.setAdapter(studentMaster_adapter);
                            }else {
                                student_rv.setVisibility(View.GONE);
                                txt_nodata.setVisibility(View.VISIBLE);
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
                                txt_nodata.setVisibility(View.GONE);
                                student_rv.setVisibility(View.VISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                student_rv.setLayoutManager(linearLayoutManager);
                                studentMaster_adapter = new StudentMaster_Adapter(context, studentModelList);
                                studentMaster_adapter.notifyDataSetChanged();
                                student_rv.setAdapter(studentMaster_adapter);
                            }else {
                                txt_nodata.setVisibility(View.VISIBLE);
                                student_rv.setVisibility(View.GONE);
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
                                txt_nodata.setVisibility(View.GONE);
                                student_rv.setVisibility(View.VISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                student_rv.setLayoutManager(linearLayoutManager);
                                studentMaster_adapter = new StudentMaster_Adapter(context, studentModelList);
                                studentMaster_adapter.notifyDataSetChanged();
                                student_rv.setAdapter(studentMaster_adapter);
                            }else {
                                txt_nodata.setVisibility(View.VISIBLE);
                                student_rv.setVisibility(View.GONE);
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
}