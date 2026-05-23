package com.ecommerce.inventory.application.dto;

import lombok.Data;

@Data
public class AddProductRequest {
    private String name;
    private Integer initialStock;
}
