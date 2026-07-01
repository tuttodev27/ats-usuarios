# HU-61 — Corregir ejemplos del README que no coinciden con los DTOs reales

**Como** desarrollador  
**Quiero** que los ejemplos del README coincidan con la estructura real de los DTOs  
**Para** evitar confusiones a quienes consumen la API basándose en la documentación.

## Descripción

El README contiene ejemplos incorrectos:
1. Creación de usuario muestra `"role": "RECRUITER"` pero `UserRequest` usa `"roleId": 2` (ID numérico del rol, no el nombre).
2. Actualización de usuario incluye `"active": true` pero `UpdateUserRequest` no tiene campo `active`.

## Criterios de aceptación

1. El ejemplo de `POST /api/users` muestra `"roleId": 2` en lugar de `"role": "RECRUITER"`.
2. El ejemplo de `PUT /api/users/{id}` no incluye `"active"`.
3. Se verifica que todos los ejemplos del README coinciden con los DTOs reales (`UserRequest`, `UpdateUserRequest`, `LoginRequest`, `LoginResponse`).
