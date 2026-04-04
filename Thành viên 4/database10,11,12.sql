-- Run this on an existing stock_manager database if you already created it before UC10-12.
USE stock_manager;

CREATE TABLE IF NOT EXISTS customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    customer_type ENUM('RETAIL', 'DEALER') NOT NULL DEFAULT 'RETAIL',
    phone VARCHAR(100),
    address TEXT
);

CREATE TABLE IF NOT EXISTS export_forms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(15, 2) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
