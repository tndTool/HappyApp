package com.example.happyapp.tracking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_camera);

        photoImageView = findViewById(R.id.capturedImageView);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        Bitmap photo = getIntent().getParcelableExtra("photo");
        photoImageView.setImageBitmap(photo);

        backButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        adapterItems = new ArrayAdapter<String>(this, R.layout.list_behaviors, behaviors);
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toasty.info(getApplicationContext(), "Item: " + item, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            onBackPressed();
            finish();
        } else if (v.getId() == R.id.nextButton) {
            String selectedBehavior = autoCompleteTextView.getText().toString();
            if (selectedBehavior.isEmpty()){
                autoCompleteTextView.setError("Select is required");
                return;
            }

            if (selectedBehavior.equals("Eating")) {
                Bitmap photo = getIntent().getParcelableExtra("photo");
                Intent intent = new Intent(TrackingCameraActivity.this, TrackingCameraEatingActivity.class);
                intent.putExtra("photo", photo);
                startActivity(intent);
            } else if (selectedBehavior.equals("Drinking")) {
                Bitmap photo = getIntent().getParcelableExtra("photo");
                Intent intent = new Intent(TrackingCameraActivity.this, TrackingCameraDrinkingActivity.class);
                intent.putExtra("photo", photo);
                startActivity(intent);
            }
        }
    }
}