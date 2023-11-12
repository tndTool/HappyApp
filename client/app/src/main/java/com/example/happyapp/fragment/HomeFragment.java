package com.example.happyapp.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.happyapp.R;
import com.example.happyapp.sensor.SensorService;


public class HomeFragment extends Fragment implements SensorEventListener {
    private TextView tvMagnetic, tvTemperature, tvProximity, tvPressure, tvLight, tvHumidity,
            tvLatitude, tvLongitude, tvAccelerometer, tvGyroscope, tvStepDetector, tvHeartRate;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private SensorManager sensorManager;
    private Sensor sensorHumidity, sensorLight, sensorMagnetic, sensorPressure, sensorTemperature,
            sensorProximity, sensorAccelerometer, sensorGyroscope, sensorStepDetector, sensorHeartRate;
    ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        Context context = getContext();

//        Intent serviceIntent = new Intent(getContext(), SensorService.class);
//        ContextCompat.startForegroundService(getContext(), serviceIntent);

        tvMagnetic = rootView.findViewById(R.id.magnetic);
        tvTemperature = rootView.findViewById(R.id.temperature);
        tvProximity = rootView.findViewById(R.id.proximity);
        tvPressure = rootView.findViewById(R.id.pressure);
        tvLight = rootView.findViewById(R.id.light);
        tvHumidity = rootView.findViewById(R.id.humidity);
        tvLatitude = rootView.findViewById(R.id.latitude);
        tvLongitude = rootView.findViewById(R.id.longitude);
        tvAccelerometer = rootView.findViewById(R.id.accelerometer);
        tvGyroscope = rootView.findViewById(R.id.gyroscope);
        tvStepDetector = rootView.findViewById(R.id.stepDetector);
        tvHeartRate = rootView.findViewById(R.id.heartRate);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        sensorHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        sensorPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorHeartRate = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        sensorStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorHeartRate = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        sensorManager.registerListener(this, sensorHumidity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorPressure, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorHeartRate, SensorManager.SENSOR_DELAY_NORMAL);


        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                tvLatitude.setText("Latitude = " + location.getLatitude());
                tvLongitude.setText("Longitude = " + location.getLongitude());
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
        }

        sensorManager.registerListener(this, sensorHumidity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorPressure, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        return rootView;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Handle sensor events here
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Handle accelerometer events
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            tvAccelerometer.setText("Accelerometer: x = " + x + ", y = " + y + ", z = " + z);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Handle gyroscope events
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            tvGyroscope.setText("Gyroscope: x = " + x + ", y = " + y + ", z = " + z);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            tvHumidity.setText("Humidity = " + sensorEvent.values[0]);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            tvLight.setText("Light = " + sensorEvent.values[0]);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) {
            tvMagnetic.setText("Magnetic = " + sensorEvent.values[0]);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE) {
            tvPressure.setText("Pressure = " + sensorEvent.values[0]);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            tvTemperature.setText("Ambient temperature = " + sensorEvent.values[0]);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            tvProximity.setText("Proximity = " + sensorEvent.values[0]);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            tvStepDetector.setText("Step detector = " + sensorEvent.values[0]);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            tvHeartRate.setText("Heart rate = " + sensorEvent.values[0]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

}