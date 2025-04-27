package com.example.testingapp1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommunityReportAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        recyclerView = view.findViewById(R.id.community_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize with mock data
        List<CommunityReport> reports = generateMockReports();
        adapter = new CommunityReportAdapter(reports);
        recyclerView.setAdapter(adapter);

        // Setup Floating Action Button for new reports
        FloatingActionButton fabAddReport = view.findViewById(R.id.fab_add_report);
        fabAddReport.setOnClickListener(v -> showAddReportDialog());

        return view;
    }

    private List<CommunityReport> generateMockReports() {
        List<CommunityReport> reports = new ArrayList<>();
        reports.add(new CommunityReport("Alex Chen", R.drawable.avatar1, "Central Park", 45, "2h ago", "Fresh morning air!"));
        reports.add(new CommunityReport("Jamal Williams", R.drawable.avatar2, "Downtown", 78, "5h ago", "Moderate traffic today"));
        reports.add(new CommunityReport("Priya Patel", R.drawable.avatar3, "Industrial District", 112, "1d ago", "Noticing more pollution lately"));

        // Added 2 more gimmick reports
        reports.add(new CommunityReport("Taylor Swift", R.drawable.avatar4, "Concert Venue", 65, "30m ago", "Crowd is energetic but air is okay"));
        reports.add(new CommunityReport("Elon Musk", R.drawable.avatar5, "Rooftop Lounge", 88, "3h ago", "Smog visible from up here"));

        return reports;
    }

    private void showAddReportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_report, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText etLocation = dialogView.findViewById(R.id.et_location);
        EditText etAqi = dialogView.findViewById(R.id.et_aqi);
        EditText etComment = dialogView.findViewById(R.id.et_comment);
        Button btnSubmit = dialogView.findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(v -> {
            String location = etLocation.getText().toString().trim();
            String aqiString = etAqi.getText().toString().trim();
            String comment = etComment.getText().toString().trim();

            if (location.isEmpty() || aqiString.isEmpty()) {
                Toast.makeText(getContext(), "Location and AQI are required", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int aqi = Integer.parseInt(aqiString);
                addNewReport(location, aqi, comment);
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter a valid AQI number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNewReport(String location, int aqi, String comment) {
        // In a real app, you would get this from user profile
        String userName = "You";
        int userAvatar = R.drawable.avatar_default;
        String timestamp = "Just now";

        CommunityReport newReport = new CommunityReport(userName, userAvatar, location, aqi, timestamp, comment);

        // Add to top of the list and notify adapter
        adapter.getReports().add(0, newReport);
        adapter.notifyItemInserted(0);
        recyclerView.smoothScrollToPosition(0);
    }
}