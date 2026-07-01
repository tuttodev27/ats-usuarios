# HU-56 — Extraer authorities/permisos a constantes

**Como** desarrollador  
**Quiero** centralizar los strings de permisos en una clase de constantes  
**Para** evitar errores tipográficos y facilitar el mantenimiento cuando se agreguen o modifiquen permisos.

## Descripción

En `SecurityConfig.java:58-82` los strings como `"ROLE_CREATE"`, `"ROLE_READ"`, `"MODULE_CREATE"`, `"MENU_READ"`, `"PERMISSION_READ"`, etc. están hardcodeados. Si un permiso cambia de nombre, hay que actualizarlo en múltiples lugares. Conviene centralizarlos en una clase como `PermissionConstants` o similar.

## Criterios de aceptación

1. Se crea una clase `PermissionConstants` (o similar) con constantes para cada permiso.
2. `SecurityConfig.java` usa las constantes en lugar de strings literales.
3. Cualquier otro archivo que use permisos como strings también usa las constantes.
4. El proyecto compila sin errores.
5. Todos los tests existentes continúan pasando.
