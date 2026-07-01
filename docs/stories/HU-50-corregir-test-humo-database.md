# HU-50 — Corregir test de humo que falla al requerir base de datos real

**Como** desarrollador  
**Quiero** que el test de humo `AtsUserApplicationTests` se ejecute sin requerir una base de datos PostgreSQL externa  
**Para** que la suite completa de tests pueda correr en cualquier entorno sin dependencias externas.

## Descripción

`AtsUserApplicationTests.java` es un `@SpringBootTest` sin configuración especial que carga el contexto completo de la aplicación. Al no excluir la auto-configuración de JPA ni activar un perfil de test, intenta conectar a `jdbc:postgresql://localhost:5433/ats_users` y falla si no hay PostgreSQL disponible.

Los demás tests (`AuthControllerTest`, `UserControllerTest`, etc.) resuelven esto excluyendo las auto-configuraciones de datasource, JPA y repositorios.

## Criterios de aceptación

1. El test de humo se ejecuta sin requerir conexión a PostgreSQL.
2. Puede usar H2 en memoria (como los otros tests) o excluir auto-configuraciones.
3. El test verifica que el contexto de Spring Boot se carga correctamente.
4. No se afectan los demás tests ni la configuración de producción.
