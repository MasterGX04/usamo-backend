package com.competitionmathacademy.mathapi.controller;

import com.competitionmathacademy.mathapi.model.User;
import com.competitionmathacademy.mathapi.service.UserService;

import jakarta.mail.MessagingException;

import com.competitionmathacademy.mathapi.dto.SignupRequest;
import com.competitionmathacademy.mathapi.dto.GoogleSignupLoginRequest;
import com.competitionmathacademy.mathapi.dto.LoginRequest;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${google.client.id}")
    private String googleClientId;

    @GetMapping("/google-client-id")
    public ResponseEntity<Map<String, String>> getGoogleClientId() {
        if (googleClientId == null || googleClientId.isEmpty()) {
            throw new IllegalStateException("Google Client ID is not configured. Please check application properties.");
        }
        return ResponseEntity.ok(Collections.singletonMap("clientId", googleClientId));
    }

    @GetMapping
    public CompletableFuture<List<User>> getAllUsers() {
        return userService.getAllUsersAsync();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            User user = userService.signup(request.getUsername(), request.getEmail(), request.getPassword(),
                    request.getBirthday());
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailTaken(email);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = userService.isUsernameTaken(username);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRquest) {
        try {
            Map<String, Object> loginResponse = userService.login(loginRquest.getUsernameOrEmail(),
                    loginRquest.getPassword());
            return ResponseEntity.ok().body(loginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/google-signup-login")
    public ResponseEntity<?> googleSignupOrLogin(@RequestBody GoogleSignupLoginRequest request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String birthday = request.getBirthday();

            if (email == null || username == null) {
                throw new IllegalArgumentException("Email and username are required");
            }

            Map<String, Object> response = userService.signupOrLoginWithGoogle(username, email, birthday);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        try {
            userService.forgotPassword(email);
            return ResponseEntity.ok(Map.of("message", "Password reset email sent"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send email"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password successfully reset"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
}
