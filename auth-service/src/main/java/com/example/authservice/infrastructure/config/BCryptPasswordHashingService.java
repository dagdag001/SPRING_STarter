package com.example.authservice.infrastructure.config;

import com.example.authservice.application.port.PasswordHashingService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * BCrypt implementation of PasswordHashingService.
 * 
 * Requirements: 1.3, 4.5
 */
@Service
public class BCryptPasswordHashingService implements PasswordHashingService {
    
    private final BCryptPasswordEncoder encoder;
    
    public BCryptPasswordHashingService() {
        this.encoder = new BCryptPasswordEncoder();
    }
    
    @Override
    public String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }
    
    @Override
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}
