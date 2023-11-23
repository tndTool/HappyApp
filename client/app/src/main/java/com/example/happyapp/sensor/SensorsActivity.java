package com.example.happyapp.sensor;

import android.Manifest;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.happyapp.R;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class SensorsActivity extends AppCompatActivity implements SensorEventListener {

    private TextView tvMagnetic, tvTemperature, tvProximity, tvPressure, tvLight, tvHumidity,
            tvLatitude, tvLongitude, tvAccelerometer, tvGyroscope, tvStepDetector, tvListSensor,
            tvAltitude, tvBluetoothName, tvBluetoothMAC;
    private List<Sensor> deviceSensors;
    private SensorManager sensorManagers;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private WifiManager wifiManager;
    private BroadcastReceiver wifiScanReceiver;
    private LocationManager locationManager;
    private Vibrator vibrator;
    private Sensor sensorHumidity, sensorLight, sensorMagnetic, sensorPressure, sensorTemperature,
            sensorProximity, sensorAccelerometer, sensorGyroscope, sensorStepDetector;
    ;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 100;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private final static int REQUEST_ENABLE_BT = 1;
    private int stepDetect = 0;
    private SharedPreferences sharedPreferences;
    private String userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userEmail = getEmailFromSharedPreferences();
//        Intent serviceIntent = new Intent(getContext(), SensorService.class);
//        ContextCompat.startForegroundService(getContext(), serviceIntent);

        tvMagnetic = findViewById(R.id.magnetic);
        tvTemperature = findViewById(R.id.temperature);
        tvProximity = findViewById(R.id.proximity);
        tvPressure = findViewById(R.id.pressure);
        tvLight = findViewById(R.id.light);
        tvHumidity = findViewById(R.id.humidity);
        tvLatitude = findViewById(R.id.latitude);
        tvLongitude = findViewById(R.id.longitude);
        tvAccelerometer = findViewById(R.id.accelerometer);
        tvGyroscope = findViewById(R.id.gyroscope);
        tvStepDetector = findViewById(R.id.stepDetector);
        tvAltitude = findViewById(R.id.altitude);
        tvListSensor = findViewById(R.id.listSensor);
        tvBluetoothName = findViewById(R.id.bluetoothName);
        tvBluetoothMAC = findViewById(R.id.bluetoothMAC);


        sensorManagers = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        deviceSensors = sensorManagers.getSensorList(Sensor.TYPE_ALL);
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


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                tvLongitude.setText("Longitude: " + String.valueOf(location.getLongitude()));
                tvLatitude.setText("Latitude: " + String.valueOf(location.getLatitude()));
                tvAltitude.setText("Attitude: " + String.valueOf(location.getAltitude()));
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
//        startBluetoothScan();
        printSensors();
    }

//    private void startWifiScan() {
//        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//
//        if (wifiManager == null || !wifiManager.isWifiEnabled()) {
//            // Wi-Fi is not supported or not enabled
//            // Handle this scenario appropriately (e.g., show an error message)
//            return;
//        }
//
//        // Request the necessary runtime permission if not granted
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_ACCESS_FINE_LOCATION);
//            return;
//        }
//
//        wifiScanReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
//                    List<ScanResult> scanResults = wifiManager.getScanResults();
//                    for (ScanResult scanResult : scanResults) {
//                        String ssid = scanResult.SSID;
//                        String bssid = scanResult.BSSID;
//
//                        // Display the ssid and bssid on the screen
//                        // Replace `textViewSSID` and `textViewBSSID` with your TextViews
//                        textViewSSID.setText(ssid);
//                        textViewBSSID.setText(bssid);
//                    }
//                }
//            }
//        };
//
//        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        wifiManager.startScan();
//    }

//    private void startBluetoothScan() {
//        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
//
//        if (bluetoothAdapter == null) {
//            Toasty.error(SensorsActivity.this, "Not found BT!", Toast.LENGTH_SHORT).show();
//        } else {
//            if (!bluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            }
//        }
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//
//
//        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//        scanCallback = new ScanCallback() {
//            @Override
//            public void onScanResult(int callbackType, ScanResult result) {
//                BluetoothDevice device = result.getDevice();
//                String deviceName = device.getName();
//                String deviceAddress = device.getAddress();
//
//                tvBluetoothName.setText(deviceName);
//                tvBluetoothMAC.setText(deviceAddress);
//            }
//
//            @Override
//            public void onScanFailed(int errorCode) {
//                // Handle scan failure
//                // Show an error message or take appropriate action
//            }
//        };
//
//        ScanSettings scanSettings = new ScanSettings.Builder()
//                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                .build();
//
//        bluetoothLeScanner.startScan(null, scanSettings, scanCallback);
//    }


    private void printSensors() {
        for (Sensor sensor : deviceSensors) {
            tvListSensor.setText(tvListSensor.getText() + "\n" + sensor.getName());
        }
    }

    protected void onPause() {
        super.onPause();
        sensorManagers.unregisterListener(this);
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

            tvAccelerometer.setText("Accelerometer: x = " + x + ", y = " + y + ", z = " + z);
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepDetect = (int) (stepDetect + sensorEvent.values[0]);
            tvStepDetector.setText("Step detector: " + String.valueOf(stepDetect));
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