# HU-60 — Agregar tests de integración para repositorios JPA

**Como** desarrollador  
**Quiero** agregar tests de integración para los repositorios JPA con base de datos H2 en memoria  
**Para** verificar que las consultas personalizadas y las especificaciones funcionan correctamente.

## Descripción

No existen tests que validen el comportamiento real de los repositorios JPA (consultas personalizadas, `@EntityGraph`, especificaciones). Estos tests usarían una base de datos H2 en memoria (como la configurada en `src/test/resources/application.yml`) para verificar que las consultas SQL generadas por JPA funcionan como se espera.

## Criterios de aceptación

1. `UserJpaRepositoryTest` cubre: `findByEmail` con y sin resultados, `existsByEmail`, búsqueda con especificaciones.
2. `RoleJpaRepositoryTest` cubre: `findWithPermissionsById`, búsqueda con especificaciones.
3. `PermissionJpaRepositoryTest` cubre: consultas personalizadas con filtros por módulo y activo.
4. Los tests usan `@DataJpaTest` con H2 en memoria.
