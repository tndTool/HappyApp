package com.example.happyapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.happyapp.api.ApiHelper;
import com.example.happyapp.databinding.ActivityMainBinding;
import com.example.happyapp.fragment.HomeFragment;
import com.example.happyapp.fragment.ProfileFragment;
import com.example.happyapp.tracking.camera.TrackingCameraActivity;
import com.example.happyapp.tracking.video.TrackingVideoActivity;
import com.example.happyapp.worker.SaveDataSensorWorker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private ActivityMainBinding binding;
    private Uri videoUri;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> recordVideoLauncher;


    // Sensor
    private SensorManager sensorManagers;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private WifiManager wifiManager;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Vibrator vibrator;
    private Sensor sensorHumidity, sensorLight, sensorMagnetic, sensorPressure, sensorTemperature,
            sensorProximity, sensorAccelerometer, sensorGyroscope, sensorStepDetector;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 100;
    private static final int REQUEST_CHANGE_WIFI_STATE = 101;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long DELAY_INTERVAL = 60 * 1000 * 15;
    private static final long DELAY_LOCATION = 60 * 1000 * 14;
    private static final String TAG_WORKER = "saveDataSensorWorker";
    private int stepDetect = 0;
    private SharedPreferences sharedPreferences;
    private String userEmail, magneticData, temperatureData, proximityData, pressureData, lightData, humidityData,
            gpsData, networkLocationData, accelerometerData, gyroscopeData, stepDetectorData, wifiData, bluetoothData;

    private Handler handler;
    private Runnable apiRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        if (getIntent().getBooleanExtra("showVideoPopup", false)) {
            showRecordVideoDialog();
        }

        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    replaceFragment(new HomeFragment());
                } else if (item.getItemId() == R.id.profile) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    replaceFragment(profileFragment);
                }
                return true;
            }
        });

        binding.cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if (imageBitmap != null) {
                            Intent displayIntent = new Intent(this, TrackingCameraActivity.class);
                            displayIntent.putExtra("photo", imageBitmap);
                            startActivity(displayIntent);
                        } else {
                            Toasty.error(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toasty.error(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        recordVideoLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    videoUri = data.getData();
                    if (videoUri != null) {
                        Intent displayIntent = new Intent(this, TrackingVideoActivity.class);
                        displayIntent.putExtra("videoUri", videoUri.toString());
                        startActivity(displayIntent);
                    } else {
                        Toasty.error(this, "Failed to capture video", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        // Sensor
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        userEmail = getEmailFromSharedPreferences();

        sensorManagers = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        assert sensorManagers != null;

        sensorHumidity = sensorManagers.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        sensorLight = sensorManagers.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorMagnetic = sensorManagers.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        sensorPressure = sensorManagers.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorTemperature = sensorManagers.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorProximity = sensorManagers.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorAccelerometer = sensorManagers.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorGyroscope = sensorManagers.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorStepDetector = sensorManagers.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);


        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ACCESS_FINE_LOCATION);
        } else {
            Toasty.info(getApplicationContext(), "Location & file access Permission Granted", Toast.LENGTH_SHORT);
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                gpsData = "Longitude: " + String.valueOf(location.getLongitude()) + ", Latitude: " + String.valueOf(location.getLatitude()) + ", Attitude: " + String.valueOf(location.getAltitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(DELAY_LOCATION);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    networkLocationData = "Longitude: " + location.getLongitude() + ", Latitude: " + location.getLatitude() + ", Attitude: " + location.getAltitude();
                }
            }
        }, Looper.getMainLooper());

        startWifiScan();

        startBluetoothScan();

        checkDataSensor();

        PeriodicWorkRequest saveDataSensorWork = new PeriodicWorkRequest.Builder(SaveDataSensorWorker.class, 15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                TAG_WORKER,
                ExistingPeriodicWorkPolicy.KEEP,
                saveDataSensorWork
        );
    }

    private void checkDataSensor() {
        magneticData = (sensorMagnetic == null) ? "" : magneticData;
        temperatureData = (sensorTemperature == null) ? "" : temperatureData;
        proximityData = (sensorProximity == null) ? "" : proximityData;
        pressureData = (sensorPressure == null) ? "" : pressureData;
        lightData = (sensorLight == null) ? "" : lightData;
        humidityData = (sensorHumidity == null) ? "" : humidityData;
        accelerometerData = (sensorAccelerometer == null) ? "" : accelerometerData;
        gyroscopeData = (sensorGyroscope == null) ? "" : gyroscopeData;
        stepDetectorData = (sensorStepDetector == null) ? "" : stepDetectorData;

        enqueueSensorDataWorker(userEmail, magneticData, temperatureData, proximityData, pressureData,
                lightData, humidityData, gpsData, networkLocationData, accelerometerData, gyroscopeData, stepDetectorData,
                wifiData, bluetoothData);
    }

    private void enqueueSensorDataWorker(String userEmail, String magneticData, String temperatureData, String proximityData,
                                         String pressureData, String lightData, String humidityData, String gpsData, String networkLocationData, String accelerometerData,
                                         String gyroscopeData, String stepDetectorData, String wifiData, String bluetoothData) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();

        Data inputData = new Data.Builder()
                .putString("userEmail", userEmail)
                .putString("magneticData", magneticData)
                .putString("temperatureData", temperatureData)
                .putString("proximityData", proximityData)
                .putString("pressureData", pressureData)
                .putString("lightData", lightData)
                .putString("humidityData", humidityData)
                .putString("gpsData", gpsData)
                .putString("networkLocationData", networkLocationData)
                .putString("accelerometerData", accelerometerData)
                .putString("gyroscopeData", gyroscopeData)
                .putString("stepDetectorData", stepDetectorData)
                .putString("wifiData", wifiData)
                .putString("bluetoothData", bluetoothData)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SaveDataSensorWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();

        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueue(workRequest);
    }

    private void showRecordVideoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Record Video")
                .setMessage("Do you want to record a video?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dispatchRecordVideoIntent();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


    private void dispatchTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            takePictureLauncher.launch(cameraIntent);
        }
    }

    private void dispatchRecordVideoIntent() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            recordVideoLauncher.launch(videoIntent);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    // Sensor
    private void startWifiScan() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null || !wifiManager.isWifiEnabled()) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                    REQUEST_CHANGE_WIFI_STATE);
            return;
        }

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                    @SuppressLint("MissingPermission") List<ScanResult> scanResults = wifiManager.getScanResults();
                    StringBuilder wifiNetworks = new StringBuilder();

                    for (ScanResult scanResult : scanResults) {
                        String ssid = scanResult.SSID;
                        String bssid = scanResult.BSSID;
                        int signalLevel = scanResult.level;

                        if (!ssid.isEmpty()) {
                            wifiNetworks.append("SSID: ").append(ssid).append(", ")
                                    .append("BSSID: ").append(bssid).append(", ")
                                    .append("Signal Level: ").append(signalLevel).append("dBm")
                                    .append("; ");
                        }
                    }

                    wifiData = wifiNetworks.toString();
                }
            }
        };
        wifiManager.startScan();
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    private void startBluetoothScan() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH},
                    REQUEST_ENABLE_BT);
            return;
        }

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                String deviceAddress = device.getAddress();

                StringBuilder bluetoothInfoBuilder = new StringBuilder();
                if (deviceName != null) {
                    bluetoothInfoBuilder.append("Name: ").append(deviceName).append(", ");
                }
                bluetoothInfoBuilder.append("Address: ").append(deviceAddress).append(";");

                bluetoothData = bluetoothInfoBuilder.toString();
            }

            @Override
            public void onScanFailed(int errorCode) {
            }
        };

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        bluetoothLeScanner.startScan(null, scanSettings, scanCallback);
    }

    protected void onPause() {
        super.onPause();
        sensorManagers.unregisterListener(this);

        if (handler != null && apiRunnable != null) {
            handler.removeCallbacks(apiRunnable);
        }
    }

    protected void onResume() {
        super.onResume();

        boolean isSwitchSensorLight = sharedPreferences.getBoolean("switchSensorLight", true);
        boolean isSwitchSensorHumidity = sharedPreferences.getBoolean("switchSensorHumidity", true);
        boolean isSwitchSensorPressure = sharedPreferences.getBoolean("switchSensorPressure", true);
        boolean isSwitchSensorProximity = sharedPreferences.getBoolean("switchSensorProximity", true);
        boolean isSwitchSensorMagnetic = sharedPreferences.getBoolean("switchSensorMagnetic", true);
        boolean isSwitchSensorGyroscope = sharedPreferences.getBoolean("switchSensorGyroscope", true);
        boolean isSwitchSensorTemperature = sharedPreferences.getBoolean("switchSensorTemperature", true);
        boolean isSwitchSensorStepDetector = sharedPreferences.getBoolean("switchSensorStepDetector", true);
        boolean isSwitchSensorAccelerometer = sharedPreferences.getBoolean("switchSensorAccelerometer", true);

        registerSensorListener(isSwitchSensorHumidity, sensorHumidity);
        registerSensorListener(isSwitchSensorLight, sensorLight);
        registerSensorListener(isSwitchSensorPressure, sensorPressure);
        registerSensorListener(isSwitchSensorProximity, sensorProximity);
        registerSensorListener(isSwitchSensorMagnetic, sensorMagnetic);
        registerSensorListener(isSwitchSensorTemperature, sensorTemperature);
        registerSensorListener(isSwitchSensorAccelerometer, sensorAccelerometer);
        registerSensorListener(isSwitchSensorGyroscope, sensorGyroscope);
        registerSensorListener(isSwitchSensorStepDetector, sensorStepDetector);

        if (handler != null && apiRunnable != null) {
            handler.postDelayed(apiRunnable, 10000);
        }
    }

    private void registerSensorListener(boolean shouldRegister, Sensor sensor) {
        if (shouldRegister) {
            sensorManagers.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            accelerometerData = "x=" + x + ", y=" + y + ", z=" + z;
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            gyroscopeData = "x=" + x + ", y=" + y + ", z=" + z;
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            humidityData = String.valueOf(sensorEvent.values[0]);
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            lightData = String.valueOf(sensorEvent.values[0]);
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) {
            magneticData = String.valueOf(sensorEvent.values[0]);
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE) {
            pressureData = String.valueOf(sensorEvent.values[0]);
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            temperatureData = String.valueOf(sensorEvent.values[0]);
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximityData = String.valueOf(sensorEvent.values[0]);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepDetect = (int) (stepDetect + sensorEvent.values[0]);
            stepDetectorData = String.valueOf(stepDetect);
        }

        handler = new Handler();
        apiRunnable = new Runnable() {
            @Override
            public void run() {
                if (isLoggedIn()) {
                    ApiHelper.saveDataSensor(userEmail, magneticData, temperatureData, proximityData, pressureData,
                            lightData, humidityData, gpsData, networkLocationData, accelerometerData, gyroscopeData,
                            stepDetectorData, wifiData, bluetoothData, new Callback() {
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (response.isSuccessful()) {
                                            } else {
                                                try {
                                                    JSONObject errorResponse = new JSONObject(response.body().string());
                                                    String errorMessage = errorResponse.getString("error");
                                                    Toasty.error(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                                } catch (JSONException | IOException e) {
                                                    e.printStackTrace();
                                                    Toasty.error(MainActivity.this, "Failed to save sensor data.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toasty.error(MainActivity.this, "External sever error.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                }
                handler.postDelayed(apiRunnable, DELAY_INTERVAL);
            }
        };
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private String getEmailFromSharedPreferences() {
        return sharedPreferences.getString("email", "");
    }

    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

}