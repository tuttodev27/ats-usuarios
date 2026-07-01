# HU-65 — Migrar secretos de configuración a variables de entorno

**Como** administrador del sistema  
**Quiero** que el JWT secret y otras credenciales se configuren mediante variables de entorno  
**Para** evitar que secretos queden hardcodeados en el repositorio y puedan filtrarse.

## Descripción

Actualmente el JWT signing secret está hardcodeado en `application.yml` como `ats-users-dev-secret-change-in-prod-2026`. Cualquier persona con acceso al código (o al JAR) puede forjar tokens JWT válidos. Los secretos deben leerse desde variables de entorno o un secrets manager.

## Criterios de aceptación

1. `security.jwt.secret` se lee de la variable de entorno `JWT_SECRET` con un valor por defecto solo para desarrollo.
2. `spring.datasource.password` se lee de variable de entorno (`DB_PASSWORD`).
3. Los valores por defecto en `application.yml` solo funcionan en perfil `dev`.
4. En perfil `prod` no hay valores por defecto: la aplicación falla al arrancar si falta algún secreto.
5. Se documenta en el README qué variables de entorno son requeridas.
