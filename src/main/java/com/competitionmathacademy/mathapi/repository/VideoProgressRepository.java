package com.competitionmathacademy.mathapi.repository;
import com.competitionmathacademy.mathapi.model.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VideoProgressRepository extends JpaRepository<VideoProgress, Long>{
    Optional<VideoProgress> findByUsernameAndVideoUrl(String username, String videoUrl);
}
