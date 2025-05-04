CREATE TABLE rgts.gold_prices
(
    price_id   SERIAL PRIMARY KEY,
    purity_id   INT                       NOT NULL REFERENCES rgts.gold_purities (id),
    price      NUMERIC(10, 2)            NOT NULL,
    updated_at TIMESTAMP DEFAULT NOW() NOT NULL
);


COMMENT ON TABLE rgts.gold_prices IS 'Historical prices for each gold carat type, including XAUGBP';
COMMENT ON COLUMN rgts.gold_prices.price_id IS 'Unique identifier for each price record';
COMMENT ON COLUMN rgts.gold_prices.purity_id IS 'Foreign key referencing gold_purities.id to indicate which carat or XAUGBP this price relates to';
COMMENT ON COLUMN rgts.gold_prices.price IS 'Gold price in GBP for the associated carat or XAUGBP at the given timestamp';
COMMENT ON COLUMN rgts.gold_prices.updated_at IS 'Timestamp (in UTC) when the price was recorded';