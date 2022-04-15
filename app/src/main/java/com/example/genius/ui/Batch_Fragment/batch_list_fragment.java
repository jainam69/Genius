package com.example.genius.ui.Batch_Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.Batch_Adapter;
import com.example.genius.Adapter.HomeworkMaster_Adapter;
import com.example.genius.Model.BatchModel;
import com.example.genius.Model.StudentModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.databinding.FragmentBatchListFragmentBinding;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.example.genius.ui.Homework_Fragment.homework_fragment;
import com.example.genius.ui.Masters_Fragment.MasterSelectorFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class batch_list_fragment extends Fragment {

    FragmentBatchListFragmentBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    Batch_Adapter batch_adapter;
    OnBackPressedCallback callback;
    UserModel userpermission;
    List<BatchModel> model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Batch Master");
        binding = FragmentBatchListFragmentBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        for (UserModel.UserPermission model : userpermission.getPermission())
        {
            if (model.getPageInfo().getPageID() == 11 && !model.getPackageRightinfo().isCreatestatus()){
                binding.fabContact.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)){
            GetAllBatchList();
        }else {
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
                getallsearch(s.toString());
            }
        });

        binding.fabContact.setOnClickListener(v -> {
            batch_fragment orderplace = new batch_fragment();
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
        return binding.getRoot();
    }

    public void GetAllBatchList()
    {
        progressBarHelper.showProgressDialog();
        Call<BatchModel.BatchData> call = apiCalling.Get_All_Batch_List(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<BatchModel.BatchData>() {
            @Override
            public void onResponse(Call<BatchModel.BatchData> call, Response<BatchModel.BatchData> response) {
                if (response.isSuccessful()){
                    BatchModel.BatchData data = response.body();
                    if (data.isCompleted()){
                        List<BatchModel> list = data.getData();
                        if (list.size() > 0 && list != null){
                            binding.txtNodata.setVisibility(View.GONE);
                            binding.batchRv.setVisibility(View.VISIBLE);
                            model = list;
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            binding.batchRv.setLayoutManager(linearLayoutManager);
                            batch_adapter = new Batch_Adapter(list,context);
                            batch_adapter.notifyDataSetChanged();
                            binding.batchRv.setAdapter(batch_adapter);
                        }else {
                            binding.txtNodata.setVisibility(View.VISIBLE);
                            binding.batchRv.setVisibility(View.GONE);
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<BatchModel.BatchData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getallsearch(String text) {
        ArrayList<BatchModel> filteredList = new ArrayList<>();
        for (BatchModel item : model) {
            if (item.getBranchCourse().getCourse().getCourseName().toLowerCase().contains(text.toLowerCase()) ||
            item.getBranchClass().getClassModel().getClassName().toLowerCase().contains(text.toLowerCase()) ||
            item.getMonFriBatchTime().toLowerCase().contains(text.toLowerCase()) || item.getSatBatchTime().toLowerCase().contains(text.toLowerCase()) ||
            item.getSunBatchTime().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.size() > 0) {
            batch_adapter.filterList(filteredList);
        } else {
            batch_adapter.filterList(filteredList);
        }
    }
}