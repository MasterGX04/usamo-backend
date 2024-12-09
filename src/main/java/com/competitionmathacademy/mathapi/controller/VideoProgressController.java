package com.competitionmathacademy.mathapi.controller;

import com.competitionmathacademy.mathapi.service.VideoProgressService;
import com.competitionmathacademy.mathapi.dto.VideoProgressRequest;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@RestController
@RequestMapping("/api/videos")
public class VideoProgressController {
    
    @Autowired
    private VideoProgressService videoProgressService;

    @PostMapping("/progress")
    public ResponseEntity<?> saveVideoProgress(@RequestBody VideoProgressRequest request, @RequestHeader("Authorization") String token) {
        try {
            videoProgressService.saveProgress(token, request.getVideoUrl(), request.getProgress());
            return ResponseEntity.ok("Video progress saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to save video progress: " + e.getMessage());
        }
    }

    @GetMapping("/progress")
    public ResponseEntity<?> getVideoProgress(@RequestParam String videoUrl, @RequestHeader("Authorization") String token) {
        try {
            double progress = videoProgressService.getProgress(token, videoUrl);
            return ResponseEntity.ok(Map.of("progress", progress));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch video progress: " + e.getMessage());
        }
    } 
}

