package com.competitionmathacademy.mathapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.competitionmathacademy.mathapi.repository.VideoProgressRepository;
import com.competitionmathacademy.mathapi.utility.JwtUtil;
import com.competitionmathacademy.mathapi.model.VideoProgress;

@Service
public class VideoProgressService {

    @Autowired
    private VideoProgressRepository videoProgressRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public void saveProgress(String token, String videoUrl, double progress) {
        String username = jwtUtil.getUsernameFromToken(token);
        VideoProgress videoProgress = videoProgressRepository.findByUsernameAndVideoUrl(username, videoUrl)
                .orElse(new VideoProgress(username, videoUrl));
        videoProgress.setProgress(progress);
        videoProgressRepository.save(videoProgress);
    }

    public double getProgress(String token, String videoUrl) {
        String username = jwtUtil.getUsernameFromToken(token);
        return videoProgressRepository.findByUsernameAndVideoUrl(username, videoUrl)
                .map(VideoProgress::getProgress)
                .orElse(0.0);
    }
}
