package com.example.genius.ui.BranchSubject;

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
import com.example.genius.Adapter.BranchClassListAdapter;
import com.example.genius.Adapter.BranchSubjectListAapter;
import com.example.genius.Model.BranchClassModel;
import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.BranchSubjectModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.databinding.FragmentBranchSubjectListBinding;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.BranchClass.BranchClassFragment;
import com.example.genius.ui.Masters_Fragment.MasterSelectorFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchSubjectListFragment extends Fragment {

    FragmentBranchSubjectListBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    BranchSubjectListAapter branchSubjectListAapter;
    UserModel.PageData userpermission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Subject Master");
        binding = FragmentBranchSubjectListBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.PageData.class);

        for (UserModel.PageInfoEntity model : userpermission.Data)
        {
            if (model.getPageID() == 76 && !model.Createstatus){
                binding.fabContact.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllSubjectBranchData();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        binding.fabContact.setOnClickListener(v -> {
            BranchSubjectFragment orderplace = new BranchSubjectFragment();
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

    public void GetAllSubjectBranchData() {
        Call<BranchSubjectModel> call = apiCalling.GetAllBranchSubjectByBranchID(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<BranchSubjectModel>() {
            @Override
            public void onResponse(@NotNull Call<BranchSubjectModel> call, @NotNull Response<BranchSubjectModel> response) {
                if (response.isSuccessful()) {
                    BranchSubjectModel data = response.body();
                    if (data != null && data.isCompleted()) {
                        List<BranchSubjectModel.BranchSubjectData> studentModelList = data.getData();
                        if (studentModelList != null) {
                            if (studentModelList.size() > 0) {
                                binding.txtNodata.setVisibility(View.GONE);
                                binding.classListRv.setVisibility(View.VISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                binding.classListRv.setLayoutManager(linearLayoutManager);
                                branchSubjectListAapter = new BranchSubjectListAapter(context, studentModelList);
                                branchSubjectListAapter.notifyDataSetChanged();
                                binding.classListRv.setAdapter(branchSubjectListAapter);
                            }else {
                                binding.txtNodata.setVisibility(View.VISIBLE);
                                binding.classListRv.setVisibility(View.GONE);
                            }
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BranchSubjectModel> call, @NotNull Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

}