package com.ecommerce.order.application.usecase;

import com.ecommerce.order.application.dto.CreateOrderRequest;
import com.ecommerce.order.domain.entity.Order;

public interface CreateOrderUseCase {
    Order execute(CreateOrderRequest request);
}
