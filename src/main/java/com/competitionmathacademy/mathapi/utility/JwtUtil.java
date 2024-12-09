package com.competitionmathacademy.mathapi.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private final Key key;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
        this.expiration = expiration;
    }

    public String generateToken(String username, Map<String, Object> claims) {
        return Jwts.builder()
        .claims(claims)
        .subject(username)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(key)
        .compact();
    }

    //Validate token and return claims if valid
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    //Check if token is expired
    public Boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    //Gets username from token
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    //Checks expiration date from token
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    //Generate a token with video progress as a claim
    public String generateTokenWithProgress(String username, int videoProgress) {
        Map<String, Object> claims = Map.of("videoProgress", videoProgress);
        return generateToken(username, claims);
    }

    //Get video progress from token
    public Integer getVideoProgressFromToken(String token) {
        return (Integer) getClaimsFromToken(token).get("videoProgress");
    }
}
