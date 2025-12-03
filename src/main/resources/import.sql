------------------ Clients ------------------
INSERT INTO clients (email, name, last_name, phone_number, address, registration_date, last_activity_date, birth_date, last_renewal_date, legacy_registration_date) VALUES ('luisa.pinto@example.com', 'Luisa', 'Pinto', '910111222', 'Calle Luna 5, Valencia', NOW(), NOW(), '1995-02-28', '2024-01-10', NULL);
INSERT INTO clients (email, name, last_name, phone_number, address, registration_date, last_activity_date, birth_date, last_renewal_date, legacy_registration_date) VALUES ('marlene.nunez@example.com', 'Marlene', 'Núñez', '34600555666', 'Plaza del Sol 8, Sevilla', NOW(), NOW(), '1988-07-15', NULL, NULL);
INSERT INTO clients (email, name, last_name, phone_number, address, registration_date, last_activity_date, birth_date, last_renewal_date, legacy_registration_date) VALUES ('damilitza.gomez@example.com', 'Damilitza', 'Gómez', '+5491198765432', 'Avenida Corrientes 200, Buenos Aires', NOW(), NOW(), '1972-11-03', '2024-03-20', NULL);

------------------ Categories ------------------
-- Categoría raíz 1 (ID=1)
INSERT INTO categories (name, description, parent_id, created_at, updated_at) VALUES ('Electrónica', 'Dispositivos y accesorios electrónicos de última generación.', NULL, NOW(), NOW());

-- Categoría raíz 2 (ID=2)
INSERT INTO categories (name, description, parent_id, created_at, updated_at) VALUES ('Hogar y Cocina', 'Productos para el hogar, cocina y organización.', NULL, NOW(), NOW());

-- Subcategoría de Electrónica (ID=3)
INSERT INTO categories (name, description, parent_id, created_at, updated_at) VALUES ('Computación', 'Laptops, PCs de escritorio, componentes y periféricos.', 1, NOW(), NOW());

-- Subcategoría de Electrónica (ID=4)
INSERT INTO categories (name, description, parent_id, created_at, updated_at) VALUES ('Audio', 'Auriculares, parlantes y sistemas de sonido.', 1, NOW(), NOW());

-- Subcategoría de Hogar y Cocina (ID=5)
INSERT INTO categories (name, description, parent_id, created_at, updated_at) VALUES ('Electrodomésticos', 'Pequeños y grandes electrodomésticos para el hogar.', 2, NOW(), NOW());

-- Subcategoría de Hogar y Cocina (ID=6)
INSERT INTO categories (name, description, parent_id, created_at, updated_at) VALUES ('Utensilios de Cocina', 'Herramientas y utensilios para cocinar.', 2, NOW(), NOW());

------------------ Products, Commercials & Inventories ------------------

-- PRODUCTO 1: Laptop (Categoría: Computación, ID=3)
-- Se asume que este producto tendrá el ID=1
INSERT INTO products (sku, name, description, category_id, created_at, updated_at) VALUES ('LAP-DELL-XPS15', 'Laptop Dell XPS 15', 'Potente laptop para diseño y programación con pantalla 4K.', 3, NOW(), NOW());
INSERT INTO commercials (product_id, base_price, promotional_price, is_promotional, currency, tax_rate, created_at, updated_at) VALUES (1, 1899.99, 1750.00, TRUE, 'USD', 21.00, NOW(), NOW());
INSERT INTO inventories (product_id, current_stock, reserved_stock, warehouse_location, status, version, created_at, updated_at) VALUES (1, 50, 5, 'ALM-A1-SEC3', 'AVAILABLE', 0, NOW(), NOW());

-- PRODUCTO 2: Auriculares (Categoría: Audio, ID=4)
-- Se asume que este producto tendrá el ID=2
INSERT INTO products (sku, name, description, category_id, created_at, updated_at) VALUES ('AUD-SONY-XM5', 'Auriculares Sony WH-1000XM5', 'Auriculares con cancelación de ruido líder en el mercado.', 4, NOW(), NOW());
INSERT INTO commercials (product_id, base_price, is_promotional, currency, tax_rate, created_at, updated_at) VALUES (2, 399.50, FALSE, 'USD', 21.00, NOW(), NOW());
INSERT INTO inventories (product_id, current_stock, reserved_stock, warehouse_location, status, version, created_at, updated_at) VALUES (2, 200, 10, 'ALM-B2-SEC1', 'AVAILABLE', 0, NOW(), NOW());

-- PRODUCTO 3: Cafetera (Categoría: Electrodomésticos, ID=5)
-- Se asume que este producto tendrá el ID=3
INSERT INTO products (sku, name, description, category_id, created_at, updated_at) VALUES ('HOG-NES-VERTUO', 'Cafetera Nespresso Vertuo', 'Sistema de café en cápsulas con tecnología Centrifusion.', 5, NOW(), NOW());
INSERT INTO commercials (product_id, base_price, is_promotional, currency, tax_rate, created_at, updated_at) VALUES (3, 149.00, FALSE, 'EUR', 18.00, NOW(), NOW());
INSERT INTO inventories (product_id, current_stock, reserved_stock, warehouse_location, status, version, created_at, updated_at) VALUES (3, 0, 0, 'ALM-C1-SEC5', 'OUT_OF_STOCK', 0, NOW(), NOW());

-- Orden 1:
INSERT INTO orders (client_id, order_date, status, total_amount, tax, discount) VALUES (1, NOW(), 'PENDING', 3084.29, 535.29, 0.00);

-- Items para la ORDEN 1 (ID de la orden se asume como 1)
INSERT INTO order_items (order_id, product_id, quantity, price_per_unit, subtotal)VALUES (1, 1, 1, 1750.00, 1750.00);

INSERT INTO order_items (order_id, product_id, quantity, price_per_unit, subtotal) VALUES (1, 2, 2, 399.50, 799.00);