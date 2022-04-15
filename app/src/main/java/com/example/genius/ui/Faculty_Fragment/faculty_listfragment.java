package com.example.genius.ui.Faculty_Fragment;

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
import com.example.genius.Adapter.Faculty_Adapter;
import com.example.genius.Adapter.StaffMaster_Adapter;
import com.example.genius.Model.FacultyModel;
import com.example.genius.Model.StaffData;
import com.example.genius.Model.StaffModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.databinding.FragmentFacultyListfragmentBinding;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Masters_Fragment.MasterSelectorFragment;
import com.example.genius.ui.Staff_Entry_Fragment.staff_entry_fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class faculty_listfragment extends Fragment {

    FragmentFacultyListfragmentBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    UserModel userpermission;
    Faculty_Adapter faculty_adapter;
    List<FacultyModel>  model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Faculty Master");
        binding = FragmentFacultyListfragmentBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        for (UserModel.UserPermission model : userpermission.getPermission())
        {
            if (model.getPageInfo().getPageID() == 77 && !model.getPackageRightinfo().isCreatestatus()){
                binding.fabContact.setVisibility(View.GONE);
            }
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
                getUserName(s.toString());
            }
        });

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllFacultyList();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        binding.fabContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faculty_fragment orderplace = new faculty_fragment();
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
                MasterSelectorFragment profileFragment = new MasterSelectorFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.nav_host_fragment, profileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        return binding.getRoot();
    }

    public void GetAllFacultyList()
    {
        Call<FacultyModel.FacultyData> call = apiCalling.Get_All_Faculty(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<FacultyModel.FacultyData>() {
            @Override
            public void onResponse(Call<FacultyModel.FacultyData> call, Response<FacultyModel.FacultyData> response) {
                if (response.isSuccessful()){
                    FacultyModel.FacultyData data = response.body();
                    if (data.isCompleted()){
                        List<FacultyModel> list = data.getData();
                        if (list != null && list.size() > 0){
                            model = list;
                            binding.txtNodata.setVisibility(View.GONE);
                            binding.facultyRv.setVisibility(View.VISIBLE);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            binding.facultyRv.setLayoutManager(linearLayoutManager);
                            faculty_adapter = new Faculty_Adapter(context, list);
                            faculty_adapter.notifyDataSetChanged();
                            binding.facultyRv.setAdapter(faculty_adapter);
                        }else {
                            binding.txtNodata.setVisibility(View.VISIBLE);
                            binding.facultyRv.setVisibility(View.GONE);
                        }
                    }
                }
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<FacultyModel.FacultyData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserName(String text) {
        ArrayList<FacultyModel> filteredList = new ArrayList<>();
        for (FacultyModel item : model) {
            if (item.getStaff().getName().toLowerCase().contains(text.toLowerCase()) || item.getBranchCourse().getCourse().getCourseName().toLowerCase().contains(text.toLowerCase())
            || item.getBranchClass().getClassModel().getClassName().toLowerCase().contains(text.toLowerCase()) || item.getBranchSubject().getSubject().getSubjectName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.size() > 0) {
            faculty_adapter.filterList(filteredList);
        } else {
            faculty_adapter.filterList(filteredList);
        }
    }
}