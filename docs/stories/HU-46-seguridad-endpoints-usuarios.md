# HU-46 — Corregir configuración de seguridad en endpoints de usuarios

**Como** administrador del sistema  
**Quiero** que los permisos granulares (`USER_CREATE`, `USER_READ`, `USER_UPDATE`, `USER_DELETE`) se validen en cada endpoint de usuarios  
**Para** que no baste con tener rol `ADMIN` para acceder a cualquier operación sobre usuarios.

## Descripción

Actualmente en `SecurityConfig.java:56` la regla `.requestMatchers("/api/users/**").hasRole("ADMIN")` captura **todas** las rutas bajo `/api/users`, impidiendo que se ejecuten las reglas más específicas por permiso (como `USER_CREATE`, `USER_READ`, etc.). Esto significa que cualquier usuario con rol `ADMIN` puede hacer cualquier operación, anulando el modelo de permisos granulares.

## Criterios de aceptación

1. `POST /api/users` requiere el permiso `USER_CREATE`.
2. `GET /api/users` requiere el permiso `USER_READ`.
3. `GET /api/users/{id}` requiere el permiso `USER_READ`.
4. `PUT /api/users/{id}` requiere el permiso `USER_UPDATE`.
5. `DELETE /api/users/{id}` requiere el permiso `USER_DELETE`.
6. `PUT /api/users/{id}/roles` requiere el permiso `USER_ROLE_UPDATE`.
7. `PATCH /api/users/{id}/status` requiere el permiso `USER_STATUS_UPDATE`.
8. `GET /api/users/roles` requiere `ROLE_READ` o los permisos de usuario correspondientes.
9. Si el usuario no está autenticado, responde **401 Unauthorized**.
10. Si el usuario está autenticado pero no tiene el permiso requerido, responde **403 Forbidden**.
