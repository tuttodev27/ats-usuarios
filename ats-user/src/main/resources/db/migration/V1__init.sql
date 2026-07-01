CREATE TABLE IF NOT EXISTS modules (
    id          BIGSERIAL    PRIMARY KEY,
    code        VARCHAR(60)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    active      BOOLEAN      NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    created_by  BIGINT,
    updated_by  BIGINT
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_module_code ON modules (code);

CREATE TABLE IF NOT EXISTS permissions (
    id          BIGSERIAL    PRIMARY KEY,
    code        VARCHAR(120) NOT NULL,
    name        VARCHAR(120),
    resource    VARCHAR(120),
    action      VARCHAR(60),
    scope       VARCHAR(60),
    description VARCHAR(255),
    active      BOOLEAN      NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    created_by  BIGINT,
    updated_by  BIGINT,
    module_id   BIGINT       NOT NULL REFERENCES modules (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_permission_code ON permissions (code);
CREATE INDEX IF NOT EXISTS idx_permission_module_id ON permissions (module_id);

CREATE TABLE IF NOT EXISTS menus (
    id                        BIGSERIAL    PRIMARY KEY,
    title                     VARCHAR(120) NOT NULL,
    path                      VARCHAR(200) NOT NULL,
    icon                      VARCHAR(120),
    order_index               INTEGER,
    required_permission_code  VARCHAR(120),
    active                    BOOLEAN      NOT NULL,
    created_at                TIMESTAMP,
    updated_at                TIMESTAMP,
    module_id                 BIGINT       NOT NULL REFERENCES modules (id)
);

CREATE INDEX IF NOT EXISTS idx_menu_module_id ON menus (module_id);
CREATE INDEX IF NOT EXISTS idx_menu_path ON menus (path);

CREATE TABLE IF NOT EXISTS roles (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    active      BOOLEAN      NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_role_name ON roles (name);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id       BIGINT    NOT NULL REFERENCES roles (id),
    permission_id BIGINT    NOT NULL REFERENCES permissions (id),
    active        BOOLEAN,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS users (
    id            BIGSERIAL    PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    country_code  VARCHAR(5),
    phone         VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    created_by    BIGINT,
    updated_by    BIGINT,
    active        BOOLEAN
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_email ON users (email);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES users (id),
    role_id BIGINT NOT NULL REFERENCES roles (id),
    PRIMARY KEY (user_id, role_id)
);
