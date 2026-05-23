package com.ecommerce.inventory.domain.repository;

import com.ecommerce.inventory.domain.entity.Product;
import java.util.Optional;
import java.util.List;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
}
