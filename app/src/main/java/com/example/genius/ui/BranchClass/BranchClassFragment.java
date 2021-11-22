package com.example.genius.ui.BranchClass;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.genius.R;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

public class BranchClassFragment extends Fragment {

    Context context;
    SearchableSpinner coursename;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_branch_class, container, false);
        return view;
    }
}