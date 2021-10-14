package com.example.genius.ui.Test_Schedule;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.genius.R;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.example.genius.ui.Test_Paper_Entry_Fragment.Test_Paper_Listfragment;


public class Test_selector_Fragment extends Fragment {


    View root;
    LinearLayout linear_test_schedule;
    CardView linear_test_paper;
    OnBackPressedCallback callback;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Test Schedule");
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_test_selector, container, false);
        linear_test_paper = root.findViewById(R.id.linear_test_paper);
        linear_test_schedule = root.findViewById(R.id.linear_test_schedule);

//        if (Preferences.getInstance(context).getString(Preferences.KEY_UPLOAD_PAPER_PERMISSION).equals("true"))
//        {
//            linear_test_paper.setVisibility(View.VISIBLE);
//        }

        linear_test_paper.setOnClickListener(v->{
            Test_Paper_Listfragment orderplace = new Test_Paper_Listfragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        linear_test_schedule.setOnClickListener(v->{
            test_Listfragment orderplace = new test_Listfragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
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
}