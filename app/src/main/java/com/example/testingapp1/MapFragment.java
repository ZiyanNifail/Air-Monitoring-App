package com.example.testingapp1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String GOOGLE_AQI_API_KEY = "YOUR_GOOGLE_AQI_API_KEY"; // Replace with your actual API key
    private GoogleMap mMap;
    private View aqiCard;
    private TextView aqiValueText;
    private TextView aqiDescriptionText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize AQI card views
        aqiCard = view.findViewById(R.id.card_aqi_info);
        aqiValueText = view.findViewById(R.id.text_aqi_value);
        aqiDescriptionText = view.findViewById(R.id.text_aqi_description);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireActivity());

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationTask = fusedLocationClient.getLastLocation();
            locationTask.addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    fetchAirQualityData(location.getLatitude(), location.getLongitude());
                }
            });
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void fetchAirQualityData(double latitude, double longitude) {
        AirQualityLocationRequest request = new AirQualityLocationRequest();
        request.setLatitude(latitude);
        request.setLongitude(longitude);

        GoogleAirQualityService service = ApiClient.getClient().create(GoogleAirQualityService.class);
        Call<AirQualityResponse> call = service.getCurrentConditions(GOOGLE_AQI_API_KEY, request);

        call.enqueue(new Callback<AirQualityResponse>() {
            @Override
            public void onResponse(Call<AirQualityResponse> call, Response<AirQualityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AirQualityResponse aqiData = response.body();
                    updateAQIDisplay(aqiData);
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch AQI data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AirQualityResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAQIDisplay(AirQualityResponse aqiData) {
        if (aqiData != null && aqiData.getIndex() != null) {
            aqiCard.setVisibility(View.VISIBLE);
            aqiValueText.setText(String.valueOf(aqiData.getIndex()));
            aqiDescriptionText.setText(getAQIDescription(aqiData.getIndex()));
        }
    }

    private String getAQIDescription(int aqiValue) {
        if (aqiValue <= 50) return "Good";
        if (aqiValue <= 100) return "Moderate";
        if (aqiValue <= 150) return "Unhealthy for Sensitive Groups";
        if (aqiValue <= 200) return "Unhealthy";
        if (aqiValue <= 300) return "Very Unhealthy";
        return "Hazardous";
    }
}