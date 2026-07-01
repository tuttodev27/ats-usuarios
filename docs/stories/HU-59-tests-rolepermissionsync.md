# HU-59 — Agregar tests para RolePermissionSyncSupport

**Como** desarrollador  
**Quiero** agregar tests para la lógica de sincronización de permisos en `RolePermissionSyncSupport`  
**Para** asegurar que la asignación y remoción de permisos a roles funciona correctamente.

## Descripción

`RolePermissionSyncSupport.sync()` contiene lógica compleja que elimina permisos existentes y agrega los nuevos, manejando la relación many-to-many entre roles y permisos. No hay tests que cubran esta funcionalidad, lo que es riesgoso dado que es una operación crítica para el modelo de autorización.

## Criterios de aceptación

1. Test: sincronizar permisos reemplaza los existentes por los nuevos.
2. Test: sincronizar con lista vacía remueve todos los permisos del rol.
3. Test: sincronizar con permisos que ya existen no crea duplicados.
4. Test: sincronizar marca como inactivos los permisos removidos.
