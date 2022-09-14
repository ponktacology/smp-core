-- INITIAL DATABASE CREATION

-- Create db version table and set current
CREATE TABLE db_version(
    version INT NOT NULL
);

INSERT INTO db_version(version) VALUES(1);