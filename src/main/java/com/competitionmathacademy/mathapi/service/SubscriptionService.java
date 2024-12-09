package com.competitionmathacademy.mathapi.service;

import com.competitionmathacademy.mathapi.model.Subscription;
import com.competitionmathacademy.mathapi.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public boolean isUserSubscribed(String username) {
        Optional<Subscription> subscriptionOptional = Optional
                .ofNullable(subscriptionRepository.findByUsername(username));
        if (subscriptionOptional.isEmpty()) {
            return false;
        }

        Subscription subscription = subscriptionOptional.get();
        return subscription.getStatus().equalsIgnoreCase("ACTIVE") &&
                (subscription.getEndDate() == null || subscription.getEndDate().isAfter(LocalDate.now()));
    }
}
