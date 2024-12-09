package com.competitionmathacademy.mathapi.repository;
import com.competitionmathacademy.mathapi.model.Subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Subscription findByUsername(String username);
}
