package com.example.testingapp1;

import com.google.gson.JsonObject;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @GET("get_reports.php")
    Call<List<CommunityReport>> getCommunityReports();

    @FormUrlEncoded
    @POST("login.php")
    Call<JsonObject> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("register.php")
    Call<JsonObject> register(
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password
    );

    // Google AQI API
    POST('https://airquality.googleapis.com/v1/currentConditions:lookup?key=AIzaSyDVkvAUqf1MgHf3MVuokhuet9EubCikXLI')
    // Google Map API
}
