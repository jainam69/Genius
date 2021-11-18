package com.example.genius.ui.Test_Paper_Entry_Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.genius.Adapter.TestScheduleMaster_Adapter;
import com.example.genius.Adapter.UploadPaperChecking_Adapter;
import com.example.genius.Model.AnswerSheetData;
import com.example.genius.Model.AnswerSheetModel;
import com.example.genius.Model.TestScheduleData;
import com.example.genius.Model.TestScheduleModel;
import com.example.genius.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Test_Schedule.test_Listfragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class Test_Paper_Checking_fragment extends Fragment {

    RecyclerView paper_checking_rv;
    UploadPaperChecking_Adapter uploadPaperChecking_adapter;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    Context context;
    TextView id, txt_nodata;
    Bundle bundle;
    OnBackPressedCallback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Test Paper Checking");
        View root = inflater.inflate(R.layout.fragment_test_paper_checking, container, false);
        context = getActivity();
        paper_checking_rv = root.findViewById(R.id.paper_checking_rv);
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        id = root.findViewById(R.id.id);
        txt_nodata = root.findViewById(R.id.txt_nodata);

        bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("TestID")) {
                id.setText("" + bundle.getLong("TestID"));
            }
        }

        if (Function.checkNetworkConnection(context)) {
            if (bundle != null) {
                progressBarHelper.showProgressDialog();
                GetCheckingDetails();
            }
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                test_Listfragment profileFragment = new test_Listfragment();
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

    public void GetCheckingDetails() {
        Call<AnswerSheetData> call = apiCalling.GetAllAnsSheetByTest(Long.parseLong(id.getText().toString()));
        call.enqueue(new Callback<AnswerSheetData>() {
            @Override
            public void onResponse(@NotNull Call<AnswerSheetData> call, @NotNull Response<AnswerSheetData> response) {
                if (response.isSuccessful()) {
                    AnswerSheetData data = response.body();
                    if (data.isCompleted()) {
                        List<AnswerSheetModel> studentModelList = data.getData();
                        if (studentModelList != null && studentModelList.size() > 0) {
                            paper_checking_rv.setVisibility(View.VISIBLE);
                            txt_nodata.setVisibility(View.GONE);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            paper_checking_rv.setLayoutManager(linearLayoutManager);
                            uploadPaperChecking_adapter = new UploadPaperChecking_Adapter(context, studentModelList);
                            uploadPaperChecking_adapter.notifyDataSetChanged();
                            paper_checking_rv.setAdapter(uploadPaperChecking_adapter);
                        }else {
                            paper_checking_rv.setVisibility(View.GONE);
                            txt_nodata.setVisibility(View.VISIBLE);
                        }
                    }
                }
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(@NotNull Call<AnswerSheetData> call, @NotNull Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

}
