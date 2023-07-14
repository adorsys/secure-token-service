CREATE TABLE sts.key_entry
(
  id                SERIAL PRIMARY KEY,
  key_store_id      INT          NOT NULL REFERENCES sts.key_store,

  alias             VARCHAR(255) NOT NULL,

  created_at        TIMESTAMP    NOT NULL,
  not_before        TIMESTAMP    NOT NULL,
  not_after         TIMESTAMP    NULL,
  expire_at         TIMESTAMP    NULL,

  validity_interval BIGINT       NOT NULL,
  legacy_interval   BIGINT       NOT NULL,

  state             VARCHAR(255) NOT NULL,
  key_usage         VARCHAR(255) NOT NULL,

  CONSTRAINT key_entry__alias__unique UNIQUE (alias)
);
