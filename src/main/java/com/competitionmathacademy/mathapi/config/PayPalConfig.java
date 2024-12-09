package com.competitionmathacademy.mathapi.config;

import com.paypal.base.rest.APIContext;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.Base64;
import java.util.Scanner;
import java.net.URL;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {
    private static final String SANDBOX_URL = "https://api-m.sandbox.paypal.com";
    private static final String LIVE_URL = "https://api-m.paypal.com";

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    public String getBaseUrl() {
        return mode.equalsIgnoreCase("live") ? LIVE_URL : SANDBOX_URL;
    }

    public String generateAccessToken() {
        try {
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            System.out.println("Encoded authorization: " + encodedAuth);
            URL url = new URI(getBaseUrl() + "/v1/oauth2/token").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                writer.write("grant_type=client_credentials");
                writer.flush();
            }

            try (Scanner scanner = new Scanner(conn.getInputStream())) { // Use try-with-resources
                scanner.useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";

                // Extract the access token from the response
                String token = response.split("\"access_token\":\"")[1].split("\"")[0];
                System.out.println("Access Token: " + token);
                return token;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate access token: " + e.getMessage(), e);
        }
    }

    @Bean
    public APIContext apiContext() {
        return new APIContext(clientId, clientSecret, mode);
    }
}
