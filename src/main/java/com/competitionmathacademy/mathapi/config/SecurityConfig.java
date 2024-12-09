package com.competitionmathacademy.mathapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
   
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin()) // Allow same-origin frames
                .defaultsDisabled()
                .addHeaderWriter((request, response) -> {
                    response.addHeader("Cross-Origin-Opener-Policy", "same-origin");
                    response.addHeader("Cross-Origin-Embedder-Policy", "require-corp");
                })
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll() // Allow access to /api/** without authentication
                .anyRequest().authenticated() // Require authentication for other requests
            )
            .formLogin(form -> form.disable()); // Disable form-based login if not needed

        return http.build();
    }
}
