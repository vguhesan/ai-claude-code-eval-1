-- Test schema
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