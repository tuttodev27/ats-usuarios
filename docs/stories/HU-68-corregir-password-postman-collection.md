# HU-68 — Corregir variable password en Postman collection

**Como** desarrollador frontend  
**Quiero** que la colección de Postman tenga la contraseña correcta para los usuarios de prueba  
**Para** poder probar la API de login sin errores de autenticación.

## Descripción

La colección Postman (`ats-user-role-crud.postman_collection.json`) tiene la variable `password` con valor `"Admin123*"` (con asterisco). Los scripts SQL de seed crean usuarios con contraseña `"Admin123"` (sin asterisco). Al usar la colección para login, la petición falla con 401 porque las contraseñas no coinciden.

## Criterios de aceptación

1. La variable `password` en la colección Postman se cambia de `"Admin123*"` a `"Admin123"`.
2. El login con la colección Postman funciona correctamente contra un entorno con datos seed.
