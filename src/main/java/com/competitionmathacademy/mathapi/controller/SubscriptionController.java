package com.competitionmathacademy.mathapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.competitionmathacademy.mathapi.service.SubscriptionService;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkSubscription(@RequestParam String username) {
        boolean isSubscribed = subscriptionService.isUserSubscribed(username);
        return ResponseEntity.ok(isSubscribed);
    }
}
