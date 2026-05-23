package com.example.authservice.application.usecase;

import com.example.authservice.application.dto.RegisterUserRequest;
import com.example.authservice.application.port.PasswordHashingService;
import com.example.authservice.domain.entity.User;
import com.example.authservice.domain.repository.UserRepository;
import com.example.authservice.domain.valueobject.Email;
import com.example.shared.event.UserRegisteredEvent;
import com.example.shared.event.UserRegisteredPayload;
import com.example.shared.exception.ValidationException;
import com.example.shared.messaging.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case for registering a new user.
 * Validates input, creates user, and publishes UserRegistered event.
 * 
 * Requirements: 1.2, 4.1, 4.2
 */
public class RegisterUserUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(RegisterUserUseCase.class);
    
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;
    private final EventPublisher eventPublisher;
    
    public RegisterUserUseCase(
            UserRepository userRepository,
            PasswordHashingService passwordHashingService,
            EventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Registers a new user.
     * 
     * @param request The registration request
     * @return The created user
     * @throws ValidationException if validation fails
     */
    public User execute(RegisterUserRequest request) {
        logger.info("Registering user with username: {}", request.getUsername());
        
        // Validate input
        validateRequest(request);
        
        // Create email value object
        Email email = new Email(request.getEmail());
        
        // Check for duplicates
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("Username already exists: " + request.getUsername());
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("Email already exists: " + request.getEmail());
        }
        
        // Hash password
        String passwordHash = passwordHashingService.hashPassword(request.getPassword());
        
        // Create user
        User user = new User(request.getUsername(), email, passwordHash);
        
        // Save user
        User savedUser = userRepository.save(user);
        
        logger.info("User registered successfully with ID: {}", savedUser.getUserId());
        
        // Publish UserRegistered event
        UserRegisteredPayload payload = new UserRegisteredPayload(
            savedUser.getUserId(),
            savedUser.getUsername(),
            savedUser.getEmail().getValue()
        );
        UserRegisteredEvent event = new UserRegisteredEvent(payload);
        
        eventPublisher.publish("user.registered", event);
        logger.info("Published UserRegistered event for user: {}", savedUser.getUserId());
        
        return savedUser;
    }
    
    private void validateRequest(RegisterUserRequest request) {
        if (request == null) {
            throw new ValidationException("Registration request cannot be null");
        }
        
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password cannot be empty");
        }
        
        if (request.getPassword().length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long");
        }
    }
}
