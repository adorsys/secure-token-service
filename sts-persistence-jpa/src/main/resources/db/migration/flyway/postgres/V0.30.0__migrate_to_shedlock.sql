CREATE TABLE sts.sts_lock
(
    name       VARCHAR(64),
    lock_until TIMESTAMP(3) NULL,
    locked_at  TIMESTAMP(3) NULL,
    locked_by  VARCHAR(255),
    PRIMARY KEY (name)
);

INSERT INTO sts.sts_lock
(name, lock_until, locked_at, locked_by)
    (SELECT name, exp, NULL, 'lock_persistence' FROM (SELECT name, MAX(expires) AS exp FROM sts.lock_persistence GROUP BY name));

DROP TABLE sts.lock_persistence;
