package com.competitionmathacademy.mathapi.model;

import jakarta.persistence.*;

@Entity
public class VideoProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String videoUrl;
    private double progress;
    
    public VideoProgress() {}

    public VideoProgress (String username, String videoUrl) {
        this.username = username;
        this.videoUrl = videoUrl;
        this.progress = 0.0;
    }
    
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public double getProgress() {
        return this.progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
