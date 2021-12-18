package com.example.genius.ui.Home_Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.genius.API.ApiCalling;
import com.example.genius.Activity.LoginActivity;
import com.example.genius.Activity.MainActivity;
import com.example.genius.Adapter.Home_Adapter;
import com.example.genius.Adapter.ViewPager_Adapter;
import com.example.genius.Model.BannerData;
import com.example.genius.Model.BannerModel;
import com.example.genius.Model.UserModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.helper.RecyclerTouchListener;
import com.example.genius.ui.Attendance_Fragment.attendance_Listfragment;
import com.example.genius.ui.FeeStructure.FeeStructureFragment;
import com.example.genius.ui.Gallery.GallerySelectorFragment;
import com.example.genius.ui.Homework_Fragment.homework_Listfragment;
import com.example.genius.ui.Library_Fragment.LibrarySelectorFragment;
import com.example.genius.ui.Live_Video.LiveVideoFragment;
import com.example.genius.ui.Marks_Entry_Fragment.marks_entry_Listfragment;
import com.example.genius.ui.Masters_Fragment.MasterSelectorFragment;
import com.example.genius.ui.Permission.PermissionListFragment;
import com.example.genius.ui.Practice_Paper_Fragment.practice_paper_Listfragment;
import com.example.genius.ui.Reminder_Fragment.reminder_fragment;
import com.example.genius.ui.Student_Registration_Fragment.student_registration_Listfragment;
import com.example.genius.ui.Task_Fragment.TaskListFragment;
import com.example.genius.ui.Test_Schedule.test_Listfragment;
import com.example.genius.ui.Youtube_Video.YoutubeVideoFragment;
import com.google.gson.Gson;
import com.viewpagerindicator.CirclePageIndicator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class home_fragment extends Fragment {

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    private static int currentPage = 0;
    private static int NUM_PAGE = 0;
    View root;
    Context context;
    UserModel userpermission;
    CirclePageIndicator circlePageIndicator;
    ViewPager viewPager;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    RecyclerView home_rv;
    Home_Adapter home_adapter;
    ArrayList<String> pagename = new ArrayList<>();
    ArrayList<Integer> image = new ArrayList<>();
    OnBackPressedCallback callback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Home");
        root = inflater.inflate(R.layout.home_fragment_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        home_rv = root.findViewById(R.id.home_rv);
        viewPager = root.findViewById(R.id.viewpager);
        circlePageIndicator = root.findViewById(R.id.circlepagerindicator);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        if (Function.isNetworkAvailable(context)) {
            GetBannerDetails();
            GetUserPermission();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        home_rv.addOnItemTouchListener(new RecyclerTouchListener(context, home_rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (pagename.get(position).equals("MASTERS")){
                    MasterSelectorFragment orderplace = new MasterSelectorFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("STUDENTS")){
                    student_registration_Listfragment orderplace = new student_registration_Listfragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("ATTENDANCE")){
                    attendance_Listfragment orderplace = new attendance_Listfragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("TEST SCHEDULE")){
                    test_Listfragment orderplace = new test_Listfragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("TEST MARKS")){
                    marks_entry_Listfragment orderplace = new marks_entry_Listfragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("PRACTICE PAPERS")){
                    practice_paper_Listfragment orderplace = new practice_paper_Listfragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("HOMEWORKS")){
                    homework_Listfragment orderplace = new homework_Listfragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("GALLERY")){
                    GallerySelectorFragment orderplace = new GallerySelectorFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("YOU-TUBE VIDEO")){
                    YoutubeVideoFragment orderplace = new YoutubeVideoFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("LIVE VIDEO")){
                    LiveVideoFragment orderplace = new LiveVideoFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("TASK")){
                    TaskListFragment orderplace = new TaskListFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("REMINDER")){
                    reminder_fragment orderplace = new reminder_fragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("FEE STRUCTURE")){
                    FeeStructureFragment orderplace = new FeeStructureFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("LIBRARY")){
                    LibrarySelectorFragment orderplace = new LibrarySelectorFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
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
                getActivity().finish();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return root;
    }

    public void GetBannerDetails() {
        progressBarHelper.showProgressDialog();
        retrofit2.Call<BannerData> call = apiCalling.GetAllBannerBranchType(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), Preferences.getInstance(context).getInt(Preferences.KEY_USER_TYPE));
        call.enqueue(new Callback<BannerData>() {
            @Override
            public void onResponse(@NotNull Call<BannerData> call, @NotNull Response<BannerData> response) {
                if (response.isSuccessful()) {
                    progressBarHelper.hideProgressDialog();
                    BannerData bannerData = response.body();
                    if (bannerData != null && bannerData.isCompleted()) {
                        List<BannerModel> bannerDataList = bannerData.getData();
                        if (bannerDataList != null) {
                            if (bannerDataList.size() > 0) {
                                ViewPager_Adapter adapter = new ViewPager_Adapter(context, bannerDataList);
                                viewPager.setPadding(0, 0, 0, 0);
                                viewPager.setAdapter(adapter);

                                NUM_PAGE = bannerDataList.size();

                                final Handler handler = new Handler();
                                final Runnable update = () -> {
                                    if (currentPage == NUM_PAGE) {
                                        currentPage = 0;
                                    }
                                    viewPager.setCurrentItem(currentPage++, true);
                                };

                                Timer swiptimer = new Timer();

                                swiptimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        handler.post(update);
                                    }
                                }, 4000, 4000);

                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<BannerData> call, @NotNull Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void GetUserPermission()
    {
        progressBarHelper.showProgressDialog();
        Call<UserModel.UserData> call = apiCalling.Get_User_Permission(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<UserModel.UserData>() {
            @Override
            public void onResponse(Call<UserModel.UserData> call, Response<UserModel.UserData> response) {
                if (response.isSuccessful()) {
                    UserModel.UserData data = response.body();
                    if (data.isCompleted()) {
                        UserModel model = data.getData();
                        if (model.getPermission().size() > 0) {
                            Preferences.getInstance(context).setString(Preferences.KEY_PERMISSION_LIST,new Gson().toJson(model));
                            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);
                        } else {
                            Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                SetUserPermission();
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<UserModel.UserData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context,t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void SetUserPermission()
    {
        if ((userpermission.getPermission().get(36).getPageInfo().getPageID() == 4 && userpermission.getPermission().get(36).getPackageRightinfo().isViewstatus()) ||
                (userpermission.getPermission().get(26).getPageInfo().getPageID() == 6 && userpermission.getPermission().get(26).getPackageRightinfo().isViewstatus()) ||
                (userpermission.getPermission().get(9).getPageInfo().getPageID() == 75 && userpermission.getPermission().get(9).getPackageRightinfo().isViewstatus()) ||
                (userpermission.getPermission().get(8).getPageInfo().getPageID() == 74 && userpermission.getPermission().get(8).getPackageRightinfo().isViewstatus()) ||
                (userpermission.getPermission().get(10).getPageInfo().getPageID() == 76 && userpermission.getPermission().get(10).getPackageRightinfo().isViewstatus()) ||
                (userpermission.getPermission().get(5).getPageInfo().getPageID() == 73 && userpermission.getPermission().get(5).getPackageRightinfo().isViewstatus()) ||
                (userpermission.getPermission().get(21).getPageInfo().getPageID() == 10 && userpermission.getPermission().get(21).getPackageRightinfo().isViewstatus()) ||
                (userpermission.getPermission().get(11).getPageInfo().getPageID() == 77 && userpermission.getPermission().get(11).getPackageRightinfo().isViewstatus())){
            pagename.add("MASTERS");
            image.add(R.drawable.master);
        }
        if (userpermission.getPermission().get(18).getPageInfo().getPageID() == 9 && userpermission.getPermission().get(18).getPackageRightinfo().isViewstatus()){
            pagename.add("STUDENTS");
            image.add(R.drawable.students);
        }
        if (userpermission.getPermission().get(4).getPageInfo().getPageID() == 19 && userpermission.getPermission().get(4).getPackageRightinfo().isViewstatus()){
            pagename.add("ATTENDANCE");
            image.add(R.drawable.attendance);
        }
        if (userpermission.getPermission().get(33).getPageInfo().getPageID() == 84 && userpermission.getPermission().get(33).getPackageRightinfo().isViewstatus()){
            pagename.add("TEST SCHEDULE");
            image.add(R.drawable.schedules);
        }
        if (userpermission.getPermission().get(20).getPageInfo().getPageID() == 82 && userpermission.getPermission().get(20).getPackageRightinfo().isViewstatus()){
            pagename.add("TEST MARKS");
            image.add(R.drawable.marks);
        }
        if (userpermission.getPermission().get(23).getPageInfo().getPageID() == 36 && userpermission.getPermission().get(23).getPackageRightinfo().isViewstatus()){
            pagename.add("PRACTICE PAPERS");
            image.add(R.drawable.practice);
        }
        if (userpermission.getPermission().get(12).getPageInfo().getPageID() == 43 && userpermission.getPermission().get(12).getPackageRightinfo().isViewstatus()){
            pagename.add("HOMEWORKS");
            image.add(R.drawable.homework);
        }
        if ((userpermission.getPermission().get(22).getPageInfo().getPageID() == 83 && userpermission.getPermission().get(22).getPackageRightinfo().isViewstatus()) ||
                (userpermission.getPermission().get(37).getPageInfo().getPageID() == 85 && userpermission.getPermission().get(37).getPackageRightinfo().isViewstatus())){
            pagename.add("GALLERY");
            image.add(R.drawable.gallery);
        }
        if (userpermission.getPermission().get(38).getPageInfo().getPageID() == 86 && userpermission.getPermission().get(38).getPackageRightinfo().isViewstatus()){
            pagename.add("YOU-TUBE VIDEO");
            image.add(R.drawable.youtube);
        }
        if (userpermission.getPermission().get(16).getPageInfo().getPageID() == 79 && userpermission.getPermission().get(16).getPackageRightinfo().isViewstatus()){
            pagename.add("LIVE VIDEO");
            image.add(R.drawable.live);
        }
        if (userpermission.getPermission().get(35).getPageInfo().getPageID() == 39 && userpermission.getPermission().get(35).getPackageRightinfo().isViewstatus()){
            pagename.add("TASK");
            image.add(R.drawable.task);
        }
        if (userpermission.getPermission().get(24).getPageInfo().getPageID() == 40 && userpermission.getPermission().get(24).getPackageRightinfo().isViewstatus()){
            pagename.add("REMINDER");
            image.add(R.drawable.reminder);
        }
        if (userpermission.getPermission().get(29).getPageInfo().getPageID() == 15 && userpermission.getPermission().get(29).getPackageRightinfo().isViewstatus()){
            pagename.add("FEE STRUCTURE");
            image.add(R.drawable.branchclass);
        }
        if ((userpermission.getPermission().get(13).getPageInfo().getPageID() == 78 && userpermission.getPermission().get(13).getPackageRightinfo().isViewstatus()) ||
                (userpermission.getPermission().get(14).getPageInfo().getPageID() == 30 && userpermission.getPermission().get(14).getPackageRightinfo().isViewstatus()) ||
                (userpermission.getPermission().get(17).getPageInfo().getPageID() == 80 && userpermission.getPermission().get(17).getPackageRightinfo().isViewstatus()) ||
                (userpermission.getPermission().get(39).getPageInfo().getPageID() == 88 && userpermission.getPermission().get(39).getPackageRightinfo().isViewstatus())){
            pagename.add("LIBRARY");
            image.add(R.drawable.library);
        }

        home_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        home_rv.setLayoutManager(new GridLayoutManager(context, 2));
        home_adapter = new Home_Adapter(context,pagename,image);
        home_adapter.notifyDataSetChanged();
        home_rv.setAdapter(home_adapter);
    }
}