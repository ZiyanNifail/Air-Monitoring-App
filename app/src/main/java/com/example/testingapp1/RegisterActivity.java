package com.example.testingapp1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText regUsername, regPassword, regConfirmPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        regUsername = findViewById(R.id.reg_username);
        regPassword = findViewById(R.id.reg_password);
        regConfirmPassword = findViewById(R.id.reg_confirm_password);
        Button regButton = findViewById(R.id.reg_button);

        // Set click listener for register button
        regButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        // Get input values
        String username = regUsername.getText().toString().trim();
        String password = regPassword.getText().toString().trim();
        String confirmPassword = regConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Passwords don't match!");
            regConfirmPassword.setError("Passwords must match");
            return;
        }

        if (password.length() < 6) {
            showToast("Password must be at least 6 characters");
            regPassword.setError("Too short");
            return;
        }

        // Register user
        boolean registrationSuccess = dbHelper.addUser(username, password);

        if (registrationSuccess) {
            showToast("Registration successful!");
            finish(); // Return to login screen
        } else {
            showToast("Username already exists");
            regUsername.setError("Username taken");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}