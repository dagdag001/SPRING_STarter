package com.example.shippingservice.infrastructure.persistence;

import com.example.shippingservice.domain.entity.Shipment;
import com.example.shippingservice.domain.repository.ShipmentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of ShipmentRepository using Spring Data JPA.
 * 
 * Requirements: 1.3, 11.5
 */
@Component
public class ShipmentRepositoryImpl implements ShipmentRepository {
    
    private final ShipmentJpaRepository jpaRepository;
    
    public ShipmentRepositoryImpl(ShipmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Shipment save(Shipment shipment) {
        ShipmentJpaEntity entity = toJpaEntity(shipment);
        ShipmentJpaEntity savedEntity = jpaRepository.save(entity);
        return toDomainEntity(savedEntity);
    }
    
    @Override
    public Optional<Shipment> findById(String shipmentId) {
        return jpaRepository.findById(shipmentId)
                .map(this::toDomainEntity);
    }
    
    @Override
    public Optional<Shipment> findByOrderId(String orderId) {
        return jpaRepository.findByOrderId(orderId)
                .map(this::toDomainEntity);
    }
    
    @Override
    public boolean existsByOrderId(String orderId) {
        return jpaRepository.existsByOrderId(orderId);
    }
    
    private ShipmentJpaEntity toJpaEntity(Shipment shipment) {
        return new ShipmentJpaEntity(
            shipment.getShipmentId(),
            shipment.getOrderId(),
            shipment.getEstimatedDeliveryDate(),
            shipment.getStatus(),
            shipment.getCreatedAt()
        );
    }
    
    private Shipment toDomainEntity(ShipmentJpaEntity entity) {
        return new Shipment(
            entity.getShipmentId(),
            entity.getOrderId(),
            entity.getEstimatedDeliveryDate(),
            entity.getStatus(),
            entity.getCreatedAt()
        );
    }
}
