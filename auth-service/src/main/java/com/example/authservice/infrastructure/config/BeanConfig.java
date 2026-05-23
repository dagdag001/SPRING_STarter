package com.example.authservice.infrastructure.config;

import com.example.authservice.application.port.JwtTokenService;
import com.example.authservice.application.port.PasswordHashingService;
import com.example.authservice.application.usecase.AuthenticateUserUseCase;
import com.example.authservice.application.usecase.RegisterUserUseCase;
import com.example.authservice.application.usecase.ValidateTokenUseCase;
import com.example.authservice.domain.repository.UserRepository;
import com.example.shared.messaging.EventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for application beans.
 * Wires together domain, application, and infrastructure components.
 * 
 * Requirements: 1.3
 */
@Configuration
public class BeanConfig {
    
    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordHashingService passwordHashingService,
            EventPublisher eventPublisher) {
        return new RegisterUserUseCase(userRepository, passwordHashingService, eventPublisher);
    }
    
    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(
            UserRepository userRepository,
            PasswordHashingService passwordHashingService,
            JwtTokenService jwtTokenService) {
        return new AuthenticateUserUseCase(userRepository, passwordHashingService, jwtTokenService);
    }
    
    @Bean
    public ValidateTokenUseCase validateTokenUseCase(JwtTokenService jwtTokenService) {
        return new ValidateTokenUseCase(jwtTokenService);
    }
}
