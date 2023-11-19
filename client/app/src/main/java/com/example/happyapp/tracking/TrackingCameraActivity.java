package com.example.happyapp.tracking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.R;

public class TrackingCameraActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView photoImageView;
    private RadioButton eatingBehavior, drinkingBehavior;
    private Button backButton, nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_camera);

        photoImageView = findViewById(R.id.capturedImageView);
        eatingBehavior = findViewById(R.id.eatingBehavior);
        drinkingBehavior = findViewById(R.id.drinkingBehavior);
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        Bitmap photo = getIntent().getParcelableExtra("photo");
        photoImageView.setImageBitmap(photo);

        backButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        eatingBehavior.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    eatingBehavior.setTextColor(Color.WHITE);
                    drinkingBehavior.setTextColor(Color.BLACK);
                }
            }
        });

        drinkingBehavior.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    eatingBehavior.setTextColor(Color.BLACK);
                    drinkingBehavior.setTextColor(Color.WHITE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            onBackPressed();
            finish();
        } else if (v.getId() == R.id.nextButton) {
            if (eatingBehavior.isChecked()) {
                Bitmap photo = getIntent().getParcelableExtra("photo");
                Intent intent = new Intent(TrackingCameraActivity.this, TrackingCameraEatingActivity.class);
                intent.putExtra("photo", photo);
                startActivity(intent);
            }
            if (drinkingBehavior.isChecked()) {
                Bitmap photo = getIntent().getParcelableExtra("photo");
                Intent intent = new Intent(TrackingCameraActivity.this, TrackingCameraDrinkingActivity.class);
                intent.putExtra("photo", photo);
                startActivity(intent);
            }
        }
    }

}