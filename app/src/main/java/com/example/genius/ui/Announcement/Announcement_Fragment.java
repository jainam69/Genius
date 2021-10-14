package com.example.genius.ui.Announcement;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.example.genius.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Announcement_Fragment extends Fragment {

    EditText announcement_title,announcement_description;
    Button add_announcement;
    Context context;
    private static final String TAG = "Announcement";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Announcement");
        View root = inflater.inflate(R.layout.fragment_announcement, container, false);
        context = getActivity();
        announcement_title = root.findViewById(R.id.announcement_title);
        announcement_description = root.findViewById(R.id.announcement_description);
        add_announcement = root.findViewById(R.id.add_announcement);
//        branch_type_spinner = root.findViewById(R.id.branch_type_spinner);
//        user_type_spinner = root.findViewById(R.id.user_type_spinner);
//        final List<String> branchlist = Arrays.asList(getResources().getStringArray(R.array.branch));
//        final List<String> userlist = Arrays.asList(getResources().getStringArray(R.array.user));

//        final List<KeyPairBoolData> listArray0 = new ArrayList<>();
  /*      for (int i = 0; i < branchlist.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(branchlist.get(i));
            h.setSelected(false);
            listArray0.add(h);
        }
        final List<KeyPairBoolData> listArray1 = new ArrayList<>();
        for (int i = 0; i < branchlist.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(branchlist.get(i));
//            h.setSelected(i < 5);
            listArray1.add(h);
        }

        final List<KeyPairBoolData> listArray01 = new ArrayList<>();
        for (int i = 0; i < userlist.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(userlist.get(i));
            h.setSelected(false);
            listArray01.add(h);
        }
        final List<KeyPairBoolData> listArray11 = new ArrayList<>();
        for (int i = 0; i < userlist.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(userlist.get(i));
//            h.setSelected(i < 5);
            listArray11.add(h);
        }

        branch_type_spinner.setSearchEnabled(true);
        branch_type_spinner.setHintText("Branch");
        branch_type_spinner.setSearchHint("Select Branch");
        branch_type_spinner.setShowSelectAllButton(true);

        branch_type_spinner.setItems(listArray1, items -> {
            //The followings are selected items.
            for (int i = 0; i < items.size(); i++) {
                Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
            }
        });

        user_type_spinner.setSearchEnabled(true);
        user_type_spinner.setHintText("Select User");
        user_type_spinner.setSearchHint("Select User");
        user_type_spinner.setShowSelectAllButton(true);

        user_type_spinner.setItems(listArray11, items -> {
            //The followings are selected items.
            for (int i = 0; i < items.size(); i++) {
                Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
            }
        });*/
        return root;
    }
}