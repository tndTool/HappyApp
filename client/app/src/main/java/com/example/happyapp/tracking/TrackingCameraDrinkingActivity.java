package com.example.happyapp.tracking;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.R;

public class TrackingCameraDrinkingActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_camera_drinking);

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            onBackPressed();
        }
    }
}