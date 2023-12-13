package com.example.happyapp.sensor;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.happyapp.R;
import com.example.happyapp.api.ApiHelper;
import com.example.happyapp.dialog.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SensorsActivity extends AppCompatActivity implements SensorEventListener {

    private TextView tvWifi, tvBluetooth;
    private SensorManager sensorManagers;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private WifiManager wifiManager;
    private LocationManager locationManager;
    private Vibrator vibrator;
    private Sensor sensorHumidity, sensorLight, sensorMagnetic, sensorPressure, sensorTemperature,
            sensorProximity, sensorAccelerometer, sensorGyroscope, sensorStepDetector;
    ;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 100, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0,
            REQUEST_CHANGE_WIFI_STATE = 101, REQUEST_ENABLE_BT = 1;
    private static final long DELAY_INTERVAL = 10000;
    private int stepDetect = 0;
    private SharedPreferences sharedPreferences;
    private String userEmail, magneticData, temperatureData, proximityData, pressureData, lightData, humidityData,
            gpsData, accelerometerData, gyroscopeData, stepDetectorData, wifiData, bluetoothData;
    private LoadingDialog loadingDialog;
    private Handler handler;
    private Runnable apiRunnable;

    private void findView() {
        tvWifi = findViewById(R.id.wifi);
        tvBluetooth = findViewById(R.id.bluetooth);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        loadingDialog = new LoadingDialog(SensorsActivity.this);
        userEmail = getEmailFromSharedPreferences();

        findView();

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


        if (ActivityCompat.checkSelfPermission(SensorsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(SensorsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(SensorsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SensorsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ACCESS_FINE_LOCATION);
        } else {
            Toasty.info(getApplicationContext(), "Location & file access Permission Granted", Toast.LENGTH_SHORT);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH},
                    REQUEST_ENABLE_BT);
            return;
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

        handler = new Handler(Looper.getMainLooper());
        apiRunnable = new Runnable() {
            @Override
            public void run() {
                if (isLoggedIn()) {
                    magneticData = (sensorMagnetic == null) ? "" : magneticData;
                    temperatureData = (sensorTemperature == null) ? "" : temperatureData;
                    proximityData = (sensorProximity == null) ? "" : proximityData;
                    pressureData = (sensorPressure == null) ? "" : pressureData;
                    lightData = (sensorLight == null) ? "" : lightData;
                    humidityData = (sensorHumidity == null) ? "" : humidityData;
                    gpsData = (locationManager == null) ? "" : gpsData;
                    accelerometerData = (sensorAccelerometer == null) ? "" : accelerometerData;
                    gyroscopeData = (sensorGyroscope == null) ? "" : gyroscopeData;
                    stepDetectorData = (sensorStepDetector == null) ? "" : stepDetectorData;
                    wifiData = (wifiManager == null) ? "" : wifiData;
                    bluetoothData = (bluetoothLeScanner == null) ? "" : bluetoothData;
                    loadingDialog.show();
                    ApiHelper.saveDataSensor(userEmail, magneticData, temperatureData, proximityData,
                            pressureData, lightData, humidityData, gpsData, accelerometerData, gyroscopeData,
                            stepDetectorData, wifiData, bluetoothData, new Callback() {
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (response.isSuccessful()) {
                                                Toasty.success(SensorsActivity.this, "Save sensor data successfully!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                try {
                                                    JSONObject errorResponse = new JSONObject(response.body().string());
                                                    String errorMessage = errorResponse.getString("error");
                                                    Toasty.error(SensorsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                                } catch (JSONException | IOException e) {
                                                    e.printStackTrace();
                                                    Toasty.error(SensorsActivity.this, "Failed to save sensor data.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            loadingDialog.dismiss();
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toasty.error(SensorsActivity.this, "External sever in SensorsActivity.", Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismiss();
                                        }
                                    });
                                }
                            });
                }
                handler.postDelayed(apiRunnable, DELAY_INTERVAL);
            }
        };

        startWifiScan();
       startBluetoothScan();
    }


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

                        wifiNetworks.append("SSID: ").append(ssid).append(", ")
                                .append("BSSID: ").append(bssid).append("; ");
                    }

                    tvWifi.setText(wifiNetworks.toString());
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
            Toasty.error(SensorsActivity.this, "Not found BT!", Toast.LENGTH_SHORT).show();
            if (bluetoothAdapter != null) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                String deviceAddress = device.getAddress();

                String bluetoothInfo = "Name: " + deviceName + ", Address: " + deviceAddress + ";";
                tvBluetooth.setText(bluetoothInfo);
                bluetoothData = bluetoothInfo;
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
        handler.removeCallbacks(apiRunnable);
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

        handler.post(apiRunnable);
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