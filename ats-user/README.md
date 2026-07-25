# ATS Usuarios

Microservicio de autenticacion y gestion de usuarios con roles, permisos, modulos y menus de navegacion.

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.2-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?logo=gradle&logoColor=white)](https://gradle.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](#)

---

## Tabla de Contenidos

- [Descripcion General](#descripcion-general)
- [Stack Tecnologico](#stack-tecnologico)
- [Arquitectura](#arquitectura)
- [Prerrequisitos](#prerrequisitos)
- [Inicio Rapido](#inicio-rapido)
- [Base de Datos](#base-de-datos)
- [Swagger - OpenAPI](#swagger--openapi)
- [Docker](#docker)


---

## Descripcion General

**ATS Usuarios** es un microservicio REST construido con Spring Boot 4 que gestiona:

- **Autenticacion** con JWT (JSON Web Tokens)
- **Usuarios** con roles asignados (CRUD completo, paginacion, busqueda)
- **Roles** con permisos granulares por autoridad
- **Permisos** organizados por modulos (RESOURCE + ACTION + SCOPE)
- **Modulos** del sistema
- **Menus** de navegacion con permisos requeridos

Sigue una **arquitectura hexagonal** (puertos y adaptadores) que separa la logica de dominio de la infraestructura.

---

## Stack Tecnologico

| Capa | Tecnologia | Version |
|------|-----------|---------|
| Lenguaje | Java | 21 |
| Framework | Spring Boot | 4.0.2 |
| Seguridad | Spring Security + JWT | JJWT 0.12.6 |
| Persistencia | Spring Data JPA + Hibernate | - |
| Base de datos | PostgreSQL | 16 |
| Migraciones | Flyway | - |
| Mapeo | MapStruct | 1.6.3 |
| Cache | Caffeine | - |
| API Docs | SpringDoc OpenAPI | 2.8.13 |
| Metricas | Micrometer + Prometheus | - |
| Validacion | Spring Validation | - |
| Build | Gradle | Wrapper |
| Contenedores | Docker + Docker Compose | - |

---

## Arquitectura

El proyecto sigue el patron **Hexagonal (Puertos y Adaptadores)**:

```
com.ats.user
├── domain/                  # Capa de dominio (nucleo)
│   ├── model/               # Entidades de negocio (User, Role, Permission, Module, Menu)
│   ├── port/
│   │   ├── in/              # Puertos de entrada (Use Cases)
│   │   └── out/             # Puertos de salida (Repository Ports)
│   ├── service/             # Servicios de dominio
│   └── exception/           # Excepciones de negocio
│
├── application/             # Capa de aplicacion
│   └── service/             # Implementacion de casos de uso
│
└── infrastructure/          # Capa de infraestructura
    ├── config/              # Configuracion (OpenAPI, etc.)
    └── adapter/
        ├── in/web/          # Adaptadores de entrada
        │   ├── controller/  # Controladores REST
        │   ├── dto/         # Requests y Responses
        │   ├── security/    # JWT, CORS, Filtros
        │   └── exception/   # Manejador global de excepciones
        └── out/persistence/ # Adaptadores de salida
            ├── adapter/     # Implementacion de puertos de repositorio
            ├── entity/      # Entidades JPA
            ├── mapper/      # Mappers MapStruct
            ├── repository/  # Interfaces JPA Repository
            └── specification/ # Specifications para busquedas
```

---

## Prerrequisitos

- **Java 21** (JDK)
- **Docker** y **Docker Compose**
- **Gradle** (o usar el wrapper `./gradlew` incluido)

---

## Inicio Rapido

### 1. Levantar la base de datos

```bash
docker compose up -d postgres
```

### 2. Ejecutar la aplicacion

```bash
./gradlew bootRun
```

La API estara disponible en: **http://localhost:8083**

### 3. Probar el login

```bash
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ats.local","password":"Admin123"}'
```

---

## Base de Datos

### Migraciones (Flyway)

Las migraciones se encuentran en `src/main/resources/db/migration/`:

| Archivo | Descripcion |
|---------|------------|
| `V1__init.sql` | Crea las 7 tablas: modules, permissions, menus, roles, role_permissions, users, user_roles |
| `V2__add_audit_columns_to_roles.sql` | Agrega columnas `created_by` y `updated_by` a la tabla roles |

---

## Swagger - OpenAPI

La documentacion interactiva de la API esta disponible en:

- **Swagger UI:** http://localhost:8083/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8083/v3/api-docs

Todos los endpoints estan documentados con anotaciones `@Operation` y `@ApiResponses` de Swagger. El esquema de seguridad Bearer JWT esta configurado automaticamente.

---

## Docker

### Docker Compose

Ejecutar la aplicacion completa (BD + App):

```bash
docker compose up -d
```

Servicios:
- **postgres** - PostgreSQL 16 en puerto `5432`
- **ats-user** - Aplicacion Spring Boot en puerto `8083`

---
## Licencia

Proyecto privado - ATS Team (Pablo Gallegos)
