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
    path VARCHAR(200) NOT NULL,
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
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_module_code ON modules(code);
CREATE INDEX IF NOT EXISTS idx_permission_code ON permissions(code);
CREATE INDEX IF NOT EXISTS idx_permission_module_id ON permissions(module_id);
CREATE INDEX IF NOT EXISTS idx_role_name ON roles(name);
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_menu_module_id ON menus(module_id);
CREATE INDEX IF NOT EXISTS idx_menu_path ON menus(path);

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
INSERT INTO permissions (code, resource, action, scope, description, active, created_at, updated_at, created_by, updated_by, module_id)
VALUES
    ('USER_CREATE', 'users', 'create', 'global', 'Crear usuario', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('USER_READ',   'users', 'read',   'global', 'Consultar usuario', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('USER_UPDATE', 'users', 'update', 'global', 'Actualizar usuario', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('USER_DELETE', 'users', 'delete', 'global', 'Desactivar usuario', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'USER')),
    ('ATS_DASHBOARD_VIEW', 'dashboard', 'read', 'global', 'Ver dashboard ATS', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS'))
ON CONFLICT (code) DO UPDATE SET
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
ON CONFLICT DO NOTHING;

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
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN ('USER_CREATE', 'USER_READ', 'USER_UPDATE', 'USER_DELETE', 'ATS_DASHBOARD_VIEW')
WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN ('USER_READ', 'ATS_DASHBOARD_VIEW')
WHERE r.name = 'RECRUITER'
ON CONFLICT DO NOTHING;

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
