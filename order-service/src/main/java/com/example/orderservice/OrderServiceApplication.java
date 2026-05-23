package com.example.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main application class for Order Service.
 * 
 * Requirements: 2.2, 11.2, 12.1
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.orderservice",
    "com.example.shared"
})
public class OrderServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
