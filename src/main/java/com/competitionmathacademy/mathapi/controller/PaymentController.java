package com.competitionmathacademy.mathapi.controller;

import com.competitionmathacademy.mathapi.service.PaymentService;
import com.competitionmathacademy.mathapi.service.ProductService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.paypal.base.rest.PayPalRESTException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Value("${paypal.client.id}")
    private String clientId;

    private final PaymentService paymentService;
    private final ProductService productService;

    public PaymentController(PaymentService paymentService, ProductService productService) {
        this.paymentService = paymentService;
        this.productService = productService;
    }

    @GetMapping("/client-id")
    public ResponseEntity<?> getClientId() {
        return ResponseEntity.ok(Map.of("clientId", clientId));
    }

    @GetMapping("/create-plan")
    public ResponseEntity<?> createPlan() {
        try {
            String productId = productService.createOrRetrieveProduct();
            String planId = paymentService.createBillingPlan(productId);
            return ResponseEntity.ok(Map.of("planId", planId));
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/create-subscription")
    public ResponseEntity<?> createSubscription(@RequestParam String planId) throws PayPalRESTException {
        try {
            String approvalUrl = paymentService.createSubscription(planId);
            return ResponseEntity.ok(Map.of("approvalUrl", approvalUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/execute-subscription")
    public ResponseEntity<?> executeSubscription(@RequestParam String subscriptionID, @RequestParam String username,
            HttpServletRequest request) {
        System.out.println("Received subscriptionID: " + subscriptionID);
        System.out.println("Received username: " + username);

        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);

        try {
            String agreementId = paymentService.executeSubscription(subscriptionID, username);
            //System.out.println("Agreement ID:" + agreementId);
            return ResponseEntity.ok(Map.of(
                    "message", "Subscription successful",
                    "agreementId", agreementId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
