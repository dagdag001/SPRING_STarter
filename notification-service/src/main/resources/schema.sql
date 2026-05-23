-- Notification Service Database Schema
-- Requirements: 1.3, 11.6, 13.4, 13.5

-- Notification logs table
CREATE TABLE IF NOT EXISTS notification_logs (
    log_id VARCHAR(36) PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    event_data TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_logs_event_type ON notification_logs(event_type);
CREATE INDEX IF NOT EXISTS idx_logs_timestamp ON notification_logs(timestamp);
