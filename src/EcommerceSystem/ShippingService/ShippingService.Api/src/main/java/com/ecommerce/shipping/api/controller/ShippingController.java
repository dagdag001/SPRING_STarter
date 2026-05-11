package com.ecommerce.shipping.api.controller;

import com.ecommerce.shipping.domain.entity.Shipment;
import com.ecommerce.shipping.domain.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShipmentRepository shipmentRepository;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Shipment> getShipmentByOrder(@PathVariable String orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
