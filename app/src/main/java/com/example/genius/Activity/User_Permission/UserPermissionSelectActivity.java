package com.example.genius.Activity.User_Permission;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.genius.Activity.MainActivity;
import com.example.genius.Activity.User_Permission.Role_Rights.RoleRightListActivity;
import com.example.genius.Activity.User_Permission.User_Rights.UserRightsListActivity;
import com.example.genius.R;
import com.example.genius.databinding.ActivityUserPermissionSelectBinding;

import java.util.Objects;

public class UserPermissionSelectActivity extends AppCompatActivity {

    ActivityUserPermissionSelectBinding binding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPermissionSelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Select User Permission");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        context = UserPermissionSelectActivity.this;

        binding.linearRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,RoleActivity.class));
            }
        });

        binding.linearRoleRights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(context, RoleRightListActivity.class)); }
        });

        binding.linearUserRights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(context, UserRightsListActivity.class)); }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }
}