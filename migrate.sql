-- migrate.sql
-- Add missing RBAC permissions required by the current application code.
-- This script is safe to run more than once because it uses INSERT IGNORE.

use souvenirdb;
INSERT IGNORE INTO permissions (resource, action, description)
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
    ('role', 'delete', 'Delete permission groups'),

    ('admin', 'read', 'View generic admin area');

-- Grant the new admin permission at least to Super Admin and Admin roles.
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.resource = 'admin' AND p.action = 'read'
WHERE r.name IN ('Super Admin', 'Admin');
