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
import com.example.genius.helper.Preferences;
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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint({"SimpleDateFormat", "SetTextI18n"})
public class homework_Listfragment extends Fragment {

    FloatingActionButton fab_contact;
    Context context;
    EditText standard, date;
    RecyclerView homework_rv;
    HomeworkMaster_Adapter homeworkMaster_adapter;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    private int year;
    private int month;
    private int day;
    Button clear;
    List<HomeworkModel> homeworkfilter;
    String Date;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Homework Entry");
        View root = inflater.inflate(R.layout.homework__listfragment_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        fab_contact = root.findViewById(R.id.fab_contact);
        homework_rv = root.findViewById(R.id.homework_rv);
        standard = root.findViewById(R.id.standard);
        date = root.findViewById(R.id.date);
        clear = root.findViewById(R.id.clear);

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetAllHomework();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        clear.setOnClickListener(v -> {
            standard.setText("");
            date.setText("");
        });

        date.setOnClickListener(v -> {
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

        standard.addTextChangedListener(new TextWatcher() {
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

        fab_contact.setOnClickListener(v -> {
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
        return root;
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
                                List<HomeworkModel> list = new ArrayList<>();
                                for (HomeworkModel singlemodel : studentModelList) {
                                    if (singlemodel.getRowStatus().getRowStatusId() == 1) {
                                        list.add(singlemodel);
                                    }
                                }
                                homeworkfilter = studentModelList;
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                homework_rv.setLayoutManager(linearLayoutManager);
                                homeworkMaster_adapter = new HomeworkMaster_Adapter(context, list);
                                homeworkMaster_adapter.notifyDataSetChanged();
                                homework_rv.setAdapter(homeworkMaster_adapter);
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

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }

    private void getDate(String text) {
        ArrayList<HomeworkModel> filteredList = new ArrayList<>();

        for (HomeworkModel item : homeworkfilter) {
            String a = item.getHomeworkDate().replace("T00:00:00", "");
            try {
                Date d = actualdate.parse(a);
                if (d != null) {
                    Date = displaydate.format(d);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (Date.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.size() > 0) {
            homeworkMaster_adapter.filterList(filteredList);
        } else {
            homeworkMaster_adapter.filterList(filteredList);
        }
    }

    private void getstandard(String text) {
        ArrayList<HomeworkModel> filteredList = new ArrayList<>();

        for (HomeworkModel item : homeworkfilter) {
            if (item.getStandardInfo().getStandard().toLowerCase().contains(text.toLowerCase()) || item.getSubjectInfo().getSubject().toLowerCase().contains(text.toLowerCase()) || item.getBatchTimeText().toLowerCase().contains(text.toLowerCase())) {
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