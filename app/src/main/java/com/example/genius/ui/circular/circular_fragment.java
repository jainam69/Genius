package com.example.genius.ui.circular;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.CircularAdapter;
import com.example.genius.Model.CircularModel;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class circular_fragment extends Fragment {

    Context context;
    RecyclerView circular_rv;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    CircularAdapter circularAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Circular");
        View root = inflater.inflate(R.layout.fragment_circular, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        circular_rv = root.findViewById(R.id.circular_rv);

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetCircular();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                home_fragment profileFragment = new home_fragment();
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

    public void GetCircular() {
        Call<CircularModel> call = apiCalling.GetAllCircular();
        call.enqueue(new Callback<CircularModel>() {
            @Override
            public void onResponse(@NonNull Call<CircularModel> call, @NonNull Response<CircularModel> response) {
                if (response.isSuccessful()) {
                    CircularModel data = response.body();
                    if (data != null && data.Completed) {
                        List<CircularModel.CircularData> list = data.Data;
                        if (list != null && list.size() > 0) {
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            circular_rv.setLayoutManager(linearLayoutManager);
                            circularAdapter = new CircularAdapter(context, list);
                            circular_rv.setAdapter(circularAdapter);
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CircularModel> call, @NonNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}