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

        // Get logged-in username from SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String loggedInUsername = prefs.getString("loggedInUsername", null);

        if (loggedInUsername != null) {
            loadUserData(loggedInUsername);
        } else {
            Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
        }

        // Setup logout button listener
        LinearLayout logoutSection = view.findViewById(R.id.logout_section);
        logoutSection.setOnClickListener(v -> {
            // Clear shared preferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("loggedInUsername");
            editor.apply();

            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Navigate back to LoginActivity (replace with your actual login activity)
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserData(String username) {
        Cursor cursor = dbHelper.getUserData(username);

        if (cursor != null && cursor.moveToFirst()) {
            String fetchedUsername = cursor.getString(cursor.getColumnIndex("username"));
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex("profile_image"));

            userName.setText(fetchedUsername);
            userEmail.setText(username + "@app.com"); // you can update this to real email if available

            if (imageBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                profileImage.setImageBitmap(bitmap);
            }
        } else {
            Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
