package com.competitionmathacademy.mathapi.repository;
import com.competitionmathacademy.mathapi.model.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByUpdatedAtDesc();
}
