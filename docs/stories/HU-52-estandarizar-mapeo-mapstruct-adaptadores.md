# HU-52 — Estandarizar mapeo dominio-entidad con MapStruct en todos los adaptadores

**Como** desarrollador  
**Quiero** que todos los adaptadores de repositorio usen MapStruct para el mapeo entre entidades JPA y modelos de dominio  
**Para** mantener consistencia y reducir código boilerplate.

## Descripción

Actualmente `UserRepositoryAdapter` y `RoleRepositoryAdapter` usan mapeadores MapStruct (`UserMapper`, `RoleMapper`), mientras que `PermissionRepositoryAdapter`, `MenuRepositoryAdapter` y `ModuleRepositoryAdapter` tienen el mapeo inline escrito a mano con construcciones manuales. Esto es inconsistente y propenso a errores.

## Criterios de aceptación

1. Se crean mapeadores MapStruct para `Permission`, `Menu` y `Module` (similar a `UserMapper` y `RoleMapper`).
2. Los adaptadores `PermissionRepositoryAdapter`, `MenuRepositoryAdapter` y `ModuleRepositoryAdapter` delegan el mapeo a estos nuevos mapeadores.
3. Se elimina el código de mapeo inline de estos adaptadores.
4. El proyecto compila sin errores.
5. Todos los tests existentes continúan pasando.
