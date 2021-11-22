package com.example.genius.ui.Homework_Fragment;

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
import com.example.genius.Adapter.HomeworkCheckingAdapter;
import com.example.genius.Adapter.HomeworkMaster_Adapter;
import com.example.genius.Model.HomeworkModel;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeWorkCheckingFragment extends Fragment {

    Context context;
    RecyclerView homework_checking_rv;
    TextView id,no_content;
    Bundle bundle;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    HomeworkCheckingAdapter homeworkCheckingAdapter;
    TextView txt_nodata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("HomeWork Checking");
        View root = inflater.inflate(R.layout.fragment_home_work_checking, container, false);
        context  =getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        homework_checking_rv = root.findViewById(R.id.homework_checking_rv);
        id = root.findViewById(R.id.id);
        no_content = root.findViewById(R.id.no_content);
        txt_nodata = root.findViewById(R.id.txt_nodata);

        bundle = getArguments();
        if (bundle != null)
        {
            if (bundle.containsKey("ID"))
            {
                id.setText(""+bundle.getLong("ID"));
            }
        }

        if (Function.checkNetworkConnection(context))
        {
            GetHomeworkCheckingList();
        }
        else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                homework_Listfragment profileFragment = new homework_Listfragment();
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

    public void GetHomeworkCheckingList()
    {
        progressBarHelper.showProgressDialog();
        Call<HomeworkModel.HomeworkDetailData> call = apiCalling.Get_Homework_Checking_List(Long.parseLong(id.getText().toString()));
        call.enqueue(new Callback<HomeworkModel.HomeworkDetailData>() {
            @Override
            public void onResponse(Call<HomeworkModel.HomeworkDetailData> call, Response<HomeworkModel.HomeworkDetailData> response) {
                if (response.isSuccessful()){
                    HomeworkModel.HomeworkDetailData data = response.body();
                    if (data != null && data.isCompleted()){
                        List<HomeworkModel> list = data.getData();
                        if (list != null && list.size() > 0){
                            homework_checking_rv.setVisibility(View.VISIBLE);
                            txt_nodata.setVisibility(View.GONE);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            homework_checking_rv.setLayoutManager(linearLayoutManager);
                            homeworkCheckingAdapter = new HomeworkCheckingAdapter(list,context);
                            homeworkCheckingAdapter.notifyDataSetChanged();
                            homework_checking_rv.setAdapter(homeworkCheckingAdapter);
                        }else {
                            homework_checking_rv.setVisibility(View.GONE);
                            txt_nodata.setVisibility(View.VISIBLE);
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<HomeworkModel.HomeworkDetailData> call, Throwable t) {
                    progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}