package com.example.inventoryservice.infrastructure.persistence;

import com.example.inventoryservice.domain.entity.StockReservation;
import com.example.inventoryservice.domain.repository.StockReservationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of StockReservationRepository using JPA.
 * 
 * Requirements: 1.3, 11.4
 */
@Component
public class StockReservationRepositoryImpl implements StockReservationRepository {
    
    private final StockReservationJpaRepository jpaRepository;
    
    public StockReservationRepositoryImpl(StockReservationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public StockReservation save(StockReservation reservation) {
        StockReservationJpaEntity entity = toJpaEntity(reservation);
        StockReservationJpaEntity saved = jpaRepository.save(entity);
        return toDomainEntity(saved);
    }
    
    @Override
    public Optional<StockReservation> findById(String reservationId) {
        return jpaRepository.findById(reservationId)
                .map(this::toDomainEntity);
    }
    
    @Override
    public List<StockReservation> findByOrderId(String orderId) {
        return jpaRepository.findByOrderId(orderId).stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByOrderId(String orderId) {
        return jpaRepository.existsByOrderId(orderId);
    }
    
    private StockReservationJpaEntity toJpaEntity(StockReservation reservation) {
        return new StockReservationJpaEntity(
                reservation.getReservationId(),
                reservation.getOrderId(),
                reservation.getProductId(),
                reservation.getQuantity(),
                reservation.getReservedAt()
        );
    }
    
    private StockReservation toDomainEntity(StockReservationJpaEntity entity) {
        return new StockReservation(
                entity.getReservationId(),
                entity.getOrderId(),
                entity.getProductId(),
                entity.getQuantity(),
                entity.getReservedAt()
        );
    }
}
