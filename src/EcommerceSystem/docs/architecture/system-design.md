# System Design

## Architecture Overview
This system follows a microservices architecture using Spring Boot and Hexagonal Architecture (Domain-Driven Design) principles.

### Components
- **API Layer**: Controller layer for handling HTTP requests.
- **Application Layer**: Business logic, use cases, and DTOs.
- **Domain Layer**: Core entities, value objects, and repository interfaces.
- **Infrastructure Layer**: Implementation details (Persistence, RabbitMQ, Security).
