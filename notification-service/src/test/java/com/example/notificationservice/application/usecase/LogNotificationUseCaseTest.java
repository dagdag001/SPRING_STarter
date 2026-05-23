package com.example.notificationservice.application.usecase;

import com.example.notificationservice.domain.entity.NotificationLog;
import com.example.notificationservice.domain.repository.NotificationLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LogNotificationUseCase.
 * Tests the notification logging logic.
 */
@ExtendWith(MockitoExtension.class)
class LogNotificationUseCaseTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;

    private ObjectMapper objectMapper;
    private LogNotificationUseCase logNotificationUseCase;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        logNotificationUseCase = new LogNotificationUseCase(notificationLogRepository, objectMapper);
    }

    @Test
    void shouldLogUserRegisteredEvent() {
        // Given
        String eventType = "UserRegistered";
        TestEvent eventObject = new TestEvent("123", "john_doe");
        
        when(notificationLogRepository.save(any(NotificationLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        NotificationLog result = logNotificationUseCase.logEvent(eventType, eventObject);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEventType()).isEqualTo(eventType);
        assertThat(result.getEventData()).contains("123");
        assertThat(result.getEventData()).contains("john_doe");
        verify(notificationLogRepository, times(1)).save(any(NotificationLog.class));
    }

    @Test
    void shouldLogOrderCreatedEvent() {
        // Given
        String eventType = "OrderCreated";
        TestEvent eventObject = new TestEvent("order-123", "customer-456");
        
        when(notificationLogRepository.save(any(NotificationLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        NotificationLog result = logNotificationUseCase.logEvent(eventType, eventObject);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEventType()).isEqualTo(eventType);
        assertThat(result.getEventData()).contains("order-123");
        verify(notificationLogRepository, times(1)).save(any(NotificationLog.class));
    }

    @Test
    void shouldLogPaymentCompletedEvent() {
        // Given
        String eventType = "PaymentCompleted";
        TestEvent eventObject = new TestEvent("pay-123", "order-456");
        
        when(notificationLogRepository.save(any(NotificationLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        NotificationLog result = logNotificationUseCase.logEvent(eventType, eventObject);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEventType()).isEqualTo(eventType);
        verify(notificationLogRepository, times(1)).save(any(NotificationLog.class));
    }

    @Test
    void shouldLogPaymentFailedEvent() {
        // Given
        String eventType = "PaymentFailed";
        TestEvent eventObject = new TestEvent("order-789", "Insufficient funds");
        
        when(notificationLogRepository.save(any(NotificationLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        NotificationLog result = logNotificationUseCase.logEvent(eventType, eventObject);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEventType()).isEqualTo(eventType);
        assertThat(result.getEventData()).contains("Insufficient funds");
        verify(notificationLogRepository, times(1)).save(any(NotificationLog.class));
    }

    @Test
    void shouldLogStockReservedEvent() {
        // Given
        String eventType = "StockReserved";
        TestEvent eventObject = new TestEvent("order-111", "reservations");
        
        when(notificationLogRepository.save(any(NotificationLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        NotificationLog result = logNotificationUseCase.logEvent(eventType, eventObject);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEventType()).isEqualTo(eventType);
        verify(notificationLogRepository, times(1)).save(any(NotificationLog.class));
    }

    @Test
    void shouldLogStockFailedEvent() {
        // Given
        String eventType = "StockFailed";
        TestEvent eventObject = new TestEvent("order-222", "prod-001");
        
        when(notificationLogRepository.save(any(NotificationLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        NotificationLog result = logNotificationUseCase.logEvent(eventType, eventObject);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEventType()).isEqualTo(eventType);
        verify(notificationLogRepository, times(1)).save(any(NotificationLog.class));
    }

    @Test
    void shouldLogShipmentCreatedEvent() {
        // Given
        String eventType = "ShipmentCreated";
        TestEvent eventObject = new TestEvent("ship-123", "order-333");
        
        when(notificationLogRepository.save(any(NotificationLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        NotificationLog result = logNotificationUseCase.logEvent(eventType, eventObject);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEventType()).isEqualTo(eventType);
        assertThat(result.getEventData()).contains("ship-123");
        verify(notificationLogRepository, times(1)).save(any(NotificationLog.class));
    }

    @Test
    void shouldGenerateUniqueLogIds() {
        // Given
        String eventType = "TestEvent";
        TestEvent eventObject = new TestEvent("id1", "data1");
        
        when(notificationLogRepository.save(any(NotificationLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        NotificationLog log1 = logNotificationUseCase.logEvent(eventType, eventObject);
        NotificationLog log2 = logNotificationUseCase.logEvent(eventType, eventObject);

        // Then
        assertThat(log1.getLogId()).isNotEqualTo(log2.getLogId());
    }

    // Helper class for testing
    private static class TestEvent {
        private final String id;
        private final String data;

        public TestEvent(String id, String data) {
            this.id = id;
            this.data = data;
        }

        public String getId() {
            return id;
        }

        public String getData() {
            return data;
        }
    }
}
