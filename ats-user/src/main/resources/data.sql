-- Seed data for development
-- Password for admin@ats.local / pgallegoscelis86@gmail.com: admin123

INSERT INTO modules (id, code, name, description, active, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 'USERS',     'Users',     'User management module',     TRUE, NOW(), NOW(), 1, 1),
    (2, 'ROLES',     'Roles',     'Role management module',     TRUE, NOW(), NOW(), 1, 1),
    (3, 'MODULES',   'Modules',   'Module management module',   TRUE, NOW(), NOW(), 1, 1),
    (4, 'MENUS',     'Menus',     'Menu management module',     TRUE, NOW(), NOW(), 1, 1),
    (5, 'PERMISSIONS', 'Permissions', 'Permission view module', TRUE, NOW(), NOW(), 1, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO permissions (id, code, name, resource, action, scope, description, active, created_at, updated_at, module_id)
VALUES
    -- Users
    (1,  'USER_CREATE',         'Create User',         'users',       'create', 'global', 'Create new users',              TRUE, NOW(), NOW(), 1),
    (2,  'USER_READ',           'Read User',           'users',       'read',   'global', 'View user details',              TRUE, NOW(), NOW(), 1),
    (3,  'USER_UPDATE',         'Update User',         'users',       'update', 'global', 'Update existing users',          TRUE, NOW(), NOW(), 1),
    (4,  'USER_DELETE',         'Delete User',         'users',       'delete', 'global', 'Deactivate users',               TRUE, NOW(), NOW(), 1),
    (5,  'USER_STATUS_UPDATE',  'Update User Status',  'users',       'status', 'global', 'Activate/deactivate users',      TRUE, NOW(), NOW(), 1),
    -- Roles
    (6,  'ROLE_CREATE',         'Create Role',         'roles',       'create', 'global', 'Create new roles',               TRUE, NOW(), NOW(), 2),
    (7,  'ROLE_READ',           'Read Role',           'roles',       'read',   'global', 'View role details',               TRUE, NOW(), NOW(), 2),
    (8,  'ROLE_UPDATE',         'Update Role',         'roles',       'update', 'global', 'Update existing roles',           TRUE, NOW(), NOW(), 2),
    (9,  'ROLE_DELETE',         'Delete Role',         'roles',       'delete', 'global', 'Deactivate roles',                TRUE, NOW(), NOW(), 2),
    (10, 'ROLE_STATUS_UPDATE',  'Update Role Status',  'roles',       'status', 'global', 'Activate/deactivate roles',       TRUE, NOW(), NOW(), 2),
    (11, 'ROLE_PERMISSION_ASSIGN', 'Assign Permissions', 'roles',    'assign', 'global', 'Assign permissions to roles',     TRUE, NOW(), NOW(), 2),
    (12, 'ROLE_PERMISSION_REMOVE', 'Remove Permissions', 'roles',    'remove', 'global', 'Remove permissions from roles',   TRUE, NOW(), NOW(), 2),
    -- Modules
    (13, 'MODULE_CREATE',        'Create Module',       'modules',    'create', 'global', 'Create new modules',              TRUE, NOW(), NOW(), 3),
    (14, 'MODULE_READ',          'Read Module',         'modules',    'read',   'global', 'View module details',              TRUE, NOW(), NOW(), 3),
    (15, 'MODULE_UPDATE',        'Update Module',       'modules',    'update', 'global', 'Update existing modules',         TRUE, NOW(), NOW(), 3),
    (16, 'MODULE_DELETE',        'Delete Module',       'modules',    'delete', 'global', 'Deactivate modules',              TRUE, NOW(), NOW(), 3),
    (17, 'MODULE_STATUS_UPDATE', 'Update Module Status', 'modules',  'status', 'global', 'Activate/deactivate modules',     TRUE, NOW(), NOW(), 3),
    -- Menus
    (18, 'MENU_CREATE',          'Create Menu',         'menus',      'create', 'global', 'Create new menu items',           TRUE, NOW(), NOW(), 4),
    (19, 'MENU_READ',            'Read Menu',           'menus',      'read',   'global', 'View menu details',               TRUE, NOW(), NOW(), 4),
    (20, 'MENU_UPDATE',          'Update Menu',         'menus',      'update', 'global', 'Update existing menu items',      TRUE, NOW(), NOW(), 4),
    (21, 'MENU_DELETE',          'Delete Menu',         'menus',      'delete', 'global', 'Deactivate menu items',           TRUE, NOW(), NOW(), 4),
    (22, 'MENU_STATUS_UPDATE',   'Update Menu Status',  'menus',      'status', 'global', 'Activate/deactivate menu items',  TRUE, NOW(), NOW(), 4),
    -- Permissions
    (23, 'PERMISSION_READ',      'Read Permission',     'permissions','read',   'global', 'View permission list',            TRUE, NOW(), NOW(), 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO roles (id, name, description, active, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 'ADMIN',     'System administrator with full access', TRUE, NOW(), NOW(), 1, 1),
    (2, 'RECRUITER', 'Recruiter with limited access',         TRUE, NOW(), NOW(), 1, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id, active, created_at, updated_at)
SELECT r.id, p.id, TRUE, NOW(), NOW()
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO role_permissions (role_id, permission_id, active, created_at, updated_at)
SELECT 2, p.id, TRUE, NOW(), NOW()
FROM permissions p
WHERE p.code IN ('USER_READ', 'ROLE_READ', 'MENU_READ', 'MODULE_READ', 'PERMISSION_READ')
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp WHERE rp.role_id = 2 AND rp.permission_id = p.id
  );

INSERT INTO users (id, name, last_name, email, password_hash, country_code, phone, active, created_at, updated_at)
VALUES
    (1, 'System', 'Admin',  'admin@ats.local',           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+57', '3001002000', TRUE, NOW(), NOW()),
    (2, 'Pablo',  'Gallegos', 'pgallegoscelis86@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+56', '989421155',  TRUE, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT 1, 1
WHERE NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = 1 AND role_id = 1);

INSERT INTO user_roles (user_id, role_id)
SELECT 2, 2
WHERE NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = 2 AND role_id = 2);

-- Sync sequences so new rows don't collide with seed IDs
SELECT setval('modules_id_seq',          COALESCE((SELECT MAX(id) FROM modules),          1));
SELECT setval('permissions_id_seq',      COALESCE((SELECT MAX(id) FROM permissions),      1));
SELECT setval('roles_id_seq',            COALESCE((SELECT MAX(id) FROM roles),            1));
SELECT setval('users_id_seq',            COALESCE((SELECT MAX(id) FROM users),            1));
SELECT setval('menus_id_seq',            COALESCE((SELECT MAX(id) FROM menus),            1));
