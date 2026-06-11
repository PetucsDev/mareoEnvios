--liquibase formatted sql

--changeset dev:002-insert-customers
INSERT INTO customer (first_name, last_name, address, city) VALUES
('Marcos',  'Gutierrez',  'la buena direccion 123', 'CABA'),
('Hernan',  'Toledo',     'falsa 456',              'CABA'),
('Silvina', 'Hernandez',  'Marcos Paz 997',         'Buenos Aires');

--changeset dev:002-insert-products
INSERT INTO product (description, weight) VALUES
('Maiz Pisingallo', 1.1),
('Tornillos',       2.2),
('Caja',            1.1),
('Modem',           0.9),
('Celular',         0.3),
('Tablet',          0.5),
('Petroleo',        1.5),
('Tizas',           0.6),
('Marcadores',      0.8),
('Mesada',          8.4),
('Marmol',         13.5),
('Puerta',          3.5),
('Cortina',         1.3),
('Televisor',       8.0),
('Colchon',        10.5);

--changeset dev:002-insert-shippings
INSERT INTO shipping (customer_id, state, send_date, arrive_date, priority) VALUES
(1, 'DELIVERED', CURRENT_DATE - 5, CURRENT_DATE, 0),
(2, 'IN_TRAVEL', CURRENT_DATE - 5, NULL,         2),
(1, 'INITIAL',   CURRENT_DATE,     NULL,         2),
(3, 'CANCELLED', CURRENT_DATE - 4, NULL,         2);

--changeset dev:002-insert-shipping-items
INSERT INTO shipping_item (shipping_id, product_id, product_count) VALUES
(1, 1, 5),
(1, 2, 2),
(1, 3, 1),
(2, 4, 6),
(2, 5, 2),
(2, 6, 2),
(3, 4, 1),
(3, 5, 2),
(4, 3, 1);
