package com.example.genius.ui.Staff_Entry_Fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.example.genius.Adapter.StaffMaster_Adapter;
import com.example.genius.Model.StaffData;
import com.example.genius.Model.StaffModel;
import com.example.genius.Model.UserModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Masters_Fragment.MasterSelectorFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class staff_entry_listfragment extends Fragment {

    FloatingActionButton fab_contact;
    Context context;
    RecyclerView staff_rv;
    EditText user_name, mno;
    TextView txt_nodata;
    Button clear, search;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    StaffMaster_Adapter staffMaster_adapter;
    List<StaffModel> staffDetails2;
    UserModel userpermission;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("User Entry");
        View root = inflater.inflate(R.layout.staff_entry_listfragment_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        fab_contact = root.findViewById(R.id.fab_contact);
        staff_rv = root.findViewById(R.id.staff_rv);
        user_name = root.findViewById(R.id.user_name);
        mno = root.findViewById(R.id.mno);
        clear = root.findViewById(R.id.clear);
        search = root.findViewById(R.id.search);
        txt_nodata = root.findViewById(R.id.txt_nodata);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        for (UserModel.UserPermission model : userpermission.getPermission())
        {
            if (model.getPageInfo().getPageID() == 4 && !model.getPackageRightinfo().isCreatestatus()){
                fab_contact.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllStaff();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_name.setText("");
                mno.setText("");
            }
        });

        user_name.addTextChangedListener(new TextWatcher() {
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

        mno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getMobileNo(s.toString());
            }
        });

        fab_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                staff_entry_fragment orderplace = new staff_entry_fragment();
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
        return root;
    }

    public void GetAllStaff() {
        Call<StaffData> call = apiCalling.GetAllStaff(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<StaffData>() {
            @Override
            public void onResponse(Call<StaffData> call, Response<StaffData> response) {
                if (response.isSuccessful()) {
                    StaffData staffData = response.body();
                    if (staffData != null) {
                        if (staffData.isCompleted()) {
                            List<StaffModel> respose = staffData.getData();
                            if (respose != null){
                                if (respose.size() > 0) {
                                    txt_nodata.setVisibility(View.GONE);
                                    staff_rv.setVisibility(View.VISIBLE);
                                    staffDetails2 = respose;
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                    staff_rv.setLayoutManager(linearLayoutManager);
                                    staffMaster_adapter = new StaffMaster_Adapter(context, respose);
                                    staffMaster_adapter.notifyDataSetChanged();
                                    staff_rv.setAdapter(staffMaster_adapter);
                                }else {
                                    staff_rv.setVisibility(View.GONE);
                                    txt_nodata.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<StaffData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserName(String text) {
        ArrayList<StaffModel> filteredList = new ArrayList<>();

        for (StaffModel item : staffDetails2) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.size() > 0) {
            staffMaster_adapter.filterList(filteredList);
        } else {
            staffMaster_adapter.filterList(filteredList);
        }
    }

    private void getMobileNo(String text) {
        ArrayList<StaffModel> filteredList = new ArrayList<>();

        for (StaffModel item : staffDetails2) {
            if (item.getMobileNo().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.size() > 0) {
            staffMaster_adapter.filterList(filteredList);
        } else {
            staffMaster_adapter.filterList(filteredList);
        }

    }
}