CREATE SCHEMA IF NOT EXISTS rgts;

CREATE TABLE rgts.gold_types
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL UNIQUE,
    carat       VARCHAR(3)     NOT NULL,
    weight      NUMERIC(10, 2) NULL,
    description TEXT           NOT NULL
);

COMMENT ON TABLE rgts.gold_types IS 'Table containing various gold forms';
COMMENT ON COLUMN rgts.gold_types.id IS 'Unique identifier for different gold types';
COMMENT ON COLUMN rgts.gold_types.name IS 'The name of the gold item';
COMMENT ON COLUMN rgts.gold_types.carat IS 'The purity of the item (e.g., 18k, 24k)';
COMMENT ON COLUMN rgts.gold_types.weight IS 'The weight of the gold item';
COMMENT ON COLUMN rgts.gold_types.description IS 'The description of the item';

INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('24 Carat', '24K', NULL, '24K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('23 Carat', '23K', NULL, '23K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('22 Carat', '22K', NULL, '22K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('21 Carat', '21K', NULL, '21K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('20 Carat', '20K', NULL, '20K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('19 Carat', '19K', NULL, '19K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('18 Carat', '18K', NULL, '18K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('17 Carat', '17K', NULL, '17K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('16 Carat', '16K', NULL, '16K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('15 Carat', '15K', NULL, '15K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('14 Carat', '14K', NULL, '14K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('13 Carat', '13K', NULL, '13K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('12 Carat', '12K', NULL, '12K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('11 Carat', '11K', NULL, '11K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('10 Carat', '10K', NULL, '10K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('9 Carat', '9K', NULL, '9K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('8 Carat', '8K', NULL, '8K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('7 Carat', '7K', NULL, '7K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('6 Carat', '6K', NULL, '6K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('5 Carat', '5K', NULL, '5K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('4 Carat', '4K', NULL, '4K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('3 Carat', '3K', NULL, '3K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('2 Carat', '2K', NULL, '2K Gold');
INSERT INTO rgts.gold_types(name, carat, weight, description) VALUES ('1 Carat', '1K', NULL, '1K Gold');
