# Docker Compose Deployment Guide

This guide explains how to deploy the Event-Driven RabbitMQ Microservices System using Docker Compose.

## Prerequisites

- Docker Engine 20.10 or higher
- Docker Compose 2.0 or higher
- At least 4GB of available RAM
- Ports 8081-8086, 5672, and 15672 available on your host machine

## Architecture

The deployment includes:
- **RabbitMQ** (ports 5672, 15672) - Message broker with management UI
- **Auth Service** (port 8081) - User authentication and registration
- **Order Service** (port 8082) - Order creation and management
- **Payment Service** (port 8083) - Payment processing
- **Inventory Service** (port 8084) - Stock management and reservation
- **Shipping Service** (port 8085) - Shipment coordination
- **Notification Service** (port 8086) - Event logging and notifications

## Quick Start

### 1. Build and Start All Services

```bash
docker-compose up --build
```

This command will:
- Build Docker images for all 6 microservices
- Start RabbitMQ with management UI
- Start all microservices with proper dependencies
- Configure health checks and automatic restarts

### 2. Start Services in Detached Mode

```bash
docker-compose up -d --build
```

### 3. View Logs

```bash
# View all service logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f auth-service
docker-compose logs -f order-service
docker-compose logs -f payment-service
```

### 4. Check Service Health

```bash
# Check all container status
docker-compose ps

# Check specific service health
curl http://localhost:8081/actuator/health  # Auth Service
curl http://localhost:8082/actuator/health  # Order Service
curl http://localhost:8083/actuator/health  # Payment Service
curl http://localhost:8084/actuator/health  # Inventory Service
curl http://localhost:8085/actuator/health  # Shipping Service
curl http://localhost:8086/actuator/health  # Notification Service
```

### 5. Access RabbitMQ Management UI

Open your browser and navigate to:
```
http://localhost:15672
```

**Default credentials:**
- Username: `guest`
- Password: `guest`

## Testing the System

### 1. Register a User

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePass123!"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePass123!"
  }'
```

### 3. Create an Order

```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "123e4567-e89b-12d3-a456-426614174000",
    "items": [
      {
        "productId": "prod-001",
        "quantity": 2,
        "price": 29.99
      },
      {
        "productId": "prod-002",
        "quantity": 1,
        "price": 49.99
      }
    ]
  }'
```

### 4. Monitor Events in RabbitMQ

1. Open RabbitMQ Management UI: http://localhost:15672
2. Navigate to "Queues" tab
3. Observe messages being processed across different queues
4. Check the "Exchanges" tab to see the `app.exchange` topic exchange

### 5. Verify End-to-End Workflow

After creating an order, the following should happen automatically:
1. **Order Service** publishes `OrderCreated` event
2. **Payment Service** processes payment and publishes `PaymentCompleted` or `PaymentFailed`
3. **Inventory Service** checks stock and publishes `StockReserved` or `StockFailed`
4. **Shipping Service** waits for both confirmations, then creates shipment
5. **Notification Service** logs all events

Check the logs to verify:
```bash
docker-compose logs -f payment-service inventory-service shipping-service notification-service
```

## Stopping the System

### Stop All Services

```bash
docker-compose down
```

### Stop and Remove Volumes

```bash
docker-compose down -v
```

### Stop and Remove Images

```bash
docker-compose down --rmi all
```

## Troubleshooting

### Services Not Starting

1. Check if ports are already in use:
```bash
netstat -an | grep -E "8081|8082|8083|8084|8085|8086|5672|15672"
```

2. Check Docker logs:
```bash
docker-compose logs
```

3. Verify Docker resources:
```bash
docker system df
docker system prune  # Clean up unused resources
```

### RabbitMQ Connection Issues

1. Verify RabbitMQ is healthy:
```bash
docker-compose ps rabbitmq
```

2. Check RabbitMQ logs:
```bash
docker-compose logs rabbitmq
```

3. Restart RabbitMQ:
```bash
docker-compose restart rabbitmq
```

### Service Health Check Failures

1. Increase health check timeout in docker-compose.yml
2. Check if Spring Boot Actuator is enabled in application.yml
3. Verify service logs for startup errors

### Memory Issues

If services are crashing due to memory:

1. Increase Docker memory limit in Docker Desktop settings
2. Add memory limits to docker-compose.yml:
```yaml
services:
  auth-service:
    deploy:
      resources:
        limits:
          memory: 512M
```

## Development Tips

### Rebuild Specific Service

```bash
docker-compose up -d --build auth-service
```

### Scale Services (if needed)

```bash
docker-compose up -d --scale payment-service=2
```

### Execute Commands in Container

```bash
docker-compose exec auth-service sh
```

### View Resource Usage

```bash
docker stats
```

## Network Configuration

All services communicate through the `microservices-network` bridge network. Services can reach each other using their container names:
- `rabbitmq:5672` - RabbitMQ AMQP
- `auth-service:8081`
- `order-service:8082`
- `payment-service:8083`
- `inventory-service:8084`
- `shipping-service:8085`
- `notification-service:8086`

## Environment Variables

Each service accepts the following environment variables:

- `SPRING_PROFILES_ACTIVE` - Active Spring profile (default: docker)
- `SPRING_RABBITMQ_HOST` - RabbitMQ host (default: rabbitmq)
- `SPRING_RABBITMQ_PORT` - RabbitMQ port (default: 5672)
- `SPRING_RABBITMQ_USERNAME` - RabbitMQ username (default: guest)
- `SPRING_RABBITMQ_PASSWORD` - RabbitMQ password (default: guest)
- `SPRING_DATASOURCE_URL` - Database URL (H2 in-memory by default)
- `SERVER_PORT` - Service HTTP port

## Production Considerations

For production deployment, consider:

1. **Use PostgreSQL instead of H2**:
   - Add PostgreSQL service to docker-compose.yml
   - Update SPRING_DATASOURCE_URL for each service

2. **Enable Security**:
   - Change RabbitMQ default credentials
   - Enable JWT validation on protected endpoints
   - Use secrets management (Docker Secrets, Vault)

3. **Add Monitoring**:
   - Integrate Prometheus for metrics
   - Add Grafana for visualization
   - Configure centralized logging (ELK stack)

4. **Configure Resource Limits**:
   - Set CPU and memory limits for each service
   - Configure JVM heap sizes

5. **Use Production-Grade RabbitMQ**:
   - Enable persistence
   - Configure clustering
   - Set up dead letter queues

6. **Implement Backup Strategy**:
   - Regular database backups
   - RabbitMQ message persistence

## Support

For issues or questions:
1. Check service logs: `docker-compose logs [service-name]`
2. Verify RabbitMQ Management UI: http://localhost:15672
3. Review health endpoints: http://localhost:808X/actuator/health
