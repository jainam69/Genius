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
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.example.genius.Adapter.TestScheduleMaster_Adapter;
import com.example.genius.Model.BannerModel;
import com.example.genius.Model.StudentData;
import com.example.genius.Model.StudentModel;
import com.example.genius.Model.TestScheduleData;
import com.example.genius.Model.TestScheduleModel;
import com.example.genius.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    FloatingActionButton fab_contact;
    Context context;
    RecyclerView testschedule_rv;
    TestScheduleMaster_Adapter testScheduleMaster_adapter;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    EditText date, std;
    Button clear, search;
    private int year;
    private int month;
    private int day;
    OnBackPressedCallback callback;
    List<TestScheduleModel> testScheduleDetails2;
    String Date;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Test Schedule");
        View root = inflater.inflate(R.layout.test_list_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        testschedule_rv = root.findViewById(R.id.testschedule_rv);
        fab_contact = root.findViewById(R.id.fab_contact);
        date = root.findViewById(R.id.date);
        std = root.findViewById(R.id.std);
        clear = root.findViewById(R.id.clear);
        search = root.findViewById(R.id.search);

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetTestScheduleDetails();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(getActivity(),
                        (view, year2, monthOfYear, dayOfMonth) -> {
                            year = year2;
                            month = monthOfYear;
                            day = dayOfMonth;
                            date.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                        }, year, month, day);
                picker.show();
            }
        });

        clear.setOnClickListener(v -> {
            date.setText("");
            std.setText("");
        });

        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getDate(s.toString());
            }
        });

        std.addTextChangedListener(new TextWatcher() {
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

        fab_contact.setOnClickListener((View.OnClickListener) v -> {

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
        return root;
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }

    public void GetTestScheduleDetails() {
        Call<TestScheduleData> call = apiCalling.GetAllTestByBranch(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
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
                                List<TestScheduleModel> list = new ArrayList<>();
                                for (TestScheduleModel singlemodel : studentModelList) {
                                    if (singlemodel.getRowStatus().getRowStatusId() == 1) {
                                        list.add(singlemodel);
                                    }
                                }
                                testScheduleDetails2 = studentModelList;
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                testschedule_rv.setLayoutManager(linearLayoutManager);
                                testScheduleMaster_adapter = new TestScheduleMaster_Adapter(context, studentModelList);
                                testScheduleMaster_adapter.notifyDataSetChanged();
                                testschedule_rv.setAdapter(testScheduleMaster_adapter);
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

    private void getDate(String text) {
        ArrayList<TestScheduleModel> filteredList = new ArrayList<>();

        for (TestScheduleModel item : testScheduleDetails2) {
            String a = item.getTestDate().replace("T00:00:00", "");
            try {
                Date d = actualdate.parse(a);
                Date = displaydate.format(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (Date.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.size() > 0) {
            testScheduleMaster_adapter.filterList(filteredList);
        } else {
            testScheduleMaster_adapter.filterList(filteredList);
        }
    }

    private void getStandard(String text) {
        ArrayList<TestScheduleModel> filteredList = new ArrayList<>();

        for (TestScheduleModel item : testScheduleDetails2) {
            if (item.getStandard().getStandard().toLowerCase().contains(text.toLowerCase()) || item.getSubject().getSubject().toLowerCase().contains(text.toLowerCase()) || item.getTestStartTime().toLowerCase().contains(text.toLowerCase()) || item.getTestEndTime().toLowerCase().contains(text.toLowerCase())) {
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