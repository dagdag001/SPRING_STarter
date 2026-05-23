package com.example.orderservice.infrastructure.persistence;

import com.example.orderservice.domain.entity.Order;
import com.example.orderservice.domain.repository.OrderRepository;
import com.example.orderservice.domain.valueobject.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of OrderRepository using Spring Data JPA.
 * 
 * Requirements: 1.3, 11.2
 */
@Component
public class OrderRepositoryImpl implements OrderRepository {
    
    private final OrderJpaRepository jpaRepository;
    
    public OrderRepositoryImpl(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = toJpaEntity(order);
        OrderJpaEntity savedEntity = jpaRepository.save(entity);
        return toDomainEntity(savedEntity);
    }
    
    @Override
    public Optional<Order> findById(String orderId) {
        return jpaRepository.findById(orderId)
                .map(this::toDomainEntity);
    }
    
    @Override
    public boolean existsById(String orderId) {
        return jpaRepository.existsById(orderId);
    }
    
    private OrderJpaEntity toJpaEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity(
            order.getOrderId(),
            order.getCustomerId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getCreatedAt()
        );
        
        // Add items
        for (OrderItem item : order.getItems()) {
            OrderItemJpaEntity itemEntity = new OrderItemJpaEntity(
                item.getProductId(),
                item.getQuantity(),
                item.getPrice()
            );
            entity.addItem(itemEntity);
        }
        
        return entity;
    }
    
    private Order toDomainEntity(OrderJpaEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
            .map(itemEntity -> new OrderItem(
                itemEntity.getProductId(),
                itemEntity.getQuantity(),
                itemEntity.getPrice()
            ))
            .collect(Collectors.toList());
        
        return new Order(
            entity.getOrderId(),
            entity.getCustomerId(),
            items,
            entity.getTotalAmount(),
            entity.getStatus(),
            entity.getCreatedAt()
        );
    }
}
