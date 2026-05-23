package com.ecommerce.inventory.api.controller;

import com.ecommerce.inventory.application.dto.AddProductRequest;
import com.ecommerce.inventory.application.service.InventoryApplicationService;
import com.ecommerce.inventory.domain.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryApplicationService inventoryService;

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody AddProductRequest request) {
        return ResponseEntity.ok(inventoryService.addProduct(request));
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(inventoryService.getAllProducts());
    }
}
