package com.example.testingapp1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.Looper;
import com.google.android.gms.location.LocationRequest;


public class HomeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private TextView aqiValue, aqiStatus, tempValue, humidityValue;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleAirQualityService googleService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        aqiValue = view.findViewById(R.id.aqi_value);
        aqiStatus = view.findViewById(R.id.aqi_status);
        tempValue = view.findViewById(R.id.temp_value);
        humidityValue = view.findViewById(R.id.humidity_value);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        googleService = GoogleApiClient.getClient().create(GoogleAirQualityService.class);

        checkLocationPermissionAndFetch();

        return view;
    }

    private void checkLocationPermissionAndFetch() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Request permission here
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

        } else {
            // Permission granted, fetch location
            fetchLocationAndUpdateAQI();
        }
    }

    private void fetchAirQualityData(double lat, double lng) {
        AirQualityLocationRequest request = new AirQualityLocationRequest(lat, lng);
        String apiKey = "AIzaSyDVkvAUqf1MgHf3MVuokhuet9EubCikXLI";

        googleService.getCurrentConditions(apiKey, request)
                .enqueue(new Callback<AirQualityResponse>() {
                    @Override
                    public void onResponse(Call<AirQualityResponse> call,  Response<AirQualityResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            AirQualityResponse.CurrentConditions conditions = response.body().currentConditions.get(0);

                            int aqi = conditions.aqi;
                            String category = conditions.category;

                            aqiValue.setText(String.valueOf(aqi));
                            aqiStatus.setText(category);

                            // Set color based on AQI category
                            // (Use same switch-case from earlier example here)

                            tempValue.setText("N/A");
                            humidityValue.setText("N/A");
                        } else {
                            showError("Failed to get AQI data");
                        }
                    }

                    @Override
                    public void onFailure(Call<AirQualityResponse> call, Throwable t) {
                        showError("API call failed: " + t.getMessage());
                    }
                });
    }

    private void showError(String message) {
        aqiValue.setText("N/A");
        aqiStatus.setText(message);
        tempValue.setText("N/A");
        humidityValue.setText("N/A");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndUpdateAQI();
            } else {
                showError("Location permission denied");
            }
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                showError("Unable to get location");
                return;
            }
            Location location = locationResult.getLastLocation();
            fetchAirQualityData(location.getLatitude(), location.getLongitude());
            fusedLocationClient.removeLocationUpdates(this); // stop updates after getting location
        }
    };

    private void fetchLocationAndUpdateAQI() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        fetchAirQualityData(location.getLatitude(), location.getLongitude());
                    } else {
                        // request location updates if last location is null
                        LocationRequest locationRequest = LocationRequest.create()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(5000)
                                .setNumUpdates(1);
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                    }
                })
                .addOnFailureListener(e -> showError("Failed to get location: " + e.getMessage()));
    }


}
