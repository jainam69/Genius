package com.example.genius.ui.Attendance_Fragment;

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
import com.example.genius.Adapter.AttendanceEntry_Adapter;
import com.example.genius.Model.AttendanceData;
import com.example.genius.Model.AttendanceModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.UserModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class attendance_Listfragment extends Fragment {

    SearchableSpinner branch;
    Button search;
    TextView no_content;
    FloatingActionButton fab_contact;
    Context context;
    RecyclerView attendance_entry_rv;
    AttendanceEntry_Adapter attendanceEntry_adapter;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> branchitem = new ArrayList<>();
    List<Integer> branchid = new ArrayList<>();
    String[] BRANCHITEM;
    Integer[] BRANCHID;
    String BranchName, BranchID;
    OnBackPressedCallback callback;
    UserModel userpermission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Attendance List");
        View root = inflater.inflate(R.layout.fragment_attendance__listfragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        branch = root.findViewById(R.id.branch);
        search = root.findViewById(R.id.search);
        fab_contact = root.findViewById(R.id.fab_contact);
        attendance_entry_rv = root.findViewById(R.id.attendance_entry_rv);
        no_content = root.findViewById(R.id.no_content);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        for (UserModel.UserPermission model : userpermission.getPermission())
        {
            if (model.getPageInfo().getPageID() == 18 && !model.getPackageRightinfo().isCreatestatus()){
                fab_contact.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            Call<AttendanceData> call = apiCalling.GetAllAttendanceByBranch(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
            call.enqueue(new Callback<AttendanceData>() {
                @Override
                public void onResponse(@NotNull Call<AttendanceData> call,@NotNull Response<AttendanceData> response) {
                    if (response.isSuccessful()) {
                        AttendanceData data = response.body();
                        if (data != null && data.isCompleted()) {
                            List<AttendanceModel> studentModelList = data.getData();
                            if (studentModelList != null) {
                                if (studentModelList.size() > 0) {
                                    no_content.setVisibility(View.GONE);
                                    attendance_entry_rv.setVisibility(View.VISIBLE);
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                    attendance_entry_rv.setLayoutManager(linearLayoutManager);
                                    attendanceEntry_adapter = new AttendanceEntry_Adapter(context, studentModelList);
                                    attendanceEntry_adapter.notifyDataSetChanged();
                                    attendance_entry_rv.setAdapter(attendanceEntry_adapter);
                                } else {
                                    attendance_entry_rv.setVisibility(View.GONE);
                                    no_content.setVisibility(View.VISIBLE);
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }
                    } else {
                        progressBarHelper.showProgressDialog();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<AttendanceData> call,@NotNull Throwable t) {
                    Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                    progressBarHelper.hideProgressDialog();
                }
            });
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.checkNetworkConnection(context)) {
                    if (branch.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Branch.", Toast.LENGTH_SHORT).show();
                    else {
                        progressBarHelper.showProgressDialog();
                        Call<AttendanceData> call = apiCalling.GetAllAttendanceByBranch(Long.parseLong(BranchID));
                        call.enqueue(new Callback<AttendanceData>() {
                            @Override
                            public void onResponse(@NotNull Call<AttendanceData> call, @NotNull Response<AttendanceData> response) {
                                if (response.isSuccessful()) {
                                    AttendanceData data = response.body();
                                    if (data.isCompleted()) {
                                        List<AttendanceModel> studentModelList = data.getData();
                                        if (studentModelList != null) {
                                            if (studentModelList.size() > 0) {
                                                List<AttendanceModel> list = new ArrayList<>();
                                                for (AttendanceModel singlemodel : studentModelList) {
                                                    if (singlemodel.getRowStatus().getRowStatusId() == 1) {
                                                        list.add(singlemodel);
                                                    }
                                                }
                                                no_content.setVisibility(View.GONE);
                                                attendance_entry_rv.setVisibility(View.VISIBLE);
                                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                                attendance_entry_rv.setLayoutManager(linearLayoutManager);
                                                attendanceEntry_adapter = new AttendanceEntry_Adapter(context, list);
                                                attendanceEntry_adapter.notifyDataSetChanged();
                                                attendance_entry_rv.setAdapter(attendanceEntry_adapter);
                                            } else {
                                                attendance_entry_rv.setVisibility(View.GONE);
                                                no_content.setVisibility(View.VISIBLE);
                                            }
                                            progressBarHelper.hideProgressDialog();
                                        }
                                    }
                                } else {
                                    progressBarHelper.showProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<AttendanceData> call, @NotNull Throwable t) {
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                                progressBarHelper.hideProgressDialog();
                            }
                        });
                    }
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fab_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                attendance_fragment orderplace = new attendance_fragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
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

        return root;
    }
}