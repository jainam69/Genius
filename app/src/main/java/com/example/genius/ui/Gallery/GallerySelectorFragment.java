package com.example.genius.ui.Gallery;

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

import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.helper.Preferences;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.example.genius.ui.Video.VideoFragment;
import com.google.gson.Gson;

import java.util.Objects;


public class GallerySelectorFragment extends Fragment {

    View root;
    CardView linear_image, linear_video;
    OnBackPressedCallback callback;
    Context context;
    UserModel userpermission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Gallery");
        root = inflater.inflate(R.layout.fragment_gallery_selector, container, false);
        context = getActivity();
        linear_video = root.findViewById(R.id.linear_video);
        linear_image = root.findViewById(R.id.linear_image);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        if (userpermission.getPermission().get(22).getPageInfo().getPageID() == 83 && !userpermission.getPermission().get(22).getPackageRightinfo().isViewstatus()){
            linear_image.setVisibility(View.GONE);
        }
        if (userpermission.getPermission().get(37).getPageInfo().getPageID() == 85 && !userpermission.getPermission().get(37).getPackageRightinfo().isViewstatus()){
            linear_video.setVisibility(View.GONE);
        }

        linear_video.setOnClickListener(v -> {
            VideoFragment orderplace = new VideoFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_image.setOnClickListener(v -> {
            GalleryFragment orderplace = new GalleryFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

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
}