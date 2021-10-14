package com.example.genius.ui.Library_Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.genius.API.ApiCalling;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class manage_library_fragment extends Fragment {

    SearchableSpinner status;
    RecyclerView manage_library_rv;
    Context context;
    TextView no_content;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> statusitem = new ArrayList<>(), statusid = new ArrayList<>();
    String[] STATUSITEM, STATUSID;
    String StatusName;
    OnBackPressedCallback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Manage Library");
        View root = inflater.inflate(R.layout.fragment_manage_library_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        status = root.findViewById(R.id.status);
        manage_library_rv = root.findViewById(R.id.manage_library_rv);
        no_content = root.findViewById(R.id.no_content);

        if (Function.checkNetworkConnection(context))
        {
        }else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }
        selectstatus();

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


    public void selectstatus()
    {
        statusitem.clear();
        statusitem.add("Select Status");
        statusitem.add("Pending");
        statusitem.add("Reject");
        statusitem.add("Approved");

        STATUSITEM = new String[statusitem.size()];
        STATUSITEM = statusitem.toArray(STATUSITEM);

        bindstatus();
    }

    public void bindstatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, STATUSITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);
        status.setSelection(1);
        status.setOnItemSelectedListener(onItemSelectedListener6);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener6 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    StatusName = statusitem.get(position);
                    if (status.getSelectedItem().equals("Select Status")) {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                            ((TextView) parent.getChildAt(0)).setTextSize(13);
                        } catch (Exception e) {
                        }
                    }
                    else {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                            ((TextView) parent.getChildAt(0)).setTextSize(14);
                        } catch (Exception e) {
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };
}