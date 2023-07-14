CREATE TABLE sts.lock_persistence (
  id      SERIAL PRIMARY KEY,
  name    VARCHAR(256),
  value   VARCHAR(36),
  expires TIMESTAMP
);
