CREATE TABLE sts.secret
(
  id       INT           NOT NULL AUTO_INCREMENT,
  subject  VARCHAR(255)  NOT NULL,
  value    VARCHAR(2047) NOT NULL,

  PRIMARY KEY (id),
  CONSTRAINT key_store__name__unique UNIQUE (audience)
);
