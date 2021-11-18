package com.example.genius.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.github.chrisbanes.photoview.PhotoView;

import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;

public class VideoViewActivity extends AppCompatActivity {

    Intent intent;
    ProgressBar mProgressBar;
    String ProIndex, BannerPath;
    PhotoView image;
    VideoView video;
    FrameLayout rl;
    boolean isImage;
    TextView subject;
    String Description;
    byte[] imageVal;
    private MediaController mediaController;
    private int position = 0;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        subject = findViewById(R.id.subject);

        intent = getIntent();
        ProIndex = intent.getStringExtra("ProIndex");
        Description = intent.getStringExtra("Description");
        String a  = Preferences.getInstance(VideoViewActivity.this).getString(Preferences.KEY_VIDEO_BASE);
        imageVal = Base64.decode(a, Base64.DEFAULT);
        try {

            FileOutputStream out = new FileOutputStream(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            + "/Convert.mp4");
            out.write(imageVal);
            out.close();
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("Error", e.toString());

        }
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/Convert.mp4";
        subject.setText(Description);
        image = findViewById(R.id.image);
        video = findViewById(R.id.video);
        rl = findViewById(R.id.rl);

        if (mediaController == null) {
            mediaController = new MediaController(VideoViewActivity.this);
            // Set the videoView that acts as the anchor for the MediaController.
            mediaController.setAnchorView(video);
            // Set MediaController for VideoView
            video.setMediaController(mediaController);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        video.setVisibility(View.VISIBLE);
        rl.setVisibility(View.VISIBLE);

        mProgressBar = findViewById(R.id.progrss);
        mProgressBar.setProgress(0);
        mProgressBar.setMax(100);

        video.setVideoPath(path);

        video.requestFocus();

        video.setOnPreparedListener(mediaPlayer -> {
            video.seekTo(position);
            if (position == 0) {
                video.start();
            }
            // When video Screen change size.
            mediaPlayer.setOnVideoSizeChangedListener((mp, width, height) -> {
                // Re-Set the videoView that acts as the anchor for the MediaController
                mediaController.setAnchorView(video);
            });
        });
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Store current position.
        savedInstanceState.putInt("CurrentPosition", video.getCurrentPosition());
        video.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Get saved position.
        position = savedInstanceState.getInt("CurrentPosition");
        video.seekTo(position);
    }

    @Override
    public void onBackPressed() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //startActivity(new Intent(getApplicationContext(), GalleryRegister_Activity.class));
        finish();
    }

}