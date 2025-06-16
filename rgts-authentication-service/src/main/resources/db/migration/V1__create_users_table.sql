CREATE SCHEMA IF NOT EXISTS "authentication-service";

CREATE TABLE IF NOT EXISTS users
(
    id                 UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    username           VARCHAR(50)  NOT NULL UNIQUE,
    email              VARCHAR(100) NOT NULL UNIQUE,
    password           TEXT         NOT NULL,
    first_name         VARCHAR(50)  NOT NULL,
    last_name          VARCHAR(50)  NOT NULL,
    phone_number       VARCHAR(20),
    enabled            BOOLEAN      NOT NULL DEFAULT FALSE,
    email_verified     BOOLEAN      NOT NULL DEFAULT FALSE,
    phone_verified     BOOLEAN      NOT NULL DEFAULT FALSE,
    locale             VARCHAR(10),
    timezone           VARCHAR(50),
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    last_login         TIMESTAMPTZ,
    account_expired    BOOLEAN      NOT NULL DEFAULT FALSE,
    account_locked     BOOLEAN      NOT NULL DEFAULT FALSE,
    password_change_at TIMESTAMPTZ
);

CREATE INDEX idx_users_username_password ON users (username, password);
CREATE INDEX idx_users_email_password ON users (email, password);