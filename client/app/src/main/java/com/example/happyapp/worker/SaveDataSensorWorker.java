package com.example.happyapp.worker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.happyapp.api.ApiHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SaveDataSensorWorker extends Worker {
    public SaveDataSensorWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String userEmail = getInputData().getString("userEmail");
        if (!userEmail.isEmpty()) {
            String magneticData = getInputData().getString("magneticData");
            String temperatureData = getInputData().getString("temperatureData");
            String proximityData = getInputData().getString("proximityData");
            String pressureData = getInputData().getString("pressureData");
            String lightData = getInputData().getString("lightData");
            String humidityData = getInputData().getString("humidityData");
            String gpsData = getInputData().getString("gpsData");
            String accelerometerData = getInputData().getString("accelerometerData");
            String gyroscopeData = getInputData().getString("gyroscopeData");
            String stepDetectorData = getInputData().getString("stepDetectorData");
            String wifiData = getInputData().getString("wifiData");
            String bluetoothData = getInputData().getString("bluetoothData");

            ApiHelper.saveDataSensor(userEmail, magneticData, temperatureData, proximityData,
                    pressureData, lightData, humidityData, gpsData, accelerometerData, gyroscopeData,
                    stepDetectorData, wifiData, bluetoothData, new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                showToast("Save sensor data successfully!", Toast.LENGTH_SHORT);
                            } else {
                                try {
                                    JSONObject errorResponse = new JSONObject(response.body().string());
                                    String errorMessage = errorResponse.getString("error");
                                    showToast(errorMessage, Toast.LENGTH_SHORT);
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                    showToast("Failed to save sensor data.", Toast.LENGTH_SHORT);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            showToast("External server error.", Toast.LENGTH_SHORT);
                        }
                    });
        }

        return Result.success();
    }

    private void showToast(final String message, final int duration) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toasty.success(getApplicationContext(), message, duration).show();
            }
        });
    }
}