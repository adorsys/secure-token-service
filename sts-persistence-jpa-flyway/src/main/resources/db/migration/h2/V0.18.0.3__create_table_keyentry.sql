CREATE TABLE sts.key_entry
(
  id                BIGINT AUTO_INCREMENT PRIMARY KEY,
  key_store_id      INT          NOT NULL REFERENCES sts.key_store,

  alias             VARCHAR(255) NOT NULL,

  created_at        TIMESTAMP WITH TIME ZONE NOT NULL,
  not_before        TIMESTAMP WITH TIME ZONE NOT NULL,
  not_after         TIMESTAMP WITH TIME ZONE NULL,
  expire_at         TIMESTAMP WITH TIME ZONE NULL,

  validity_interval BIGINT       NOT NULL,
  legacy_interval   BIGINT       NOT NULL,

  state             VARCHAR(255) NOT NULL,
  key_usage         VARCHAR(255) NOT NULL,

  CONSTRAINT key_entry__alias__unique UNIQUE (alias)
);
