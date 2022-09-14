-- INITIAL DATABASE CREATION

-- Create db version table and set current
CREATE TABLE db_version
(
    version INT NOT NULL
);

INSERT INTO db_version(version)
VALUES (1);

-- Table to store player ranks
CREATE TABLE grants
(
    id            SERIAL       NOT NULL PRIMARY KEY,
    player        UUID         NOT NULL,
    rank          VARCHAR(16)  NOT NULL,
    added_at      BIGINT       NOT NULL,
    duration      BIGINT       NOT NULL,
    issuer        UUID         NOT NULL,
    reason        VARCHAR(256) NOT NULL,
    removed       BOOLEAN      NOT NULL,
    removed_at    BIGINT,
    remover       UUID,
    remove_reason VARCHAR(256)
);

-- Table to store player ranks
CREATE TABLE punishments
(
    id            SERIAL       NOT NULL PRIMARY KEY,
    player        UUID         NOT NULL,
    type          VARCHAR(16)  NOT NULL,
    added_at      BIGINT       NOT NULL,
    duration      BIGINT       NOT NULL,
    issuer        UUID         NOT NULL,
    reason        VARCHAR(256) NOT NULL,
    removed       BOOLEAN      NOT NULL,
    removed_at    BIGINT,
    remover       UUID,
    remove_reason VARCHAR(256)
);