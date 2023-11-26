package com.example.happyapp.tracking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.R;

import es.dmoral.toasty.Toasty;

public class TrackingVideoActivity extends AppCompatActivity implements View.OnClickListener {
    private VideoView videoView;
    private MediaController mediaController;
    private Button backButton, nextButton;
    private String[] behaviors = {"Eating", "Drinking"};
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_video);
        findViews();
        setVideo();
        setListeners();
        setAutoCompleteTextViewAdapter();
    }

    private void findViews() {
        videoView = findViewById(R.id.capturedVideoView);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
    }

    private void setVideo() {
        String videoUriString = getIntent().getStringExtra("videoUri");
        Uri videoUri = Uri.parse(videoUriString);
        videoView.setVideoURI(videoUri);

        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.start();
    }

    private void setListeners() {
        backButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    private void setAutoCompleteTextViewAdapter() {
        adapterItems = new ArrayAdapter<>(this, R.layout.list_behaviors, behaviors);
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            String item = adapterView.getItemAtPosition(i).toString();
            Toasty.info(getApplicationContext(), "Item: " + item, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            onBackPressed();
            finish();
        } else if (v.getId() == R.id.nextButton) {
            String selectedBehavior = autoCompleteTextView.getText().toString().trim();
            if (selectedBehavior.isEmpty()) {
                autoCompleteTextView.setError("Selection is required");
                return;
            }

            Uri videoUri = Uri.parse(getIntent().getStringExtra("videoUri"));

            Intent intent;
            if (selectedBehavior.equals("Eating")) {
                intent = new Intent(this, TrackingCameraEatingActivity.class);
            } else if (selectedBehavior.equals("Drinking")) {
                intent = new Intent(this, TrackingCameraDrinkingActivity.class);
            } else {
                return;
            }

            intent.putExtra("videoUri", videoUri.toString());
            startActivity(intent);
        }
    }
}