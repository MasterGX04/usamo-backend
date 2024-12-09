package com.competitionmathacademy.mathapi.dto;

public class VideoProgressRequest {
    private String videoUrl;
    private double progress;

    // Default constructor
    public VideoProgressRequest() {}

    // Constructor with parameters
    public VideoProgressRequest(String videoUrl, double progress) {
        this.videoUrl = videoUrl;
        this.progress = progress;
    }

    // Getters and setters
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "VideoProgressRequest{" +
                "videoUrl='" + videoUrl + '\'' +
                ", progress=" + progress +
                '}';
    }
}
