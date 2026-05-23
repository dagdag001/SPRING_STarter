package com.example.inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Inventory Service.
 * 
 * Requirements: 2.4, 11.4, 12.1
 */
@SpringBootApplication(scanBasePackages = {
    "com.example.inventoryservice",
    "com.example.shared"
})
public class InventoryServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
