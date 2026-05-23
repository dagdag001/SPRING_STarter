-- Inventory Service Database Schema
-- Requirements: 11.4

-- Products table
CREATE TABLE IF NOT EXISTS products (
    product_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    stock_quantity INT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Stock Reservations table
CREATE TABLE IF NOT EXISTS stock_reservations (
    reservation_id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL,
    reserved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_reservations_order ON stock_reservations(order_id);
CREATE INDEX IF NOT EXISTS idx_reservations_product ON stock_reservations(product_id);
