# HU-62 — Agregar documentación de APIs faltantes en README

**Como** desarrollador frontend  
**Quiero** encontrar en el README la documentación de todos los endpoints disponibles  
**Para** poder consumir la API completa sin tener que leer el código fuente.

## Descripción

El README solo documenta los endpoints de autenticación y CRUD de usuarios. Faltan las secciones de:
- `/api/modules` (CRUD de módulos)
- `/api/menus` (CRUD de menús de navegación)
- `/api/permissions` (consulta de catálogo de permisos)
- `/api/roles` (CRUD de roles y asignación de permisos)

## Criterios de aceptación

1. El README incluye una sección para cada grupo de endpoints faltante.
2. Cada sección documenta: método HTTP, ruta, descripción, ejemplo de request y response.
3. Los ejemplos son consistentes con los DTOs reales.
4. Se indican los permisos requeridos para cada endpoint.
