package com.example.genius.ui.Task_Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.genius.Adapter.TaskRegister_Adapter;
import com.example.genius.Model.TodoData;
import com.example.genius.Model.TodoModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskListFragment extends Fragment {

    OnBackPressedCallback callback;
    View root;
    Context context;
    FloatingActionButton fab_task;
    SearchableSpinner status;
    RecyclerView task_reg_rv;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> statusitem = new ArrayList<>(), statusid = new ArrayList<>();
    String[] STATUSITEM, STATUSID;
    String StatusName;
    HashMap<Integer, Object> dateHashMap;
    Calendar calendar;
    TextView no_content;
    TaskRegister_Adapter taskRegister_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Task");
        root = inflater.inflate(R.layout.fragment_task_list, container, false);
        context = getActivity();

        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        fab_task = root.findViewById(R.id.fab_task);
        status = root.findViewById(R.id.status);
        task_reg_rv = root.findViewById(R.id.task_reg_rv);
        no_content = root.findViewById(R.id.no_content);

        if (Function.checkNetworkConnection(context)) {

            selectstatus();
            GetAllTask();
        } else {
            Toast.makeText(context, "Please check your internet connectivity..", Toast.LENGTH_SHORT).show();
        }

        fab_task.setOnClickListener(v -> {
            TaskFragment orderplace = new TaskFragment();
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

    public void selectstatus() {
        statusitem.clear();
        statusitem.add("Select Status");
        statusitem.add("Pending");
        statusitem.add("Done");

        STATUSITEM = new String[statusitem.size()];
        STATUSITEM = statusitem.toArray(STATUSITEM);

        bindstatus();
    }

    public void bindstatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, STATUSITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);
        status.setSelection(1);
        status.setOnItemSelectedListener(onItemSelectedListener61);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener61 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    StatusName = statusitem.get(position);
                    if (status.getSelectedItem().equals("Select Status")) {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                            ((TextView) parent.getChildAt(0)).setTextSize(13);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                            ((TextView) parent.getChildAt(0)).setTextSize(14);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };

    public void GetAllTask() {
        progressBarHelper.showProgressDialog();
        Call<TodoData> call = apiCalling.GetAllToDoWithoutContentByBranchAndUser(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID));
        call.enqueue(new Callback<TodoData>() {
            @Override
            public void onResponse(@NotNull Call<TodoData> call, @NotNull Response<TodoData> response) {
                progressBarHelper.hideProgressDialog();
                if (response.isSuccessful()) {
                    TodoData data = response.body();
                    if (data != null && data.isCompleted()) {
                        List<TodoModel> modelList = data.getData();
                        if (modelList != null) {
                            if (modelList.size() > 0) {
                                task_reg_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                                taskRegister_adapter = new TaskRegister_Adapter(context, modelList);
                                taskRegister_adapter.notifyDataSetChanged();
                                task_reg_rv.setAdapter(taskRegister_adapter);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<TodoData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

}