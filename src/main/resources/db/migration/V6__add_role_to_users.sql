ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER';

UPDATE users SET role = 'ROLE_ADMIN' WHERE username = 'admin';

CREATE INDEX idx_users_role ON users(role);
