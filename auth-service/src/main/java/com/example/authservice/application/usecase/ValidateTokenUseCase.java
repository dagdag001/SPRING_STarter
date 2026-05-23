package com.example.authservice.application.usecase;

import com.example.authservice.application.port.JwtTokenService;
import com.example.shared.exception.AuthenticationException;
import com.example.shared.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case for validating JWT tokens.
 * 
 * Requirements: 1.2, 4.6
 */
public class ValidateTokenUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(ValidateTokenUseCase.class);
    
    private final JwtTokenService jwtTokenService;
    
    public ValidateTokenUseCase(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }
    
    /**
     * Validates a JWT token and extracts user information.
     * 
     * @param token The JWT token to validate
     * @return The user ID extracted from the token
     * @throws AuthenticationException if the token is invalid
     * @throws ValidationException if the token is null or empty
     */
    public String execute(String token) {
        logger.debug("Validating JWT token");
        
        if (token == null || token.trim().isEmpty()) {
            throw new ValidationException("Token cannot be empty");
        }
        
        if (!jwtTokenService.validateToken(token)) {
            logger.warn("Invalid JWT token provided");
            throw new AuthenticationException("Invalid or expired token");
        }
        
        String userId = jwtTokenService.extractUserId(token);
        logger.debug("Token validated successfully for user: {}", userId);
        
        return userId;
    }
}
