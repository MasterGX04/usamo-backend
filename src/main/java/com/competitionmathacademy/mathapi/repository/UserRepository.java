package com.competitionmathacademy.mathapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.competitionmathacademy.mathapi.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByEmail(String email); 
    Optional<User> findByResetToken(String resetToken);
}
