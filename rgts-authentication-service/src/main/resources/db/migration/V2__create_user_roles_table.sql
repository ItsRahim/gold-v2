CREATE TABLE IF NOT EXISTS user_roles
(
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role)
)