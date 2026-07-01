# HU-49 — Corregir dependencias de test duplicadas en build.gradle

**Como** desarrollador  
**Quiero** eliminar dependencias de test inválidas o duplicadas del `build.gradle`  
**Para** que el proyecto compile correctamente sin artefactos que no existen.

## Descripción

En `build.gradle:64-65` existen dos dependencias:
```groovy
testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
testImplementation 'org.springframework.boot:spring-boot-webmvc-test'
```

Estos artefactos no son estándar de Spring Boot. La dependencia `spring-boot-starter-test` ya incluye MockMvc y todo lo necesario para tests web. La importación `@AutoConfigureMockMvc` que usan los tests proviene de `spring-boot-starter-test`.

## Criterios de aceptación

1. Se eliminan las dependencias `spring-boot-starter-webmvc-test` y `spring-boot-webmvc-test`.
2. Todos los tests existentes compilan y pasan sin cambios.
3. La anotación `@AutoConfigureMockMvc` sigue funcionando correctamente (provista por `spring-boot-starter-test`).
