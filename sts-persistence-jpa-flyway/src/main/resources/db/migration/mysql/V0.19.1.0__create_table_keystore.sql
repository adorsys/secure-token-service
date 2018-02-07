CREATE TABLE sts.key_store
(
  id       INT          NOT NULL AUTO_INCREMENT,
  name     VARCHAR(255) NOT NULL,
  type     VARCHAR(255) NOT NULL,
  keystore LONGBLOB,

  PRIMARY KEY (id),
  CONSTRAINT key_store__name__unique UNIQUE (name)
);
