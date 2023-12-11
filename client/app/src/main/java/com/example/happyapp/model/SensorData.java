package com.example.happyapp.model;

public class SensorData {
    private String sensor;
    private String value;

    public SensorData(String sensor, String value) {
        this.sensor = sensor;
        this.value = value;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
