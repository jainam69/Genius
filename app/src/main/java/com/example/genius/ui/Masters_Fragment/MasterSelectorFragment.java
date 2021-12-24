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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.Adapter.Masters_Adapter;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.RecyclerTouchListener;
import com.example.genius.ui.Banner.Banner_Fragment;
import com.example.genius.ui.BranchClass.BranchClassFragment;
import com.example.genius.ui.BranchClass.BranchClassListFragment;
import com.example.genius.ui.BranchCource.BranchCourseFragment;
import com.example.genius.ui.BranchCource.BranchCourseListFragment;
import com.example.genius.ui.BranchSubject.BranchSubjectFragment;
import com.example.genius.ui.BranchSubject.BranchSubjectListFragment;
import com.example.genius.ui.Faculty_Fragment.faculty_listfragment;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.example.genius.ui.Notification.NotificationFragment;
import com.example.genius.ui.Staff_Entry_Fragment.staff_entry_listfragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;


public class MasterSelectorFragment extends Fragment {

    View root;
    Context context;
    OnBackPressedCallback callback;
    UserModel userpermission;
    RecyclerView master_rv;
    Masters_Adapter masters_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Masters");
        root = inflater.inflate(R.layout.fragment_master_selector, container, false);
        context = getActivity();
        master_rv = root.findViewById(R.id.master_rv);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        ArrayList<String> pagename = new ArrayList<>();
        ArrayList<Integer> image = new ArrayList<>();

        for (UserModel.UserPermission model : userpermission.getPermission())
        {
            if (model.getPageInfo().getPageID() == 4 && model.getPackageRightinfo().isViewstatus()){
                pagename.add("USER MASTER");
                image.add(R.drawable.staff);
            }
            if (model.getPageInfo().getPageID() == 6 && model.getPackageRightinfo().isViewstatus()){
                pagename.add("SCHOOL MASTER");
                image.add(R.drawable.school);
            }
            if (model.getPageInfo().getPageID() == 73 && model.getPackageRightinfo().isViewstatus()){
                pagename.add("BANNER MASTER");
                image.add(R.drawable.banner2);
            }
            if (model.getPageInfo().getPageID() == 10 && model.getPackageRightinfo().isViewstatus()){
                pagename.add("NOTIFICATION MASTER");
                image.add(R.drawable.noti);
            }
            if (model.getPageInfo().getPageID() == 75 && model.getPackageRightinfo().isViewstatus()){
                pagename.add("BRANCH COURSE MASTER");
                image.add(R.drawable.course);
            }
            if (model.getPageInfo().getPageID() == 74 && model.getPackageRightinfo().isViewstatus()){
                pagename.add("BRANCH CLASS MASTER");
                image.add(R.drawable.branchclass);
            }
            if (model.getPageInfo().getPageID() == 76 && model.getPackageRightinfo().isViewstatus()){
                pagename.add("BRANCH SUBJECT MASTER");
                image.add(R.drawable.branchsubject);
            }
            if (model.getPageInfo().getPageID() == 77 && model.getPackageRightinfo().isViewstatus()){
                pagename.add("FACULTY MASTER");
                image.add(R.drawable.user);
            }
        }

        master_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        master_rv.setLayoutManager(new GridLayoutManager(context, 2));
        masters_adapter = new Masters_Adapter(context,pagename,image);
        masters_adapter.notifyDataSetChanged();
        master_rv.setAdapter(masters_adapter);

        master_rv.addOnItemTouchListener(new RecyclerTouchListener(context, master_rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (pagename.get(position).equals("USER MASTER")){
                    staff_entry_listfragment orderplace = new staff_entry_listfragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("SCHOOL MASTER")){
                    master_schoolFragment orderplace = new master_schoolFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("BANNER MASTER")){
                    Banner_Fragment orderplace = new Banner_Fragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("NOTIFICATION MASTER")){
                    NotificationFragment orderplace = new NotificationFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("BRANCH COURSE MASTER")){
                    BranchCourseListFragment orderplace = new BranchCourseListFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("BRANCH CLASS MASTER")){
                    BranchClassListFragment orderplace = new BranchClassListFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("BRANCH SUBJECT MASTER")){
                    BranchSubjectListFragment orderplace = new BranchSubjectListFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("FACULTY MASTER")){
                    faculty_listfragment orderplace = new faculty_listfragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

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