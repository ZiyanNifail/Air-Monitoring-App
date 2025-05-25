package com.example.testingapp1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "HomeFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String API_BASE_URL = "https://api.waqi.info/feed/";
    private static final String API_TOKEN = "YOUR_API_TOKEN"; // Replace with your token

    private TextView locationName, aqiValue, aqiStatus, tempValue, humidityValue, recommendations;
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;
    private GoogleMap mMap;
    private final OkHttpClient client = new OkHttpClient();
    private double currentLat, currentLng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        locationName = view.findViewById(R.id.location_name);
        aqiValue = view.findViewById(R.id.aqi_value);
        aqiStatus = view.findViewById(R.id.aqi_status);
        tempValue = view.findViewById(R.id.temp_value);
        humidityValue = view.findViewById(R.id.humidity_value);
        recommendations = view.findViewById(R.id.recommendations);

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        geocoder = new Geocoder(requireContext(), Locale.getDefault());

        // Check location permission
        if (checkLocationPermission()) {
            getCurrentLocation();
        } else {
            requestLocationPermission();
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (checkLocationPermission()) {
            updateMapWithCurrentLocation();
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
                if (mMap != null) {
                    // Use requireContext() instead of this
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    updateMapWithCurrentLocation();
                }
            } else {
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLng = location.getLongitude();
                        updateLocationUI(location);
                        fetchAqiData(currentLat, currentLng);
                        updateMapWithCurrentLocation();
                    } else {
                        Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void updateMapWithCurrentLocation() {
        if (mMap != null) {
            try {
                mMap.setMyLocationEnabled(true);
                LatLng currentLatLng = new LatLng(currentLat, currentLng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f));
                mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Your Location"));
            } catch (SecurityException e) {
                Log.e(TAG, "Error updating map with location", e);
            }
        }
    }

    private void updateLocationUI(Location location) {
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String city = address.getLocality();
                if (city == null) city = address.getAdminArea();
                locationName.setText(city != null ? city : "Current Location");
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder error", e);
            locationName.setText("Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
        }
    }

    private void fetchAqiData(double lat, double lng) {
        String url = API_BASE_URL + "geo:" + lat + ";" + lng + "/?token=" + API_TOKEN;

        Log.d(TAG, "Fetching AQI from: " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Failed to fetch AQI data", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "API call failed", e);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Log.d(TAG, "API response: " + responseData);

                    try {
                        JSONObject json = new JSONObject(responseData);
                        if (json.getString("status").equals("ok")) {
                            JSONObject data = json.getJSONObject("data");
                            JSONObject iaqi = data.getJSONObject("iaqi");

                            final int aqi = data.getInt("aqi");
                            final double temp = iaqi.has("t") ? iaqi.getJSONObject("t").getDouble("v") : 0;
                            final double humidity = iaqi.has("h") ? iaqi.getJSONObject("h").getDouble("v") : 0;

                            requireActivity().runOnUiThread(() -> {
                                updateAqiDisplay(aqi, temp, humidity);
                            });
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing AQI data", e);
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Error processing AQI data", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        });
    }

    private void updateAqiDisplay(int aqi, double temp, double humidity) {
        aqiValue.setText(String.valueOf(aqi));

        if (aqi <= 50) {
            aqiStatus.setText("Good");
            aqiStatus.setTextColor(getResources().getColor(R.color.green));
            recommendations.setText(getGoodAirRecommendations());
        } else if (aqi <= 100) {
            aqiStatus.setText("Moderate");
            aqiStatus.setTextColor(getResources().getColor(R.color.yellow));
            recommendations.setText(getModerateAirRecommendations());
        } else if (aqi <= 150) {
            aqiStatus.setText("Unhealthy for Sensitive Groups");
            aqiStatus.setTextColor(getResources().getColor(R.color.orange));
            recommendations.setText(getUnhealthySensitiveRecommendations());
        } else if (aqi <= 200) {
            aqiStatus.setText("Unhealthy");
            aqiStatus.setTextColor(getResources().getColor(R.color.red));
            recommendations.setText(getUnhealthyAirRecommendations());
        } else if (aqi <= 300) {
            aqiStatus.setText("Very Unhealthy");
            aqiStatus.setTextColor(getResources().getColor(R.color.purple));
            recommendations.setText(getVeryUnhealthyRecommendations());
        } else {
            aqiStatus.setText("Hazardous");
            aqiStatus.setTextColor(getResources().getColor(R.color.red));
            recommendations.setText(getHazardousRecommendations());
        }

        tempValue.setText(String.format(Locale.getDefault(), "%.1f°C", temp));
        humidityValue.setText(String.format(Locale.getDefault(), "%.0f%%", humidity));
    }

    // Health recommendation methods (same as before)
    private String getGoodAirRecommendations() {
        return "Air quality is satisfactory.\n\n" +
                "• Enjoy outdoor activities\n" +
                "• Open windows for ventilation\n" +
                "• Great day for exercise outside";
    }

    private String getModerateAirRecommendations() {
        return "Air quality is acceptable but may affect sensitive individuals.\n\n" +
                "• Sensitive people should reduce prolonged outdoor exertion\n" +
                "• Keep windows slightly open for ventilation\n" +
                "• Consider indoor plants to maintain air quality\n" +
                "• Stay hydrated to help your body cope";
    }

    private String getUnhealthySensitiveRecommendations() {
        return "Sensitive groups may experience health effects.\n\n" +
                "• Children, elderly, and people with respiratory issues should limit outdoor activities\n" +
                "• Close windows during peak pollution hours\n" +
                "• Use air purifiers if available\n" +
                "• Wear N95 masks if going outside for long periods\n" +
                "• Stay hydrated and avoid strenuous activities";
    }

    private String getUnhealthyAirRecommendations() {
        return "Everyone may begin to experience health effects.\n\n" +
                "• Limit outdoor activities, especially exercise\n" +
                "• Keep windows and doors closed\n" +
                "• Use air purifiers with HEPA filters\n" +
                "• Wear N95 masks when outside\n" +
                "• Sensitive groups should stay indoors\n" +
                "• Increase intake of antioxidant-rich foods";
    }

    private String getVeryUnhealthyRecommendations() {
        return "Health warnings of emergency conditions.\n\n" +
                "• Avoid all outdoor activities\n" +
                "• Stay indoors with windows closed\n" +
                "• Run air purifiers continuously\n" +
                "• Wear N95 masks if going outside is necessary\n" +
                "• Sensitive groups should consider temporary relocation\n" +
                "• Drink plenty of water and avoid smoking";
    }

    private String getHazardousRecommendations() {
        return "Health alert: everyone may experience serious effects.\n\n" +
                "• Remain indoors with windows and doors closed\n" +
                "• Use high-efficiency air purifiers\n" +
                "• Avoid any outdoor exposure\n" +
                "• Consider evacuating if air quality persists\n" +
                "• Wear respirator masks if going outside is unavoidable\n" +
                "• Monitor for symptoms like difficulty breathing";
    }
}