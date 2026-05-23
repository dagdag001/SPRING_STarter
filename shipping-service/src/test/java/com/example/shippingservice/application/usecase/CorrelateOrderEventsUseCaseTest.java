package com.example.shippingservice.application.usecase;

import com.example.shippingservice.domain.entity.OrderConfirmation;
import com.example.shippingservice.domain.repository.OrderConfirmationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CorrelateOrderEventsUseCase.
 * Tests the event correlation logic for payment and stock confirmations.
 */
@ExtendWith(MockitoExtension.class)
class CorrelateOrderEventsUseCaseTest {

    @Mock
    private OrderConfirmationRepository orderConfirmationRepository;

    private CorrelateOrderEventsUseCase correlateOrderEventsUseCase;

    @BeforeEach
    void setUp() {
        correlateOrderEventsUseCase = new CorrelateOrderEventsUseCase(orderConfirmationRepository);
    }

    @Test
    void shouldRecordPaymentConfirmationForNewOrder() {
        // Given
        String orderId = "order-123";
        when(orderConfirmationRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(orderConfirmationRepository.save(any(OrderConfirmation.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        OrderConfirmation result = correlateOrderEventsUseCase.recordPaymentConfirmation(orderId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.isPaymentConfirmed()).isTrue();
        assertThat(result.isStockConfirmed()).isFalse();
        verify(orderConfirmationRepository, times(1)).save(any(OrderConfirmation.class));
    }

    @Test
    void shouldRecordStockConfirmationForNewOrder() {
        // Given
        String orderId = "order-456";
        when(orderConfirmationRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(orderConfirmationRepository.save(any(OrderConfirmation.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        OrderConfirmation result = correlateOrderEventsUseCase.recordStockConfirmation(orderId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.isPaymentConfirmed()).isFalse();
        assertThat(result.isStockConfirmed()).isTrue();
        verify(orderConfirmationRepository, times(1)).save(any(OrderConfirmation.class));
    }

    @Test
    void shouldUpdateExistingOrderConfirmationWithPayment() {
        // Given
        String orderId = "order-789";
        OrderConfirmation existing = new OrderConfirmation(orderId, false, true, java.time.LocalDateTime.now());
        when(orderConfirmationRepository.findByOrderId(orderId)).thenReturn(Optional.of(existing));
        when(orderConfirmationRepository.save(any(OrderConfirmation.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        OrderConfirmation result = correlateOrderEventsUseCase.recordPaymentConfirmation(orderId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isPaymentConfirmed()).isTrue();
        assertThat(result.isStockConfirmed()).isTrue();
        assertThat(result.isBothConfirmed()).isTrue();
        verify(orderConfirmationRepository, times(1)).save(any(OrderConfirmation.class));
    }

    @Test
    void shouldReturnTrueWhenBothConfirmationsPresent() {
        // Given
        String orderId = "order-complete";
        OrderConfirmation confirmation = new OrderConfirmation(orderId, true, true, java.time.LocalDateTime.now());
        when(orderConfirmationRepository.findByOrderId(orderId)).thenReturn(Optional.of(confirmation));

        // When
        boolean result = correlateOrderEventsUseCase.areBothConfirmationsReceived(orderId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenOnlyPaymentConfirmed() {
        // Given
        String orderId = "order-partial";
        OrderConfirmation confirmation = new OrderConfirmation(orderId, true, false, java.time.LocalDateTime.now());
        when(orderConfirmationRepository.findByOrderId(orderId)).thenReturn(Optional.of(confirmation));

        // When
        boolean result = correlateOrderEventsUseCase.areBothConfirmationsReceived(orderId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenOnlyStockConfirmed() {
        // Given
        String orderId = "order-partial-2";
        OrderConfirmation confirmation = new OrderConfirmation(orderId, false, true, java.time.LocalDateTime.now());
        when(orderConfirmationRepository.findByOrderId(orderId)).thenReturn(Optional.of(confirmation));

        // When
        boolean result = correlateOrderEventsUseCase.areBothConfirmationsReceived(orderId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenNoConfirmationExists() {
        // Given
        String orderId = "order-none";
        when(orderConfirmationRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        // When
        boolean result = correlateOrderEventsUseCase.areBothConfirmationsReceived(orderId);

        // Then
        assertThat(result).isFalse();
    }
}
