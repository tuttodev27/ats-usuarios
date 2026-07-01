# HU-48 — Agregar @Transactional a servicios que modifican múltiples entidades

**Como** desarrollador  
**Quiero** que las operaciones que modifican múltiples entidades se ejecuten dentro de una transacción  
**Para** garantizar consistencia de datos ante fallos parciales.

## Descripción

Los servicios `UserService`, `RoleService`, `MenuService` y `ModuleService` realizan operaciones que afectan múltiples tablas (ej: `RoleService.assignPermissions()` elimina permisos existentes y agrega nuevos) sin la anotación `@Transactional`. Cada operación JPA se ejecuta en su propia transacción auto-commit, lo que puede producir estados inconsistentes si ocurre un error a medio camino.

## Criterios de aceptación

1. `UserService` tiene `@Transactional` en los métodos `create`, `update`, `changeUserRole`, `updateStatus`, `delete`.
2. `RoleService` tiene `@Transactional` en `create`, `update`, `assignPermissions`, `removePermission`, `updateStatus`.
3. `MenuService` tiene `@Transactional` en `create`, `update`, `updateStatus`.
4. `ModuleService` tiene `@Transactional` en `create`, `update`, `updateStatus`.
5. Los métodos de solo lectura (`getById`, `list`, `findAll`) no requieren `@Transactional` o usan `readOnly = true`.
6. Si ocurre una excepción durante una operación de escritura, todos los cambios se revierten (rollback).
