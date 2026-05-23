# Ecommerce System - Onion Architecture & Event-Driven

## Architecture: Onion Model
Each service in this system strictly follows the Onion Architecture pattern:

1.  **Domain Layer** (`.Domain`): Pure business logic. Entities, Value Objects, and Repository Interfaces. No dependencies on frameworks (Spring, JPA, RabbitMQ).
2.  **Application Layer** (`.Application`): Use cases and orchestration. Maps request DTOs to Domain objects and triggers Domain actions.
3.  **Infrastructure Layer** (`.Infrastructure`): Implementation of adapters. JPA Repositories, RabbitMQ Producers/Consumers, External APIs.
4.  **Presentation Layer** (`.Api`): REST Controllers and Application Entry point.

## Event-Driven Flow (RabbitMQ)

The system uses a **Topic Exchange** (`app.exchange`) for decoupled communication:

1.  **Auth Service** publishes `user.registered`.
2.  **Order Service** publishes `order.created`.
3.  **Inventory Service** & **Payment Service** listen to `order.created` in parallel.
4.  **Inventory** publishes `stock.reserved` or `stock.failed`.
5.  **Payment** publishes `payment.completed` or `payment.failed`.
6.  **Shipping Service** listens for BOTH `payment.completed` AND `stock.reserved` to initiate shipment.
7.  **Notification Service** listens to ALL events to log/notify users.

## Routing Keys
- `user.registered`
- `order.created`
- `payment.completed` / `payment.failed`
- `stock.reserved` / `stock.failed`
- `shipment.created`

## How to Run
1. Start infrastructure: `docker-compose -f docker/docker-compose.yml up -d`
2. Build all modules: `mvn clean install`
3. Run services individually (e.g., `mvn spring-boot:run` in `AuthService/AuthService.Api`).
