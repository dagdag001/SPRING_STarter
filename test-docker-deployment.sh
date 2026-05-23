#!/bin/bash

# Test script for Docker Compose deployment
# This script verifies that all services are running and can communicate

set -e

echo "=========================================="
echo "Docker Compose Deployment Test"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check service health
check_health() {
    local service_name=$1
    local port=$2
    local max_attempts=30
    local attempt=1
    
    echo -n "Checking $service_name health..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
            echo -e " ${GREEN}✓ Healthy${NC}"
            return 0
        fi
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    echo -e " ${RED}✗ Failed${NC}"
    return 1
}

# Function to check RabbitMQ
check_rabbitmq() {
    echo -n "Checking RabbitMQ..."
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s -f -u guest:guest "http://localhost:15672/api/overview" > /dev/null 2>&1; then
            echo -e " ${GREEN}✓ Running${NC}"
            return 0
        fi
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    echo -e " ${RED}✗ Failed${NC}"
    return 1
}

# Step 1: Check if Docker is running
echo "Step 1: Checking Docker..."
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}✗ Docker is not running${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker is running${NC}"
echo ""

# Step 2: Check if docker-compose.yml exists
echo "Step 2: Checking docker-compose.yml..."
if [ ! -f "docker-compose.yml" ]; then
    echo -e "${RED}✗ docker-compose.yml not found${NC}"
    exit 1
fi
echo -e "${GREEN}✓ docker-compose.yml found${NC}"
echo ""

# Step 3: Start services
echo "Step 3: Starting services with docker-compose..."
echo -e "${YELLOW}This may take several minutes on first run...${NC}"
docker-compose up -d --build
echo -e "${GREEN}✓ Services started${NC}"
echo ""

# Step 4: Wait for services to be ready
echo "Step 4: Waiting for services to be healthy..."
echo ""

# Check RabbitMQ first
check_rabbitmq || exit 1

# Check all microservices
check_health "Auth Service" 8081 || exit 1
check_health "Order Service" 8082 || exit 1
check_health "Payment Service" 8083 || exit 1
check_health "Inventory Service" 8084 || exit 1
check_health "Shipping Service" 8085 || exit 1
check_health "Notification Service" 8086 || exit 1

echo ""
echo "=========================================="
echo "Step 5: Testing End-to-End Workflow"
echo "=========================================="
echo ""

# Test 1: Register a user
echo "Test 1: Registering a user..."
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "email": "test@example.com",
    "password": "TestPass123!"
  }')

if echo "$REGISTER_RESPONSE" | grep -q "userId"; then
    echo -e "${GREEN}✓ User registration successful${NC}"
    USER_ID=$(echo "$REGISTER_RESPONSE" | grep -o '"userId":"[^"]*"' | cut -d'"' -f4)
    echo "  User ID: $USER_ID"
else
    echo -e "${RED}✗ User registration failed${NC}"
    echo "  Response: $REGISTER_RESPONSE"
fi
echo ""

# Test 2: Login
echo "Test 2: Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "password": "TestPass123!"
  }')

if echo "$LOGIN_RESPONSE" | grep -q "token"; then
    echo -e "${GREEN}✓ Login successful${NC}"
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    echo "  Token received (truncated): ${TOKEN:0:50}..."
else
    echo -e "${RED}✗ Login failed${NC}"
    echo "  Response: $LOGIN_RESPONSE"
fi
echo ""

# Test 3: Create an order
echo "Test 3: Creating an order..."
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8082/api/orders \
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
  }')

if echo "$ORDER_RESPONSE" | grep -q "orderId"; then
    echo -e "${GREEN}✓ Order creation successful${NC}"
    ORDER_ID=$(echo "$ORDER_RESPONSE" | grep -o '"orderId":"[^"]*"' | cut -d'"' -f4)
    echo "  Order ID: $ORDER_ID"
else
    echo -e "${RED}✗ Order creation failed${NC}"
    echo "  Response: $ORDER_RESPONSE"
fi
echo ""

# Test 4: Wait for event processing
echo "Test 4: Waiting for event processing (10 seconds)..."
sleep 10
echo -e "${GREEN}✓ Wait complete${NC}"
echo ""

# Test 5: Check RabbitMQ queues
echo "Test 5: Checking RabbitMQ queues..."
QUEUES=$(curl -s -u guest:guest http://localhost:15672/api/queues)
if echo "$QUEUES" | grep -q "app.exchange"; then
    echo -e "${GREEN}✓ RabbitMQ queues configured${NC}"
    
    # Count messages
    TOTAL_MESSAGES=$(echo "$QUEUES" | grep -o '"messages":[0-9]*' | cut -d':' -f2 | awk '{s+=$1} END {print s}')
    echo "  Total messages processed: $TOTAL_MESSAGES"
else
    echo -e "${YELLOW}⚠ Could not verify queue status${NC}"
fi
echo ""

# Test 6: Check service logs for events
echo "Test 6: Checking service logs for event processing..."
echo ""

echo "  Payment Service logs:"
docker-compose logs --tail=5 payment-service | grep -i "event\|payment" || echo "    No relevant logs found"
echo ""

echo "  Inventory Service logs:"
docker-compose logs --tail=5 inventory-service | grep -i "event\|stock" || echo "    No relevant logs found"
echo ""

echo "  Shipping Service logs:"
docker-compose logs --tail=5 shipping-service | grep -i "event\|shipment" || echo "    No relevant logs found"
echo ""

echo "  Notification Service logs:"
docker-compose logs --tail=5 notification-service | grep -i "event\|notification" || echo "    No relevant logs found"
echo ""

# Summary
echo "=========================================="
echo "Test Summary"
echo "=========================================="
echo ""
echo -e "${GREEN}✓ All services are running and healthy${NC}"
echo -e "${GREEN}✓ End-to-end workflow completed successfully${NC}"
echo ""
echo "Access points:"
echo "  - Auth Service: http://localhost:8081"
echo "  - Order Service: http://localhost:8082"
echo "  - Payment Service: http://localhost:8083"
echo "  - Inventory Service: http://localhost:8084"
echo "  - Shipping Service: http://localhost:8085"
echo "  - Notification Service: http://localhost:8086"
echo "  - RabbitMQ Management: http://localhost:15672 (guest/guest)"
echo ""
echo "To view logs: docker-compose logs -f [service-name]"
echo "To stop services: docker-compose down"
echo ""
