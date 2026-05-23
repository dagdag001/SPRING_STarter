package com.example.inventoryservice.infrastructure.config;

import com.example.inventoryservice.domain.entity.Product;
import com.example.inventoryservice.domain.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Initializes sample product data on application startup.
 * 
 * Requirements: 1.3, 11.4
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final ProductRepository productRepository;
    
    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public void run(String... args) {
        logger.info("Initializing sample product data...");
        
        LocalDateTime now = LocalDateTime.now();
        
        // Create sample products
        Product product1 = new Product("prod-001", "Laptop", 50, now);
        Product product2 = new Product("prod-002", "Mouse", 200, now);
        Product product3 = new Product("prod-003", "Keyboard", 150, now);
        Product product4 = new Product("prod-004", "Monitor", 75, now);
        Product product5 = new Product("prod-005", "Headphones", 100, now);
        
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        productRepository.save(product4);
        productRepository.save(product5);
        
        logger.info("Sample product data initialized successfully");
        logger.info("Products: prod-001 (Laptop: 50), prod-002 (Mouse: 200), prod-003 (Keyboard: 150), prod-004 (Monitor: 75), prod-005 (Headphones: 100)");
    }
}
