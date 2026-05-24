# Postman Testing Guide

This guide provides step-by-step instructions for testing your Event-Driven Microservices System using Postman.

## Prerequisites

1. **Start the services** using Docker Compose:
   ```bash
   docker-compose up -d --build
   ```

2. **Verify all services are running**:
   ```bash
   docker-compose ps
   ```

3. **Install Postman** from https://www.postman.com/downloads/

## Service Endpoints

| Service | Port | Base URL |
|---------|------|----------|
| Auth Service | 8081 | http://localhost:8081 |
| Order Service | 8082 | http://localhost:8082 |
| Payment Service | 8083 | http://localhost:8083 |
| Inventory Service | 8084 | http://localhost:8084 |
| Shipping Service | 8085 | http://localhost:8085 |
| Notification Service | 8086 | http://localhost:8086 |
| RabbitMQ Management | 15672 | http://localhost:15672 |

---

## 1. Health Check Endpoints

Test that all services are running properly.

### Check Auth Service Health
- **Method**: `GET`
- **URL**: `http://localhost:8081/actuator/health`
- **Expected Response**: 
  ```json
  {
    "status": "UP"
  }
  ```

### Check All Services
Repeat the above for each service by changing the port:
- Order Service: `http://localhost:8082/actuator/health`
- Payment Service: `http://localhost:8083/actuator/health`
- Inventory Service: `http://localhost:8084/actuator/health`
- Shipping Service: `http://localhost:8085/actuator/health`
- Notification Service: `http://localhost:8086/actuator/health`

---

## 2. Auth Service - User Registration

### Register a New User
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/auth/register`
- **Headers**:
  ```
  Content-Type: application/json
  ```
- **Body** (raw JSON):
  ```json
  {
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePass123!"
  }
  ```
- **Expected Response** (201 Created):
  ```json
  {
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "username": "john_doe",
    "email": "john@example.com",
    "createdAt": "2026-05-24T10:30:00"
  }
  ```

### Test Validation Errors
Try registering with invalid data:

**Missing Required Fields**:
```json
{
  "username": "john_doe"
}
```
Expected: 400 Bad Request

**Invalid Email Format**:
```json
{
  "username": "john_doe",
  "email": "invalid-email",
  "password": "SecurePass123!"
}
```
Expected: 400 Bad Request

---

## 3. Auth Service - User Login

### Login with Credentials
- **Method**: `POST`
- **URL**: `http://localhost:8081/api/auth/login`
- **Headers**:
  ```
  Content-Type: application/json
  ```
- **Body** (raw JSON):
  ```json
  {
    "username": "john_doe",
    "password": "SecurePass123!"
  }
  ```
- **Expected Response** (200 OK):
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresInHours": 1
  }
  ```

**💡 Tip**: Save the token for future authenticated requests!

### Test Invalid Login
**Wrong Password**:
```json
{
  "username": "john_doe",
  "password": "WrongPassword"
}
```
Expected: 401 Unauthorized

---

## 4. Order Service - Create Order

### Create a New Order
- **Method**: `POST`
- **URL**: `http://localhost:8082/api/orders`
- **Headers**:
  ```
  Content-Type: application/json
  ```
- **Body** (raw JSON):
  ```json
  {
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
  }
  ```
- **Expected Response** (201 Created):
  ```json
  {
    "orderId": "ord-123e4567-e89b-12d3-a456-426614174001",
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
    ],
    "totalAmount": 109.97,
    "status": "PENDING",
    "createdAt": "2026-05-24T10:35:00"
  }
  ```

**💡 Important**: Save the `orderId` from the response for the next request!

---

## 5. Order Service - Get Order Details

### Retrieve Order by ID
- **Method**: `GET`
- **URL**: `http://localhost:8082/api/orders/{orderId}`
  - Replace `{orderId}` with the actual order ID from the previous response
  - Example: `http://localhost:8082/api/orders/ord-123e4567-e89b-12d3-a456-426614174001`
- **Headers**: None required
- **Expected Response** (200 OK):
  ```json
  {
    "orderId": "ord-123e4567-e89b-12d3-a456-426614174001",
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
    ],
    "totalAmount": 109.97,
    "status": "COMPLETED",
    "createdAt": "2026-05-24T10:35:00"
  }
  ```

**Note**: The status may have changed from `PENDING` to `COMPLETED` after payment and inventory processing.

---

## 6. Testing Event-Driven Flow

After creating an order, the following happens automatically through RabbitMQ:

1. **Order Service** → Publishes `OrderCreated` event
2. **Payment Service** → Processes payment → Publishes `PaymentCompleted`
3. **Inventory Service** → Reserves stock → Publishes `StockReserved`
4. **Shipping Service** → Creates shipment → Publishes `ShipmentCreated`
5. **Notification Service** → Logs all events

### Monitor Events in RabbitMQ Management UI

1. Open browser: `http://localhost:15672`
2. Login with:
   - Username: `guest`
   - Password: `guest`
3. Navigate to **Queues** tab
4. You should see queues like:
   - `payment.queue`
   - `inventory.queue`
   - `shipping.queue`
   - `notification.queue`
5. Click on a queue to see message details

### Check Service Logs

Monitor the event processing in real-time:

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f payment-service
docker-compose logs -f inventory-service
docker-compose logs -f shipping-service
docker-compose logs -f notification-service
```

---

## 7. Creating a Postman Collection

### Option A: Manual Setup

1. **Create a new Collection**:
   - Click "New" → "Collection"
   - Name it: "Microservices Event-Driven System"

2. **Add Environment Variables**:
   - Click "Environments" → "Create Environment"
   - Name: "Local Development"
   - Add variables:
     ```
     base_url_auth: http://localhost:8081
     base_url_order: http://localhost:8082
     jwt_token: (leave empty, will be set after login)
     customer_id: 123e4567-e89b-12d3-a456-426614174000
     order_id: (leave empty, will be set after order creation)
     ```

3. **Create Folders** in your collection:
   - Health Checks
   - Auth Service
   - Order Service

4. **Add Requests** as described above in each folder

### Option B: Import Collection (JSON)

Create a file named `Microservices_Collection.postman_collection.json` with the following content:

```json
{
  "info": {
    "name": "Microservices Event-Driven System",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Health Checks",
      "item": [
        {
          "name": "Auth Service Health",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "http://localhost:8081/actuator/health",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8081",
              "path": ["actuator", "health"]
            }
          }
        },
        {
          "name": "Order Service Health",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "http://localhost:8082/actuator/health",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8082",
              "path": ["actuator", "health"]
            }
          }
        }
      ]
    },
    {
      "name": "Auth Service",
      "item": [
        {
          "name": "Register User",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"john_doe\",\n  \"email\": \"john@example.com\",\n  \"password\": \"SecurePass123!\"\n}"
            },
            "url": {
              "raw": "http://localhost:8081/api/auth/register",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8081",
              "path": ["api", "auth", "register"]
            }
          }
        },
        {
          "name": "Login User",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"john_doe\",\n  \"password\": \"SecurePass123!\"\n}"
            },
            "url": {
              "raw": "http://localhost:8081/api/auth/login",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8081",
              "path": ["api", "auth", "login"]
            }
          }
        }
      ]
    },
    {
      "name": "Order Service",
      "item": [
        {
          "name": "Create Order",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"customerId\": \"123e4567-e89b-12d3-a456-426614174000\",\n  \"items\": [\n    {\n      \"productId\": \"prod-001\",\n      \"quantity\": 2,\n      \"price\": 29.99\n    },\n    {\n      \"productId\": \"prod-002\",\n      \"quantity\": 1,\n      \"price\": 49.99\n    }\n  ]\n}"
            },
            "url": {
              "raw": "http://localhost:8082/api/orders",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8082",
              "path": ["api", "orders"]
            }
          }
        },
        {
          "name": "Get Order by ID",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "http://localhost:8082/api/orders/{{order_id}}",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8082",
              "path": ["api", "orders", "{{order_id}}"]
            }
          }
        }
      ]
    }
  ]
}
```

Then import it in Postman:
1. Click "Import" button
2. Select the JSON file
3. Click "Import"

---

## 8. Testing Workflow (Complete End-to-End)

Follow this sequence to test the complete system:

### Step 1: Verify Services are Running
```
GET http://localhost:8081/actuator/health
GET http://localhost:8082/actuator/health
GET http://localhost:8083/actuator/health
GET http://localhost:8084/actuator/health
GET http://localhost:8085/actuator/health
GET http://localhost:8086/actuator/health
```
✅ All should return `{"status": "UP"}`

### Step 2: Register a User
```
POST http://localhost:8081/api/auth/register
Body: { "username": "john_doe", "email": "john@example.com", "password": "SecurePass123!" }
```
✅ Should return 201 with user details

### Step 3: Login
```
POST http://localhost:8081/api/auth/login
Body: { "username": "john_doe", "password": "SecurePass123!" }
```
✅ Should return 200 with JWT token

### Step 4: Create an Order
```
POST http://localhost:8082/api/orders
Body: { "customerId": "...", "items": [...] }
```
✅ Should return 201 with order details
📝 **Save the orderId from response**

### Step 5: Monitor RabbitMQ
1. Open http://localhost:15672
2. Go to Queues tab
3. Watch messages being processed

### Step 6: Check Order Status
```
GET http://localhost:8082/api/orders/{orderId}
```
✅ Status should change from PENDING → COMPLETED

### Step 7: View Logs
```bash
docker-compose logs -f payment-service inventory-service shipping-service
```
✅ You should see event processing logs

---

## 9. Common Issues and Troubleshooting

### Issue: Connection Refused
**Solution**: Ensure services are running:
```bash
docker-compose ps
```

### Issue: 404 Not Found
**Solution**: Check the URL path and port number match the service

### Issue: 400 Bad Request
**Solution**: Verify JSON body format and required fields

### Issue: 500 Internal Server Error
**Solution**: Check service logs:
```bash
docker-compose logs [service-name]
```

### Issue: Order Status Not Changing
**Solution**: 
1. Check RabbitMQ is running: `docker-compose ps rabbitmq`
2. Verify message queues: http://localhost:15672
3. Check consumer service logs

---

## 10. Advanced Testing

### Test Concurrent Orders
Create multiple orders simultaneously to test event handling:
1. Send 5-10 order creation requests
2. Monitor RabbitMQ queues
3. Verify all orders are processed correctly

### Test Invalid Data
Try various invalid inputs:
- Negative quantities
- Zero prices
- Missing required fields
- Invalid UUIDs

### Performance Testing
Use Postman's Collection Runner:
1. Select your collection
2. Click "Run"
3. Set iterations (e.g., 100)
4. Monitor response times and success rates

---

## Quick Reference Card

| Action | Method | URL | Body Required |
|--------|--------|-----|---------------|
| Health Check | GET | `http://localhost:808X/actuator/health` | No |
| Register | POST | `http://localhost:8081/api/auth/register` | Yes |
| Login | POST | `http://localhost:8081/api/auth/login` | Yes |
| Create Order | POST | `http://localhost:8082/api/orders` | Yes |
| Get Order | GET | `http://localhost:8082/api/orders/{id}` | No |

---

## Support

For issues:
1. Check service logs: `docker-compose logs [service-name]`
2. Verify RabbitMQ: http://localhost:15672
3. Review health endpoints
4. Check Docker container status: `docker-compose ps`

Happy Testing! 🚀
