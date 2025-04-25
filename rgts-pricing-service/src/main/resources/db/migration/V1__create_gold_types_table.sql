CREATE SCHEMA IF NOT EXISTS rgts;

CREATE TABLE rgts.gold_types
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL UNIQUE,
    carat       VARCHAR(3)     NOT NULL,
    weight      NUMERIC(10, 2) NULL,
    unit        VARCHAR(10)    NOT NULL,
    description TEXT           NOT NULL
);

COMMENT ON TABLE rgts.gold_types IS 'Table containing various gold forms';
COMMENT ON COLUMN rgts.gold_types.id IS 'Unique identifier for different gold types';
COMMENT ON COLUMN rgts.gold_types.name IS 'The name of the gold item';
COMMENT ON COLUMN rgts.gold_types.carat IS 'The purity of the item (e.g., 18k, 24k)';
COMMENT ON COLUMN rgts.gold_types.weight IS 'The weight of the gold item';
COMMENT ON COLUMN rgts.gold_types.unit IS 'The unit of measure of the gold item';
COMMENT ON COLUMN rgts.gold_types.description IS 'The description of the item';
