CREATE TABLE refresh_tokens
(
    id          SERIAL PRIMARY KEY,
    token       TEXT UNIQUE           NOT NULL,
    user_id     BIGINT                NOT NULL,
    expires_at  TIMESTAMP             NOT NULL,
    created_at  TIMESTAMP             NOT NULL,
    revoked     BOOLEAN DEFAULT FALSE NOT NULL,
    revoked_at  TIMESTAMP             NULL,
    device_info VARCHAR(255)          NULL,
    ip_address  INET                  NULL,

    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_token_token ON refresh_tokens (token);
CREATE INDEX idx_refresh_token_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_token_expires_at ON refresh_tokens (expires_at);
CREATE INDEX idx_refresh_token_revoked ON refresh_tokens (revoked);
CREATE INDEX idx_refresh_token_user_revoked ON refresh_tokens (user_id, revoked);
CREATE INDEX idx_refresh_token_user_active ON refresh_tokens (user_id, revoked, expires_at);
CREATE INDEX idx_refresh_token_active_only ON refresh_tokens (user_id, expires_at) WHERE revoked = FALSE;