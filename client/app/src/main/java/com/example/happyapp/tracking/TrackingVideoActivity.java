package com.example.happyapp.tracking;

import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.R;

public class TrackingVideoActivity extends AppCompatActivity {
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_video);

        videoView = findViewById(R.id.capturedVideoView);
        String videoUriString = getIntent().getStringExtra("videoUri");
        Uri videoUri = Uri.parse(videoUriString);
        videoView.setVideoURI(videoUri);
        videoView.start();
    }
}