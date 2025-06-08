CREATE TABLE IF NOT EXISTS user_2fa
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    user_id      UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    secret       VARCHAR(255) NOT NULL,
    enabled      BOOLEAN               NOT NULL DEFAULT FALSE,
    backup_codes TEXT[],
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    enabled_at   TIMESTAMPTZ
);

CREATE UNIQUE INDEX idx_user_2fa_user_id ON user_2fa(user_id);