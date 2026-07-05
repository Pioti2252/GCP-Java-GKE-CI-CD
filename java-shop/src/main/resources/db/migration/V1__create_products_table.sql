CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price NUMERIC(19, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL
);