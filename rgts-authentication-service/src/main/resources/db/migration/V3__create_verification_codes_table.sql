CREATE TABLE IF NOT EXISTS verification_codes
(
    id         UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    user_id    UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    code       TEXT        NOT NULL,
    type       VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL,
    attempts   INTEGER              DEFAULT 0
);

CREATE INDEX idx_verification_codes_user_type ON verification_codes (user_id, type);
CREATE INDEX idx_verification_codes_code ON verification_codes (code);
CREATE INDEX idx_verification_codes_expires_at ON verification_codes (expires_at);