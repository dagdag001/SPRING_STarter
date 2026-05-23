package com.example.inventoryservice.application.usecase;

import com.example.inventoryservice.domain.entity.Product;
import com.example.inventoryservice.domain.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use case for checking stock availability.
 * 
 * Requirements: 1.2, 7.1
 */
@Service
public class CheckStockUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckStockUseCase.class);
    
    private final ProductRepository productRepository;
    
    public CheckStockUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * Checks if sufficient stock is available for the requested items.
     * 
     * @param items Map of productId to requested quantity
     * @return Map of productId to availability status (true if available, false otherwise)
     */
    public Map<String, Boolean> checkStockAvailability(Map<String, Integer> items) {
        logger.info("Checking stock availability for {} items", items.size());
        
        Map<String, Boolean> availability = new HashMap<>();
        
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String productId = entry.getKey();
            int requestedQuantity = entry.getValue();
            
            Product product = productRepository.findById(productId).orElse(null);
            
            if (product == null) {
                logger.warn("Product not found: {}", productId);
                availability.put(productId, false);
            } else {
                boolean isAvailable = product.hasSufficientStock(requestedQuantity);
                availability.put(productId, isAvailable);
                
                logger.debug("Product: {}, requested: {}, available: {}, sufficient: {}", 
                           productId, requestedQuantity, product.getStockQuantity(), isAvailable);
            }
        }
        
        return availability;
    }
    
    /**
     * Checks if all requested items have sufficient stock.
     * 
     * @param items Map of productId to requested quantity
     * @return true if all items have sufficient stock, false otherwise
     */
    public boolean allItemsAvailable(Map<String, Integer> items) {
        Map<String, Boolean> availability = checkStockAvailability(items);
        return availability.values().stream().allMatch(Boolean::booleanValue);
    }
}
