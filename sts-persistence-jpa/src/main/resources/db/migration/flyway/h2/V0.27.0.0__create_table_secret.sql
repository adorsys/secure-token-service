CREATE TABLE sts.secret
(
  id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  subject  VARCHAR(255)  NOT NULL,
  value    VARCHAR(2047) NOT NULL,

  CONSTRAINT key_store__name__unique UNIQUE (audience)
);
