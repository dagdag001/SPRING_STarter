package com.example.inventoryservice.domain.repository;

import com.example.inventoryservice.domain.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity.
 * Defines contract for product persistence operations.
 * 
 * Requirements: 1.1, 7.1
 */
public interface ProductRepository {
    
    /**
     * Saves a product to the repository.
     * 
     * @param product The product to save
     * @return The saved product
     */
    Product save(Product product);
    
    /**
     * Finds a product by its ID.
     * 
     * @param productId The product ID
     * @return Optional containing the product if found
     */
    Optional<Product> findById(String productId);
    
    /**
     * Finds all products by their IDs.
     * 
     * @param productIds The list of product IDs
     * @return List of products found
     */
    List<Product> findAllById(List<String> productIds);
    
    /**
     * Finds all products in the repository.
     * 
     * @return List of all products
     */
    List<Product> findAll();
}
