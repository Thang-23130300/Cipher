SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS roles
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_system   BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS permissions
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    resource    VARCHAR(50) NOT NULL,
    action      VARCHAR(30) NOT NULL,
    description VARCHAR(255),
    CONSTRAINT uq_perm UNIQUE (resource, action)
);

CREATE TABLE IF NOT EXISTS role_permissions
(
    role_id       INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_perm FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id     BIGINT    NOT NULL,
    role_id     INT       NOT NULL,
    assigned_by BIGINT,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

INSERT INTO permissions (resource, action, description) VALUES
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

INSERT INTO roles (name, description, is_system) VALUES
('Admin', 'Full access to the administration area', TRUE),
('Customer', 'Default customer account', TRUE)
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    is_system = VALUES(is_system);

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.name = 'Admin'
;

INSERT INTO user_roles (user_id, role_id, assigned_by)
SELECT u.id, r.id, NULL
FROM users u
JOIN roles r ON r.name = 'Admin'
LEFT JOIN user_roles ur ON ur.user_id = u.id AND ur.role_id = r.id
WHERE u.role = 'Admin' AND ur.user_id IS NULL;

INSERT INTO user_roles (user_id, role_id, assigned_by)
SELECT u.id, r.id, NULL
FROM users u
JOIN roles r ON r.name = 'Customer'
LEFT JOIN user_roles ur ON ur.user_id = u.id AND ur.role_id = r.id  
WHERE (u.role IS NULL OR u.role <> 'Admin') AND ur.user_id IS NULL;

SET FOREIGN_KEY_CHECKS = 1; 
