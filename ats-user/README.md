# ATS Usuarios

API REST para autenticación y gestión de usuarios con roles usando Spring Boot, Spring Security (JWT) y PostgreSQL.

## Requisitos
- Java 21
- Docker y Docker Compose
- Gradle Wrapper (`./gradlew`)
- Postman (opcional para pruebas)

## Tecnologías
- Spring Boot 4
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL 16
- MapStruct

## Estructura del proyecto
- Código backend: `ats-user/src/main/java`
- Configuración: `ats-user/src/main/resources/application.yml`
- Script SQL inicial: `ats-user/src/main/resources/db/init/01_schema_and_seed.sql`
- Docker DB: `ats-user/docker-compose.yml`

## Levantar base de datos
Desde `ats-user/`:

```bash
docker compose up -d
```

Datos de conexión local:
- Host: `localhost`
- Puerto: `5433`
- DB: `ats_users`
- Usuario: `postgres`
- Password: `postgres`

## Cargar esquema y datos semilla
Desde la raíz del repo:

```bash
docker exec -i ats-user-postgres psql -U postgres -d ats_users < ats-user/src/main/resources/db/init/01_schema_and_seed.sql
```

Usuarios semilla:
- `admin@ats.local` / `Admin123`
- `recruiter@ats.local` / `Recruiter123`

## Ejecutar la aplicación
Desde `ats-user/`:

```bash
./gradlew bootRun
```

La API queda en:
- `http://localhost:8083`

## Flujo de prueba en Postman
1. Login
```http
POST /api/auth/login
```
Body:
```json
{
  "email": "admin@ats.local",
  "password": "Admin123"
}
```

2. Obtener roles (requiere token)
```http
GET /api/users/roles
Authorization: Bearer <TOKEN>
```

3. Crear usuario (requiere token)
```http
POST /api/users
Authorization: Bearer <TOKEN>
Content-Type: application/json
```
Body:
```json
{
  "name": "Pablo",
  "lastName": "Gallegos",
  "email": "pablo@ats.local",
  "countryCode": "+56",
  "phone": "989421155",
  "password": "Clave123",
  "role": "RECRUITER"
}
```

4. Listar usuarios activos
```http
GET /api/users
Authorization: Bearer <TOKEN>
```

5. Obtener usuario por id
```http
GET /api/users/{id}
Authorization: Bearer <TOKEN>
```

6. Actualizar usuario
```http
PUT /api/users/{id}
Authorization: Bearer <TOKEN>
Content-Type: application/json
```
Body:
```json
{
  "name": "Pablo",
  "lastName": "Gallegos",
  "countryCode": "+56",
  "phone": "989421156",
  "active": true
}
```

7. Eliminar usuario (borrado lógico)
```http
DELETE /api/users/{id}
Authorization: Bearer <TOKEN>
```

## Errores comunes
- `401 UNAUTHORIZED`: token inválido, incompleto o sin prefijo `Bearer `.
- `ROLE_NOT_FOUND`: el rol enviado no existe o está inactivo en BD.
- `EMAIL_ALREADY_EXISTS`: ya existe un usuario con ese correo.

## Notas
- La columna usada para indicativo es `country_code`.
- No usar header `Authorization` manual y `Bearer Token` al mismo tiempo en Postman.
