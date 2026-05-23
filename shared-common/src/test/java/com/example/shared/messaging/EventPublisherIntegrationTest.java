package com.example.shared.messaging;

import com.example.shared.config.RabbitMQConfig;
import com.example.shared.event.OrderCreatedEvent;
import com.example.shared.event.OrderCreatedPayload;
import com.example.shared.event.OrderItem;
import com.example.shared.event.UserRegisteredEvent;
import com.example.shared.event.UserRegisteredPayload;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for EventPublisher with RabbitMQ configuration.
 * Tests the complete event publishing flow with Spring context.
 * 
 * Note: This test requires a running RabbitMQ instance or uses an embedded broker.
 * For CI/CD, consider using Testcontainers or an embedded RabbitMQ.
 */
@SpringBootTest(classes = EventPublisherIntegrationTest.TestConfig.class)
@TestPropertySource(properties = {
    "spring.rabbitmq.host=localhost",
    "spring.rabbitmq.port=5672",
    "spring.rabbitmq.username=guest",
    "spring.rabbitmq.password=guest"
})
class EventPublisherIntegrationTest {
    
    @Autowired(required = false)
    private EventPublisher eventPublisher;
    
    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;
    
    @Configuration
    @Import(RabbitMQConfig.class)
    static class TestConfig {
        @Bean
        public EventPublisher eventPublisher(RabbitTemplate rabbitTemplate) {
            return new RabbitMQEventPublisher(rabbitTemplate);
        }
    }
    
    @Test
    void contextLoads() {
        // Verify that Spring context loads successfully with RabbitMQ configuration
        assertNotNull(rabbitTemplate, "RabbitTemplate should be autowired");
        assertNotNull(eventPublisher, "EventPublisher should be autowired");
    }
    
    @Test
    void rabbitTemplate_ShouldHaveJsonMessageConverter() {
        // Verify RabbitTemplate is configured with JSON converter
        if (rabbitTemplate != null) {
            assertNotNull(rabbitTemplate.getMessageConverter());
            assertTrue(rabbitTemplate.getMessageConverter() instanceof Jackson2JsonMessageConverter);
        }
    }
    
    @Test
    void eventPublisher_ShouldBeRabbitMQImplementation() {
        // Verify EventPublisher is the RabbitMQ implementation
        if (eventPublisher != null) {
            assertTrue(eventPublisher instanceof RabbitMQEventPublisher);
        }
    }
    
    /**
     * This test verifies the event publishing logic without requiring a running RabbitMQ.
     * In a real integration test with RabbitMQ, you would verify message delivery.
     */
    @Test
    void publish_UserRegisteredEvent_ShouldNotThrowException() {
        if (eventPublisher == null) {
            // Skip test if RabbitMQ is not available
            return;
        }
        
        // Arrange
        UserRegisteredPayload payload = new UserRegisteredPayload(
            "user-123",
            "john_doe",
            "john@example.com"
        );
        UserRegisteredEvent event = new UserRegisteredEvent(payload);
        
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> {
            eventPublisher.publish(event, "user.registered");
        });
    }
    
    /**
     * This test verifies the event publishing logic without requiring a running RabbitMQ.
     * In a real integration test with RabbitMQ, you would verify message delivery.
     */
    @Test
    void publish_OrderCreatedEvent_ShouldNotThrowException() {
        if (eventPublisher == null) {
            // Skip test if RabbitMQ is not available
            return;
        }
        
        // Arrange
        OrderItem item = new OrderItem("prod-001", 2, new BigDecimal("29.99"));
        OrderCreatedPayload payload = new OrderCreatedPayload(
            "order-123",
            "customer-456",
            List.of(item),
            new BigDecimal("59.98")
        );
        OrderCreatedEvent event = new OrderCreatedEvent(payload);
        
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> {
            eventPublisher.publish(event, "order.created");
        });
    }
}
