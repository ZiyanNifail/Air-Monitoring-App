package com.example.testingapp1;
public class CommunityReport {
    private final String userName;
    private final int userAvatar;
    private final String location;
    private final int aqiValue;
    private final String timestamp;
    private final String comment;

    public CommunityReport(String userName, int userAvatar, String location,
                           int aqiValue, String timestamp, String comment) {
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.location = location;
        this.aqiValue = aqiValue;
        this.timestamp = timestamp;
        this.comment = comment;
    }

    // Getters
    public String getUserName() { return userName; }
    public int getUserAvatar() { return userAvatar; }
    public String getLocation() { return location; }
    public int getAqiValue() { return aqiValue; }
    public String getTimestamp() { return timestamp; }
    public String getComment() { return comment; }
}