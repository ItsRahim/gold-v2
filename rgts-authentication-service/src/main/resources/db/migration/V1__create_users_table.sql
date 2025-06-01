CREATE SCHEMA IF NOT EXISTS rgts;

CREATE TABLE IF NOT EXISTS rgts.users
(
    id                  SERIAL PRIMARY KEY,
    username            VARCHAR(50)  NOT NULL UNIQUE,
    email               VARCHAR(100) NOT NULL UNIQUE,
    password            TEXT         NOT NULL,
    enabled             BOOLEAN      NOT NULL DEFAULT TRUE,
    verification_code   VARCHAR(10),
    verification_expiry TIMESTAMP
);

COMMENT ON TABLE rgts.users IS 'Stores user authentication and account information.';
COMMENT ON COLUMN rgts.users.id IS 'Unique identifier for the user (auto-incremented).';
COMMENT ON COLUMN rgts.users.username IS 'Username selected by the user; must be unique.';
COMMENT ON COLUMN rgts.users.email IS 'User email address; must be unique.';
COMMENT ON COLUMN rgts.users.password IS 'Hashed password for user authentication.';
COMMENT ON COLUMN rgts.users.enabled IS 'Indicates whether the user account is active.';
COMMENT ON COLUMN rgts.users.verification_code IS 'Code sent to user for email or account verification.';
COMMENT ON COLUMN rgts.users.verification_expiry IS 'Timestamp when the verification code expires.';
