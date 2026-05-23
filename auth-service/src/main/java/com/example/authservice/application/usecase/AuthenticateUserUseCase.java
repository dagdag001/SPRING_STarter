package com.example.authservice.application.usecase;

import com.example.authservice.application.dto.AuthenticationRequest;
import com.example.authservice.application.port.JwtTokenService;
import com.example.authservice.application.port.PasswordHashingService;
import com.example.authservice.domain.entity.User;
import com.example.authservice.domain.repository.UserRepository;
import com.example.shared.exception.AuthenticationException;
import com.example.shared.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case for authenticating a user.
 * Validates credentials and generates JWT token.
 * 
 * Requirements: 1.2, 4.3, 4.4
 */
public class AuthenticateUserUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticateUserUseCase.class);
    
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;
    private final JwtTokenService jwtTokenService;
    
    public AuthenticateUserUseCase(
            UserRepository userRepository,
            PasswordHashingService passwordHashingService,
            JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
        this.jwtTokenService = jwtTokenService;
    }
    
    /**
     * Authenticates a user and generates a JWT token.
     * 
     * @param request The authentication request
     * @return The generated JWT token
     * @throws AuthenticationException if authentication fails
     * @throws ValidationException if validation fails
     */
    public String execute(AuthenticationRequest request) {
        logger.info("Authenticating user: {}", request.getUsername());
        
        // Validate input
        validateRequest(request);
        
        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new AuthenticationException("Invalid credentials"));
        
        // Verify password
        if (!passwordHashingService.verifyPassword(request.getPassword(), user.getPasswordHash())) {
            logger.warn("Failed authentication attempt for user: {}", request.getUsername());
            throw new AuthenticationException("Invalid credentials");
        }
        
        // Generate JWT token
        String token = jwtTokenService.generateToken(user.getUserId(), user.getUsername());
        
        logger.info("User authenticated successfully: {}", user.getUserId());
        
        return token;
    }
    
    private void validateRequest(AuthenticationRequest request) {
        if (request == null) {
            throw new ValidationException("Authentication request cannot be null");
        }
        
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password cannot be empty");
        }
    }
}
