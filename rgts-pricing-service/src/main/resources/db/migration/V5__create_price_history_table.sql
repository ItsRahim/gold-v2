CREATE TABLE price_history
(
    id           SERIAL PRIMARY KEY,
    gold_type_id INT       NOT NULL REFERENCES gold_types (id),
    price        NUMERIC(10, 2),
    updated_at   TIMESTAMP NOT NULL
);

COMMENT ON TABLE price_history IS 'Stores hourly historical price records for each gold type.';
COMMENT ON COLUMN price_history.id IS 'Primary key for the price history record.';
COMMENT ON COLUMN price_history.gold_type_id IS 'Foreign key reference to gold_types.id to associate a price record with a gold type.';
COMMENT ON COLUMN price_history.price IS 'Price of the gold item at the recorded timestamp.';
COMMENT ON COLUMN price_history.updated_at IS 'Timestamp representing when this price was recorded (usually hourly).';
