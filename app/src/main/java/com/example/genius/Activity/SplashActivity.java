package com.example.genius.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.genius.helper.Preferences;
import com.example.genius.R;

public class SplashActivity extends AppCompatActivity {

    Preferences preferences;
    Boolean a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferences = new Preferences(SplashActivity.this);
        a = preferences.getBoolean(Preferences.KEY_LOGIN);

        Thread timer = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);
                    if(a){
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }else{
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    SplashActivity.this.finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }
}