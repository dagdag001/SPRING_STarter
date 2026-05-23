package com.ecommerce.inventory.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String id;
    private String name;
    private Integer stockQuantity;

    public boolean hasStock(Integer quantity) {
        return stockQuantity >= quantity;
    }

    public void reserve(Integer quantity) {
        if (!hasStock(quantity)) {
            throw new RuntimeException("Insufficient stock");
        }
        this.stockQuantity -= quantity;
    }
}
