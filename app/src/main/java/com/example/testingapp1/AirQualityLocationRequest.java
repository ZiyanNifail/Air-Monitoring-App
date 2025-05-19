package com.example.testingapp1;

public class AirQualityLocationRequest {
    private double latitude;
    private double longitude;

    public AirQualityLocationRequest(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // getters
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
