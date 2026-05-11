package com.ecommerce.order.domain.repository;

import com.ecommerce.order.domain.entity.Order;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String id);
}
