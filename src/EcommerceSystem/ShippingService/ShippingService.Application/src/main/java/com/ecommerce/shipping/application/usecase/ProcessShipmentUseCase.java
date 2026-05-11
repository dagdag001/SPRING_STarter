package com.ecommerce.shipping.application.usecase;

import com.ecommerce.shipping.domain.entity.Shipment;

public interface ProcessShipmentUseCase {
    Shipment initiate(String orderId);
}
