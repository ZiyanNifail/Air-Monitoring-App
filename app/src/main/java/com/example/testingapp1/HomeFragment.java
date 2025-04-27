package com.example.testingapp1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Random;

public class HomeFragment extends Fragment {
    private TextView aqiValue, aqiStatus, tempValue, humidityValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        aqiValue = view.findViewById(R.id.aqi_value);
        aqiStatus = view.findViewById(R.id.aqi_status);
        tempValue = view.findViewById(R.id.temp_value);
        humidityValue = view.findViewById(R.id.humidity_value);

        // Simulate data - in real app, you'd fetch from API
        updateAirQualityData();

        return view;
    }

    private void updateAirQualityData() {
        Random random = new Random();

        // Simulate AQI (0-500)
        int aqi = random.nextInt(500);
        aqiValue.setText(String.valueOf(aqi));

        // Set status based on AQI
        if (aqi <= 50) {
            aqiStatus.setText("Good");
            aqiStatus.setTextColor(getResources().getColor(R.color.green));
        } else if (aqi <= 100) {
            aqiStatus.setText("Moderate");
            aqiStatus.setTextColor(getResources().getColor(R.color.yellow));
        } else if (aqi <= 150) {
            aqiStatus.setText("Unhealthy for Sensitive Groups");
            aqiStatus.setTextColor(getResources().getColor(R.color.orange));
        } else if (aqi <= 200) {
            aqiStatus.setText("Unhealthy");
            aqiStatus.setTextColor(getResources().getColor(R.color.red));
        } else {
            aqiStatus.setText("Hazardous");
            aqiStatus.setTextColor(getResources().getColor(R.color.purple));
        }

        // Simulate temperature (0-40°C)
        int temp = random.nextInt(40);
        tempValue.setText(temp + "°C");

        // Simulate humidity (0-100%)
        int humidity = random.nextInt(100);
        humidityValue.setText(humidity + "%");
    }
}