SET FOREIGN_KEY_CHECKS = 0;

-- =========================================================
-- USERS
-- =========================================================

SET @ddl = IF(
    (SELECT COUNT(*)
     FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'users'
       AND COLUMN_NAME = 'gender') = 0,
    'ALTER TABLE users ADD COLUMN gender VARCHAR(10) NULL AFTER phone',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    (SELECT COUNT(*)
     FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'users'
       AND COLUMN_NAME = 'dob') = 0,
    'ALTER TABLE users ADD COLUMN dob DATE NULL AFTER gender',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =========================================================
-- USER_CREDENTIALS
-- =========================================================

SET @ddl = IF(
    (SELECT COUNT(*)
     FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'user_credentials'
       AND COLUMN_NAME = 'created_at') = 0,
    'ALTER TABLE user_credentials ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    (SELECT COUNT(*)
     FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'user_credentials'
       AND COLUMN_NAME = 'updated_at') = 0,
    'ALTER TABLE user_credentials ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    (SELECT COUNT(*)
     FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'user_credentials'
       AND COLUMN_NAME = 'deleted_at') = 0,
    'ALTER TABLE user_credentials ADD COLUMN deleted_at TIMESTAMP NULL',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =========================================================
-- OAUTH_ACCOUNTS
-- =========================================================

SET @ddl = IF(
    (SELECT COUNT(*)
     FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'oauth_accounts'
       AND COLUMN_NAME = 'created_at') = 0,
    'ALTER TABLE oauth_accounts ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    (SELECT COUNT(*)
     FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'oauth_accounts'
       AND COLUMN_NAME = 'updated_at') = 0,
    'ALTER TABLE oauth_accounts ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    (SELECT COUNT(*)
     FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'oauth_accounts'
       AND COLUMN_NAME = 'deleted_at') = 0,
    'ALTER TABLE oauth_accounts ADD COLUMN deleted_at TIMESTAMP NULL',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =========================================================
-- SESSIONS
-- =========================================================

SET @ddl = IF(
    (SELECT COUNT(*)
     FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'sessions'
       AND COLUMN_NAME = 'updated_at') = 0,
    'ALTER TABLE sessions ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    (SELECT COUNT(*)
     FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'sessions'
       AND COLUMN_NAME = 'deleted_at') = 0,
    'ALTER TABLE sessions ADD COLUMN deleted_at TIMESTAMP NULL',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =========================================================
-- ROLES
-- =========================================================

CREATE TABLE IF NOT EXISTS roles
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_system   BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP   NULL
);

-- =========================================================
-- PERMISSIONS
-- =========================================================

CREATE TABLE IF NOT EXISTS permissions
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    resource    VARCHAR(50) NOT NULL,
    action      VARCHAR(30) NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP   NULL,
    CONSTRAINT uq_perm UNIQUE (resource, action)
);

-- =========================================================
-- ROLE PERMISSIONS
-- =========================================================

CREATE TABLE IF NOT EXISTS role_permissions
(
    role_id       INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role
        FOREIGN KEY (role_id)
            REFERENCES roles (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_rp_perm
        FOREIGN KEY (permission_id)
            REFERENCES permissions (id)
            ON DELETE CASCADE
);

-- =========================================================
-- USER ROLES
-- =========================================================

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id     BIGINT    NOT NULL,
    role_id     INT       NOT NULL,
    assigned_by BIGINT,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_ur_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_ur_role
        FOREIGN KEY (role_id)
            REFERENCES roles (id)
            ON DELETE CASCADE
);

-- =========================================================
-- INDEXES
-- =========================================================

CREATE INDEX idx_users_email
    ON users(email);

CREATE INDEX idx_cred_user
    ON user_credentials(user_id);

CREATE INDEX idx_oauth_user
    ON oauth_accounts(user_id);

CREATE INDEX idx_sessions_user
    ON sessions(user_id);

CREATE INDEX idx_sessions_expires
    ON sessions(expires_at);

-- =========================================================
-- SEED PERMISSIONS
-- =========================================================

INSERT INTO permissions (resource, action, description)
VALUES
    ('dashboard', 'read', 'View admin dashboard'),

    ('product', 'read', 'View products'),
    ('product', 'create', 'Create products'),
    ('product', 'update', 'Update products'),
    ('product', 'delete', 'Delete products'),

    ('category', 'read', 'View categories'),
    ('category', 'create', 'Create categories'),
    ('category', 'update', 'Update categories'),
    ('category', 'delete', 'Delete categories'),

    ('order', 'read', 'View orders'),
    ('order', 'update', 'Update orders'),

    ('customer', 'read', 'View customers'),
    ('customer', 'create', 'Create customers'),
    ('customer', 'update', 'Update customers'),
    ('customer', 'delete', 'Delete customers'),

    ('banner', 'read', 'View banners'),
    ('banner', 'create', 'Create banners'),
    ('banner', 'update', 'Update banners'),
    ('banner', 'delete', 'Delete banners'),

    ('settings', 'read', 'View settings'),
    ('settings', 'update', 'Update settings'),

    ('role', 'read', 'View permission groups'),
    ('role', 'create', 'Create permission groups'),
    ('role', 'update', 'Update permission groups'),
    ('role', 'delete', 'Delete permission groups')

ON DUPLICATE KEY UPDATE
    description = VALUES(description);

-- =========================================================
-- SEED ROLES
-- =========================================================

INSERT INTO roles (name, description, is_system)
VALUES
    ('Admin', 'Full access to the administration area', TRUE),
    ('Customer', 'Default customer account', TRUE)

ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    is_system = VALUES(is_system);

-- =========================================================
-- ADMIN GETS ALL PERMISSIONS
-- =========================================================

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'Admin';

-- =========================================================
-- ASSIGN CUSTOMER ROLE TO USERS WITHOUT ROLE
-- =========================================================

INSERT INTO user_roles (user_id, role_id, assigned_by)
SELECT
    u.id,
    r.id,
    NULL
FROM users u
JOIN roles r
    ON r.name = 'Customer'
LEFT JOIN user_roles ur
    ON ur.user_id = u.id
WHERE ur.user_id IS NULL;

-- =========================================================
-- OPTIONAL:
-- ASSIGN FIRST USER AS ADMIN
-- =========================================================

INSERT IGNORE INTO user_roles (user_id, role_id, assigned_by)
SELECT
    u.id,
    r.id,
    NULL
FROM users u
JOIN roles r
    ON r.name = 'Admin'
WHERE u.id = 1;

SET FOREIGN_KEY_CHECKS = 1;