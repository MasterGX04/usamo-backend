package com.competitionmathacademy.mathapi.service;

import com.competitionmathacademy.mathapi.model.User;
import com.competitionmathacademy.mathapi.repository.UserRepository;
import com.competitionmathacademy.mathapi.utility.JwtUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender mailSender;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Async
    public CompletableFuture<List<User>> getAllUsersAsync() {
        List<User> users = userRepository.findAll();
        return CompletableFuture.completedFuture(users);
    }

    public User signup(String username, String email, String password, String birthday) {
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Username or email already exists");
        }

        // hash password before saving
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setBirthday(birthday);
        user.setLoginType(User.LoginType.NORMAL);

        return userRepository.save(user);
    }

    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public Map<String, Object> login(String usernameOrEmail, String password) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username/email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username/email or password");
        }

        int videoProgress = 0;
        String token = jwtUtil.generateTokenWithProgress(user.getUsername(), videoProgress);

        return Map.of(
                "token", token,
                "username", user.getUsername());
    }

    public Map<String, Object> signupOrLoginWithGoogle(String username, String email, String birthday) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isPresent()) {
            int videoProgress = 0;
            User user = existingUser.get();
            String token = jwtUtil.generateTokenWithProgress(user.getUsername(), videoProgress);
            return Map.of(
                    "token", token,
                    "username", user.getUsername());
        }

        if (birthday == null) {
            throw new IllegalArgumentException("Birthday is required for new users.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(null);
        user.setBirthday(birthday);
        user.setLoginType(User.LoginType.GOOGLE);

        User savedUser = userRepository.save(user);

        int videoProgress = 0; // Customize based on your logic
        String token = jwtUtil.generateTokenWithProgress(savedUser.getUsername(), videoProgress);

        return Map.of(
                "token", token,
                "username", savedUser.getUsername());
    }

    public void forgotPassword(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email address not found"));

        // Generate unique token
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        sendResetEmail(email, token);
    }

    private void sendResetEmail(String email, String token) throws MessagingException {
        String resetUrl = "http://localhost:5173/reset-password?token=" + token;
        String subject = "Password Reset Request";
        String message = "<p>Click the link below to reset your password:</p>" +
                "<a href=\"" + resetUrl + "\">Reset Password</a>";

        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(message, true);

        mailSender.send(mailMessage);
    }

    public void resetPassword(String token, String newPassword) {
        // Find user by reset tokeen
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }
}
