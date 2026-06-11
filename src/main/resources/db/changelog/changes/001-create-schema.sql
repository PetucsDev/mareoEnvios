--liquibase formatted sql

--changeset dev:001-create-customer
CREATE TABLE IF NOT EXISTS customer (
    id         SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    address    VARCHAR(255),
    city       VARCHAR(255)
);

--changeset dev:001-create-product
CREATE TABLE IF NOT EXISTS product (
    id          SERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    weight      DOUBLE PRECISION NOT NULL
);

--changeset dev:001-create-shipping
CREATE TABLE IF NOT EXISTS shipping (
    id          SERIAL PRIMARY KEY,
    customer_id INTEGER      NOT NULL REFERENCES customer(id),
    state       VARCHAR(50)  NOT NULL DEFAULT 'INITIAL',
    send_date   DATE,
    arrive_date DATE,
    priority    INTEGER      NOT NULL DEFAULT 0
);

--changeset dev:001-create-shipping-item
CREATE TABLE IF NOT EXISTS shipping_item (
    id             SERIAL PRIMARY KEY,
    shipping_id    INTEGER NOT NULL REFERENCES shipping(id),
    product_id     INTEGER NOT NULL REFERENCES product(id),
    product_count  INTEGER NOT NULL DEFAULT 1
);
