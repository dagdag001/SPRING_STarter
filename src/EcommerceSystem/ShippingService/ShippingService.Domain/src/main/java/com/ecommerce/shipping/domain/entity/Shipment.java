package com.ecommerce.shipping.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {
    private String id;
    private String orderId;
    private String trackingNumber;
    private ShipmentStatus status;
    private LocalDateTime estimatedDelivery;

    public enum ShipmentStatus {
        PENDING, SHIPPED, DELIVERED
    }
}
