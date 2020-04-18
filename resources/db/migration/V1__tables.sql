CREATE SCHEMA IF NOT EXISTS example;

CREATE TABLE example.pizza (
  id     SERIAL PRIMARY KEY,
  name   TEXT    NOT NULL,
  size   TEXT    NOT NULL,
  price  INTEGER NOT NULL,
  origin JSONB
);
