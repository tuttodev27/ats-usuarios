# HU-64 — Eliminar stage BootJar redundante del Jenkinsfile

**Como** desarrollador  
**Quiero** eliminar el stage `BootJar` del Jenkinsfile  
**Para** reducir el tiempo de build eliminando ejecuciones redundantes de Gradle.

## Descripción

El Jenkinsfile tiene dos stages consecutivos: `Build` (ejecuta `./gradlew clean build`) y `BootJar` (ejecuta `./gradlew bootJar`). Dado que `clean build` ya incluye `bootJar`, el segundo stage duplica el tiempo de compilación.

## Criterios de aceptación

1. Se elimina el stage `BootJar` del Jenkinsfile.
2. El stage `Build` existente produce el JAR ejecutable (ya lo hace porque `build` incluye `bootJar`).
3. El artefacto JAR sigue estando disponible para el stage `Archive`.
