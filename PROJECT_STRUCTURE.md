# Project Structure Documentation

## Overview

This document describes the Maven multi-module project structure for the Event-Driven RabbitMQ Microservices System.

## Root Project

**Artifact**: `event-driven-rabbitmq-system`  
**Type**: Maven multi-module parent POM  
**Spring Boot Version**: 3.2.1  
**Java Version**: 17

### Parent POM Configuration

The root `pom.xml` includes:

- **Parent**: `spring-boot-starter-parent:3.2.1`
- **Modules**: 7 modules (1 shared + 6 services)
- **Dependency Management**: Centralized version management for all dependencies
- **Common Dependencies**:
  - Spring Boot Starter Web
  - Spring Boot Starter AMQP (RabbitMQ)
  - Spring Boot Starter Data JPA
  - H2 Database
  - Lombok (optional)
  - Spring Boot Starter Test
  - Spring AMQP Test
  - jqwik (property-based testing)

## Modules

### 1. shared-common

**Purpose**: Shared event classes, utilities, and common code used by all microservices

**Package Structure**:
```
com.example.shared/
├── event/          # Event base classes and concrete event types
└── exception/      # Shared exception hierarchy
```

**Dependencies**:
- Jackson (JSON serialization)
- Jackson JSR310 (Java 8 date/time support)

### 2. auth-service

**Purpose**: User registration, authentication, and JWT token management  
**Port**: 8081

**Onion Architecture Layers**:
```
com.example.authservice/
├── domain/
│   ├── entity/         # User entity
│   ├── valueobject/    # Email value object
│   └── repository/     # UserRepository interface
├── application/
│   ├── usecase/        # RegisterUserUseCase, AuthenticateUserUseCase, ValidateTokenUseCase
│   ├── dto/            # Application DTOs
│   └── port/           # Port interfaces (EventPublisher, etc.)
├── infrastructure/
│   ├── persistence/    # JPA entities and repository implementations
│   ├── messaging/      # RabbitMQ publishers and consumers
│   └── config/         # Spring configuration
└── presentation/
    ├── controller/     # REST controllers (AuthController)
    ├── dto/            # Request/Response DTOs
    └── exception/      # Exception handlers
```

**Additional Dependencies**:
- Spring Security Crypto (BCrypt password hashing)
- jjwt (JWT token generation and validation)

### 3. order-service

**Purpose**: Order creation and management  
**Port**: 8082

**Onion Architecture Layers**:
```
com.example.orderservice/
├── domain/
│   ├── entity/         # Order entity
│   ├── valueobject/    # OrderItem value object
│   └── repository/     # OrderRepository interface
├── application/
│   ├── usecase/        # CreateOrderUseCase, GetOrderUseCase
│   ├── dto/            # Application DTOs
│   └── port/           # Port interfaces
├── infrastructure/
│   ├── persistence/    # JPA entities and repository implementations
│   ├── messaging/      # RabbitMQ publishers
│   └── config/         # Spring configuration
└── presentation/
    ├── controller/     # REST controllers (OrderController)
    ├── dto/            # Request/Response DTOs
    └── exception/      # Exception handlers
```

### 4. payment-service

**Purpose**: Payment processing for orders  
**Port**: 8083

**Onion Architecture Layers**:
```
com.example.paymentservice/
├── domain/
│   ├── entity/         # Payment entity
│   └── repository/     # PaymentRepository interface
├── application/
│   ├── usecase/        # ProcessPaymentUseCase
│   └── port/           # Port interfaces
└── infrastructure/
    ├── persistence/    # JPA entities and repository implementations
    ├── messaging/      # RabbitMQ consumers (OrderCreatedConsumer) and publishers
    └── config/         # Spring configuration
```

**Event Consumption**: Listens to `order.created` routing key  
**Event Publishing**: Publishes `payment.completed` or `payment.failed`

### 5. inventory-service

**Purpose**: Stock management and reservations  
**Port**: 8084

**Onion Architecture Layers**:
```
com.example.inventoryservice/
├── domain/
│   ├── entity/         # Product, StockReservation entities
│   └── repository/     # ProductRepository, StockReservationRepository interfaces
├── application/
│   ├── usecase/        # CheckStockUseCase, ReserveStockUseCase
│   └── port/           # Port interfaces
└── infrastructure/
    ├── persistence/    # JPA entities and repository implementations
    ├── messaging/      # RabbitMQ consumers (OrderCreatedConsumer) and publishers
    └── config/         # Spring configuration
```

**Event Consumption**: Listens to `order.created` routing key  
**Event Publishing**: Publishes `stock.reserved` or `stock.failed`

### 6. shipping-service

**Purpose**: Shipment coordination (waits for payment and stock confirmations)  
**Port**: 8085

**Onion Architecture Layers**:
```
com.example.shippingservice/
├── domain/
│   ├── entity/         # Shipment, OrderConfirmation entities
│   └── repository/     # ShipmentRepository, OrderConfirmationRepository interfaces
├── application/
│   ├── usecase/        # CorrelateOrderEventsUseCase, CreateShipmentUseCase
│   └── port/           # Port interfaces
└── infrastructure/
    ├── persistence/    # JPA entities and repository implementations
    ├── messaging/      # RabbitMQ consumers (PaymentCompletedConsumer, StockReservedConsumer) and publishers
    └── config/         # Spring configuration
```

**Event Consumption**: Listens to `payment.completed` and `stock.reserved` routing keys  
**Event Publishing**: Publishes `shipment.created`  
**Special Logic**: Correlates events by order ID before creating shipment

### 7. notification-service

**Purpose**: Logging and notification for all system events  
**Port**: 8086

**Onion Architecture Layers**:
```
com.example.notificationservice/
├── domain/
│   ├── entity/         # NotificationLog entity
│   └── repository/     # NotificationLogRepository interface
├── application/
│   ├── usecase/        # LogNotificationUseCase
│   └── port/           # Port interfaces
└── infrastructure/
    ├── persistence/    # JPA entities and repository implementations
    ├── messaging/      # RabbitMQ consumers (AllEventsConsumer)
    └── config/         # Spring configuration
```

**Event Consumption**: Listens to `#` wildcard (all events)  
**Event Publishing**: None (only consumes)

## Onion Architecture Principles

Each service follows strict Onion Architecture with these dependency rules:

1. **Domain Layer** (innermost):
   - Contains business entities, value objects, and repository interfaces
   - Has ZERO external dependencies
   - Pure Java/Kotlin code

2. **Application Layer**:
   - Contains use cases and application services
   - Depends ONLY on Domain Layer
   - Orchestrates domain logic

3. **Infrastructure Layer**:
   - Contains implementations for databases, RabbitMQ, external services
   - Depends on Domain and Application layers
   - Implements interfaces defined in inner layers

4. **Presentation Layer** (outermost):
   - Contains REST controllers and API endpoints
   - Depends on Application Layer
   - Handles HTTP requests/responses

## Build Configuration

### Maven Commands

```bash
# Build all modules
mvn clean install

# Build specific module
cd auth-service
mvn clean package

# Run tests
mvn test

# Run specific service
cd auth-service
mvn spring-boot:run
```

### Module Dependencies

```
shared-common (no dependencies)
    ↑
    ├── auth-service
    ├── order-service
    ├── payment-service
    ├── inventory-service
    ├── shipping-service
    └── notification-service
```

All service modules depend on `shared-common` for event classes and utilities.

## Testing Structure

Each service has a test directory structure:

```
src/test/java/com/example/servicename/
├── domain/         # Domain layer unit tests
├── application/    # Application layer unit tests
├── infrastructure/ # Infrastructure layer integration tests
├── presentation/   # Presentation layer controller tests
└── property/       # Property-based tests (jqwik)
```

## Configuration Files

Each service will have:

- `src/main/resources/application.yml`: Service configuration
- `src/main/resources/schema.sql`: Database schema (optional)
- `src/test/resources/application-test.yml`: Test configuration

## Next Steps

With the project structure complete, the next tasks are:

1. **Task 2**: Implement shared event base classes in `shared-common`
2. **Task 3**: Set up RabbitMQ configuration infrastructure
3. **Task 4-12**: Implement each microservice with domain, application, infrastructure, and presentation layers
4. **Task 13+**: Add error handling, testing, and optional features

## Summary

✅ **Completed**: Task 1 - Set up project structure and shared components

- ✅ Maven multi-module parent POM with Spring Boot 3.2.1
- ✅ 7 modules created (shared-common + 6 services)
- ✅ Onion Architecture package structure for all services
- ✅ Dependencies configured (Spring Boot, AMQP, JPA, H2, jqwik)
- ✅ Build configuration with dependency management
- ✅ Test directories created
- ✅ Documentation (README.md, PROJECT_STRUCTURE.md)
