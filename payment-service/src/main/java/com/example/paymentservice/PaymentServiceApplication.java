package com.example.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Payment Service.
 * 
 * Requirements: 2.3, 11.3, 12.1
 */
@SpringBootApplication(scanBasePackages = {
    "com.example.paymentservice",
    "com.example.shared"
})
public class PaymentServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
