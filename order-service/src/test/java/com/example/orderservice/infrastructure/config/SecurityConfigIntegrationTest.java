package com.example.orderservice.infrastructure.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for Spring Security configuration.
 * Tests that /api/orders endpoints are protected and require valid JWT tokens.
 * 
 * Requirements: 15.1, 15.2, 15.3, 15.4
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Value("${jwt.secret:my-secret-key-that-is-at-least-256-bits-long-for-security}")
    private String jwtSecret;
    
    @Test
    void testProtectedEndpointWithoutTokenReturns401() throws Exception {
        // Attempt to access protected endpoint without token
        mockMvc.perform(get("/api/orders/123"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void testProtectedEndpointWithInvalidTokenReturns401() throws Exception {
        // Attempt to access protected endpoint with invalid token
        mockMvc.perform(get("/api/orders/123")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void testProtectedEndpointWithExpiredTokenReturns401() throws Exception {
        // Generate expired token
        String expiredToken = generateExpiredToken("user123", "john_doe");
        
        // Attempt to access protected endpoint with expired token
        mockMvc.perform(get("/api/orders/123")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void testProtectedEndpointWithValidTokenReturnsSuccess() throws Exception {
        // Generate valid token
        String validToken = generateValidToken("user123", "john_doe");
        
        // Attempt to access protected endpoint with valid token
        // Note: This will return 404 if order doesn't exist, but that's OK
        // We're testing authentication, not the business logic
        mockMvc.perform(get("/api/orders/123")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isNotFound()); // 404 means we passed authentication
    }
    
    @Test
    void testCreateOrderWithoutTokenReturns401() throws Exception {
        // Attempt to create order without token
        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content("{\"customerId\":\"cust123\",\"items\":[]}"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void testCreateOrderWithValidTokenAllowsAccess() throws Exception {
        // Generate valid token
        String validToken = generateValidToken("user123", "john_doe");
        
        // Attempt to create order with valid token
        // Note: This will return 400 if validation fails, but that's OK
        // We're testing authentication, not the business logic
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType("application/json")
                        .content("{\"customerId\":\"cust123\",\"items\":[]}"))
                .andExpect(status().isBadRequest()); // 400 means we passed authentication
    }
    
    @Test
    void testH2ConsoleIsAccessibleWithoutAuthentication() throws Exception {
        // H2 console should be accessible without authentication (for development)
        mockMvc.perform(get("/h2-console"))
                .andExpect(status().isOk());
    }
    
    /**
     * Helper method to generate a valid JWT token.
     */
    private String generateValidToken(String userId, String username) {
        String paddedSecret = jwtSecret.length() >= 32 ? jwtSecret : String.format("%-32s", jwtSecret).replace(' ', '0');
        SecretKey secretKey = Keys.hmacShaKeyFor(paddedSecret.getBytes(StandardCharsets.UTF_8));
        
        Instant now = Instant.now();
        Instant expiration = now.plus(1, ChronoUnit.HOURS);
        
        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }
    
    /**
     * Helper method to generate an expired JWT token.
     */
    private String generateExpiredToken(String userId, String username) {
        String paddedSecret = jwtSecret.length() >= 32 ? jwtSecret : String.format("%-32s", jwtSecret).replace(' ', '0');
        SecretKey secretKey = Keys.hmacShaKeyFor(paddedSecret.getBytes(StandardCharsets.UTF_8));
        
        Instant past = Instant.now().minus(2, ChronoUnit.HOURS);
        Instant expiration = past.plus(1, ChronoUnit.HOURS);
        
        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .issuedAt(Date.from(past))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }
}
