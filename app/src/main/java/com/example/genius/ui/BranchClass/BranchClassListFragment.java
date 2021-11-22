package com.example.genius.ui.BranchClass;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.genius.R;

public class BranchClassListFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Branch Class Master");
        view = inflater.inflate(R.layout.fragment_branch_class_list, container, false);
        return view;
    }
}