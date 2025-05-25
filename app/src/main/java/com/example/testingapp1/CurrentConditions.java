package com.example.testingapp1;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CurrentConditions {
    @SerializedName("indexes")
    private List<AirQualityIndex> indexes;

    // Getter method
    public List<AirQualityIndex> getIndexes() {
        return indexes;
    }

    // Setter method (optional but recommended)
    public void setIndexes(List<AirQualityIndex> indexes) {
        this.indexes = indexes;
    }
}
