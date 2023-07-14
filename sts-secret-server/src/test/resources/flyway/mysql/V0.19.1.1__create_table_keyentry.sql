CREATE TABLE sts.key_entry
(
  id                INT          NOT NULL AUTO_INCREMENT,
  key_store_id      INT          NOT NULL,

  alias             VARCHAR(255) NOT NULL,

  created_at        TIMESTAMP    NOT NULL,
  not_before        TIMESTAMP    NOT NULL,
  not_after         TIMESTAMP    NULL,
  expire_at         TIMESTAMP    NULL,

  validity_interval BIGINT       NOT NULL,
  legacy_interval   BIGINT       NOT NULL,

  state             VARCHAR(255) NOT NULL,
  key_usage         VARCHAR(255) NOT NULL,

  PRIMARY KEY (id),
  FOREIGN KEY (key_store_id) REFERENCES sts.key_store (id) ON DELETE CASCADE,
  CONSTRAINT key_entry__alias__unique UNIQUE (alias)
);
