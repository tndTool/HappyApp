package com.example.happyapp.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.happyapp.R;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class PrivacySettingActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backButton;
    private SharedPreferences sharedPreferences;
    private Map<Integer, String> sensorPreferencesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_setting);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        backButton = findViewById(R.id.backButton);

        sensorPreferencesMap = new HashMap<>();
        sensorPreferencesMap.put(R.id.switch_sensor_light, "switchSensorLight");
        sensorPreferencesMap.put(R.id.switch_sensor_temperature, "switchSensorTemperature");
        sensorPreferencesMap.put(R.id.switch_sensor_humidity, "switchSensorHumidity");
        sensorPreferencesMap.put(R.id.switch_sensor_accelerometer, "switchSensorAccelerometer");
        sensorPreferencesMap.put(R.id.switch_sensor_gyroscope, "switchSensorGyroscope");
        sensorPreferencesMap.put(R.id.switch_sensor_magnetic, "switchSensorMagnetic");
        sensorPreferencesMap.put(R.id.switch_sensor_pressure, "switchSensorPressure");
        sensorPreferencesMap.put(R.id.switch_sensor_proximity, "switchSensorProximity");
        sensorPreferencesMap.put(R.id.switch_sensor_stepDetector, "switchSensorStepDetector");

        for (Map.Entry<Integer, String> entry : sensorPreferencesMap.entrySet()) {
            int switchId = entry.getKey();
            String preferenceKey = entry.getValue();

            Switch sensorSwitch = findViewById(switchId);
            sensorSwitch.setChecked(sharedPreferences.getBoolean(preferenceKey, true));
            sensorSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(preferenceKey, isChecked);
                editor.apply();
                showNotification(isChecked, preferenceKey);
            });
        }

        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            onBackPressed();
        }
    }

    private void showNotification(boolean isChecked, String preferenceKey) {
        String sensorName = getSensorName(preferenceKey);
        String message = isChecked ? sensorName + " sensor turned on" : sensorName + " sensor turned off";
        Toasty.info(this, message, Toasty.LENGTH_SHORT).show();
    }

    private String getSensorName(String preferenceKey) {
        // You can customize this method to return the appropriate sensor name based on the preference key
        switch (preferenceKey) {
            case "switchSensorLight":
                return "Light";
            case "switchSensorTemperature":
                return "Temperature";
            case "switchSensorHumidity":
                return "Humidity";
            case "switchSensorAccelerometer":
                return "Accelerometer";
            case "switchSensorGyroscope":
                return "Gyroscope";
            case "switchSensorMagnetic":
                return "Magnetic";
            case "switchSensorPressure":
                return "Pressure";
            case "switchSensorProximity":
                return "Proximity";
            case "switchSensorStepDetector":
                return "Step Detector";
            default:
                return "Unknown Sensor";
        }
    }
}