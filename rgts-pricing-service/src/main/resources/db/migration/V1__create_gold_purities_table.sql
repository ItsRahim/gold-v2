CREATE SCHEMA IF NOT EXISTS rgts;

CREATE TABLE rgts.gold_purities
(
    id          SERIAL PRIMARY KEY,
    label       VARCHAR(10) NOT NULL UNIQUE,
    numerator   INTEGER     NOT NULL,
    denominator INTEGER     NOT NULL,
    is_base     BOOLEAN DEFAULT FALSE
);

COMMENT ON TABLE rgts.gold_purities IS 'Reference table for gold purity levels and XAUGBP';
COMMENT ON COLUMN rgts.gold_purities.id IS 'Unique identifier for each entry in the table.';
COMMENT ON COLUMN rgts.gold_purities.label IS 'Label representing the gold purity level (e.g., "23K", "24K").';
COMMENT ON COLUMN rgts.gold_purities.numerator IS 'Numerator of the fraction representing purity.';
COMMENT ON COLUMN rgts.gold_purities.denominator IS 'Denominator of the fraction representing purity (always 24).';
COMMENT ON COLUMN rgts.gold_purities.is_base IS 'Indicates whether this gold purity level is the base value (TRUE or FALSE).';