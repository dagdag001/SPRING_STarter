package com.ecommerce.auth.application.service;

import com.ecommerce.auth.application.dto.AuthResponse;
import com.ecommerce.auth.application.dto.RegisterUserRequest;
import com.ecommerce.auth.application.dto.LoginRequest;
import com.ecommerce.auth.application.ports.EventPublisher;
import com.ecommerce.auth.application.usecase.LoginUseCase;
import com.ecommerce.auth.application.usecase.RegisterUserUseCase;
import com.ecommerce.auth.domain.entity.User;
import com.ecommerce.auth.domain.repository.UserRepository;
import com.ecommerce.shared.messaging.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthApplicationService implements RegisterUserUseCase, LoginUseCase {

    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    @Override
    public AuthResponse register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.builder()
                    .message("User already exists")
                    .build();
        }

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .email(request.getEmail())
                .password(request.getPassword()) // In a real app, encode this
                .fullName(request.getFullName())
                .build();

        userRepository.save(user);

        // Publish Event
        eventPublisher.publish(new UserRegisteredEvent(user.getId(), user.getEmail(), user.getFullName()));

        return AuthResponse.builder()
                .userId(user.getId())
                .token("mock-jwt-token")
                .message("User registered successfully")
                .build();
    @Override
    public AuthResponse login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(user -> user.getPassword().equals(request.getPassword())) // In real app, use BCrypt
                .map(user -> AuthResponse.builder()
                        .userId(user.getId())
                        .token("mock-jwt-token-for-" + user.getEmail())
                        .message("Login successful")
                        .build())
                .orElse(AuthResponse.builder()
                        .message("Invalid credentials")
                        .build());
    }
}
