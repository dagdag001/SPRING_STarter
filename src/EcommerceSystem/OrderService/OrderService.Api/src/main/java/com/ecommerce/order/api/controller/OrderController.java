package com.ecommerce.order.api.controller;

import com.ecommerce.order.application.service.OrderApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderApplicationService orderService;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestParam String userId, @RequestParam BigDecimal amount) {
        String orderId = orderService.createOrder(userId, amount);
        return ResponseEntity.ok("Order created with ID: " + orderId);
    }
}
