package com.example.happyapp.tracking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.R;

import es.dmoral.toasty.Toasty;

public class TrackingCameraActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView photoImageView;
    private Button backButton, nextButton;
    private String[] behaviors = {"Eating", "Drinking"};
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_camera);
        findViews();
        setPhoto();
        setListeners();
        setAutoCompleteTextViewAdapter();
    }

    private void findViews() {
        photoImageView = findViewById(R.id.capturedImageView);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
    }

    private void setPhoto() {
        Bitmap photo = getIntent().getParcelableExtra("photo");
        photoImageView.setImageBitmap(photo);
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

            Bitmap photo = getIntent().getParcelableExtra("photo");
            Intent intent;
            if (selectedBehavior.equals("Eating")) {
                intent = new Intent(this, TrackingCameraEatingActivity.class);
            } else if (selectedBehavior.equals("Drinking")) {
                intent = new Intent(this, TrackingCameraDrinkingActivity.class);
            } else {
                return;
            }
            intent.putExtra("photo", photo);
            startActivity(intent);
        }
    }
}