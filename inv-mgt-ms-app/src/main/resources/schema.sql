-- Initialize H2 in-memory database for products and widgets

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