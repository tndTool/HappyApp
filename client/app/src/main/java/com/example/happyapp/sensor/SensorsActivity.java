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
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.happyapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class SensorsActivity extends AppCompatActivity {

    private TextView tvWifi, tvBluetooth, tvGPS, tvNetworkLocation;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private WifiManager wifiManager;
    private LocationManager locationManager;
    private static final long DELAY_INTERVAL = 60 * 1000 * 1;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 100;
    private static final int REQUEST_CHANGE_WIFI_STATE = 101;
    private static final int REQUEST_ENABLE_BT = 1;
    private String gpsData, networkLocationData;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private void findView() {
        tvWifi = findViewById(R.id.wifi);
        tvBluetooth = findViewById(R.id.bluetooth);
        tvGPS = findViewById(R.id.gps);
        tvNetworkLocation = findViewById(R.id.networkLocation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        findView();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
                gpsData = "Longitude: " + String.valueOf(location.getLongitude()) + ", Latitude: " + String.valueOf(location.getLatitude()) + ", Attitude: " + String.valueOf(location.getAltitude());
                tvGPS.setText(gpsData);
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
        locationRequest.setInterval(DELAY_INTERVAL);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    networkLocationData = "Longitude: " + location.getLongitude() + ", Latitude: " + location.getLatitude() + ", Attitude: " + location.getAltitude();
                    tvNetworkLocation.setText(networkLocationData);
                }
            }
        }, Looper.getMainLooper());


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
                        int signalLevel = scanResult.level;

                        if (!ssid.isEmpty()) {
                            wifiNetworks.append("SSID: ").append(ssid).append(", ")
                                    .append("BSSID: ").append(bssid).append(", ")
                                    .append("Signal Level: ").append(signalLevel).append("dBm")
                                    .append("; ");
                        }
                    }

                    tvWifi.setText(wifiNetworks.toString());
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

                String bluetoothInfo = bluetoothInfoBuilder.toString();
                tvBluetooth.setText(bluetoothInfo);
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
}