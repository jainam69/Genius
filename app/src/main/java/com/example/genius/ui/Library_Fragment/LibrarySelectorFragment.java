package com.example.genius.ui.Library_Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.genius.R;
import com.example.genius.ui.Home_Fragment.home_fragment;


public class LibrarySelectorFragment extends Fragment {

    LinearLayout linear_create_library, linear_show_library, linear_approve_library;
    CardView linear_manage_library;
    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
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
    Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Library");
        View root = inflater.inflate(R.layout.library_selector_fragment, container, false);
        linear_create_library = root.findViewById(R.id.linear_create_library);
        linear_show_library = root.findViewById(R.id.linear_show_library);
        linear_manage_library = root.findViewById(R.id.linear_manage_library);
        linear_approve_library = root.findViewById(R.id.linear_approve_library);
        context = getActivity();

        linear_create_library.setOnClickListener(v -> {
            library_Listfragment orderplace = new library_Listfragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_show_library.setOnClickListener(v -> {
            library_Listfragment fragment = new library_Listfragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_manage_library.setOnClickListener((View.OnClickListener) v -> {
            library_videolist_fragment fragment = new library_videolist_fragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        linear_approve_library.setOnClickListener((View.OnClickListener) v -> {
            LibraryApproveFragment fragment = new LibraryApproveFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        return root;
    }
}