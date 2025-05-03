CREATE TABLE rgts.gold_prices
(
    price_id     SERIAL PRIMARY KEY,
    gold_type_id INT REFERENCES rgts.gold_types (id),
    price        NUMERIC(10, 2) NOT NULL,
    updated_at   TIMESTAMP      NOT NULL
);

COMMENT ON COLUMN rgts.gold_prices.price_id IS 'Unique identifier for each price entry';
COMMENT ON COLUMN rgts.gold_prices.gold_type_id IS 'Foreign key referencing the gold type';
COMMENT ON COLUMN rgts.gold_prices.price IS 'Price of the gold in GBP';
COMMENT ON COLUMN rgts.gold_prices.updated_at IS 'Timestamp when the price was last updated';;