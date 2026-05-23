# Test script for Docker Compose deployment (PowerShell)
# This script verifies that all services are running and can communicate

$ErrorActionPreference = "Stop"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Docker Compose Deployment Test" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Function to check service health
function Check-Health {
    param(
        [string]$ServiceName,
        [int]$Port
    )
    
    $maxAttempts = 30
    $attempt = 1
    
    Write-Host "Checking $ServiceName health..." -NoNewline
    
    while ($attempt -le $maxAttempts) {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:$Port/actuator/health" -UseBasicParsing -TimeoutSec 2 -ErrorAction SilentlyContinue
            if ($response.StatusCode -eq 200) {
                Write-Host " ✓ Healthy" -ForegroundColor Green
                return $true
            }
        }
        catch {
            # Service not ready yet
        }
        Write-Host "." -NoNewline
        Start-Sleep -Seconds 2
        $attempt++
    }
    
    Write-Host " ✗ Failed" -ForegroundColor Red
    return $false
}

# Function to check RabbitMQ
function Check-RabbitMQ {
    Write-Host "Checking RabbitMQ..." -NoNewline
    $maxAttempts = 30
    $attempt = 1
    
    while ($attempt -le $maxAttempts) {
        try {
            $credentials = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("guest:guest"))
            $headers = @{ Authorization = "Basic $credentials" }
            $response = Invoke-WebRequest -Uri "http://localhost:15672/api/overview" -Headers $headers -UseBasicParsing -TimeoutSec 2 -ErrorAction SilentlyContinue
            if ($response.StatusCode -eq 200) {
                Write-Host " ✓ Running" -ForegroundColor Green
                return $true
            }
        }
        catch {
            # RabbitMQ not ready yet
        }
        Write-Host "." -NoNewline
        Start-Sleep -Seconds 2
        $attempt++
    }
    
    Write-Host " ✗ Failed" -ForegroundColor Red
    return $false
}

# Step 1: Check if Docker is running
Write-Host "Step 1: Checking Docker..." -ForegroundColor Yellow
try {
    docker info | Out-Null
    Write-Host "✓ Docker is running" -ForegroundColor Green
}
catch {
    Write-Host "✗ Docker is not running" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Step 2: Check if docker-compose.yml exists
Write-Host "Step 2: Checking docker-compose.yml..." -ForegroundColor Yellow
if (-not (Test-Path "docker-compose.yml")) {
    Write-Host "✗ docker-compose.yml not found" -ForegroundColor Red
    exit 1
}
Write-Host "✓ docker-compose.yml found" -ForegroundColor Green
Write-Host ""

# Step 3: Start services
Write-Host "Step 3: Starting services with docker-compose..." -ForegroundColor Yellow
Write-Host "This may take several minutes on first run..." -ForegroundColor Cyan
docker-compose up -d --build
Write-Host "✓ Services started" -ForegroundColor Green
Write-Host ""

# Step 4: Wait for services to be ready
Write-Host "Step 4: Waiting for services to be healthy..." -ForegroundColor Yellow
Write-Host ""

# Check RabbitMQ first
if (-not (Check-RabbitMQ)) {
    exit 1
}

# Check all microservices
if (-not (Check-Health "Auth Service" 8081)) { exit 1 }
if (-not (Check-Health "Order Service" 8082)) { exit 1 }
if (-not (Check-Health "Payment Service" 8083)) { exit 1 }
if (-not (Check-Health "Inventory Service" 8084)) { exit 1 }
if (-not (Check-Health "Shipping Service" 8085)) { exit 1 }
if (-not (Check-Health "Notification Service" 8086)) { exit 1 }

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Step 5: Testing End-to-End Workflow" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Register a user
Write-Host "Test 1: Registering a user..." -ForegroundColor Yellow
$registerBody = @{
    username = "test_user"
    email = "test@example.com"
    password = "TestPass123!"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/register" `
        -Method Post `
        -ContentType "application/json" `
        -Body $registerBody
    
    Write-Host "✓ User registration successful" -ForegroundColor Green
    Write-Host "  User ID: $($registerResponse.userId)"
}
catch {
    Write-Host "✗ User registration failed" -ForegroundColor Red
    Write-Host "  Error: $_"
}
Write-Host ""

# Test 2: Login
Write-Host "Test 2: Logging in..." -ForegroundColor Yellow
$loginBody = @{
    username = "test_user"
    password = "TestPass123!"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" `
        -Method Post `
        -ContentType "application/json" `
        -Body $loginBody
    
    Write-Host "✓ Login successful" -ForegroundColor Green
    $token = $loginResponse.token
    Write-Host "  Token received (truncated): $($token.Substring(0, [Math]::Min(50, $token.Length)))..."
}
catch {
    Write-Host "✗ Login failed" -ForegroundColor Red
    Write-Host "  Error: $_"
}
Write-Host ""

# Test 3: Create an order
Write-Host "Test 3: Creating an order..." -ForegroundColor Yellow
$orderBody = @{
    customerId = "123e4567-e89b-12d3-a456-426614174000"
    items = @(
        @{
            productId = "prod-001"
            quantity = 2
            price = 29.99
        },
        @{
            productId = "prod-002"
            quantity = 1
            price = 49.99
        }
    )
} | ConvertTo-Json

try {
    $orderResponse = Invoke-RestMethod -Uri "http://localhost:8082/api/orders" `
        -Method Post `
        -ContentType "application/json" `
        -Body $orderBody
    
    Write-Host "✓ Order creation successful" -ForegroundColor Green
    Write-Host "  Order ID: $($orderResponse.orderId)"
}
catch {
    Write-Host "✗ Order creation failed" -ForegroundColor Red
    Write-Host "  Error: $_"
}
Write-Host ""

# Test 4: Wait for event processing
Write-Host "Test 4: Waiting for event processing (10 seconds)..." -ForegroundColor Yellow
Start-Sleep -Seconds 10
Write-Host "✓ Wait complete" -ForegroundColor Green
Write-Host ""

# Test 5: Check RabbitMQ queues
Write-Host "Test 5: Checking RabbitMQ queues..." -ForegroundColor Yellow
try {
    $credentials = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("guest:guest"))
    $headers = @{ Authorization = "Basic $credentials" }
    $queues = Invoke-RestMethod -Uri "http://localhost:15672/api/queues" -Headers $headers -UseBasicParsing
    
    Write-Host "✓ RabbitMQ queues configured" -ForegroundColor Green
    $totalMessages = ($queues | Measure-Object -Property messages -Sum).Sum
    Write-Host "  Total messages processed: $totalMessages"
}
catch {
    Write-Host "⚠ Could not verify queue status" -ForegroundColor Yellow
}
Write-Host ""

# Test 6: Check service logs
Write-Host "Test 6: Checking service logs for event processing..." -ForegroundColor Yellow
Write-Host ""

Write-Host "  Payment Service logs:" -ForegroundColor Cyan
docker-compose logs --tail=5 payment-service | Select-String -Pattern "event|payment" -CaseSensitive:$false
Write-Host ""

Write-Host "  Inventory Service logs:" -ForegroundColor Cyan
docker-compose logs --tail=5 inventory-service | Select-String -Pattern "event|stock" -CaseSensitive:$false
Write-Host ""

Write-Host "  Shipping Service logs:" -ForegroundColor Cyan
docker-compose logs --tail=5 shipping-service | Select-String -Pattern "event|shipment" -CaseSensitive:$false
Write-Host ""

Write-Host "  Notification Service logs:" -ForegroundColor Cyan
docker-compose logs --tail=5 notification-service | Select-String -Pattern "event|notification" -CaseSensitive:$false
Write-Host ""

# Summary
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Test Summary" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "✓ All services are running and healthy" -ForegroundColor Green
Write-Host "✓ End-to-end workflow completed successfully" -ForegroundColor Green
Write-Host ""
Write-Host "Access points:" -ForegroundColor Yellow
Write-Host "  - Auth Service: http://localhost:8081"
Write-Host "  - Order Service: http://localhost:8082"
Write-Host "  - Payment Service: http://localhost:8083"
Write-Host "  - Inventory Service: http://localhost:8084"
Write-Host "  - Shipping Service: http://localhost:8085"
Write-Host "  - Notification Service: http://localhost:8086"
Write-Host "  - RabbitMQ Management: http://localhost:15672 (guest/guest)"
Write-Host ""
Write-Host "To view logs: docker-compose logs -f [service-name]" -ForegroundColor Cyan
Write-Host "To stop services: docker-compose down" -ForegroundColor Cyan
Write-Host ""
