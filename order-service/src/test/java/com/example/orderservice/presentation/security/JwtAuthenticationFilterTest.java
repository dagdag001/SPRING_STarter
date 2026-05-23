package com.example.orderservice.presentation.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtAuthenticationFilter.
 * Tests JWT extraction, validation, and authentication setup.
 * 
 * Requirements: 15.1, 15.2, 15.3, 15.4
 */
class JwtAuthenticationFilterTest {
    
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private FilterChain filterChain;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private SecretKey secretKey;
    private static final String SECRET = "my-secret-key-that-is-at-least-256-bits-long-for-security";
    
    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(SECRET);
        filterChain = mock(FilterChain.class);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        
        // Initialize secret key same way as filter
        String paddedSecret = SECRET.length() >= 32 ? SECRET : String.format("%-32s", SECRET).replace(' ', '0');
        secretKey = Keys.hmacShaKeyFor(paddedSecret.getBytes(StandardCharsets.UTF_8));
        
        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }
    
    @Test
    void testValidJwtTokenSetsAuthentication() throws ServletException, IOException {
        // Generate valid JWT token
        String token = generateValidToken("user123", "john_doe");
        
        // Set Authorization header
        request.addHeader("Authorization", "Bearer " + token);
        
        // Execute filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Verify authentication is set in SecurityContext
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user123", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        
        // Verify filter chain continues
        verify(filterChain, times(1)).doFilter(request, response);
    }
    
    @Test
    void testMissingAuthorizationHeaderDoesNotSetAuthentication() throws ServletException, IOException {
        // No Authorization header
        
        // Execute filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Verify authentication is NOT set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        // Verify filter chain continues
        verify(filterChain, times(1)).doFilter(request, response);
    }
    
    @Test
    void testInvalidJwtTokenDoesNotSetAuthentication() throws ServletException, IOException {
        // Set invalid token
        request.addHeader("Authorization", "Bearer invalid.token.here");
        
        // Execute filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Verify authentication is NOT set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        // Verify filter chain continues
        verify(filterChain, times(1)).doFilter(request, response);
    }
    
    @Test
    void testExpiredJwtTokenDoesNotSetAuthentication() throws ServletException, IOException {
        // Generate expired token
        String token = generateExpiredToken("user123", "john_doe");
        
        // Set Authorization header
        request.addHeader("Authorization", "Bearer " + token);
        
        // Execute filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Verify authentication is NOT set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        // Verify filter chain continues
        verify(filterChain, times(1)).doFilter(request, response);
    }
    
    @Test
    void testMalformedAuthorizationHeaderDoesNotSetAuthentication() throws ServletException, IOException {
        // Set malformed header (missing "Bearer " prefix)
        request.addHeader("Authorization", "InvalidFormat token");
        
        // Execute filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Verify authentication is NOT set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        // Verify filter chain continues
        verify(filterChain, times(1)).doFilter(request, response);
    }
    
    @Test
    void testFilterContinuesEvenOnException() throws ServletException, IOException {
        // Set Authorization header with token that will cause parsing exception
        request.addHeader("Authorization", "Bearer malformed");
        
        // Execute filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Verify filter chain continues even on exception
        verify(filterChain, times(1)).doFilter(request, response);
    }
    
    /**
     * Helper method to generate a valid JWT token.
     */
    private String generateValidToken(String userId, String username) {
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
