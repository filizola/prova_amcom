CREATE TABLE orders (
    id              UUID PRIMARY KEY,
    external_id     VARCHAR(100) NOT NULL,
    status          VARCHAR(30)  NOT NULL,
    total_amount    NUMERIC(19, 4) NOT NULL DEFAULT 0,
    currency        VARCHAR(3)   NOT NULL DEFAULT 'BRL',
    customer_name   VARCHAR(255),
    version         BIGINT       NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    processed_at    TIMESTAMPTZ,
    CONSTRAINT uk_orders_external_id UNIQUE (external_id)
);

CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_created_at ON orders (created_at DESC);
CREATE INDEX idx_orders_status_created_at ON orders (status, created_at DESC);

CREATE TABLE order_items (
    id              UUID PRIMARY KEY,
    order_id        UUID NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_code    VARCHAR(100) NOT NULL,
    product_name    VARCHAR(255) NOT NULL,
    quantity        INTEGER NOT NULL CHECK (quantity > 0),
    unit_price      NUMERIC(19, 4) NOT NULL CHECK (unit_price >= 0),
    line_total      NUMERIC(19, 4) NOT NULL CHECK (line_total >= 0),
    CONSTRAINT uk_order_items_order_product UNIQUE (order_id, product_code)
);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);
