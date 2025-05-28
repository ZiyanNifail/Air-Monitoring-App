package com.example.testingapp1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Load home fragment by default
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check login status
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String loggedInUsername = prefs.getString("loggedInUsername", null);

        if (loggedInUsername == null) {
            // User not logged in → redirect to LoginActivity
            Intent intent = new Intent(MainActivity.this,AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    int id = item.getItemId();

                    if (id == R.id.nav_home) {
                        selectedFragment = new HomeFragment();
                    } else if (id == R.id.nav_community) {
                        selectedFragment = new CommunityFragment();
                    } else if (id == R.id.nav_map) {
                        selectedFragment = new MapFragment();
                    } else if (id == R.id.nav_profile) {
                        selectedFragment = new ProfileFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment)
                                .commit();
                    }
                    return true;
                }
            };
}
