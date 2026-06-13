--liquibase formatted sql

--changeset dev:001-create-indexes
CREATE INDEX IF NOT EXISTS idx_shipping_customer_id ON shipping(customer_id);
CREATE INDEX IF NOT EXISTS idx_shipping_state ON shipping(state);
CREATE INDEX IF NOT EXISTS idx_shipping_send_date ON shipping(send_date);
CREATE INDEX IF NOT EXISTS idx_shipping_item_shipping_id ON shipping_item(shipping_id);
CREATE INDEX IF NOT EXISTS idx_shipping_item_product_id ON shipping_item(product_id);
CREATE INDEX IF NOT EXISTS idx_shipping_composite ON shipping(state, send_date);