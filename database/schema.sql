CREATE DATABASE IF NOT EXISTS stock_manager;
USE stock_manager;

CREATE TABLE IF NOT EXISTS categories (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS products (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category_id VARCHAR(10),
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS suppliers (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_info VARCHAR(255),
    address TEXT
);

CREATE TABLE IF NOT EXISTS supply_forms (
    id VARCHAR(10) PRIMARY KEY,
    supplier_name VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(15, 2) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(10) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'EMPLOYEE') NOT NULL DEFAULT 'EMPLOYEE'
);

CREATE TABLE IF NOT EXISTS customers (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    customer_type ENUM('RETAIL', 'DEALER') NOT NULL DEFAULT 'RETAIL',
    phone VARCHAR(100),
    address TEXT
);

CREATE TABLE IF NOT EXISTS export_forms (
    id VARCHAR(10) PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(15, 2) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert a default admin user if no users exist
INSERT IGNORE INTO users (id, username, password, role) VALUES ('U01', 'admin', 'admin123', 'ADMIN');
