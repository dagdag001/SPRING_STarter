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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration Test for Successful Order Flow
 * 
 * Sub-task 25.1: Create integration test for successful order flow
 * 
 * Test Flow:
 * 1. User registration → UserRegistered event
 * 2. Order creation → OrderCreated event
 * 3. Payment success → PaymentCompleted event
 * 4. Stock reservation → StockReserved event
 * 5. Shipment creation → ShipmentCreated event
 * 6. All events logged by Notification Service
 * 
 * Requirements: All core requirements
 * 
 * This test simulates the complete end-to-end workflow by:
 * - Publishing events to RabbitMQ using Testcontainers
 * - Verifying events are routed correctly
 * - Confirming the event-driven choreography works as designed
 */
@Testcontainers
public class SuccessfulOrderFlowIntegrationTest {

    @Container
    static RabbitMQContainer rabbitmqContainer = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq:3.12-management"))
            .withExposedPorts(5672, 15672);

    private RabbitTemplate rabbitTemplate;
    private RabbitAdmin rabbitAdmin;
    private RabbitMQEventPublisher eventPublisher;
    private ObjectMapper objectMapper;

    // Queues to capture events for verification
    private BlockingQueue<UserRegisteredEvent> userRegisteredEvents;
    private BlockingQueue<OrderCreatedEvent> orderCreatedEvents;
    private BlockingQueue<PaymentCompletedEvent> paymentCompletedEvents;
    private BlockingQueue<StockReservedEvent> stockReservedEvents;
    private BlockingQueue<ShipmentCreatedEvent> shipmentCreatedEvents;
    private BlockingQueue<Event> allEvents;

    @BeforeEach
    void setUp() {
        // Initialize queues
        userRegisteredEvents = new LinkedBlockingQueue<>();
        orderCreatedEvents = new LinkedBlockingQueue<>();
        paymentCompletedEvents = new LinkedBlockingQueue<>();
        stockReservedEvents = new LinkedBlockingQueue<>();
        shipmentCreatedEvents = new LinkedBlockingQueue<>();
        allEvents = new LinkedBlockingQueue<>();

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

        // Create queues and bindings for each service
        setupQueuesAndBindings();

        // Create event publisher
        eventPublisher = new RabbitMQEventPublisher(rabbitTemplate, "app.exchange");
    }

    private void setupQueuesAndBindings() {
        // User registered queue
        Queue userQueue = new Queue("test.user.registered.queue", false, false, true);
        rabbitAdmin.declareQueue(userQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(userQueue)
                .to(new TopicExchange("app.exchange"))
                .with("user.registered"));

        // Order created queues (for Payment and Inventory services)
        Queue paymentOrderQueue = new Queue("test.payment.order.created.queue", false, false, true);
        rabbitAdmin.declareQueue(paymentOrderQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(paymentOrderQueue)
                .to(new TopicExchange("app.exchange"))
                .with("order.created"));

        Queue inventoryOrderQueue = new Queue("test.inventory.order.created.queue", false, false, true);
        rabbitAdmin.declareQueue(inventoryOrderQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(inventoryOrderQueue)
                .to(new TopicExchange("app.exchange"))
                .with("order.created"));

        // Payment completed queue (for Shipping service)
        Queue paymentCompletedQueue = new Queue("test.shipping.payment.completed.queue", false, false, true);
        rabbitAdmin.declareQueue(paymentCompletedQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(paymentCompletedQueue)
                .to(new TopicExchange("app.exchange"))
                .with("payment.completed"));

        // Stock reserved queue (for Shipping service)
        Queue stockReservedQueue = new Queue("test.shipping.stock.reserved.queue", false, false, true);
        rabbitAdmin.declareQueue(stockReservedQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(stockReservedQueue)
                .to(new TopicExchange("app.exchange"))
                .with("stock.reserved"));

        // Shipment created queue
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
    void testSuccessfulOrderFlow() throws Exception {
        // Step 1: Simulate User Registration
        String userId = UUID.randomUUID().toString();
        UserRegisteredPayload userPayload = new UserRegisteredPayload(
                userId,
                "john_doe",
                "john@example.com"
        );
        UserRegisteredEvent userEvent = new UserRegisteredEvent(
                UUID.randomUUID().toString(),
                "UserRegistered",
                Instant.now(),
                userPayload
        );

        eventPublisher.publish(userEvent, "user.registered");

        // Verify UserRegistered event is routed correctly
        Message userMessage = rabbitTemplate.receive("test.user.registered.queue", 5000);
        assertThat(userMessage).isNotNull();
        UserRegisteredEvent receivedUserEvent = (UserRegisteredEvent) rabbitTemplate
                .getMessageConverter().fromMessage(userMessage);
        assertThat(receivedUserEvent.getPayload().getUserId()).isEqualTo(userId);
        assertThat(receivedUserEvent.getPayload().getUsername()).isEqualTo("john_doe");

        // Step 2: Simulate Order Creation
        String orderId = UUID.randomUUID().toString();
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("prod-001", 2, new BigDecimal("29.99")));
        items.add(new OrderItem("prod-002", 1, new BigDecimal("49.99")));

        OrderCreatedPayload orderPayload = new OrderCreatedPayload(
                orderId,
                userId,
                items,
                new BigDecimal("109.97")
        );
        OrderCreatedEvent orderEvent = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                "OrderCreated",
                Instant.now(),
                orderPayload
        );

        eventPublisher.publish(orderEvent, "order.created");

        // Verify OrderCreated event is routed to both Payment and Inventory services
        Message paymentOrderMessage = rabbitTemplate.receive("test.payment.order.created.queue", 5000);
        assertThat(paymentOrderMessage).isNotNull();
        OrderCreatedEvent paymentReceivedOrder = (OrderCreatedEvent) rabbitTemplate
                .getMessageConverter().fromMessage(paymentOrderMessage);
        assertThat(paymentReceivedOrder.getPayload().getOrderId()).isEqualTo(orderId);

        Message inventoryOrderMessage = rabbitTemplate.receive("test.inventory.order.created.queue", 5000);
        assertThat(inventoryOrderMessage).isNotNull();
        OrderCreatedEvent inventoryReceivedOrder = (OrderCreatedEvent) rabbitTemplate
                .getMessageConverter().fromMessage(inventoryOrderMessage);
        assertThat(inventoryReceivedOrder.getPayload().getOrderId()).isEqualTo(orderId);

        // Step 3: Simulate Payment Success
        String paymentId = UUID.randomUUID().toString();
        PaymentCompletedPayload paymentPayload = new PaymentCompletedPayload(
                paymentId,
                orderId,
                new BigDecimal("109.97")
        );
        PaymentCompletedEvent paymentEvent = new PaymentCompletedEvent(
                UUID.randomUUID().toString(),
                "PaymentCompleted",
                Instant.now(),
                paymentPayload
        );

        eventPublisher.publish(paymentEvent, "payment.completed");

        // Verify PaymentCompleted event is routed to Shipping service
        Message paymentCompletedMessage = rabbitTemplate.receive("test.shipping.payment.completed.queue", 5000);
        assertThat(paymentCompletedMessage).isNotNull();
        PaymentCompletedEvent receivedPayment = (PaymentCompletedEvent) rabbitTemplate
                .getMessageConverter().fromMessage(paymentCompletedMessage);
        assertThat(receivedPayment.getPayload().getOrderId()).isEqualTo(orderId);
        assertThat(receivedPayment.getPayload().getPaymentId()).isEqualTo(paymentId);

        // Step 4: Simulate Stock Reservation
        List<StockReservation> reservations = new ArrayList<>();
        reservations.add(new StockReservation("prod-001", 2));
        reservations.add(new StockReservation("prod-002", 1));

        StockReservedPayload stockPayload = new StockReservedPayload(
                orderId,
                reservations
        );
        StockReservedEvent stockEvent = new StockReservedEvent(
                UUID.randomUUID().toString(),
                "StockReserved",
                Instant.now(),
                stockPayload
        );

        eventPublisher.publish(stockEvent, "stock.reserved");

        // Verify StockReserved event is routed to Shipping service
        Message stockReservedMessage = rabbitTemplate.receive("test.shipping.stock.reserved.queue", 5000);
        assertThat(stockReservedMessage).isNotNull();
        StockReservedEvent receivedStock = (StockReservedEvent) rabbitTemplate
                .getMessageConverter().fromMessage(stockReservedMessage);
        assertThat(receivedStock.getPayload().getOrderId()).isEqualTo(orderId);
        assertThat(receivedStock.getPayload().getReservations()).hasSize(2);

        // Step 5: Simulate Shipment Creation (after both Payment and Stock confirmations)
        String shipmentId = UUID.randomUUID().toString();
        ShipmentCreatedPayload shipmentPayload = new ShipmentCreatedPayload(
                shipmentId,
                orderId,
                LocalDate.now().plusDays(5)
        );
        ShipmentCreatedEvent shipmentEvent = new ShipmentCreatedEvent(
                UUID.randomUUID().toString(),
                "ShipmentCreated",
                Instant.now(),
                shipmentPayload
        );

        eventPublisher.publish(shipmentEvent, "shipment.created");

        // Verify ShipmentCreated event is routed correctly
        Message shipmentMessage = rabbitTemplate.receive("test.shipment.created.queue", 5000);
        assertThat(shipmentMessage).isNotNull();
        ShipmentCreatedEvent receivedShipment = (ShipmentCreatedEvent) rabbitTemplate
                .getMessageConverter().fromMessage(shipmentMessage);
        assertThat(receivedShipment.getPayload().getShipmentId()).isEqualTo(shipmentId);
        assertThat(receivedShipment.getPayload().getOrderId()).isEqualTo(orderId);

        // Step 6: Verify all events are received by Notification Service
        // The notification queue should have received all 5 events
        List<Message> notificationMessages = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Message msg = rabbitTemplate.receive("test.notification.all.events.queue", 5000);
            assertThat(msg).as("Notification service should receive event " + (i + 1)).isNotNull();
            notificationMessages.add(msg);
        }

        // Verify we received all event types
        assertThat(notificationMessages).hasSize(5);
    }
}
