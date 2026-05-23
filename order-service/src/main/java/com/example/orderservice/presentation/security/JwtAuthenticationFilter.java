package com.example.orderservice.presentation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * JWT Authentication Filter that intercepts requests to protected endpoints.
 * Extracts JWT from Authorization header, validates token, and sets authentication in SecurityContext.
 * 
 * Requirements: 15.1, 15.2, 15.3, 15.4
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final SecretKey secretKey;
    
    public JwtAuthenticationFilter(
            @Value("${jwt.secret:my-secret-key-that-is-at-least-256-bits-long-for-security}") String secret) {
        // Ensure the secret is at least 256 bits (32 bytes) - same as Auth Service
        String paddedSecret = secret.length() >= 32 ? secret : String.format("%-32s", secret).replace(' ', '0');
        this.secretKey = Keys.hmacShaKeyFor(paddedSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extract JWT from Authorization header
            String jwt = extractJwtFromRequest(request);
            
            if (jwt != null && validateToken(jwt)) {
                // Extract user information from token
                String userId = extractUserId(jwt);
                String username = extractUsername(jwt);
                
                // Create authentication token and set in SecurityContext
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Log the error but don't block the filter chain
            // Spring Security will handle unauthorized access
            logger.error("Cannot set user authentication: " + e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extracts JWT token from Authorization header.
     * Expected format: "Bearer <token>"
     * 
     * @param request HTTP request
     * @return JWT token or null if not present
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    /**
     * Validates JWT token.
     * 
     * @param token JWT token
     * @return true if token is valid, false otherwise
     */
    private boolean validateToken(String token) {
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
    
    /**
     * Extracts user ID from JWT token.
     * 
     * @param token JWT token
     * @return user ID
     */
    private String extractUserId(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }
    
    /**
     * Extracts username from JWT token.
     * 
     * @param token JWT token
     * @return username
     */
    private String extractUsername(String token) {
        Claims claims = extractClaims(token);
        return claims.get("username", String.class);
    }
    
    /**
     * Extracts all claims from JWT token.
     * 
     * @param token JWT token
     * @return claims
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
