-- Migracion: usuarios adicionales (admin y recruiter) + catalogos ATS
-- Mantiene los usuarios del seed y agrega:
--   admin@worksync.cl     -> Admin123    (rol ADMIN, todos los permisos)
--   recruiter@worksync.cl -> Recruiter123 (rol RECRUITER, solo ATS)
--
-- Permisos nuevos del modulo ATS:
--   CANDIDATE_*, SOLICITUD_*, MATCH_RUN
--
-- Menus nuevos:
--   /candidates, /candidates/new, /solicitudes, /solicitudes/new, /matching
--
-- Idempotente: usa ON CONFLICT para poder re-ejecutarse sin duplicar.
-- Ejecutar con:
--   psql -h localhost -p 5433 -U postgres -d ats_users -f 02_seed_users_and_ats_catalogs.sql

BEGIN;

-- 1) Nuevos permisos del modulo ATS
INSERT INTO permissions (code, resource, action, scope, description, active, created_at, updated_at, created_by, updated_by, module_id)
VALUES
    ('CANDIDATE_CREATE', 'candidates', 'create', 'global', 'Crear candidato', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('CANDIDATE_READ',   'candidates', 'read',   'global', 'Consultar candidato', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('CANDIDATE_UPDATE', 'candidates', 'update', 'global', 'Actualizar candidato', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('CANDIDATE_DELETE', 'candidates', 'delete', 'global', 'Desactivar candidato', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('SOLICITUD_CREATE', 'solicitudes', 'create', 'global', 'Crear solicitud', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('SOLICITUD_READ',   'solicitudes', 'read',   'global', 'Consultar solicitud', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('SOLICITUD_UPDATE', 'solicitudes', 'update', 'global', 'Actualizar solicitud', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('SOLICITUD_DELETE', 'solicitudes', 'delete', 'global', 'Desactivar solicitud', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS')),
    ('MATCH_RUN',        'matching',   'run',    'global', 'Ejecutar matching de candidatos', TRUE, NOW(), NOW(), 1, 1, (SELECT id FROM modules WHERE code = 'ATS'))
ON CONFLICT (code) DO UPDATE SET
    description = EXCLUDED.description,
    active = EXCLUDED.active,
    updated_at = NOW(),
    updated_by = EXCLUDED.updated_by;

-- 2) Menus del ATS (visibles segun el permiso requerido)
INSERT INTO menus (title, path, order_index, required_permission_code, active, created_at, updated_at, module_id)
VALUES
    ('Postulantes',     '/candidates',       1, 'CANDIDATE_READ',    TRUE, NOW(), NOW(), (SELECT id FROM modules WHERE code = 'ATS')),
    ('Nuevo Candidato', '/candidates/new',   2, 'CANDIDATE_CREATE',  TRUE, NOW(), NOW(), (SELECT id FROM modules WHERE code = 'ATS')),
    ('Solicitudes',     '/solicitudes',      3, 'SOLICITUD_READ',    TRUE, NOW(), NOW(), (SELECT id FROM modules WHERE code = 'ATS')),
    ('Nueva Solicitud', '/solicitudes/new',  4, 'SOLICITUD_CREATE',  TRUE, NOW(), NOW(), (SELECT id FROM modules WHERE code = 'ATS')),
    ('Matching',        '/matching',         5, 'MATCH_RUN',         TRUE, NOW(), NOW(), (SELECT id FROM modules WHERE code = 'ATS'))
ON CONFLICT (path) DO UPDATE SET
    title = EXCLUDED.title,
    order_index = EXCLUDED.order_index,
    required_permission_code = EXCLUDED.required_permission_code,
    active = EXCLUDED.active,
    updated_at = NOW(),
    module_id = EXCLUDED.module_id;

-- 3) Permisos ATS para el rol RECRUITER (incluye el ATS_DASHBOARD_VIEW del seed)
INSERT INTO role_permissions (role_id, permission_id, active, created_at, updated_at)
SELECT r.id, p.id, TRUE, NOW(), NOW()
FROM roles r
JOIN permissions p ON p.code IN (
    'ATS_DASHBOARD_VIEW',
    'CANDIDATE_CREATE', 'CANDIDATE_READ', 'CANDIDATE_UPDATE', 'CANDIDATE_DELETE',
    'SOLICITUD_CREATE', 'SOLICITUD_READ', 'SOLICITUD_UPDATE', 'SOLICITUD_DELETE',
    'MATCH_RUN'
)
WHERE r.name = 'RECRUITER'
ON CONFLICT (role_id, permission_id) DO UPDATE SET
    active = TRUE,
    updated_at = NOW();

-- 4) Los mismos permisos ATS al rol ADMIN (aseguramos cobertura completa)
INSERT INTO role_permissions (role_id, permission_id, active, created_at, updated_at)
SELECT r.id, p.id, TRUE, NOW(), NOW()
FROM roles r
JOIN permissions p ON p.code IN (
    'CANDIDATE_CREATE', 'CANDIDATE_READ', 'CANDIDATE_UPDATE', 'CANDIDATE_DELETE',
    'SOLICITUD_CREATE', 'SOLICITUD_READ', 'SOLICITUD_UPDATE', 'SOLICITUD_DELETE',
    'MATCH_RUN'
)
WHERE r.name = 'ADMIN'
ON CONFLICT (role_id, permission_id) DO UPDATE SET
    active = TRUE,
    updated_at = NOW();

-- 5) Usuarios nuevos
--    Hashes bcrypt (cost 10):
--      Admin123    -> $2y$10$mwMJ3Kx6zImKQqs5/EfuiOsJsaG6H6/lU/cSUrOP/aXZekNX34z6m
--      Recruiter123 -> $2y$10$EkFTq0Up17IU1fxzU7F2OeqSzqEbdY/65bDd9a9y2J2E1vlWD8Poa
INSERT INTO users (name, last_name, email, country_code, phone, password_hash, created_at, updated_at, created_by, updated_by, active)
VALUES
    ('Pablo', 'Admin',     'admin@worksync.cl',     '+56', '911111111', '$2y$10$mwMJ3Kx6zImKQqs5/EfuiOsJsaG6H6/lU/cSUrOP/aXZekNX34z6m', NOW(), NOW(), 1, 1, TRUE),
    ('Maria', 'Recruiter', 'recruiter@worksync.cl', '+56', '922222222', '$2y$10$EkFTq0Up17IU1fxzU7F2OeqSzqEbdY/65bDd9a9y2J2E1vlWD8Poa', NOW(), NOW(), 1, 1, TRUE)
ON CONFLICT (email) DO UPDATE SET
    name = EXCLUDED.name,
    last_name = EXCLUDED.last_name,
    country_code = EXCLUDED.country_code,
    phone = EXCLUDED.phone,
    password_hash = EXCLUDED.password_hash,
    active = EXCLUDED.active,
    updated_at = NOW(),
    updated_by = EXCLUDED.updated_by;

-- 6) Asignacion de roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.email = 'admin@worksync.cl'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'RECRUITER'
WHERE u.email = 'recruiter@worksync.cl'
ON CONFLICT DO NOTHING;

COMMIT;
