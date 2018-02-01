CREATE TABLE sts.key_store
(
  id       SERIAL PRIMARY KEY,
  name     VARCHAR(255) NOT NULL,
  type     VARCHAR(255) NOT NULL,
  keystore BYTEA,

  CONSTRAINT key_store__name__unique UNIQUE (name)
);
