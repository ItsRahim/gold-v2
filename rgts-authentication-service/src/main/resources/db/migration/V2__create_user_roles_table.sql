CREATE TABLE IF NOT EXISTS user_roles
(
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users (id) ON DELETE CASCADE,
    role    VARCHAR(20) NOT NULL
);

CREATE INDEX idx_user_roles_user_id ON user_roles (user_id);