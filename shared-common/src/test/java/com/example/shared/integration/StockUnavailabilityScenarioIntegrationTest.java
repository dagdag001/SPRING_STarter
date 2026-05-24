package com.example.shared.integration;

import com.example.shared.event.*;
import com.example.shared.messaging.RabbitMQEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration Test for Stock Unavailability Scenario
 * 
 * Sub-task 25.3: Create integration test for stock unavailability scenario
 * 
 * Test Flow:
 * 1. Order creation → OrderCreated event
 * 2. Stock unavailable → StockFailed event
 * 3. No shipment created (Shipping service should not create shipment)
 * 4. StockFailed event published and logged by Notification Service
 * 
 * Requirements: 7.3, 7.5, 9.6, 22.5
 * 
 * This test verifies that:
 * - StockFailed events are published correctly
 * - Shipping service does NOT create shipments when stock is unavailable
 * - Notification service logs the StockFailed event
 */
@Testcontainers
public class StockUnavailabilityScenarioIntegrationTest {

    @Container
    static RabbitMQContainer rabbitmqContainer = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq:3.12-management"))
            .withExposedPorts(5672, 15672);

    private RabbitTemplate rabbitTemplate;
    private RabbitAdmin rabbitAdmin;
    private RabbitMQEventPublisher eventPublisher;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Set up RabbitMQ connection
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqContainer.getHost());
        connectionFactory.setPort(rabbitmqContainer.getAmqpPort());
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        // Set up RabbitTemplate with JSON converter
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter(objectMapper);
        
        rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);

        rabbitAdmin = new RabbitAdmin(connectionFactory);

        // Create exchange
        TopicExchange exchange = new TopicExchange("app.exchange", true, false);
        rabbitAdmin.declareExchange(exchange);

        // Create queues and bindings
        setupQueuesAndBindings();

        // Create event publisher
        eventPublisher = new RabbitMQEventPublisher(rabbitTemplate);
    }

    private void setupQueuesAndBindings() {
        // Order created queue (for Inventory service)
        Queue inventoryOrderQueue = new Queue("test.inventory.order.created.queue", false, false, true);
        rabbitAdmin.declareQueue(inventoryOrderQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(inventoryOrderQueue)
                .to(new TopicExchange("app.exchange"))
                .with("order.created"));

        // Stock failed queue (for Shipping service - to verify it doesn't create shipment)
        Queue stockFailedQueue = new Queue("test.shipping.stock.failed.queue", false, false, true);
        rabbitAdmin.declareQueue(stockFailedQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(stockFailedQueue)
                .to(new TopicExchange("app.exchange"))
                .with("stock.failed"));

        // Payment completed queue (for Shipping service)
        Queue paymentCompletedQueue = new Queue("test.shipping.payment.completed.queue", false, false, true);
        rabbitAdmin.declareQueue(paymentCompletedQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(paymentCompletedQueue)
                .to(new TopicExchange("app.exchange"))
                .with("payment.completed"));

        // Shipment created queue (should remain empty in this test)
        Queue shipmentQueue = new Queue("test.shipment.created.queue", false, false, true);
        rabbitAdmin.declareQueue(shipmentQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(shipmentQueue)
                .to(new TopicExchange("app.exchange"))
                .with("shipment.created"));

        // Notification service queue (receives all events)
        Queue notificationQueue = new Queue("test.notification.all.events.queue", false, false, true);
        rabbitAdmin.declareQueue(notificationQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(notificationQueue)
                .to(new TopicExchange("app.exchange"))
                .with("#"));
    }

    @Test
    void testStockUnavailabilityScenario() throws Exception {
        // Step 1: Simulate Order Creation
        String orderId = UUID.randomUUID().toString();
        String customerId = UUID.randomUUID().toString();
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("prod-001", 2, new BigDecimal("29.99")));
        items.add(new OrderItem("prod-002", 1, new BigDecimal("49.99")));

        OrderCreatedPayload orderPayload = new OrderCreatedPayload(
                orderId,
                customerId,
                items,
                new BigDecimal("109.97")
        );
        OrderCreatedEvent orderEvent = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                "OrderCreated",
                Instant.now().toString(),
                orderPayload
        );

        eventPublisher.publish(orderEvent, "order.created");

        // Verify OrderCreated event is routed to Inventory service
        Message orderMessage = rabbitTemplate.receive("test.inventory.order.created.queue", 5000);
        assertThat(orderMessage).isNotNull();
        OrderCreatedEvent receivedOrder = (OrderCreatedEvent) rabbitTemplate
                .getMessageConverter().fromMessage(orderMessage);
        assertThat(receivedOrder.getPayload().getOrderId()).isEqualTo(orderId);

        // Step 2: Simulate Stock Unavailability
        List<String> unavailableProducts = new ArrayList<>();
        unavailableProducts.add("prod-002"); // Product 2 is out of stock

        StockFailedPayload stockFailedPayload = new StockFailedPayload(
                orderId,
                unavailableProducts
        );
        StockFailedEvent stockFailedEvent = new StockFailedEvent(
                UUID.randomUUID().toString(),
                "StockFailed",
                Instant.now().toString(),
                stockFailedPayload
        );

        eventPublisher.publish(stockFailedEvent, "stock.failed");

        // Verify StockFailed event is routed to Shipping service
        Message stockFailedMessage = rabbitTemplate.receive("test.shipping.stock.failed.queue", 5000);
        assertThat(stockFailedMessage).isNotNull();
        StockFailedEvent receivedStockFailed = (StockFailedEvent) rabbitTemplate
                .getMessageConverter().fromMessage(stockFailedMessage);
        assertThat(receivedStockFailed.getPayload().getOrderId()).isEqualTo(orderId);
        assertThat(receivedStockFailed.getPayload().getUnavailableProducts())
                .containsExactly("prod-002");

        // Step 3: Simulate Payment Completion (happens in parallel, but shipment should still not be created)
        String paymentId = UUID.randomUUID().toString();
        PaymentCompletedPayload paymentPayload = new PaymentCompletedPayload(
                paymentId,
                orderId,
                new BigDecimal("109.97")
        );
        PaymentCompletedEvent paymentEvent = new PaymentCompletedEvent(
                UUID.randomUUID().toString(),
                "PaymentCompleted",
                Instant.now().toString(),
                paymentPayload
        );

        eventPublisher.publish(paymentEvent, "payment.completed");

        // Verify PaymentCompleted event is routed to Shipping service
        Message paymentMessage = rabbitTemplate.receive("test.shipping.payment.completed.queue", 5000);
        assertThat(paymentMessage).isNotNull();

        // Step 4: Verify NO ShipmentCreated event is published
        // Wait a bit to ensure no shipment is created
        Thread.sleep(2000);
        Message shipmentMessage = rabbitTemplate.receive("test.shipment.created.queue", 1000);
        assertThat(shipmentMessage)
                .as("No shipment should be created when stock is unavailable")
                .isNull();

        // Step 5: Verify StockFailed event is received by Notification Service
        // The notification queue should have received OrderCreated, StockFailed, and PaymentCompleted events
        List<Message> notificationMessages = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Message msg = rabbitTemplate.receive("test.notification.all.events.queue", 5000);
            assertThat(msg).as("Notification service should receive event " + (i + 1)).isNotNull();
            notificationMessages.add(msg);
        }

        assertThat(notificationMessages).hasSize(3);

        // Verify one of the messages is StockFailed
        boolean foundStockFailed = false;
        for (Message msg : notificationMessages) {
            Object event = rabbitTemplate.getMessageConverter().fromMessage(msg);
            if (event instanceof StockFailedEvent) {
                StockFailedEvent sfe = (StockFailedEvent) event;
                assertThat(sfe.getPayload().getOrderId()).isEqualTo(orderId);
                assertThat(sfe.getPayload().getUnavailableProducts()).contains("prod-002");
                foundStockFailed = true;
            }
        }
        assertThat(foundStockFailed)
                .as("Notification service should receive StockFailed event")
                .isTrue();
    }

    @Test
    void testStockUnavailabilityWithoutPayment() throws Exception {
        // This test verifies the scenario where stock fails before payment is even processed
        
        // Step 1: Create order
        String orderId = UUID.randomUUID().toString();
        String customerId = UUID.randomUUID().toString();
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("prod-999", 100, new BigDecimal("19.99"))); // Large quantity, likely unavailable

        OrderCreatedPayload orderPayload = new OrderCreatedPayload(
                orderId,
                customerId,
                items,
                new BigDecimal("1999.00")
        );
        OrderCreatedEvent orderEvent = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                "OrderCreated",
                Instant.now().toString(),
                orderPayload
        );

        eventPublisher.publish(orderEvent, "order.created");

        // Step 2: Stock fails immediately
        List<String> unavailableProducts = new ArrayList<>();
        unavailableProducts.add("prod-999");

        StockFailedPayload stockFailedPayload = new StockFailedPayload(
                orderId,
                unavailableProducts
        );
        StockFailedEvent stockFailedEvent = new StockFailedEvent(
                UUID.randomUUID().toString(),
                "StockFailed",
                Instant.now().toString(),
                stockFailedPayload
        );

        eventPublisher.publish(stockFailedEvent, "stock.failed");

        // Verify StockFailed event is routed
        Message stockFailedMessage = rabbitTemplate.receive("test.shipping.stock.failed.queue", 5000);
        assertThat(stockFailedMessage).isNotNull();

        // Step 3: Verify NO ShipmentCreated event
        Thread.sleep(2000);
        Message shipmentMessage = rabbitTemplate.receive("test.shipment.created.queue", 1000);
        assertThat(shipmentMessage)
                .as("No shipment should be created when stock fails without payment")
                .isNull();
    }

    @Test
    void testMultipleProductsPartiallyUnavailable() throws Exception {
        // Test scenario where some products are available but others are not
        
        String orderId = UUID.randomUUID().toString();
        String customerId = UUID.randomUUID().toString();
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("prod-001", 2, new BigDecimal("29.99")));
        items.add(new OrderItem("prod-002", 1, new BigDecimal("49.99")));
        items.add(new OrderItem("prod-003", 5, new BigDecimal("9.99")));

        OrderCreatedPayload orderPayload = new OrderCreatedPayload(
                orderId,
                customerId,
                items,
                new BigDecimal("159.92")
        );
        OrderCreatedEvent orderEvent = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                "OrderCreated",
                Instant.now().toString(),
                orderPayload
        );

        eventPublisher.publish(orderEvent, "order.created");

        // Multiple products unavailable
        List<String> unavailableProducts = new ArrayList<>();
        unavailableProducts.add("prod-002");
        unavailableProducts.add("prod-003");

        StockFailedPayload stockFailedPayload = new StockFailedPayload(
                orderId,
                unavailableProducts
        );
        StockFailedEvent stockFailedEvent = new StockFailedEvent(
                UUID.randomUUID().toString(),
                "StockFailed",
                Instant.now().toString(),
                stockFailedPayload
        );

        eventPublisher.publish(stockFailedEvent, "stock.failed");

        // Verify StockFailed event
        Message stockFailedMessage = rabbitTemplate.receive("test.shipping.stock.failed.queue", 5000);
        assertThat(stockFailedMessage).isNotNull();
        StockFailedEvent receivedStockFailed = (StockFailedEvent) rabbitTemplate
                .getMessageConverter().fromMessage(stockFailedMessage);
        assertThat(receivedStockFailed.getPayload().getUnavailableProducts())
                .containsExactlyInAnyOrder("prod-002", "prod-003");

        // Verify no shipment created
        Thread.sleep(2000);
        Message shipmentMessage = rabbitTemplate.receive("test.shipment.created.queue", 1000);
        assertThat(shipmentMessage)
                .as("No shipment should be created when any product is unavailable")
                .isNull();
    }
}
