package com.example.happyapp.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.R;

import es.dmoral.toasty.Toasty;

public class PrivacySettingActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backButton;
    private Switch switchSensorCollection;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "sensor_collection_pref";
    private static final String KEY_SENSOR_COLLECTION_ENABLED = "sensor_collection_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_setting);

        backButton = findViewById(R.id.backButton);
        switchSensorCollection = findViewById(R.id.switch_sensor_collection);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        boolean isSensorCollectionEnabled = sharedPreferences.getBoolean(KEY_SENSOR_COLLECTION_ENABLED, false);
        switchSensorCollection.setChecked(isSensorCollectionEnabled);
        switchSensorCollection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startSensorCollection();
                } else {
                    stopSensorCollection();
                }
                saveSwitchState(isChecked);
            }
        });


        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            onBackPressed();
        }
    }

    private void startSensorCollection() {
        Toasty.info(this, "Sensor collection started", Toast.LENGTH_SHORT).show();
    }

    private void stopSensorCollection() {
        Toasty.info(this, "Sensor collection stopped", Toast.LENGTH_SHORT).show();
    }
    private void saveSwitchState(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_SENSOR_COLLECTION_ENABLED, isChecked);
        editor.apply();
    }
}