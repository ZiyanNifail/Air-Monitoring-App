package com.example.testingapp1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private TextView userName, userEmail;
    private ImageView profileImage;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        profileImage = view.findViewById(R.id.profile_picture);

        // Initialize database helper
        dbHelper = new DatabaseHelper(getContext());

        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String loggedInUsername = prefs.getString("loggedInUsername", null);

            if (loggedInUsername != null) {
                loadUserData(loggedInUsername);
            } else {
                Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
            }

            // Setup logout button listener
            LinearLayout logoutSection = view.findViewById(R.id.logout_section);
            if (logoutSection != null) {
                logoutSection.setOnClickListener(v -> {
                    // Clear saved session
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("loggedInUsername");
                    editor.apply();

                    Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

                    // Navigate back to AuthActivity (login screen)
                    Intent intent = new Intent(getActivity(), AuthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
            }
        } else {
            Toast.makeText(getContext(), "Error: Activity context not available.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadUserData(String username) {
        Cursor cursor = null;

        try {
            cursor = dbHelper.getUserData(username);

            if (cursor != null && cursor.moveToFirst()) {
                int usernameIndex = cursor.getColumnIndex("username");
                int imageIndex = cursor.getColumnIndex("profile_image");

                String fetchedUsername = (usernameIndex != -1) ? cursor.getString(usernameIndex) : "Unknown User";
                byte[] imageBytes = (imageIndex != -1) ? cursor.getBlob(imageIndex) : null;

                userName.setText(fetchedUsername);
                userEmail.setText(username + "@app.com"); // replace with actual email if available

                if (imageBytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    profileImage.setImageBitmap(bitmap);
                }
            } else {
                Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading user data.", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
