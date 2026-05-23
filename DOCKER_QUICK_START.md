# Docker Compose Quick Start

## Prerequisites
- Docker Engine 20.10+
- Docker Compose 2.0+
- 4GB RAM available
- Ports 8081-8086, 5672, 15672 free

## Start the System

```bash
# Build and start all services
docker-compose up --build

# Or run in background
docker-compose up -d --build
```

## Test the System

### Option 1: Automated Test (Bash)
```bash
chmod +x test-docker-deployment.sh
./test-docker-deployment.sh
```

### Option 2: Automated Test (PowerShell)
```powershell
.\test-docker-deployment.ps1
```

### Option 3: Manual Testing

**1. Register a User**
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"Pass123!"}'
```

**2. Login**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"Pass123!"}'
```

**3. Create Order**
```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId":"123e4567-e89b-12d3-a456-426614174000",
    "items":[
      {"productId":"prod-001","quantity":2,"price":29.99},
      {"productId":"prod-002","quantity":1,"price":49.99}
    ]
  }'
```

## Monitor the System

**View Logs**
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f payment-service
```

**Check Health**
```bash
curl http://localhost:8081/actuator/health  # Auth
curl http://localhost:8082/actuator/health  # Order
curl http://localhost:8083/actuator/health  # Payment
curl http://localhost:8084/actuator/health  # Inventory
curl http://localhost:8085/actuator/health  # Shipping
curl http://localhost:8086/actuator/health  # Notification
```

**RabbitMQ Management UI**
- URL: http://localhost:15672
- Username: `guest`
- Password: `guest`

## Stop the System

```bash
# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Stop and remove images
docker-compose down --rmi all
```

## Troubleshooting

**Services won't start?**
```bash
# Check logs
docker-compose logs

# Check container status
docker-compose ps

# Restart specific service
docker-compose restart payment-service
```

**Port conflicts?**
```bash
# Check what's using the ports
netstat -an | grep -E "8081|8082|8083|8084|8085|8086|5672|15672"

# Or on Windows
netstat -an | findstr "8081 8082 8083 8084 8085 8086 5672 15672"
```

**Out of memory?**
- Increase Docker memory limit in Docker Desktop settings
- Minimum 4GB recommended

## Service Ports

| Service | Port | Endpoint |
|---------|------|----------|
| Auth Service | 8081 | http://localhost:8081 |
| Order Service | 8082 | http://localhost:8082 |
| Payment Service | 8083 | http://localhost:8083 |
| Inventory Service | 8084 | http://localhost:8084 |
| Shipping Service | 8085 | http://localhost:8085 |
| Notification Service | 8086 | http://localhost:8086 |
| RabbitMQ AMQP | 5672 | amqp://localhost:5672 |
| RabbitMQ Management | 15672 | http://localhost:15672 |

## Expected Workflow

1. User registers → `UserRegistered` event published
2. User creates order → `OrderCreated` event published
3. Payment Service processes payment → `PaymentCompleted` or `PaymentFailed` event
4. Inventory Service checks stock → `StockReserved` or `StockFailed` event
5. Shipping Service waits for both confirmations → `ShipmentCreated` event
6. Notification Service logs all events

Check RabbitMQ Management UI to see events flowing through queues!
