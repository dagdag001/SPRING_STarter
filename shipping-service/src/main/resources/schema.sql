-- Shipping Service Database Schema
-- Requirements: 1.3, 11.5, 13.4, 13.5

-- Shipments table
CREATE TABLE IF NOT EXISTS shipments (
    shipment_id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL UNIQUE,
    estimated_delivery_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_shipments_order ON shipments(order_id);

-- Order confirmations table for event correlation
CREATE TABLE IF NOT EXISTS order_confirmations (
    order_id VARCHAR(36) PRIMARY KEY,
    payment_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    stock_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_confirmations_status ON order_confirmations(payment_confirmed, stock_confirmed);
