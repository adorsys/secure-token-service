CREATE TABLE lock_persistence.lock (
  id      SERIAL PRIMARY KEY,
  name    VARCHAR(256),
  value   VARCHAR(36),
  expires TIMESTAMP
);
