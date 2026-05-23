# RabbitMQ Event Flow

## Exchange
- **Name**: `app.exchange`
- **Type**: `topic` (Suggested)

## Queues and Routing Keys

| Queue Name | Routing Key | Description |
|------------|-------------|-------------|
| `auth.queue` | `user.registered` | Triggered when a new user registers |
| `order.queue` | `order.created` | Triggered when a new order is placed |
| `payment.queue` | `payment.completed`, `payment.failed` | Triggered based on payment status |
| `inventory.queue` | `stock.reserved`, `stock.failed` | Triggered for stock management |
| `shipping.queue` | `shipment.created` | Triggered when shipping is initiated |
| `notification.queue` | `*.*` (Listen to relevant events) | General notification queue |
