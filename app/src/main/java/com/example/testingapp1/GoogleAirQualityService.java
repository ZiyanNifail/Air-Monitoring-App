package com.example.testingapp1;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import com.example.testingapp1.AirQualityLocationRequest;
import com.example.testingapp1.AirQualityResponse;

public interface GoogleAirQualityService {

    @POST("currentConditions:lookup")
    Call<AirQualityResponse> getCurrentConditions(
            @Query("key") String apiKey,
            @Body AirQualityLocationRequest requestBody
    );
}
