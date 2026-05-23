package com.example.shippingservice.application.usecase;

import com.example.shared.messaging.EventPublisher;
import com.example.shippingservice.domain.entity.Shipment;
import com.example.shippingservice.domain.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateShipmentUseCase.
 * Tests the shipment creation logic.
 */
@ExtendWith(MockitoExtension.class)
class CreateShipmentUseCaseTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private EventPublisher eventPublisher;

    private CreateShipmentUseCase createShipmentUseCase;

    @BeforeEach
    void setUp() {
        createShipmentUseCase = new CreateShipmentUseCase(shipmentRepository, eventPublisher);
    }

    @Test
    void shouldCreateShipmentSuccessfully() {
        // Given
        String orderId = "order-123";
        when(shipmentRepository.existsByOrderId(orderId)).thenReturn(false);
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        createShipmentUseCase.createShipment(orderId);

        // Then
        verify(shipmentRepository, times(1)).existsByOrderId(orderId);
        verify(shipmentRepository, times(1)).save(any(Shipment.class));
        verify(eventPublisher, times(1)).publish(any(), anyString());
    }

    @Test
    void shouldNotCreateDuplicateShipment() {
        // Given
        String orderId = "order-456";
        when(shipmentRepository.existsByOrderId(orderId)).thenReturn(true);

        // When
        createShipmentUseCase.createShipment(orderId);

        // Then
        verify(shipmentRepository, times(1)).existsByOrderId(orderId);
        verify(shipmentRepository, never()).save(any(Shipment.class));
        verify(eventPublisher, never()).publish(any(), anyString());
    }

    @Test
    void shouldPublishShipmentCreatedEvent() {
        // Given
        String orderId = "order-789";
        when(shipmentRepository.existsByOrderId(orderId)).thenReturn(false);
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        createShipmentUseCase.createShipment(orderId);

        // Then
        verify(eventPublisher, times(1)).publish(any(), anyString());
    }
}
