package com.example.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Auth Service.
 * 
 * Requirements: 2.1, 11.1, 12.1
 */
@SpringBootApplication(scanBasePackages = {
    "com.example.authservice",
    "com.example.shared"
})
public class AuthServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
