package com.example.authservice.infrastructure.config;

import com.example.authservice.application.port.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JWT implementation of JwtTokenService using jjwt library.
 * 
 * Requirements: 1.3, 4.3, 4.5, 4.6
 */
@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    
    private final SecretKey secretKey;
    private final long expirationHours;
    
    public JwtTokenServiceImpl(
            @Value("${jwt.secret:my-secret-key-that-is-at-least-256-bits-long-for-security}") String secret,
            @Value("${jwt.expiration-hours:1}") long expirationHours) {
        // Ensure the secret is at least 256 bits (32 bytes)
        String paddedSecret = secret.length() >= 32 ? secret : String.format("%-32s", secret).replace(' ', '0');
        this.secretKey = Keys.hmacShaKeyFor(paddedSecret.getBytes(StandardCharsets.UTF_8));
        this.expirationHours = expirationHours;
    }
    
    @Override
    public String generateToken(String userId, String username) {
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationHours, ChronoUnit.HOURS);
        
        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String extractUserId(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }
    
    @Override
    public String extractUsername(String token) {
        Claims claims = extractClaims(token);
        return claims.get("username", String.class);
    }
    
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
