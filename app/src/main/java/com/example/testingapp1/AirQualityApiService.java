package com.example.testingapp1;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Body;
import com.example.testingapp1.AirQualityResponse;
import com.example.testingapp1.AirQualityRequest;

public interface AirQualityApiService {
    @POST("v1/currentConditions:lookup")
    Call<AirQualityResponse> getCurrentAirQuality(
            @Query("key") String apiKey,
            @Body AirQualityRequest request
    );
}