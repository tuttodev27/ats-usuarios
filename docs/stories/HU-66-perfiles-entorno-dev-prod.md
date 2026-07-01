# HU-66 — Agregar perfiles de entorno separados (dev/prod)

**Como** desarrollador  
**Quiero** separar la configuración en perfiles `dev` y `prod`  
**Para** tener configuraciones diferenciadas según el entorno.

## Descripción

Actualmente solo existe `application.yml` con una configuración única. Esto mezcla configuraciones de desarrollo (como `show-sql: true` o `format_sql: true`) con las de producción. Deben separarse en perfiles.

## Criterios de aceptación

1. `application.yml` contiene solo la configuración común a todos los entornos.
2. `application-dev.yml` incluye: `show-sql: true`, CORS para localhost, secretos con valores por defecto.
3. `application-prod.yml` incluye: `show-sql: false`, CORS restringido a dominios reales, sin valores por defecto para secretos, niveles de logging diferentes.
4. El perfil activo se puede configurar mediante la propiedad `spring.profiles.active` o variable de entorno `SPRING_PROFILES_ACTIVE`.
5. Todos los tests existentes continúan pasando (usando su propio `application.yml` en test).
