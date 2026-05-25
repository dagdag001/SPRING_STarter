# Event-Driven RabbitMQ Microservices System

A distributed event-driven microservices system built with Spring Boot 3.x and RabbitMQ. The system consists of 6 independent microservices that communicate exclusively through asynchronous events, demonstrating event-driven choreography and Onion Architecture principles.

## 🔧 Recent Fixes

**✅ RESOLVED: Bean Name Conflict (May 2026)**
- Fixed RabbitMQ configuration conflicts across all services
- Renamed service-specific configs from `RabbitMQConfig` to `*ServiceQueueConfig` pattern
- Removed duplicate bean definitions (appExchange, messageConverter, rabbitTemplate)
- All services now use shared RabbitMQ beans from `shared-common` module
- Services only define their specific queues and bindings
- **Impact**: Order Service and all other services now start successfully without bean conflicts

## 🏗️ Architecture

This system follows **Onion Architecture** principles with strict layer separation:

- **Domain Layer**: Business entities and rules (no external dependencies)
- **Application Layer**: Use cases and application services
- **Infrastructure Layer**: Database, RabbitMQ, and external integrations
- **Presentation Layer**: REST controllers and API endpoints

## 🚀 Microservices

| Service | Port | Description |
|---------|------|-------------|
| **Auth Service** | 8081 | User registration and JWT authentication |
| **Order Service** | 8082 | Order creation and management |
| **Payment Service** | 8083 | Payment processing (mock) |
| **Inventory Service** | 8084 | Stock management and reservations |
| **Shipping Service** | 8085 | Shipment coordination (event correlation) |
| **Notification Service** | 8086 | Event logging and notifications |

## 🛠️ Technology Stack

- **Java**: 17+
- **Spring Boot**: 3.2.1
- **Spring AMQP**: RabbitMQ integration
- **Spring Data JPA**: Database persistence
- **H2 Database**: In-memory database (development)
- **RabbitMQ**: Message broker (3.12+)
- **jqwik**: Property-based testing
- **JUnit 5**: Unit testing
- **Testcontainers**: Integration testing
- **Maven**: Build tool
- **Docker**: Containerization

## 📋 Prerequisites

- Java 17 or later
- Maven 3.6+
- RabbitMQ 3.12+ (or Docker)
- Docker & Docker Compose (for containerized deployment)

## 🏃 Quick Start

### Option 1: Docker Compose (Recommended)

```bash
# Start all services with RabbitMQ
docker-compose up --build

# Or run in detached mode
docker-compose up -d --build
```

See [DOCKER_QUICK_START.md](DOCKER_QUICK_START.md) for detailed instructions.

### Option 2: Local Development

**1. Start RabbitMQ**
```bash
docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management
```

**2. Build the project**
```bash
mvn clean install
```

**3. Run services individually**
```bash
# Terminal 1: Auth Service
cd auth-service && mvn spring-boot:run

# Terminal 2: Order Service
cd order-service && mvn spring-boot:run

# Terminal 3: Payment Service
cd payment-service && mvn spring-boot:run

# Terminal 4: Inventory Service
cd inventory-service && mvn spring-boot:run

# Terminal 5: Shipping Service
cd shipping-service && mvn spring-boot:run

# Terminal 6: Notification Service
cd notification-service && mvn spring-boot:run
```

## 🔄 Event Flow

The system demonstrates event-driven choreography:

1. **User Registration** → Auth Service publishes `UserRegistered` event
2. **Order Creation** → Order Service publishes `OrderCreated` event
3. **Payment Processing** → Payment Service publishes `PaymentCompleted` or `PaymentFailed`
4. **Stock Reservation** → Inventory Service publishes `StockReserved` or `StockFailed`
5. **Shipment Creation** → Shipping Service waits for both confirmations, then publishes `ShipmentCreated`
6. **Event Logging** → Notification Service logs all events

## 📡 RabbitMQ Configuration

- **Exchange**: `app.exchange` (Topic Exchange)
- **Routing Keys**:
  - `user.registered`
  - `order.created`
  - `payment.completed` / `payment.failed`
  - `stock.reserved` / `stock.failed`
  - `shipment.created`

**RabbitMQ Management UI**: http://localhost:15672 (guest/guest)

## 🧪 Testing

The project includes comprehensive testing:

```bash
# Run all tests
mvn test

# Run tests for specific service
mvn test -pl auth-service

# Run integration tests
mvn test -pl shared-common -Dtest=*IntegrationTest
```

**Test Types**:
- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test end-to-end workflows with Testcontainers
- **Property-Based Tests**: Test universal properties using jqwik (optional)

## 📚 Documentation

- [Docker Deployment Guide](DOCKER_DEPLOYMENT.md) - Comprehensive Docker deployment instructions
- [Docker Quick Start](DOCKER_QUICK_START.md) - Quick start guide for Docker
- [Project Structure](PROJECT_STRUCTURE.md) - Detailed architecture and module structure
- [RabbitMQ Setup](shared-common/RABBITMQ_SETUP.md) - RabbitMQ configuration guide
- [JWT Validation](order-service/JWT_VALIDATION_README.md) - JWT authentication feature

## 🔐 Security Features

- **JWT Authentication**: Optional JWT validation on Order Service endpoints
- **Password Hashing**: BCrypt password hashing in Auth Service
- **Stateless Sessions**: No server-side session management

## 🐳 Docker Support

The project includes full Docker support:

- **Dockerfiles**: Each service has an optimized multi-stage Dockerfile
- **Docker Compose**: Complete orchestration with RabbitMQ
- **Health Checks**: Automatic health monitoring
- **Auto-restart**: Services restart on failure

## 📊 Monitoring

- **Spring Boot Actuator**: Health and metrics endpoints on `/actuator`
- **RabbitMQ Management**: Web UI for queue monitoring
- **Logs**: Structured logging with correlation IDs

## 🏗️ Project Structure

```
event-driven-rabbitmq-system/
├── shared-common/              # Shared event classes and utilities
├── auth-service/               # Authentication microservice
├── order-service/              # Order management microservice
├── payment-service/            # Payment processing microservice
├── inventory-service/          # Inventory management microservice
├── shipping-service/           # Shipping coordination microservice
├── notification-service/       # Notification logging microservice
├── docker-compose.yml          # Docker orchestration
├── pom.xml                     # Parent POM
└── README.md                   # This file
```

Each service follows Onion Architecture:
```
service-name/
├── domain/         # Business entities and rules
├── application/    # Use cases and DTOs
├── infrastructure/ # Database, RabbitMQ, config
└── presentation/   # REST controllers
```

## 🤝 Contributing

This is a demonstration project showcasing event-driven microservices architecture with Spring Boot and RabbitMQ.

## 📝 License

This project is provided as-is for educational and demonstration purposes.

## 🎯 Key Features

✅ Event-driven choreography (no orchestrator)  
✅ Onion Architecture with strict layer separation  
✅ Independent microservices with separate databases  
✅ Asynchronous communication via RabbitMQ  
✅ Parallel event processing (Payment & Inventory)  
✅ Event correlation (Shipping Service)  
✅ Comprehensive testing (Unit, Integration, Property-based)  
✅ Docker support with health checks  
✅ JWT authentication (optional)  
✅ Production-ready error handling  

## 🔗 Useful Links

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring AMQP Documentation](https://docs.spring.io/spring-amqp/reference/)
- [RabbitMQ Tutorials](https://www.rabbitmq.com/getstarted.html)
- [Testcontainers Documentation](https://www.testcontainers.org/)
