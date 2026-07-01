# HU-57 — Agregar tests de integración para controladores Module, Menu, Permission y Role

**Como** desarrollador  
**Quiero** agregar tests de integración para los controladores `ModuleController`, `MenuController`, `PermissionController` y `RoleController`  
**Para** asegurar que cada endpoint funciona correctamente y validar el comportamiento ante casos de éxito y error.

## Descripción

Actualmente existen tests de seguridad (`*SecurityTest`) para estos controladores, pero no hay tests funcionales equivalentes a `UserControllerTest` que validen el comportamiento completo (creación, actualización, listado, eliminación). Sin estos tests, los refactors o cambios pueden introducir bugs no detectados.

## Criterios de aceptación

1. `ModuleControllerTest` cubre: crear módulo (201), listar (200), obtener por id (200), actualizar (200), cambiar estado (200), errores 400/404/409.
2. `MenuControllerTest` cubre: crear menú (201), listar (200), obtener por id (200), actualizar (200), cambiar estado (200), errores 400/404/409.
3. `PermissionControllerTest` cubre: listar permisos con filtros (200), errores 401/403.
4. `RoleControllerTest` cubre: crear rol (201), listar (200), obtener por id (200), actualizar (200), asignar/remover permisos (200), errores 400/404/409.
5. Todos los tests usan el mismo patrón que `UserControllerTest` (`@SpringBootTest` con exclusiones + `@MockitoBean`).
