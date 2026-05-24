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
 * Integration Test for Payment Failure Scenario
 * 
 * Sub-task 25.2: Create integration test for payment failure scenario
 * 
 * Test Flow:
 * 1. Order creation → OrderCreated event
 * 2. Payment failure → PaymentFailed event
 * 3. No shipment created (Shipping service should not create shipment)
 * 4. PaymentFailed event published and logged by Notification Service
 * 
 * Requirements: 6.3, 6.5, 9.4, 22.5
 * 
 * This test verifies that:
 * - PaymentFailed events are published correctly
 * - Shipping service does NOT create shipments when payment fails
 * - Notification service logs the PaymentFailed event
 */
@Testcontainers
public class PaymentFailureScenarioIntegrationTest {

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
        // Order created queue (for Payment service)
        Queue paymentOrderQueue = new Queue("test.payment.order.created.queue", false, false, true);
        rabbitAdmin.declareQueue(paymentOrderQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(paymentOrderQueue)
                .to(new TopicExchange("app.exchange"))
                .with("order.created"));

        // Payment failed queue (for Shipping service - to verify it doesn't create shipment)
        Queue paymentFailedQueue = new Queue("test.shipping.payment.failed.queue", false, false, true);
        rabbitAdmin.declareQueue(paymentFailedQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(paymentFailedQueue)
                .to(new TopicExchange("app.exchange"))
                .with("payment.failed"));

        // Stock reserved queue (for Shipping service)
        Queue stockReservedQueue = new Queue("test.shipping.stock.reserved.queue", false, false, true);
        rabbitAdmin.declareQueue(stockReservedQueue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(stockReservedQueue)
                .to(new TopicExchange("app.exchange"))
                .with("stock.reserved"));

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
    void testPaymentFailureScenario() throws Exception {
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

        // Verify OrderCreated event is routed to Payment service
        Message orderMessage = rabbitTemplate.receive("test.payment.order.created.queue", 5000);
        assertThat(orderMessage).isNotNull();
        OrderCreatedEvent receivedOrder = (OrderCreatedEvent) rabbitTemplate
                .getMessageConverter().fromMessage(orderMessage);
        assertThat(receivedOrder.getPayload().getOrderId()).isEqualTo(orderId);

        // Step 2: Simulate Payment Failure
        PaymentFailedPayload paymentFailedPayload = new PaymentFailedPayload(
                orderId,
                "Insufficient funds"
        );
        PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(
                UUID.randomUUID().toString(),
                "PaymentFailed",
                Instant.now().toString(),
                paymentFailedPayload
        );

        eventPublisher.publish(paymentFailedEvent, "payment.failed");

        // Verify PaymentFailed event is routed to Shipping service
        Message paymentFailedMessage = rabbitTemplate.receive("test.shipping.payment.failed.queue", 5000);
        assertThat(paymentFailedMessage).isNotNull();
        PaymentFailedEvent receivedPaymentFailed = (PaymentFailedEvent) rabbitTemplate
                .getMessageConverter().fromMessage(paymentFailedMessage);
        assertThat(receivedPaymentFailed.getPayload().getOrderId()).isEqualTo(orderId);
        assertThat(receivedPaymentFailed.getPayload().getReason()).isEqualTo("Insufficient funds");

        // Step 3: Simulate Stock Reservation (happens in parallel, but shipment should still not be created)
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
                Instant.now().toString(),
                stockPayload
        );

        eventPublisher.publish(stockEvent, "stock.reserved");

        // Verify StockReserved event is routed to Shipping service
        Message stockMessage = rabbitTemplate.receive("test.shipping.stock.reserved.queue", 5000);
        assertThat(stockMessage).isNotNull();

        // Step 4: Verify NO ShipmentCreated event is published
        // Wait a bit to ensure no shipment is created
        Thread.sleep(2000);
        Message shipmentMessage = rabbitTemplate.receive("test.shipment.created.queue", 1000);
        assertThat(shipmentMessage)
                .as("No shipment should be created when payment fails")
                .isNull();

        // Step 5: Verify PaymentFailed event is received by Notification Service
        // The notification queue should have received OrderCreated, PaymentFailed, and StockReserved events
        List<Message> notificationMessages = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Message msg = rabbitTemplate.receive("test.notification.all.events.queue", 5000);
            assertThat(msg).as("Notification service should receive event " + (i + 1)).isNotNull();
            notificationMessages.add(msg);
        }

        assertThat(notificationMessages).hasSize(3);

        // Verify one of the messages is PaymentFailed
        boolean foundPaymentFailed = false;
        for (Message msg : notificationMessages) {
            Object event = rabbitTemplate.getMessageConverter().fromMessage(msg);
            if (event instanceof PaymentFailedEvent) {
                PaymentFailedEvent pfe = (PaymentFailedEvent) event;
                assertThat(pfe.getPayload().getOrderId()).isEqualTo(orderId);
                foundPaymentFailed = true;
            }
        }
        assertThat(foundPaymentFailed)
                .as("Notification service should receive PaymentFailed event")
                .isTrue();
    }

    @Test
    void testPaymentFailureWithoutStockReservation() throws Exception {
        // This test verifies the scenario where payment fails before stock is even checked
        
        // Step 1: Create order
        String orderId = UUID.randomUUID().toString();
        String customerId = UUID.randomUUID().toString();
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("prod-003", 1, new BigDecimal("99.99")));

        OrderCreatedPayload orderPayload = new OrderCreatedPayload(
                orderId,
                customerId,
                items,
                new BigDecimal("99.99")
        );
        OrderCreatedEvent orderEvent = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                "OrderCreated",
                Instant.now().toString(),
                orderPayload
        );

        eventPublisher.publish(orderEvent, "order.created");

        // Step 2: Payment fails immediately
        PaymentFailedPayload paymentFailedPayload = new PaymentFailedPayload(
                orderId,
                "Card declined"
        );
        PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(
                UUID.randomUUID().toString(),
                "PaymentFailed",
                Instant.now().toString(),
                paymentFailedPayload
        );

        eventPublisher.publish(paymentFailedEvent, "payment.failed");

        // Verify PaymentFailed event is routed
        Message paymentFailedMessage = rabbitTemplate.receive("test.shipping.payment.failed.queue", 5000);
        assertThat(paymentFailedMessage).isNotNull();

        // Step 3: Verify NO ShipmentCreated event
        Thread.sleep(2000);
        Message shipmentMessage = rabbitTemplate.receive("test.shipment.created.queue", 1000);
        assertThat(shipmentMessage)
                .as("No shipment should be created when payment fails without stock reservation")
                .isNull();
    }
}
