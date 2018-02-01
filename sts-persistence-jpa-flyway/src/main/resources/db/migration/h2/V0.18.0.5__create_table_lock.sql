CREATE TABLE lock_persistence.lock (
  id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  name    VARCHAR(256),
  value   VARCHAR(36),
  expires TIMESTAMP
);
