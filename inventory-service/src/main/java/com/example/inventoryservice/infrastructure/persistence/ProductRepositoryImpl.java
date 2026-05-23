package com.example.inventoryservice.infrastructure.persistence;

import com.example.inventoryservice.domain.entity.Product;
import com.example.inventoryservice.domain.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ProductRepository using JPA.
 * 
 * Requirements: 1.3, 11.4
 */
@Component
public class ProductRepositoryImpl implements ProductRepository {
    
    private final ProductJpaRepository jpaRepository;
    
    public ProductRepositoryImpl(ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = toJpaEntity(product);
        ProductJpaEntity saved = jpaRepository.save(entity);
        return toDomainEntity(saved);
    }
    
    @Override
    public Optional<Product> findById(String productId) {
        return jpaRepository.findById(productId)
                .map(this::toDomainEntity);
    }
    
    @Override
    public List<Product> findAllById(List<String> productIds) {
        return jpaRepository.findAllById(productIds).stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }
    
    private ProductJpaEntity toJpaEntity(Product product) {
        return new ProductJpaEntity(
                product.getProductId(),
                product.getName(),
                product.getStockQuantity(),
                product.getUpdatedAt()
        );
    }
    
    private Product toDomainEntity(ProductJpaEntity entity) {
        return new Product(
                entity.getProductId(),
                entity.getName(),
                entity.getStockQuantity(),
                entity.getUpdatedAt()
        );
    }
}
