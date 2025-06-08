CREATE TABLE IF NOT EXISTS audit_log
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    user_id    UUID         REFERENCES users (id) ON DELETE SET NULL,
    action     VARCHAR(100) NOT NULL,
    resource   VARCHAR(100),
    ip_address INET,
    user_agent TEXT,
    success    BOOLEAN      NOT NULL,
    details    JSONB,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_log_user_id ON audit_log(user_id);
CREATE INDEX idx_audit_log_created_at ON audit_log(created_at);