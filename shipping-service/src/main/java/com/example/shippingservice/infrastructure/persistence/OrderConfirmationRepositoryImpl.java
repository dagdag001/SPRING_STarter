package com.example.shippingservice.infrastructure.persistence;

import com.example.shippingservice.domain.entity.OrderConfirmation;
import com.example.shippingservice.domain.repository.OrderConfirmationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of OrderConfirmationRepository using Spring Data JPA.
 * 
 * Requirements: 1.3, 11.5
 */
@Component
public class OrderConfirmationRepositoryImpl implements OrderConfirmationRepository {
    
    private final OrderConfirmationJpaRepository jpaRepository;
    
    public OrderConfirmationRepositoryImpl(OrderConfirmationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public OrderConfirmation save(OrderConfirmation orderConfirmation) {
        OrderConfirmationJpaEntity entity = toJpaEntity(orderConfirmation);
        OrderConfirmationJpaEntity savedEntity = jpaRepository.save(entity);
        return toDomainEntity(savedEntity);
    }
    
    @Override
    public Optional<OrderConfirmation> findByOrderId(String orderId) {
        return jpaRepository.findById(orderId)
                .map(this::toDomainEntity);
    }
    
    @Override
    public boolean existsByOrderId(String orderId) {
        return jpaRepository.existsByOrderId(orderId);
    }
    
    private OrderConfirmationJpaEntity toJpaEntity(OrderConfirmation orderConfirmation) {
        return new OrderConfirmationJpaEntity(
            orderConfirmation.getOrderId(),
            orderConfirmation.isPaymentConfirmed(),
            orderConfirmation.isStockConfirmed(),
            orderConfirmation.getUpdatedAt()
        );
    }
    
    private OrderConfirmation toDomainEntity(OrderConfirmationJpaEntity entity) {
        return new OrderConfirmation(
            entity.getOrderId(),
            entity.isPaymentConfirmed(),
            entity.isStockConfirmed(),
            entity.getUpdatedAt()
        );
    }
}
