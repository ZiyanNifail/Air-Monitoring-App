package com.example.testingapp1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.JsonObject;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        String email = username + "@example.com"; // optional, or add real email field

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);

        if (isLoginMode) {
            // LOGIN mode
            api.login(username, password).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null &&
                            response.body().get("success").getAsBoolean()) {
                        Toast.makeText(AuthActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AuthActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(AuthActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(AuthActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // REGISTER mode
            api.register(username, email, password).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null &&
                            response.body().get("success").getAsBoolean()) {
                        Toast.makeText(AuthActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        toggleAuthMode(); // Go back to login
                    } else {
                        Toast.makeText(AuthActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(AuthActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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