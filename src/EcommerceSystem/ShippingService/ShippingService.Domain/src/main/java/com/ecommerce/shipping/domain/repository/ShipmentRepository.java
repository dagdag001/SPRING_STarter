package com.ecommerce.shipping.domain.repository;

import com.ecommerce.shipping.domain.entity.Shipment;
import java.util.Optional;

public interface ShipmentRepository {
    Shipment save(Shipment shipment);
    Optional<Shipment> findByOrderId(String orderId);
}
