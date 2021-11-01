package com.example.genius.ui.Permission;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.PermissionMaster_Adapter;
import com.example.genius.Model.UserData1;
import com.example.genius.Model.UserModel;
import com.example.genius.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PermissionListFragment extends Fragment {
    FloatingActionButton fab_contact;
    Context context;
    RecyclerView permission_rv;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    Button clear;
    EditText date, emp_name;
    OnBackPressedCallback callback;
    PermissionMaster_Adapter permissionMaster_adapter;
    View root;
    private int year;
    private int month;
    private int day;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("User Permission");
        root = inflater.inflate(R.layout.fragment_permission_list, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        fab_contact = root.findViewById(R.id.fab_contact);
        permission_rv = root.findViewById(R.id.permission_rv);
        clear = root.findViewById(R.id.clear);
        date = root.findViewById(R.id.date);
        emp_name = root.findViewById(R.id.emp_name);

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetPermissionDetails();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date.setText("");
                emp_name.setText("");
            }
        });

        fab_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionFragment orderplace = new PermissionFragment();
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

    public void GetPermissionDetails() {
//        Call<UserData1> call = apiCalling.GetAllUsers(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        Call<UserData1> call = apiCalling.GetAllUsersddl(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<UserData1>() {
            @Override
            public void onResponse(Call<UserData1> call, Response<UserData1> response) {
                if (response.isSuccessful()){
                    UserData1 userData1= response.body();
                    if (userData1!=null){
                        if(userData1.isCompleted()){
                            List<UserModel> userModelList = userData1.getData();
                            if (userModelList !=null){
                                if (userModelList.size()>0){
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                    permission_rv.setLayoutManager(linearLayoutManager);
                                    permissionMaster_adapter = new PermissionMaster_Adapter(context, userModelList);
                                    permissionMaster_adapter.notifyDataSetChanged();
                                    permission_rv.setAdapter(permissionMaster_adapter);
                                }
                            }

                        }
                    }
                }
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<UserData1> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }
}