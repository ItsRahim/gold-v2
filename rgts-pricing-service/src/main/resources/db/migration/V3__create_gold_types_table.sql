CREATE TABLE gold_types
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255)   NOT NULL UNIQUE,
    purity_id   INT            NOT NULL REFERENCES gold_purities (id),
    weight      NUMERIC(10, 2) NULL,
    unit        VARCHAR(10)    NOT NULL,
    description TEXT           NOT NULL,
    price       NUMERIC(10, 2) NOT NULL,
    image_url   VARCHAR(512)   NULL
);

COMMENT ON TABLE gold_types IS 'Table containing various gold items with associated carat, weight, unit, description, and current price';
COMMENT ON COLUMN gold_types.id IS 'Unique identifier for each gold type entry';
COMMENT ON COLUMN gold_types.name IS 'The display name of the gold item (e.g., Sovereign Coin, Necklace)';
COMMENT ON COLUMN gold_types.purity_id IS 'Foreign key referencing gold_purities.id indicating purity or XAUGBP';
COMMENT ON COLUMN gold_types.weight IS 'The weight of the gold item, typically in grams';
COMMENT ON COLUMN gold_types.unit IS 'The unit of measurement for the weight (e.g., g, oz)';
COMMENT ON COLUMN gold_types.description IS 'Description or details about the gold item';
COMMENT ON COLUMN gold_types.price IS 'The calculated price in GBP based on carat purity and weight';
COMMENT ON COLUMN gold_types.image_url IS 'URL or path to the image representing the gold type';