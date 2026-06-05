# ÉPICA — Autorización por permisos

**Como** administrador del sistema  
**Quiero** controlar el acceso a funcionalidades mediante permisos granulares  
**Para** permitir que cada usuario o rol acceda solo a las acciones que realmente le corresponden.

---

## Descripción funcional

Actualmente el acceso puede controlarse por rol. Se requiere evolucionar el modelo para validar permisos específicos por operación, por ejemplo `USER_CREATE`, `USER_READ`, `ROLE_UPDATE` o `ROLE_PERMISSION_ASSIGN`.

## Objetivo

Permitir que el backend valide permisos específicos por endpoint, desacoplando el acceso de reglas rígidas basadas solo en rol.

## Criterios de aceptación

1. Las rutas protegidas se validan por permisos específicos y no solo por rol.
2. Si el usuario no está autenticado, responde **401 Unauthorized**.
3. Si el usuario está autenticado pero no tiene el permiso requerido, responde **403 Forbidden**.
4. La autorización por permiso se valida en backend antes de ejecutar la lógica del endpoint.
5. Los permisos se derivan de roles activos asignados al usuario.
6. Solo se consideran permisos activos asociados a roles activos.
7. El token o contexto de seguridad permite identificar los permisos activos del usuario.
8. Deben existir pruebas que validen acceso permitido y denegado por permiso.
9. La implementación debe permitir incorporar nuevos permisos sin modificar reglas hardcodeadas por rol.
