# HU-67 — Reemplazar scripts SQL sin gestión por herramienta de migraciones

**Como** desarrollador  
**Quiero** reemplazar los scripts SQL manuales por migraciones versionadas con Flyway o Liquibase  
**Para** tener un control de cambios de esquema trazable, repetible y automatizado.

## Descripción

Actualmente existen scripts SQL en `src/main/resources/db/init/` (`01_schema_and_seed.sql`, `02_seed_users_and_ats_catalogs.sql`) que requieren ejecución manual. Además, JPA tiene `ddl-auto: update` que modifica el esquema automáticamente. Esto crea un conflicto potencial entre ambos enfoques y no hay trazabilidad de cambios.

## Criterios de aceptación

1. Se agrega Flyway o Liquibase como dependencia en `build.gradle`.
2. Los scripts SQL existentes se convierten en migraciones versionadas.
3. Se deshabilita `ddl-auto: update` en producción (se usa `validate` o `none`).
4. Las migraciones se ejecutan automáticamente al iniciar la aplicación.
5. Existe una migración base que crea todas las tablas y datos semilla.
6. Migraciones futuras se agregan como nuevos archivos versionados.
