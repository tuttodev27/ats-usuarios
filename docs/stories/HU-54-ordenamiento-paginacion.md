# HU-54 — Agregar ordenamiento por defecto en consultas paginadas

**Como** usuario del sistema  
**Quiero** que los resultados paginados de usuarios y roles aparezcan ordenados de forma predecible  
**Para** facilitar la navegación y evitar cambios de orden entre páginas.

## Descripción

Las consultas paginadas en `UserSpecification` y `RoleSpecification` crean un `PageRequest` sin especificar `Sort`, por lo que el orden de los resultados es indeterminado y puede variar entre ejecuciones. Esto es especialmente problemático en paginación, donde los elementos pueden saltar entre páginas.

## Criterios de aceptación

1. `GET /api/users` con paginación retorna usuarios ordenados por `createdAt` descendente por defecto.
2. `GET /api/roles` con paginación retorna roles ordenados por `createdAt` descendente por defecto.
3. Se puede especificar un orden personalizado mediante parámetros de consulta (`sortBy`, `sortDirection`).
4. Todos los tests existentes continúan pasando.
