package com.example.genius.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.genius.databinding.ActivitySplashBinding;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.squareup.picasso.Picasso;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.blink);
        binding.linear.setAnimation(anim);

        String logo = Preferences.getInstance(context).getString(Preferences.KEY_APP_LOGO);
        Picasso.get().load(logo).into(binding.linearImage);

        Thread timer = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(4000);
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }
}