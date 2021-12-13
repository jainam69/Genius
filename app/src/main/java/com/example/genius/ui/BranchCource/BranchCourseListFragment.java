package com.example.genius.ui.BranchCource;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.BranchCourseList_Adapter;
import com.example.genius.Adapter.HomeworkMaster_Adapter;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.HomeworkData;
import com.example.genius.Model.HomeworkModel;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Homework_Fragment.homework_fragment;
import com.example.genius.ui.Masters_Fragment.MasterSelectorFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchCourseListFragment extends Fragment {

    View view;
    FloatingActionButton fab_contact;
    TextView txt_nodata;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    RecyclerView course_list_rv;
    BranchCourseList_Adapter branchCourseListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Branch Course List");
        view = inflater.inflate(R.layout.fragment_branch_cource_list, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        fab_contact = view.findViewById(R.id.fab_contact);
        course_list_rv = view.findViewById(R.id.course_list_rv);
        txt_nodata = view.findViewById(R.id.txt_nodata);

        if (Function.checkNetworkConnection(context)) {
            GetAllCource();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        fab_contact.setOnClickListener(v -> {
            BranchCourseFragment orderplace = new BranchCourseFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MasterSelectorFragment profileFragment = new MasterSelectorFragment();
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.nav_host_fragment, profileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);

        return view;
    }

    public void GetAllCource() {
        progressBarHelper.showProgressDialog();
        Call<BranchCourseModel> call = apiCalling.GetBranchCourseByBranchCourseID(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<BranchCourseModel>() {
            @Override
            public void onResponse(@NotNull Call<BranchCourseModel> call, @NotNull Response<BranchCourseModel> response) {
                if (response.isSuccessful()) {
                    BranchCourseModel data = response.body();
                    if (data != null && data.isCompleted()) {
                        List<BranchCourseModel.BranchCourceData> list = data.getData();
                        if (list != null && list.size() > 0) {
                            txt_nodata.setVisibility(View.GONE);
                            course_list_rv.setVisibility(View.VISIBLE);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            course_list_rv.setLayoutManager(linearLayoutManager);
                            branchCourseListFragment = new BranchCourseList_Adapter(context, data);
                            branchCourseListFragment.notifyDataSetChanged();
                            course_list_rv.setAdapter(branchCourseListFragment);
                        }else {
                            txt_nodata.setVisibility(View.VISIBLE);
                            course_list_rv.setVisibility(View.GONE);
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BranchCourseModel> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}