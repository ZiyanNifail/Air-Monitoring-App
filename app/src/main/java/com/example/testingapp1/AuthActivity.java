package com.example.testingapp1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {
    private EditText usernameInput, passwordInput;
    private DatabaseHelper dbHelper;
    private TextView toggleAuthText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Check saved session first
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String loggedInUsername = prefs.getString("loggedInUsername", null);

            if (loggedInUsername != null) {
                // User already logged in, skip to MainActivity
                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            setContentView(R.layout.activity_auth);

            // Initialize database helper
            dbHelper = new DatabaseHelper(this);

            // Initialize views
            usernameInput = findViewById(R.id.username_input);
            passwordInput = findViewById(R.id.password_input);
            Button actionButton = findViewById(R.id.action_button);
            toggleAuthText = findViewById(R.id.toggle_auth_text);

            if (actionButton != null) {
                actionButton.setOnClickListener(v -> handleLogin());
            }

            if (toggleAuthText != null) {
                toggleAuthText.setOnClickListener(v -> {
                    Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
                    startActivity(intent);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unexpected error occurred. Please restart the app.", Toast.LENGTH_LONG).show();
        }
    }

    private void handleLogin() {
        String username = usernameInput != null ? usernameInput.getText().toString().trim() : "";
        String password = passwordInput != null ? passwordInput.getText().toString().trim() : "";

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check credentials safely
        try {
            if (dbHelper != null && dbHelper.checkUser(username, password)) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                // Save session in SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                prefs.edit().putString("loggedInUsername", username).apply();

                // Navigate to MainActivity
                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                if (passwordInput != null) {
                    passwordInput.setText(""); // Clear password field
                    passwordInput.requestFocus(); // Focus on password field
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Login error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
