CREATE DATABASE IF NOT EXISTS zenon;
USE zenon;

CREATE TABLE IF NOT EXISTS TRANSACTIONS (
    id BIGINT NOT NULL AUTO_INCREMENT,
    step INT NOT NULL,
    type VARCHAR(16) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    origin_name VARCHAR(32) NOT NULL,
    origin_old_balance DECIMAL(15, 2) NOT NULL,
    origin_new_balance DECIMAL(15, 2) NOT NULL,
    recipient_name VARCHAR(32) NOT NULL,
    recipient_old_balance DECIMAL(15, 2) NOT NULL,
    recipient_new_balance DECIMAL(15, 2) NOT NULL,
    is_fraud BOOLEAN NOT NULL,
    is_flagged_fraud BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    KEY idx_transactions_origin_name (origin_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
