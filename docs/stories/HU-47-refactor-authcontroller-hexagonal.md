# HU-47 — Refactorizar AuthController para respetar la arquitectura hexagonal

**Como** desarrollador  
**Quiero** que `AuthController` dependa de puertos de dominio (`UserUseCase`, `RoleUseCase`) en lugar de infraestructura directa  
**Para** mantener la independencia de capas y facilitar las pruebas unitarias.

## Descripción

`AuthController.java` inyecta directamente `UserJpaRepository` (interfaz JPA de infraestructura) y utiliza `RoleEntity` (entidad JPA). Esto viola el principio de la arquitectura hexagonal donde los controladores solo deben depender de puertos de dominio (`port/in`). Además, dificulta el testeo al requerir mockear repositorios JPA en lugar de puertos simples.

## Criterios de aceptación

1. `AuthController` no inyecta ningún repositorio JPA ni entidad de infraestructura.
2. La lógica de login utiliza `UserUseCase` para obtener datos del usuario.
3. El mapeo de roles a nombres/permisos se realiza en un servicio o mapper, no en el controlador.
4. Todos los tests existentes continúan pasando sin modificaciones en su lógica de assertions.
5. La funcionalidad de login permanece idéntica desde la perspectiva del cliente HTTP.
