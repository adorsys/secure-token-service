CREATE TABLE sts.secret
(
  id       INT           NOT NULL AUTO_INCREMENT,
  subject  VARCHAR(255)  NOT NULL,
  value    VARCHAR(2047) NOT NULL,

  PRIMARY KEY (id),
  CONSTRAINT secret__name__unique UNIQUE (subject)
);
