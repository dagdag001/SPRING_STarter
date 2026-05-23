package com.example.authservice.presentation.controller;

import com.example.authservice.application.dto.AuthenticationRequest;
import com.example.authservice.application.dto.RegisterUserRequest;
import com.example.authservice.application.usecase.AuthenticateUserUseCase;
import com.example.authservice.application.usecase.RegisterUserUseCase;
import com.example.authservice.domain.entity.User;
import com.example.authservice.presentation.dto.LoginRequest;
import com.example.authservice.presentation.dto.LoginResponse;
import com.example.authservice.presentation.dto.RegisterRequest;
import com.example.authservice.presentation.dto.RegisterResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication endpoints.
 * 
 * Requirements: 1.4, 14.1, 14.2
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;
    
    @Value("${jwt.expiration-hours:1}")
    private long jwtExpirationHours;
    
    public AuthController(
            RegisterUserUseCase registerUserUseCase,
            AuthenticateUserUseCase authenticateUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
    }
    
    /**
     * Register a new user.
     * 
     * @param request The registration request
     * @return The registration response with user details
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Received registration request for username: {}", request.getUsername());
        
        RegisterUserRequest useCaseRequest = new RegisterUserRequest(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );
        
        User user = registerUserUseCase.execute(useCaseRequest);
        
        RegisterResponse response = new RegisterResponse(
            user.getUserId(),
            user.getUsername(),
            user.getEmail().getValue(),
            user.getCreatedAt()
        );
        
        logger.info("User registered successfully: {}", user.getUserId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Authenticate a user and generate JWT token.
     * 
     * @param request The login request
     * @return The login response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Received login request for username: {}", request.getUsername());
        
        AuthenticationRequest useCaseRequest = new AuthenticationRequest(
            request.getUsername(),
            request.getPassword()
        );
        
        String token = authenticateUserUseCase.execute(useCaseRequest);
        
        LoginResponse response = new LoginResponse(token, jwtExpirationHours);
        
        logger.info("User authenticated successfully: {}", request.getUsername());
        
        return ResponseEntity.ok(response);
    }
}
