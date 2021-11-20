package com.example.genius.ui.Masters_Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.genius.R;
import com.example.genius.ui.Banner.Banner_Fragment;
import com.example.genius.ui.BranchClass.BranchClassFragment;
import com.example.genius.ui.BranchClass.BranchClassListFragment;
import com.example.genius.ui.BranchCource.BranchCourseFragment;
import com.example.genius.ui.BranchCource.BranchCourseListFragment;
import com.example.genius.ui.BranchSubject.BranchSubjectFragment;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.example.genius.ui.Notification.NotificationFragment;
import com.example.genius.ui.Staff_Entry_Fragment.staff_entry_listfragment;


public class MasterSelectorFragment extends Fragment {

    View root;
    Context context;
    LinearLayout linear_user, linear_standard, linear_school, linear_subject, linear_banner, linear_notification, linear_branch_course, linear__branch_class, linear_branch_subject;
    OnBackPressedCallback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Masters");
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_master_selector, container, false);
        context = getActivity();
        linear_user = root.findViewById(R.id.linear_user);
        linear_standard = root.findViewById(R.id.linear_standard);
        linear_school = root.findViewById(R.id.linear_school);
        linear_subject = root.findViewById(R.id.linear_subject);
        linear_banner = root.findViewById(R.id.linear_banner);
        linear_notification = root.findViewById(R.id.linear_nitification);
        linear_branch_course = root.findViewById(R.id.linear_branch_course);
        linear__branch_class = root.findViewById(R.id.linear__branch_class);
        linear_branch_subject = root.findViewById(R.id.linear_branch_subject);

        linear_user.setOnClickListener((View.OnClickListener) v -> {
            staff_entry_listfragment orderplace = new staff_entry_listfragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_standard.setOnClickListener((View.OnClickListener) v -> {
            master_standardFragment orderplace = new master_standardFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_school.setOnClickListener((View.OnClickListener) v -> {
            master_schoolFragment orderplace = new master_schoolFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_subject.setOnClickListener((View.OnClickListener) v -> {
            master_subjectFragment orderplace = new master_subjectFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_banner.setOnClickListener((View.OnClickListener) v -> {
            Banner_Fragment orderplace = new Banner_Fragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_notification.setOnClickListener((View.OnClickListener) v -> {
            NotificationFragment orderplace = new NotificationFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_branch_course.setOnClickListener((View.OnClickListener) view -> {
            BranchCourseListFragment orderplace = new BranchCourseListFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear__branch_class.setOnClickListener((View.OnClickListener) view -> {
            BranchClassListFragment orderplace = new BranchClassListFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_branch_subject.setOnClickListener((View.OnClickListener) view -> {
            BranchSubjectFragment orderplace = new BranchSubjectFragment();
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