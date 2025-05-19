package com.example.testingapp1;

import java.util.List;

public class AirQualityResponse {
    public List<CurrentConditions> currentConditions;

    public static class CurrentConditions {
        public int aqi;
        public String category;
        // Add other fields as per API response
    }
}
