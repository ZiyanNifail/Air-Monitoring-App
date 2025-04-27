package com.example.testingapp1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private TextView userName, userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);

        // Set user data (in real app, fetch from database or shared preferences)
        userName.setText("Ziyan Nifail");
        userEmail.setText("ziyannifail14@gmail.com");

        return view;
    }
}