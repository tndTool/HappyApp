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

public class NotificationSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backButton;
    private Switch switchNotification;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "notification_pref";
    private static final String KEY_SENSOR_COLLECTION_ENABLED = "notification_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);

        backButton = findViewById(R.id.backButton);
        switchNotification = findViewById(R.id.switch_notification);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        boolean isSensorCollectionEnabled = sharedPreferences.getBoolean(KEY_SENSOR_COLLECTION_ENABLED, false);
        switchNotification.setChecked(isSensorCollectionEnabled);
        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startNotification();
                } else {
                    stopNotification();
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

    private void startNotification() {
        Toasty.info(this, "Notification started", Toast.LENGTH_SHORT).show();
    }

    private void stopNotification() {
        Toasty.info(this, "Notification stopped", Toast.LENGTH_SHORT).show();
    }

    private void saveSwitchState(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_SENSOR_COLLECTION_ENABLED, isChecked);
        editor.apply();
    }
}