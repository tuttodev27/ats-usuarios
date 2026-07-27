# AGENTS.md — ats-usuarios (ats-user)

## Dev environment tips
- Este repo es un microservicio independiente — no forma parte de un monorepo.
- El servicio se encuentra dentro de la subcarpeta `ats-user/`.
- Usa `./gradlew build -x test` desde `ats-user/` para compilar sin correr tests.
- El nombre del servicio es `ats-user` (confirmado en `settings.gradle`).
- Puerto del servicio: **8083** (definido en `application.yml`).
- Usa `./gradlew bootRun` desde `ats-user/` para levantarlo localmente.
- Levanta su contenedor de Postgres con `docker compose up -d` desde `ats-user/` antes de correr el servicio.
- Si el servicio necesita a otro (ej. ats-user llama a ats-candidate), levanta ese otro repo por separado y confirma su puerto en su `application.yml`.
- La base de datos se llama `ats_user` y usa PostgreSQL 16.
- El proyecto usa Flyway para migraciones de base de datos.
- Variables de entorno principales: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`.

## Testing instructions
- Corre `./gradlew test` desde `ats-user/` para ejecutar todos los tests del servicio.
- Para un test puntual: `./gradlew test --tests "com.ats.user.ClassName.methodName"`.
- Los tests usan H2 como base de datos en memoria (`testRuntimeOnly 'com.h2database:h2'`).
- Corrige cualquier error de test o compilación hasta que el build quede en verde.
- Después de mover clases o cambiar paquetes, corre `./gradlew check` (incluye tests + Checkstyle/SpotBugs si están configurados).
- Agrega o actualiza tests para el código que cambies, aunque nadie lo pida explícitamente.

## Architecture conventions
- Respeta la arquitectura hexagonal dentro del servicio:
  - `domain` — modelos, puertos (interfaces), excepciones y servicios de dominio. No depende de `infrastructure` ni `application`.
  - `application` — casos de uso (implementaciones de puertos de entrada).
  - `infrastructure` — adaptadores REST controllers, repositorios JPA, mappers, DTOs, configuración de seguridad.
- Paquete base: `com.ats.user`
- Estructura de adaptadores de entrada: `infrastructure.adapter.in.web` (controllers, DTOs, security, mappers).
- Estructura de adaptadores de salida: `infrastructure.adapter.out.persistence` (repositories JPA, entities, mappers, adapters).
- No importes clases de otro microservicio directamente — la comunicación entre servicios va por HTTP/eventos, no por dependencia de código.
- Usa MapStruct para mapeo entre entidades de dominio, persistence y web DTOs.
- Usa Lombok para reducir boilerplate.
- Las entidades JPA están en `infrastructure.adapter.out.persistence.entity`.
- Los repositorios JPA están en `infrastructure.adapter.out.persistence.repository`.
- Los puertos de salida (interfaces) están en `domain.port.out`.
- Los puertos de entrada se definen como interfaces en `domain` y se implementan en `application.service`.

## PR instructions
- Formato de título: `[ats-usuarios] <Title>`
- Siempre corre `./gradlew check` antes de hacer commit.
