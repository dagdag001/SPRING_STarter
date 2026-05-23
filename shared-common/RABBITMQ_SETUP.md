# RabbitMQ Configuration Infrastructure

This document describes the RabbitMQ configuration infrastructure implemented in the shared-common module.

## Overview

The RabbitMQ configuration infrastructure provides a reusable foundation for all microservices to publish and consume events through RabbitMQ. It includes:

1. **RabbitMQConfig** - Base configuration class with common beans
2. **EventPublisher** - Interface for publishing events
3. **RabbitMQEventPublisher** - RabbitMQ implementation of EventPublisher

## Components

### 1. RabbitMQConfig

**Location**: `com.example.shared.config.RabbitMQConfig`

**Purpose**: Provides common RabbitMQ beans for all microservices.

**Beans**:
- `TopicExchange appExchange()` - Creates the "app.exchange" topic exchange
- `Jackson2JsonMessageConverter messageConverter()` - JSON message converter with JavaTimeModule
- `RabbitTemplate rabbitTemplate(ConnectionFactory)` - RabbitTemplate configured with JSON converter

**Usage**:
```java
@Configuration
@Import(RabbitMQConfig.class)
public class ServiceSpecificConfig {
    // Service-specific queue and binding configurations
}
```

### 2. EventPublisher Interface

**Location**: `com.example.shared.messaging.EventPublisher`

**Purpose**: Defines the contract for publishing events to the message broker.

**Method**:
```java
<T> void publish(Event<T> event, String routingKey);
```

**Parameters**:
- `event` - The event to publish (must not be null)
- `routingKey` - The routing key for message routing (must not be null or empty)

**Throws**:
- `IllegalArgumentException` - If event or routingKey is null/empty
- `EventPublishException` - If publishing fails

### 3. RabbitMQEventPublisher

**Location**: `com.example.shared.messaging.RabbitMQEventPublisher`

**Purpose**: RabbitMQ implementation of EventPublisher using RabbitTemplate.

**Features**:
- Validates input parameters
- Logs event publication (INFO level)
- Logs successful publication (DEBUG level)
- Handles errors and throws EventPublishException
- Uses JSON serialization via Jackson2JsonMessageConverter

**Example Usage**:
```java
@Service
public class OrderService {
    
    private final EventPublisher eventPublisher;
    
    public OrderService(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    public void createOrder(CreateOrderRequest request) {
        // Create order logic...
        
        // Publish event
        OrderCreatedPayload payload = new OrderCreatedPayload(
            orderId, customerId, items, totalAmount
        );
        OrderCreatedEvent event = new OrderCreatedEvent(payload);
        eventPublisher.publish(event, "order.created");
    }
}
```

## Configuration

### Application Properties

Each microservice should configure RabbitMQ connection in `application.yml`:

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### Service-Specific Configuration

Each service should extend RabbitMQConfig with service-specific queues and bindings:

```java
@Configuration
@Import(RabbitMQConfig.class)
public class PaymentServiceRabbitMQConfig {
    
    @Bean
    public Queue paymentOrderCreatedQueue() {
        return new Queue("payment.order.created.queue", true);
    }
    
    @Bean
    public Binding paymentOrderCreatedBinding(
            Queue paymentOrderCreatedQueue, 
            TopicExchange appExchange) {
        return BindingBuilder
            .bind(paymentOrderCreatedQueue)
            .to(appExchange)
            .with("order.created");
    }
}
```

## Error Handling

### EventPublishException

Thrown when event publishing fails. Contains:
- Original exception as cause
- Event type in error message
- Full stack trace for debugging

**Handling**:
```java
try {
    eventPublisher.publish(event, routingKey);
} catch (EventPublishException e) {
    logger.error("Failed to publish event", e);
    // Handle error (retry, alert, etc.)
}
```

### Logging

The RabbitMQEventPublisher logs:
- **INFO**: Event publication attempts with eventId, eventType, routingKey
- **DEBUG**: Successful publications with eventId, eventType
- **ERROR**: Failed publications with full details and stack trace

## Testing

### Unit Tests

- `RabbitMQConfigTest` - Tests bean creation and configuration
- `RabbitMQEventPublisherTest` - Tests publishing logic with mocked RabbitTemplate

### Integration Tests

- `EventPublisherIntegrationTest` - Tests complete Spring context with RabbitMQ

**Running Tests**:
```bash
mvn test -pl shared-common
```

## Requirements Mapping

This implementation satisfies the following requirements:

- **Requirement 3.1**: Topic exchange "app.exchange" created
- **Requirement 3.2**: EventPublisher interface and implementation
- **Requirement 3.4**: RabbitTemplate with JSON message converter
- **Requirement 12.1**: RabbitMQ broker configuration
- **Requirement 13.1**: Error handling and logging

## Dependencies

Required dependencies (already in parent POM):
- `spring-boot-starter-amqp` - Spring AMQP support
- `jackson-databind` - JSON serialization
- `jackson-datatype-jsr310` - Java 8 date/time support

## Next Steps

After implementing this infrastructure, each microservice should:

1. Import RabbitMQConfig in their configuration
2. Define service-specific queues and bindings
3. Inject EventPublisher to publish events
4. Implement message consumers for event consumption

## Example: Complete Service Setup

```java
// 1. Configuration
@Configuration
@Import(RabbitMQConfig.class)
public class OrderServiceConfig {
    
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue("order.created.queue", true);
    }
    
    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(appExchange).with("order.created");
    }
}

// 2. Publishing Events
@Service
public class OrderApplicationService {
    
    private final EventPublisher eventPublisher;
    
    public OrderApplicationService(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    public OrderResponse createOrder(CreateOrderRequest request) {
        // Business logic...
        
        // Publish event
        OrderCreatedPayload payload = new OrderCreatedPayload(orderId, customerId, items, total);
        OrderCreatedEvent event = new OrderCreatedEvent(payload);
        eventPublisher.publish(event, "order.created");
        
        return response;
    }
}

// 3. Consuming Events (in other services)
@Component
public class OrderCreatedConsumer {
    
    @RabbitListener(queues = "payment.order.created.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Process event...
    }
}
```

## Troubleshooting

### Connection Refused

If you see "Connection refused" errors:
1. Ensure RabbitMQ is running: `docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management`
2. Check connection properties in application.yml
3. Verify network connectivity

### Serialization Errors

If you see JSON serialization errors:
1. Ensure all event classes have proper Jackson annotations
2. Verify JavaTimeModule is registered (handled by RabbitMQConfig)
3. Check that all fields are serializable

### Message Not Delivered

If messages are not being delivered:
1. Verify exchange exists: Check RabbitMQ management UI (http://localhost:15672)
2. Verify queue bindings are correct
3. Check routing key matches binding pattern
4. Enable DEBUG logging for org.springframework.amqp

## References

- [Spring AMQP Documentation](https://docs.spring.io/spring-amqp/reference/)
- [RabbitMQ Tutorial](https://www.rabbitmq.com/getstarted.html)
- [Design Document](../../.kiro/specs/event-driven-rabbitmq-system/design.md)
- [Requirements Document](../../.kiro/specs/event-driven-rabbitmq-system/requirements.md)
