package com.example.testingapp1;
import com.google.gson.annotations.SerializedName;

public class AirQualityResponse {
    @SerializedName("currentConditions")
    private CurrentConditions currentConditions;

    // Getter method
    public CurrentConditions getCurrentConditions() {
        return currentConditions;
    }

    // Setter method (optional but recommended)
    public void setCurrentConditions(CurrentConditions currentConditions) {
        this.currentConditions = currentConditions;
    }
}