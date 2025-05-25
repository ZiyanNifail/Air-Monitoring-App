package com.example.testingapp1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";
    private static final String WAQI_MAP_URL = "https://aqicn.org/map/world/";
    private static final String API_BASE_URL = "https://api.waqi.info/feed/";
    private static final String API_TOKEN = "bafa04bab1bd447ae9dc05fd84dae510e935f937"; // Replace with your token

    private WebView webView;
    private EditText searchInput;
    private CardView aqiInfoCard;
    private TextView cityName, aqiValue, aqiStatus, aqiDescription;
    private final OkHttpClient client = new OkHttpClient();

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize views
        webView = view.findViewById(R.id.webView);
        searchInput = view.findViewById(R.id.search_input);
        aqiInfoCard = view.findViewById(R.id.card_aqi_info);
        cityName = view.findViewById(R.id.city_name);
        aqiValue = view.findViewById(R.id.aqi_value);
        aqiStatus = view.findViewById(R.id.aqi_status);
        aqiDescription = view.findViewById(R.id.aqi_description);

        // Configure WebView
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Additional setup after page loads if needed
            }
        });
        webView.loadUrl(WAQI_MAP_URL);

        // Set up search functionality
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        return view;
    }

    private void performSearch() {
        String query = searchInput.getText().toString().trim();
        if (!query.isEmpty()) {
            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);

            // First try to search on the WAQI map
            searchOnWaqiMap(query);

            // Then fetch detailed AQI data
            fetchAqiData(query);
        }
    }

    private void searchOnWaqiMap(String query) {
        String searchUrl = WAQI_MAP_URL + "#" + query.replace(" ", "%20");
        webView.loadUrl(searchUrl);
    }

    private void fetchAqiData(String location) {
        String url = API_BASE_URL + location + "/?token=" + API_TOKEN;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Failed to fetch AQI data", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        if (json.getString("status").equals("ok")) {
                            JSONObject data = json.getJSONObject("data");
                            JSONObject city = data.getJSONObject("city");

                            final String name = city.getString("name");
                            final int aqi = data.getInt("aqi");
                            final String status = getAqiStatus(aqi);
                            final String description = getAqiDescription(aqi);

                            requireActivity().runOnUiThread(() -> {
                                cityName.setText(name);
                                aqiValue.setText(String.valueOf(aqi));
                                aqiStatus.setText(status);
                                aqiDescription.setText(description);
                                aqiInfoCard.setVisibility(View.VISIBLE);
                            });
                        } else {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "City not found", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Error parsing AQI data", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "AQI data not available", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private String getAqiStatus(int aqi) {
        if (aqi <= 50) return "Good";
        else if (aqi <= 100) return "Moderate";
        else if (aqi <= 150) return "Unhealthy for Sensitive Groups";
        else if (aqi <= 200) return "Unhealthy";
        else if (aqi <= 300) return "Very Unhealthy";
        else return "Hazardous";
    }

    private String getAqiDescription(int aqi) {
        if (aqi <= 50) return "Air quality is satisfactory, and air pollution poses little or no risk.";
        else if (aqi <= 100) return "Air quality is acceptable; however, there may be a moderate health concern for a very small number of people.";
        else if (aqi <= 150) return "Members of sensitive groups may experience health effects. The general public is not likely to be affected.";
        else if (aqi <= 200) return "Everyone may begin to experience health effects; members of sensitive groups may experience more serious health effects.";
        else if (aqi <= 300) return "Health warnings of emergency conditions. The entire population is more likely to be affected.";
        else return "Health alert: everyone may experience more serious health effects.";
    }
}