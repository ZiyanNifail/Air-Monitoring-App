package com.example.testingapp1;
import com.google.gson.annotations.SerializedName;

public class AirQualityIndex {
    @SerializedName("code")
    private String code;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("aqi")
    private int aqi;

    @SerializedName("aqiDisplay")
    private String aqiDisplay;

    // Getters
    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }
    public int getAqi() { return aqi; }
    public String getAqiDisplay() { return aqiDisplay; }

    // Setters (optional but recommended)
    public void setCode(String code) { this.code = code; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setAqi(int aqi) { this.aqi = aqi; }
    public void setAqiDisplay(String aqiDisplay) { this.aqiDisplay = aqiDisplay; }
}