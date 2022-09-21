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
    id            SERIAL  NOT NULL PRIMARY KEY,
    player        UUID    NOT NULL,
    rank          VARCHAR NOT NULL,
    added_at      BIGINT  NOT NULL,
    duration      BIGINT  NOT NULL,
    issuer        UUID    NOT NULL,
    reason        VARCHAR NOT NULL,
    removed       BOOLEAN NOT NULL,
    removed_at    BIGINT,
    remover       UUID,
    remove_reason VARCHAR
);

CREATE INDEX playerGrants ON grants USING btree (player);

-- Table to store player punishments
CREATE TABLE punishments
(
    id            SERIAL  NOT NULL PRIMARY KEY,
    player        UUID    NOT NULL,
    type          VARCHAR NOT NULL,
    added_at      BIGINT  NOT NULL,
    duration      BIGINT  NOT NULL,
    issuer        UUID    NOT NULL,
    reason        VARCHAR NOT NULL,
    removed       BOOLEAN NOT NULL,
    removed_at    BIGINT,
    remover       UUID,
    remove_reason VARCHAR
);

CREATE INDEX playerPunishments ON punishments USING btree (player);

-- Table to store pm settings
CREATE TABLE pm_settings
(
    id      SERIAL  NOT NULL PRIMARY KEY,
    player  UUID    NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE INDEX playerPmSettings ON pm_settings USING btree (player);

-- Table to store pm ignored players
CREATE TABLE pm_ignored
(
    id      SERIAL NOT NULL PRIMARY KEY,
    player  UUID   NOT NULL,
    ignored UUID   NOT NULL
);

CREATE INDEX playerPmIgnored ON pm_ignored USING btree (player);