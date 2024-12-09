package com.competitionmathacademy.mathapi.service;

import com.competitionmathacademy.mathapi.model.Subscription;
import com.competitionmathacademy.mathapi.repository.SubscriptionRepository;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Service;
import com.competitionmathacademy.mathapi.config.PayPalConfig;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;

@Service
public class PaymentService {
    private final PayPalConfig payPalConfig;
    private final SubscriptionRepository subscriptionRepository;

    public PaymentService(PayPalConfig payPalConfig, SubscriptionRepository subscriptionRepository) {
        this.payPalConfig = payPalConfig;
        this.subscriptionRepository = subscriptionRepository;
    }

    public String createBillingPlan(String productId) throws PayPalRESTException {
        try {
            String accessToken = payPalConfig.generateAccessToken();

            URL url = new URI(payPalConfig.getBaseUrl() + "/v1/billing/plans").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String payload = buildPlanPayload(productId);
            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                writer.write(payload);
                writer.flush();
            }

            try (Scanner scanner = new Scanner(conn.getInputStream())) { // Use try-with-resources
                scanner.useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                // Parse and return plan ID
                String planId = response.split("\"id\":\"")[1].split("\"")[0];
                return planId;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create billing plan: " + e.getMessage());
        }
    }

    private String buildPlanPayload(String productId) { // Need to fix product_id

        return "{\n" +
                "  \"product_id\": \"" + productId + "\",\n" +
                "  \"name\": \"Monthly Subscription Plan\",\n" +
                "  \"description\": \"Plan for $0.99 USD per month\",\n" +
                "  \"billing_cycles\": [\n" +
                "    {\n" +
                "      \"frequency\": {\n" +
                "        \"interval_unit\": \"MONTH\",\n" +
                "        \"interval_count\": 1\n" +
                "      },\n" +
                "      \"tenure_type\": \"REGULAR\",\n" +
                "      \"sequence\": 1,\n" +
                "      \"total_cycles\": 12,\n" + // Adjust cycles as needed
                "      \"pricing_scheme\": {\n" +
                "        \"fixed_price\": {\n" +
                "          \"value\": \"0.99\",\n" +
                "          \"currency_code\": \"USD\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"payment_preferences\": {\n" +
                "    \"auto_bill_outstanding\": true,\n" +
                "    \"setup_fee\": {\n" +
                "      \"value\": \"0.00\",\n" +
                "      \"currency_code\": \"USD\"\n" +
                "    },\n" +
                "    \"setup_fee_failure_action\": \"CONTINUE\",\n" +
                "    \"payment_failure_threshold\": 3\n" +
                "  },\n" +
                "  \"taxes\": {\n" +
                "    \"percentage\": \"6.625\",\n" +
                "    \"inclusive\": false\n" +
                "  }\n" +
                "}";
    }

    public String createSubscription(String planId) throws PayPalRESTException {
        try {
            String accessToken = payPalConfig.generateAccessToken();

            URL url = new URI(payPalConfig.getBaseUrl() + "/v1/billing/subscriptions").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String payload = buildSubscriptionPayload(planId);
            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                writer.write(payload);
                writer.flush();
            }

            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                scanner.useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                // Parse and return subscription ID
                String subscriptionId = response.split("\"id\":\"")[1].split("\"")[0];
                return subscriptionId;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create subscription: " + e.getMessage(), e);
        }
    }

    private String buildSubscriptionPayload(String planId) {
        return "{\n" +
                "  \"plan_id\": \"" + planId + "\",\n" +
                "  \"application_context\": {\n" +
                "    \"brand_name\": \"YourBrandName\",\n" +
                "    \"locale\": \"en-US\",\n" +
                "    \"shipping_preference\": \"NO_SHIPPING\",\n" +
                "    \"user_action\": \"SUBSCRIBE_NOW\",\n" +
                "    \"return_url\": \"https://example.com/success\",\n" +
                "    \"cancel_url\": \"https://example.com/cancel\"\n" +
                "  }\n" +
                "}";
    }

    public String executeSubscription(String subscriptionID, String username) {
        try {
            String accessToken = payPalConfig.generateAccessToken();

            URL url = new URI(payPalConfig.getBaseUrl() + "/v1/billing/subscriptions/" + subscriptionID).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                try (Scanner scanner = new Scanner(conn.getErrorStream())) {
                    scanner.useDelimiter("\\A");
                    String errorResponse = scanner.hasNext() ? scanner.next() : "";
                    throw new RuntimeException("PayPal API Error: " + errorResponse);
                }
            }

            String response;
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                scanner.useDelimiter("\\A");
                response = scanner.hasNext() ? scanner.next() : "";
            }

                String startDate = response.split("\"start_time\":\"")[1].split("\"")[0];
                String endDate = response.contains("\"next_billing_time\":\"")
                        ? response.split("\"next_billing_time\":\"")[1].split("\"")[0]
                        : null;
                String status = response.split("\"status\":\"")[1].split("\"")[0];

                Subscription subscription = new Subscription();
                subscription.setSubscriptionId(subscriptionID);
                subscription.setUsername(username);
                subscription.setStartDate(LocalDate.parse(startDate.substring(0, 10))); // Extract date only
                if (endDate != null) {
                    subscription.setEndDate(LocalDate.parse(endDate.substring(0, 10))); // Extract date only
                }
                subscription.setStatus(status);
                System.out.println("Subscription object: " + subscription.toString()); 

                subscriptionRepository.save(subscription);
                return subscriptionID;
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute subscription: " + e.getMessage(), e);
        }
    }
}
