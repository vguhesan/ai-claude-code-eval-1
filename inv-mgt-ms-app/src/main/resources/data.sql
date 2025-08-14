-- Insert sample product data
INSERT INTO products (product_id, product_name, price, size, upc_code, amount, category, color, weight, in_stock)
VALUES 
    (1001, 'Wireless Headphones', 129.99, 'Medium', '845973410258', 45, 'Electronics', 'Black', 0.35, TRUE),
    (1002, 'Cotton T-Shirt', 19.95, 'Large', '724619853047', 120, 'Clothing', 'White', 0.20, TRUE),
    (1003, 'Stainless Steel Water Bottle', 24.50, '500ml', '936284751093', 78, 'Kitchen', 'Silver', 0.50, TRUE),
    (1004, 'Yoga Mat', 35.99, 'Standard', '618493027584', 32, 'Fitness', 'Purple', 1.20, TRUE),
    (1005, 'LED Desk Lamp', 42.75, 'Small', '570382941625', 56, 'Home', 'White', 0.80, TRUE);

-- Insert sample widget data
INSERT INTO widgets (widget_id, widget_name, price, size, upc_code, amount, category, material, weight, is_available)
VALUES 
    ('W1001', 'Rotary Widget', 45.99, 'Medium', '845973410123', 32, 'Industrial', 'Steel', 0.75, TRUE),
    ('W1002', 'Linear Widget', 29.95, 'Small', '724619853012', 87, 'Consumer', 'Plastic', 0.25, TRUE),
    ('W1003', 'Precision Widget', 89.50, 'Micro', '936284751034', 14, 'Medical', 'Titanium', 0.12, TRUE),
    ('W1004', 'Heavy Duty Widget', 124.99, 'Large', '618493027512', 8, 'Construction', 'Alloy', 2.50, TRUE),
    ('W1005', 'Compact Widget', 19.75, 'Mini', '570382941645', 65, 'Electronics', 'Composite', 0.18, TRUE);