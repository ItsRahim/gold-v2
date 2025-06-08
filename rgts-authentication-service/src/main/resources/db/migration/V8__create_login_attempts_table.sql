CREATE TABLE IF NOT EXISTS login_attempts
(
    id             UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    identifier     VARCHAR(100) NOT NULL,
    ip_address     INET         NOT NULL,
    success        BOOLEAN      NOT NULL,
    failure_reason VARCHAR(100),
    user_agent     TEXT,
    attempted_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_login_attempts_identifier ON login_attempts(identifier);
CREATE INDEX idx_login_attempts_ip_address ON login_attempts(ip_address);
CREATE INDEX idx_login_attempts_attempted_at ON login_attempts(attempted_at);