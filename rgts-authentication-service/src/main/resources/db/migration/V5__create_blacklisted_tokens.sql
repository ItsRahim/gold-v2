CREATE TABLE IF NOT EXISTS blacklisted_tokens
(
    id             UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    token_hash     VARCHAR(255) NOT NULL UNIQUE,
    user_id        UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    expires_at     TIMESTAMPTZ  NOT NULL,
    blacklisted_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    reason         VARCHAR(50)
);

CREATE INDEX idx_blacklisted_tokens_user_id ON blacklisted_tokens(user_id);
CREATE INDEX idx_blacklisted_tokens_expires_at ON blacklisted_tokens(expires_at);