package com.example.genius.ui.Library_Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.LibraryMaster_Adapter;
import com.example.genius.Adapter.ViewLibrary_Adapter;
import com.example.genius.Model.LibraryData;
import com.example.genius.Model.LibraryModel;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.crypto.spec.GCMParameterSpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowLibraryFragment extends Fragment {

    RecyclerView view_library;
    Context context;
    ViewLibrary_Adapter viewLibrary_adapter;
    TextView txt_nodata;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    Bundle bundle;
    List<LibraryModel> model = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Show Library");
        View root = inflater.inflate(R.layout.show_library_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        view_library = root.findViewById(R.id.view_library);
        txt_nodata = root.findViewById(R.id.txt_nodata);

        bundle = getArguments();
        if (bundle != null) {
            if (bundle.getInt("Type") == 1) {
                Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Show Video Library");
            } else {
                Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Show Books Library");
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetViewLibrary();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                LibrarySelectorFragment profileFragment = new LibrarySelectorFragment();
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

    public void GetViewLibrary() {
        Call<LibraryData> call = apiCalling.GetLibraryApprovalByBranch(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<LibraryData>() {
            @Override
            public void onResponse(@NotNull Call<LibraryData> call, @NotNull Response<LibraryData> response) {
                progressBarHelper.hideProgressDialog();
                if (response.isSuccessful()) {
                    LibraryData data = response.body();
                    if (data.isCompleted()) {
                        List<LibraryModel> studentModelList = data.getData();
                        if (studentModelList != null) {
                            if (studentModelList.size() > 0) {
                                txt_nodata.setVisibility(View.GONE);
                                view_library.setVisibility(View.VISIBLE);
                                for (LibraryModel model1 : studentModelList) {
                                    if (bundle.getInt("Type") == model1.getLibrary_Type()) {
                                        model.add(model1);
                                    }
                                }
                                view_library.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                view_library.setLayoutManager(new GridLayoutManager(context, 2));
                                viewLibrary_adapter = new ViewLibrary_Adapter(context, model);
                                viewLibrary_adapter.notifyDataSetChanged();
                                view_library.setAdapter(viewLibrary_adapter);
                            }else {
                                txt_nodata.setVisibility(View.VISIBLE);
                                view_library.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<LibraryData> call, @NotNull Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

}