package com.example.testingapp1;

import android.content.Intent;
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
        setContentView(R.layout.activity_auth);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        Button actionButton = findViewById(R.id.action_button);
        toggleAuthText = findViewById(R.id.toggle_auth_text);

        // Login button click listener
        actionButton.setOnClickListener(v -> handleLogin());

        // Register text click listener
        toggleAuthText.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check credentials
        if (dbHelper.checkUser(username, password)) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            // Navigate to MainActivity
            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the AuthActivity so user can't go back
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            passwordInput.setText(""); // Clear password field
            passwordInput.requestFocus(); // Move focus to password field
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}