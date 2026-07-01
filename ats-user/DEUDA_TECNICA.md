# Deuda Técnica — ats-user

> Generado: 2026-06-10
> Proyecto: Spring Boot 4.0.2 / Java 21 / Hexagonal Architecture

---

## 🔴 Crítico

| # | Deuda | Archivo | Línea(s) |
|---|-------|---------|----------|
| C1 | JWT secret hardcodeado en código fuente | `src/main/resources/application.yml` | 22 |
| C2 | Credenciales de BD hardcodeadas | `src/main/resources/application.yml` | 5-7 |
| C3 | Violación de arquitectura hexagonal: `AuthController` inyecta `UserJpaRepository` directamente, saltando puertos y casos de uso | `src/main/java/com/ats/user/infrastructure/in/web/controller/auth/AuthController.java` | 40, 71-101 |
| C4 | Falta `.gitignore` — build artifacts (`build/`, `.gradle/`, `bin/`) versionables | raíz del proyecto | — |

---

## 🟠 Alta

| # | Deuda | Archivo | Línea(s) |
|---|-------|---------|----------|
| H1 | Sin `@Transactional` en ningún servicio — riesgo de `LazyInitializationException` y sin garantía de rollback | `UserService.java`, `RoleService.java`, `MenuService.java`, `ModuleService.java`, `PermissionService.java` | Todos |
| H2 | Typo en nombre de interfaz: `ModuleUserCase` → debe ser `ModuleUseCase` | `ModuleUserCase.java`, `ModuleController.java`, `ModuleService.java` | Todos |
| H3 | Mapeo inconsistente: Permission/Menu/Module adapters usan `toDomain()` manual en vez de MapStruct (User/Role sí usan mapper) | `PermissionRepositoryAdapter.java`, `MenuRepositoryAdapter.java`, `ModuleRepositoryAdapter.java` | Varios |
| H4 | Estrategia de autorización inconsistente: `/api/users/**` usa `hasRole("ADMIN")` mientras que `/api/roles/**` usa authorities granulares (`ROLE_CREATE`, `ROLE_READ`, etc.) | `SecurityConfig.java` | 56 |
| H5 | Faltan tests funcionales de controladores Menu, Module, Role, Permission (solo existen tests de seguridad) | `src/test/.../controller/` | — |
| H6 | Faltan tests de adapters para User, Menu, Module, Permission (solo existe RoleRepositoryAdapterTest) | `src/test/.../adapter/` | — |

---

## 🟡 Media

| # | Deuda | Archivo | Línea(s) |
|---|-------|---------|----------|
| M1 | Inyección por `@Value` en `AuthController` en vez de constructor injection | `AuthController.java` | 42-43 |
| M2 | Handler genérico de excepciones traga errores reales ("Unexpected error") sin detalle | `GlobalExceptionHandler.java` | 167-172 |
| M3 | Sin logging de request/response (no hay filter, interceptor ni AOP) | — | — |
| M4 | Sin rate limiting en `/api/auth/login` — sin protección contra brute-force | — | — |
| M5 | `ddl-auto: update` en configuración — peligro de pérdida de datos en producción | `application.yml` | 13 |
| M6 | Comentario poco profesional: `# OJO: ddl, no dll` | `application.yml` | 13 |
| M7 | Scripts SQL seed no se ejecutan automáticamente por falta de `spring.sql.init.mode` | `application.yml` | — |
| M8 | Null checks innecesarios en `PermissionRepositoryAdapter.toDomain()` — `module` es `optional = false` en la entidad | `PermissionRepositoryAdapter.java` | 54-56 |
| M9 | Sin caché para consultas de permisos/roles — golpean BD cada vez | — | — |
| M10 | `RoleMapper` ignora `createdBy`/`updatedBy` al mapear a dominio | `RoleMapper.java` | 18-19 |
| M11 | `delete` en `UserController` retorna 200 en vez de 204 No Content (inconsistente con otros endpoints) | `UserController.java` | 142-145 |
| M12 | Duplicación de lógica en adapters — varios con el mismo patrón manual de mapeo | Varios adapters | — |

---

## 🟢 Baja

| # | Deuda | Archivo | Línea(s) |
|---|-------|---------|----------|
| L1 | Typo en dependencia: `spring-boot-starter-webmvc-test` (debe ser `spring-boot-starter-test`) | `build.gradle` | 64-65 |
| L2 | Falta `Dockerfile` para la aplicación (solo PostgreSQL contenedorizada) | raíz del proyecto | — |
| L3 | Sin configuración de CI/CD (GitHub Actions, Jenkins, etc.) | raíz del proyecto | — |
| L4 | Sin configuraciones por entorno (`application-dev.yml`, `application-prod.yml`) | `src/main/resources/` | — |
| L5 | CORS origins hardcodeados a localhost solamente — no configurables via properties | `CorsConfig.java` | 17-24 |
| L6 | Postman collection solo cubre CRUD de roles — faltan user, module, menu, permission, auth | `postman/` | — |
| L7 | `ModuleService.deleteModuleById` no cascada desactivación de menús asociados | `ModuleService.java` | 77-83 |
| L8 | Actuator incluido como dependencia pero sin `management.endpoints.web.exposure.include` configurado | `application.yml` | — |
| L9 | `HELP.md` boilerplate de Spring Initializr no eliminado | raíz del proyecto | — |
| L10 | Configuración frágil en tests: `DataJpaRepositoriesAutoConfiguration` excluido pero se inyectan JPA repos con `@MockitoBean` | `UserControllerTest.java` | 42-43 |

---

## Notas

- Pendiente: definir criterio de aceptación para cada ítem antes de resolver.
- Ítems críticos (C1-C4) deberían resolverse antes del próximo deploy a producción.
