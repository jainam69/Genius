package com.example.genius.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.genius.databinding.ActivityMainBinding;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.ui.Notification.NotificationFragment;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Context context;
    TextView branchname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        branchname = findViewById(R.id.branchname);
        context = MainActivity.this;

        if (Preferences.getInstance(context).getString(Preferences.KEY_FIRE_NOTIFICATION).equals("notification")){
            Fragment fragment = new NotificationFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
            Preferences.getInstance(context).setString(Preferences.KEY_FIRE_NOTIFICATION,"");
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        branchname.setText(Preferences.getInstance(context).getString(Preferences.KEY_BRANCH_NAME));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change_pass) {
            startActivity(new Intent(MainActivity.this,ChangePasswordActivity.class));
        }
        if (id == R.id.action_profile) {
            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
        }
        if (id == R.id.action_signout) {
            alertDialog().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are You Sure You Want to Logout?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Preferences.getInstance(context).setBoolean(Preferences.KEY_LOGIN,false);
                startActivity(new Intent(context, LoginActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple_200));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.purple_200));
            }
        });
        dialog.show();

        return dialog;
    }
}