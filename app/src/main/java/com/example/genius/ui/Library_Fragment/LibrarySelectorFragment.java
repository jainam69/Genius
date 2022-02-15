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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.Adapter.Masters_Adapter;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.RecyclerTouchListener;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.google.gson.Gson;

import java.util.ArrayList;


public class LibrarySelectorFragment extends Fragment {

    LinearLayout linear_create_library, linear_show_library, linear_approve_library;
    CardView linear_manage_library;
    UserModel userpermission;
    CardView linear_show_image_video_library;
    Context context;
    RecyclerView library_rv;
    Masters_Adapter masters_adapter;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Library");
        View root = inflater.inflate(R.layout.library_selector_fragment, container, false);
        library_rv = root.findViewById(R.id.library_rv);
        context = getActivity();
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        ArrayList<String> pagename = new ArrayList<>();
        ArrayList<Integer> image = new ArrayList<>();

        for (UserModel.UserPermission model : userpermission.getPermission())
        {
            if (model.getPageInfo().getPageID() == 78 && model.getPackageRightinfo().isViewstatus()){
                pagename.add("IMAGES/DOCUMENTS");
                image.add(R.drawable.library);
            }
            if (model.getPageInfo().getPageID() == 30 && model.getPackageRightinfo().isViewstatus()){
                pagename.add("VIDEOS");
                image.add(R.drawable.create_library);
            }
            if (model.getPageInfo().getPageID() == 80 && model.getPackageRightinfo().isViewstatus()){
                pagename.add("MANAGE LIBRARY");
                image.add(R.drawable.course);
            }
            if (model.getPageInfo().getPageID() == 88 && model.getPackageRightinfo().isViewstatus()) {
                pagename.add("SHOW BOOKS LIBRARY");
                image.add(R.drawable.show_library);
                pagename.add("SHOW VIDEO LIBRARY");
                image.add(R.drawable.video);
            }
        }

        library_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        library_rv.setLayoutManager(new GridLayoutManager(context, 2));
        masters_adapter = new Masters_Adapter(context,pagename,image);
        masters_adapter.notifyDataSetChanged();
        library_rv.setAdapter(masters_adapter);

        library_rv.addOnItemTouchListener(new RecyclerTouchListener(context, library_rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (pagename.get(position).equals("IMAGES/DOCUMENTS")){
                    library_Listfragment fragment = new library_Listfragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("VIDEOS")){
                    library_videolist_fragment fragment = new library_videolist_fragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("MANAGE LIBRARY")){
                    LibraryApproveFragment fragment = new LibraryApproveFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("SHOW BOOKS LIBRARY")) {
                    ShowLibraryFragment orderplace = new ShowLibraryFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Type", 2);
                    orderplace.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if (pagename.get(position).equals("SHOW VIDEO LIBRARY")) {
                    ShowLibraryFragment orderplace = new ShowLibraryFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Type", 1);
                    orderplace.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        return root;
    }
}