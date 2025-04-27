package com.example.testingapp1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {

    private boolean isLoginMode = true;
    private EditText usernameInput, passwordInput;
    private Button actionButton;
    private TextView toggleAuthText, titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        actionButton = findViewById(R.id.action_button);
        toggleAuthText = findViewById(R.id.toggle_auth_text);
        titleText = findViewById(R.id.title_text);

        actionButton.setOnClickListener(v -> handleAuthAction());
        toggleAuthText.setOnClickListener(v -> toggleAuthMode());
    }

    private void handleAuthAction() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (isLoginMode) {
            // Login logic
            if (username.equals("ziyan") && password.equals("12345678")) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Register logic (in this simple version, same as login)
            if (!username.isEmpty() && !password.isEmpty()) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                toggleAuthMode(); // Switch back to login
            } else {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toggleAuthMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            titleText.setText("Login");
            actionButton.setText("Login");
            toggleAuthText.setText("Don't have an account? Register");
        } else {
            titleText.setText("Register");
            actionButton.setText("Register");
            toggleAuthText.setText("Already have an account? Login");
        }
        usernameInput.setText("");
        passwordInput.setText("");
    }
}