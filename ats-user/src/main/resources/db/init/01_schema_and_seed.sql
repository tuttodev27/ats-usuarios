-- PostgreSQL schema + seed data for ATS usuarios
-- Includes: users, roles, permissions, modules, menus and join tables

BEGIN;

CREATE TABLE IF NOT EXISTS modules (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(60) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(120) NOT NULL UNIQUE,
    name VARCHAR(120),
    resource VARCHAR(120),
    action VARCHAR(60),
    scope VARCHAR(60),
    description VARCHAR(255),
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    module_id BIGINT NOT NULL,
    CONSTRAINT fk_permissions_module FOREIGN KEY (module_id) REFERENCES modules(id)
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    country_code VARCHAR(5),
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    active BOOLEAN
);

CREATE TABLE IF NOT EXISTS menus (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    path VARCHAR(200) NOT NULL UNIQUE,
    icon VARCHAR(120),
    order_index INTEGER,
    required_permission_code VARCHAR(120),
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    module_id BIGINT NOT NULL,
    CONSTRAINT fk_menus_module FOREIGN KEY (module_id) REFERENCES modules(id)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

ALTER TABLE role_permissions ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE role_permissions ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE role_permissions ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_module_code ON modules(code);
CREATE INDEX IF NOT EXISTS idx_permission_code ON permissions(code);
CREATE INDEX IF NOT EXISTS idx_permission_module_id ON permissions(module_id);
CREATE INDEX IF NOT EXISTS idx_role_name ON roles(name);
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_menu_module_id ON menus(module_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_menu_path ON menus(path);

-- Modules
INSERT INTO modules (code, name, description, active, created_at, updated_at, created_by, updated_by)
VALUES
    ('USER', 'Usuarios', 'Gestión de usuarios y autenticación', TRUE, NOW(), NOW(), 1, 1),
    ('ATS', 'ATS Core', 'Configuraciones y operaciones del ATS', TRUE, NOW(), NOW(), 1, 1)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    active = EXCLUDED.active,
    updated_at = NOW(),
    updated_by = EXCLUDED.updated_by;

-- Permissions
INSERT INTO permissions (code, name, resource, action, scope, description, active, created_at, updated_at, created_by, updated_by, module_id)
VALUES
    ('USER_CREATE', 'Crear Usuario', 'users', 'create', 'global', 'Crear usuario', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('USER_READ',   'Ver Usuarios',  'users', 'read',   'global', 'Consultar usuario', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('USER_UPDATE', 'Editar Usuario', 'users', 'update', 'global', 'Actualizar usuario', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('USER_DELETE', 'Desactivar Usuario', 'users', 'delete', 'global', 'Desactivar usuario', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('USER_STATUS_UPDATE', 'Estado de Usuario', 'users', 'status-update', 'global', 'Activar o desactivar usuario', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('USER_ROLE_UPDATE', 'Cambiar Rol de Usuario', 'users', 'role-update', 'global', 'Asignar o cambiar el rol de un usuario', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('ROLE_CREATE', 'Crear Rol', 'roles', 'create', 'global', 'Crear rol', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('ROLE_READ', 'Ver Roles', 'roles', 'read', 'global', 'Consultar rol', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('ROLE_UPDATE', 'Editar Rol', 'roles', 'update', 'global', 'Actualizar rol', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('ROLE_DELETE', 'Desactivar Rol', 'roles', 'delete', 'global', 'Desactivar rol', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('ROLE_STATUS_UPDATE', 'Estado de Rol', 'roles', 'status-update', 'global', 'Actualizar estado de rol', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('ROLE_PERMISSION_ASSIGN', 'Asignar Permisos a Rol', 'role-permissions', 'assign', 'global', 'Asignar permisos a rol', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('ROLE_PERMISSION_REMOVE', 'Quitar Permisos de Rol', 'role-permissions', 'remove', 'global', 'Quitar permisos de rol', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('ROLE_PERMISSION_READ', 'Ver Permisos del Rol', 'role-permissions', 'read', 'global', 'Consultar permisos asignados a un rol', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('MODULE_CREATE', 'Crear Módulo', 'modules', 'create', 'global', 'Crear modulo', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('MODULE_READ', 'Ver Módulos', 'modules', 'read', 'global', 'Consultar modulos', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('MODULE_UPDATE', 'Editar Módulo', 'modules', 'update', 'global', 'Actualizar modulo', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('MODULE_DELETE', 'Desactivar Módulo', 'modules', 'delete', 'global', 'Desactivar modulo', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('MODULE_STATUS_UPDATE', 'Estado de Módulo', 'modules', 'status-update', 'global', 'Activar o desactivar modulo', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('MENU_CREATE', 'Crear Menú', 'menus', 'create', 'global', 'Crear menu', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('MENU_READ', 'Ver Menús', 'menus', 'read', 'global', 'Consultar menus', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('MENU_UPDATE', 'Editar Menú', 'menus', 'update', 'global', 'Actualizar menu', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('MENU_DELETE', 'Desactivar Menú', 'menus', 'delete', 'global', 'Desactivar menu', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('MENU_STATUS_UPDATE', 'Estado de Menú', 'menus', 'status-update', 'global', 'Activar o desactivar menu', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('PERMISSION_READ', 'Ver Permisos', 'permissions', 'read', 'global', 'Consultar catalogo de permisos', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('ATS_DASHBOARD_VIEW', 'Ver Dashboard', 'dashboard', 'read', 'global', 'Ver dashboard ATS', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS'))
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    resource = EXCLUDED.resource,
    action = EXCLUDED.action,
    scope = EXCLUDED.scope,
    description = EXCLUDED.description,
    active = EXCLUDED.active,
    updated_at = NOW(),
    updated_by = EXCLUDED.updated_by,
    module_id = EXCLUDED.module_id;

-- Roles
INSERT INTO roles (name, description, active, created_at, updated_at)
VALUES
    ('ADMIN', 'Administrador del sistema', TRUE, NOW(), NOW()),
    ('RECRUITER', 'Reclutador con acceso operativo', TRUE, NOW(), NOW())
ON CONFLICT (name) DO UPDATE SET
    description = EXCLUDED.description,
    active = EXCLUDED.active,
    updated_at = NOW();

-- Menus
INSERT INTO menus (title, path, order_index, required_permission_code, active, created_at, updated_at, module_id)
VALUES
    ('Usuarios', '/users', 1, 'USER_READ', TRUE, NOW(), NOW(), (SELECT id FROM modules WHERE code = 'USER')),
    ('Nuevo Usuario', '/users/new', 2, 'USER_CREATE', TRUE, NOW(), NOW(), (SELECT id FROM modules WHERE code = 'USER')),
    ('Dashboard', '/dashboard', 1, 'ATS_DASHBOARD_VIEW', TRUE, NOW(), NOW(), (SELECT id FROM modules WHERE code = 'ATS'))
ON CONFLICT (path) DO UPDATE SET
    title = EXCLUDED.title,
    order_index = EXCLUDED.order_index,
    required_permission_code = EXCLUDED.required_permission_code,
    active = EXCLUDED.active,
    updated_at = NOW(),
    module_id = EXCLUDED.module_id;

-- Users
-- Passwords:
-- admin@ats.local -> Admin123
-- recruiter@ats.local -> Recruiter123
INSERT INTO users (name, last_name, email, country_code, phone, password_hash, created_at, updated_at, created_by, updated_by, active)
VALUES
    ('System', 'Admin', 'admin@ats.local', '+57', '3001002000', '$2y$10$mwMJ3Kx6zImKQqs5/EfuiOsJsaG6H6/lU/cSUrOP/aXZekNX34z6m', NOW(), NOW(), 1, 1, TRUE),
    ('Ana', 'Recruiter', 'recruiter@ats.local', '+57', '3005556677', '$2y$10$EkFTq0Up17IU1fxzU7F2OeqSzqEbdY/65bDd9a9y2J2E1vlWD8Poa', NOW(), NOW(), 1, 1, TRUE)
ON CONFLICT (email) DO UPDATE SET
    name = EXCLUDED.name,
    last_name = EXCLUDED.last_name,
    country_code = EXCLUDED.country_code,
    phone = EXCLUDED.phone,
    password_hash = EXCLUDED.password_hash,
    active = EXCLUDED.active,
    updated_at = NOW(),
    updated_by = EXCLUDED.updated_by;

-- Role-Permission mapping
INSERT INTO role_permissions (role_id, permission_id, active, created_at, updated_at)
SELECT r.id, p.id, TRUE, NOW(), NOW()
FROM roles r
JOIN permissions p ON p.code IN (
    'USER_CREATE',
    'USER_READ',
    'USER_UPDATE',
    'USER_DELETE',
    'USER_STATUS_UPDATE',
    'USER_ROLE_UPDATE',
    'ROLE_CREATE',
    'ROLE_READ',
    'ROLE_UPDATE',
    'ROLE_DELETE',
    'ROLE_STATUS_UPDATE',
    'ROLE_PERMISSION_ASSIGN',
    'ROLE_PERMISSION_REMOVE',
    'ROLE_PERMISSION_READ',
    'MODULE_CREATE',
    'MODULE_READ',
    'MODULE_UPDATE',
    'MODULE_DELETE',
    'MODULE_STATUS_UPDATE',
    'MENU_CREATE',
    'MENU_READ',
    'MENU_UPDATE',
    'MENU_DELETE',
    'MENU_STATUS_UPDATE',
    'PERMISSION_READ',
    'ATS_DASHBOARD_VIEW'
)
WHERE r.name = 'ADMIN'
ON CONFLICT (role_id, permission_id) DO UPDATE SET
    active = TRUE,
    updated_at = NOW();

INSERT INTO role_permissions (role_id, permission_id, active, created_at, updated_at)
SELECT r.id, p.id, TRUE, NOW(), NOW()
FROM roles r
JOIN permissions p ON p.code IN ('ATS_DASHBOARD_VIEW', 'PERMISSION_READ')
WHERE r.name = 'RECRUITER'
ON CONFLICT (role_id, permission_id) DO UPDATE SET
    active = TRUE,
    updated_at = NOW();

-- Ensure RECRUITER only has the explicitly granted permissions active
UPDATE role_permissions rp
SET active = FALSE,
    updated_at = NOW()
FROM roles r
WHERE rp.role_id = r.id
  AND r.name = 'RECRUITER'
  AND rp.permission_id NOT IN (
      SELECT p.id FROM permissions p
      WHERE p.code IN ('ATS_DASHBOARD_VIEW', 'PERMISSION_READ')
  );

-- User-Role mapping
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.email = 'admin@ats.local'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'RECRUITER'
WHERE u.email = 'recruiter@ats.local'
ON CONFLICT DO NOTHING;

COMMIT;
