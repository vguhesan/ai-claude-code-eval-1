-- Initialize H2 in-memory database for products and widgets
-- Run this script to create the necessary tables and load sample data

-- Create Products table
CREATE TABLE IF NOT EXISTS products (
    product_id INT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    size VARCHAR(50) NOT NULL,
    upc_code VARCHAR(12) NOT NULL,
    amount INT NOT NULL,
    category VARCHAR(100) NOT NULL,
    color VARCHAR(50) NOT NULL,
    weight DECIMAL(10, 2) NOT NULL,
    in_stock BOOLEAN NOT NULL,
    CONSTRAINT upc_unique UNIQUE (upc_code)
);

-- Create Widgets table
CREATE TABLE IF NOT EXISTS widgets (
    widget_id VARCHAR(10) PRIMARY KEY,
    widget_name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    size VARCHAR(50) NOT NULL,
    upc_code VARCHAR(12) NOT NULL,
    amount INT NOT NULL,
    category VARCHAR(100) NOT NULL,
    material VARCHAR(100) NOT NULL,
    weight DECIMAL(10, 2) NOT NULL,
    is_available BOOLEAN NOT NULL,
    CONSTRAINT widget_upc_unique UNIQUE (upc_code)
);

-- Create indexes for better performance
CREATE INDEX idx_product_category ON products(category);
CREATE INDEX idx_product_price ON products(price);
CREATE INDEX idx_product_in_stock ON products(in_stock);

CREATE INDEX idx_widget_category ON widgets(category);
CREATE INDEX idx_widget_price ON widgets(price);
CREATE INDEX idx_widget_is_available ON widgets(is_available);

-- Optional: Create a view that combines both products and widgets for inventory management
CREATE VIEW inventory_view AS
SELECT 
    'Product' as item_type,
    CAST(product_id AS VARCHAR) as item_id,
    product_name as item_name,
    price,
    category,
    amount,
    in_stock as available
FROM 
    products
UNION ALL
SELECT 
    'Widget' as item_type,
    widget_id as item_id,
    widget_name as item_name,
    price,
    category,
    amount,
    is_available as available
FROM 
    widgets;

-- Insert sample product data
INSERT INTO products (product_id, product_name, price, size, upc_code, amount, category, color, weight, in_stock)
VALUES 
    (1001, 'Wireless Headphones', 129.99, 'Medium', '845973410258', 45, 'Electronics', 'Black', 0.35, TRUE),
    (1002, 'Cotton T-Shirt', 19.95, 'Large', '724619853047', 120, 'Clothing', 'White', 0.20, TRUE),
    (1003, 'Stainless Steel Water Bottle', 24.50, '500ml', '936284751093', 78, 'Kitchen', 'Silver', 0.50, TRUE),
    (1004, 'Yoga Mat', 35.99, 'Standard', '618493027584', 32, 'Fitness', 'Purple', 1.20, TRUE),
    (1005, 'LED Desk Lamp', 42.75, 'Small', '570382941625', 56, 'Home', 'White', 0.80, TRUE),
    (1006, 'Bluetooth Speaker', 89.99, 'Compact', '391746582039', 23, 'Electronics', 'Blue', 0.60, FALSE),
    (1007, 'Leather Wallet', 49.95, 'Standard', '482057391648', 67, 'Accessories', 'Brown', 0.15, TRUE),
    (1008, 'Ceramic Coffee Mug', 12.99, '12oz', '759284613095', 94, 'Kitchen', 'Red', 0.40, TRUE),
    (1009, 'Running Shoes', 79.50, '9.5', '628491375026', 18, 'Footwear', 'Black', 0.70, FALSE),
    (1010, 'Portable Power Bank', 34.99, '10000mAh', '194736582047', 51, 'Electronics', 'Gray', 0.30, TRUE);

-- Insert sample widget data
INSERT INTO widgets (widget_id, widget_name, price, size, upc_code, amount, category, material, weight, is_available)
VALUES 
    ('W1001', 'Rotary Widget', 45.99, 'Medium', '845973410123', 32, 'Industrial', 'Steel', 0.75, TRUE),
    ('W1002', 'Linear Widget', 29.95, 'Small', '724619853012', 87, 'Consumer', 'Plastic', 0.25, TRUE),
    ('W1003', 'Precision Widget', 89.50, 'Micro', '936284751034', 14, 'Medical', 'Titanium', 0.12, TRUE),
    ('W1004', 'Heavy Duty Widget', 124.99, 'Large', '618493027512', 8, 'Construction', 'Alloy', 2.50, TRUE),
    ('W1005', 'Compact Widget', 19.75, 'Mini', '570382941645', 65, 'Electronics', 'Composite', 0.18, TRUE),
    ('W1006', 'Flexible Widget', 37.50, 'Standard', '391746582067', 41, 'Automotive', 'Rubber', 0.45, FALSE),
    ('W1007', 'Smart Widget', 149.95, 'Medium', '482057391623', 23, 'IoT', 'Aluminum', 0.35, TRUE),
    ('W1008', 'Basic Widget', 12.99, 'Small', '759284613078', 112, 'Household', 'Plastic', 0.22, TRUE),
    ('W1009', 'Premium Widget', 199.50, 'Large', '628491375091', 7, 'Professional', 'Carbon Fiber', 0.55, FALSE),
    ('W1010', 'Eco Widget', 34.99, 'Medium', '194736582015', 53, 'Sustainable', 'Bamboo', 0.40, TRUE);

-- Sample queries to verify data
-- SELECT * FROM products;
-- SELECT * FROM widgets;
-- SELECT * FROM inventory_view ORDER BY price DESC;
