package com.example.testingapp1;

public class AirQualityRequest {
    private final Location location; // Made final since it's never modified

    public AirQualityRequest(Location location) {
        this.location = location;
    }

    public static class Location {
        private final double latitude;
        private final double longitude;

        public Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        // Add getters
        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    // Add getter
    public Location getLocation() {
        return location;
    }
}