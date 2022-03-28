package com.example.genius.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.github.chrisbanes.photoview.PhotoView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;

public class VideoViewActivity extends AppCompatActivity {

    Intent intent;
    ProgressBar mProgressBar;
    String ProIndex,Description,path;
    PhotoView image;
    VideoView video;
    FrameLayout rl;
    TextView subject;
    byte[] imageVal;
    private MediaController mediaController;
    private int position = 0;
    File outputFile,apkStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        subject = findViewById(R.id.subject);

        intent = getIntent();
        ProIndex = intent.getStringExtra("ProIndex");
        Description = intent.getStringExtra("Description");
        String a  = Preferences.getInstance(VideoViewActivity.this).getString(Preferences.KEY_VIDEO_BASE);

        subject.setText(Description);
        image = findViewById(R.id.image);
        video = findViewById(R.id.video);
        rl = findViewById(R.id.rl);

        if (mediaController == null) {
            mediaController = new MediaController(VideoViewActivity.this);
            mediaController.setAnchorView(video);
            video.setMediaController(mediaController);
        }
        if (ProIndex.equals("Images")){
            image.setVisibility(View.VISIBLE);
            video.setVisibility(View.GONE);
            rl.setVisibility(View.GONE);
            Glide.with(VideoViewActivity.this).load(a).into(image);
        }else{
            try {
                apkStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e("TAG", "Directory Created.");
                }
                outputFile = File.createTempFile("Convert",".mp4",apkStorage);//Create Output file in Main File
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e("TAG", "File Created");
                }
                FileOutputStream out = new FileOutputStream(outputFile);
                out.write(imageVal);
                out.close();
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("Error", e.toString());
            }
            path = outputFile.getPath();
            image.setVisibility(View.GONE);
            video.setVisibility(View.VISIBLE);
            rl.setVisibility(View.VISIBLE);
            mProgressBar = (ProgressBar) findViewById(R.id.progrss);
            mProgressBar.setProgress(0);
            mProgressBar.setMax(100);
            video.setVideoPath(a);
            video.requestFocus();
        }

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                video.seekTo(position);
                if (position == 0) {
                    video.start();
                }
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        mediaController.setAnchorView(video);
                    }
                });
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("CurrentPosition", video.getCurrentPosition());
        video.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("CurrentPosition");
        video.seekTo(position);
    }

    @Override
    public void onBackPressed() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        finish();
    }

}