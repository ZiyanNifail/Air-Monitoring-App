package com.example.testingapp1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CommunityReportAdapter extends RecyclerView.Adapter<CommunityReportAdapter.ReportViewHolder> {

    private List<CommunityReport> reports;
    private Context context;

    public CommunityReportAdapter(List<CommunityReport> reports) {
        this.reports = reports;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_community_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        CommunityReport report = reports.get(position);

        // Set report data
        holder.userAvatar.setImageResource(report.getUserAvatar());
        holder.userName.setText(report.getUserName());
        holder.location.setText(report.getLocation());
        holder.aqiValue.setText(String.valueOf(report.getAqiValue()));
        holder.timestamp.setText(report.getTimestamp());
        holder.comment.setText(report.getComment());

        // Set AQI color based on value
        int aqiColor = getAqiColor(report.getAqiValue());
        holder.aqiValue.setTextColor(aqiColor);

        // Optional: Add AQI level indicator
        holder.aqiLevel.setText(getAqiLevel(report.getAqiValue()));
        holder.aqiLevel.setTextColor(aqiColor);
    }

    private int getAqiColor(int aqiValue) {
        if (aqiValue <= 50) {
            return ContextCompat.getColor(context, R.color.aqi_good); // Green
        } else if (aqiValue <= 100) {
            return ContextCompat.getColor(context, R.color.aqi_moderate); // Yellow
        } else if (aqiValue <= 150) {
            return ContextCompat.getColor(context, R.color.aqi_unhealthy_sensitive); // Orange
        } else if (aqiValue <= 200) {
            return ContextCompat.getColor(context, R.color.aqi_unhealthy); // Red
        } else if (aqiValue <= 300) {
            return ContextCompat.getColor(context, R.color.aqi_very_unhealthy); // Purple
        } else {
            return ContextCompat.getColor(context, R.color.aqi_hazardous); // Maroon
        }
    }

    private String getAqiLevel(int aqiValue) {
        if (aqiValue <= 50) return "Good";
        else if (aqiValue <= 100) return "Moderate";
        else if (aqiValue <= 150) return "Unhealthy for Sensitive Groups";
        else if (aqiValue <= 200) return "Unhealthy";
        else if (aqiValue <= 300) return "Very Unhealthy";
        else return "Hazardous";
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    // Proper getter method for reports
    public List<CommunityReport> getReports() {
        return reports;
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userName, location, aqiValue, aqiLevel, timestamp, comment;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.user_avatar);
            userName = itemView.findViewById(R.id.user_name);
            location = itemView.findViewById(R.id.location);
            aqiValue = itemView.findViewById(R.id.aqi_value);
            aqiLevel = itemView.findViewById(R.id.aqi_level); // Add this TextView to your layout
            timestamp = itemView.findViewById(R.id.timestamp);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}