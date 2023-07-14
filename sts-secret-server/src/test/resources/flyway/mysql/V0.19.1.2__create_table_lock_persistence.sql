CREATE TABLE sts.lock_persistence (
  id      INT NOT NULL AUTO_INCREMENT,
  name    VARCHAR(256),
  value   VARCHAR(36),
  expires TIMESTAMP,

  PRIMARY KEY (id)
);
