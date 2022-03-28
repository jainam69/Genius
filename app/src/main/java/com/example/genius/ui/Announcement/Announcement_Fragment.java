package com.example.genius.ui.Announcement;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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


import com.example.genius.API.ApiCalling;
import com.example.genius.Model.AnnouncementModel;
import com.example.genius.Model.AnnouncementSingleModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Masters_Fragment.MasterSelectorFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Announcement_Fragment extends Fragment {

    EditText announcement_description;
    Button add_announcement;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    long annID = 0L, transaction_id = 0L;
    OnBackPressedCallback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Announcement Master");
        View root = inflater.inflate(R.layout.fragment_announcement, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        announcement_description = root.findViewById(R.id.announcement_description);
        add_announcement = root.findViewById(R.id.add_announcement);

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAnnouncement();
        } else
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();

        add_announcement.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (announcement_description.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Enter Announcement.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    AnnouncementModel.AnnouncementData model;
                    if (annID == 0) {
                        model = new AnnouncementModel.AnnouncementData(annID
                                , new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID))
                                , new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME))
                                , new RowStatusModel(1)
                                , announcement_description.getText().toString());
                    } else {
                        model = new AnnouncementModel.AnnouncementData(annID
                                , new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID))
                                , new TransactionModel(transaction_id, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0)
                                , new RowStatusModel(1)
                                , announcement_description.getText().toString());
                    }
                    Call<AnnouncementSingleModel> call = apiCalling.AnnouncementMaintenance(model);
                    call.enqueue(new Callback<AnnouncementSingleModel>() {
                        @Override
                        public void onResponse(Call<AnnouncementSingleModel> call, Response<AnnouncementSingleModel> response) {
                            if (response.isSuccessful()) {
                                AnnouncementSingleModel data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context,data.getMessage(), Toast.LENGTH_SHORT).show();
                                    GetAnnouncement();
                                }else {
                                    Toast.makeText(context,data.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(Call<AnnouncementSingleModel> call, Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
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
        return root;
    }

    public void GetAnnouncement() {
        Call<AnnouncementModel> call = apiCalling.GetAllAnnouncement(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<AnnouncementModel>() {
            @Override
            public void onResponse(@NotNull Call<AnnouncementModel> call, @NotNull Response<AnnouncementModel> response) {
                if (response.isSuccessful()) {
                    AnnouncementModel data = response.body();
                    if (data != null && data.isCompleted()) {
                        List<AnnouncementModel.AnnouncementData> notimodel = data.getData();
                        if (notimodel != null && notimodel.size() > 0) {
                            annID = notimodel.get(0).AnnouncementID;
                            transaction_id = notimodel.get(0).TransactionData.getTransactionId();
                            announcement_description.setText(notimodel.get(0).AnnouncementText);
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<AnnouncementModel> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}