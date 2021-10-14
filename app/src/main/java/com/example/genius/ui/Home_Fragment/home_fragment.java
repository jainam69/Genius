package com.example.genius.ui.Home_Fragment;

import android.Manifest;
import android.content.Context;
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
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.ViewPager_Adapter;
import com.example.genius.Model.BannerData;
import com.example.genius.Model.BannerModel;
import com.example.genius.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Attendance_Fragment.attendance_Listfragment;
import com.example.genius.ui.Gallery.GallerySelectorFragment;
import com.example.genius.ui.Homework_Fragment.HomeworkSelectorFragment;
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
import com.example.genius.ui.Test_Schedule.Test_selector_Fragment;
import com.example.genius.ui.Test_Schedule.test_Listfragment;
import com.example.genius.ui.Youtube_Video.YoutubeVideoFragment;
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
    RecyclerView home_rv;
    CirclePageIndicator circlePageIndicator;
    ViewPager viewPager;
    ProgressBarHelper progressBarHelper;
    List<String> list = new ArrayList<>();
    NestedScrollView home_scroll;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    LinearLayout linear_masters, linear_students, linear_attendance, linear_test_schedule, linear_test_marks, linear_practice_papers,
            linear_homework, linear_gallery, linear_youtube_video, linear_live_video, linear_task, linear_reminder, linear_permission, linear_library;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Home");
        root = inflater.inflate(R.layout.home_fragment_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        linear_masters = root.findViewById(R.id.linear_masters);
        linear_students = root.findViewById(R.id.linear_students);
        linear_gallery = root.findViewById(R.id.linear_gallery);
        linear_homework = root.findViewById(R.id.linear_homework);
        linear_youtube_video = root.findViewById(R.id.linear_youtube_video);
        linear_library = root.findViewById(R.id.linear_library);
        linear_attendance = root.findViewById(R.id.linear_attendance);
        linear_live_video = root.findViewById(R.id.linear_live_video);
        linear_practice_papers = root.findViewById(R.id.linear_practice_papers);
        linear_reminder = root.findViewById(R.id.linear_reminder);
        linear_test_schedule = root.findViewById(R.id.linear_test_schedule);
        linear_test_marks = root.findViewById(R.id.linear_test_marks);
        linear_task = root.findViewById(R.id.linear_task);
        linear_permission = root.findViewById(R.id.linear_permission);
        viewPager = root.findViewById(R.id.viewpager);
        circlePageIndicator = root.findViewById(R.id.circlepagerindicator);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
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
        if (Function.checkNetworkConnection(context)) {
            GetBannerDetails();
        } else {
            Toast.makeText(context, "No Internet Connection..", Toast.LENGTH_SHORT).show();
        }
        linear_masters.setOnClickListener(v -> {
            MasterSelectorFragment orderplace = new MasterSelectorFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        linear_students.setOnClickListener(v -> {
            student_registration_Listfragment orderplace = new student_registration_Listfragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        linear_gallery.setOnClickListener(v -> {
            GallerySelectorFragment orderplace = new GallerySelectorFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_youtube_video.setOnClickListener(v -> {
            YoutubeVideoFragment orderplace = new YoutubeVideoFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_homework.setOnClickListener(v -> {
            homework_Listfragment orderplace = new homework_Listfragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_library.setOnClickListener(v -> {
            LibrarySelectorFragment orderplace = new LibrarySelectorFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        linear_task.setOnClickListener(v -> {
            TaskListFragment orderplace = new TaskListFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_reminder.setOnClickListener(v -> {
            reminder_fragment orderplace = new reminder_fragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_practice_papers.setOnClickListener(v -> {
            practice_paper_Listfragment orderplace = new practice_paper_Listfragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_test_marks.setOnClickListener(v -> {
            marks_entry_Listfragment orderplace = new marks_entry_Listfragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_test_schedule.setOnClickListener(v -> {
            test_Listfragment orderplace = new test_Listfragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_live_video.setOnClickListener(v -> {
            LiveVideoFragment orderplace = new LiveVideoFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_attendance.setOnClickListener(v -> {
            attendance_Listfragment orderplace = new attendance_Listfragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        linear_permission.setOnClickListener(v -> {
            PermissionListFragment orderplace = new PermissionListFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        return root;
    }

    public void GetBannerDetails() {
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
                                ViewPager_Adapter adapter = new ViewPager_Adapter(context
                                        , bannerDataList);
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
}