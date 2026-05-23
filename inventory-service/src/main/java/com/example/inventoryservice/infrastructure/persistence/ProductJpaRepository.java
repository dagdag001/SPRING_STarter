package com.example.inventoryservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for ProductJpaEntity.
 * 
 * Requirements: 1.3, 11.4
 */
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, String> {
}
