# HU-63 — Agregar stages de Docker build y deploy al Jenkinsfile

**Como** DevOps  
**Quiero** que el pipeline de Jenkins construya una imagen Docker y la publique en un registry  
**Para** que el microservicio pueda desplegarse automáticamente en los entornos correspondientes.

## Descripción

El Jenkinsfile actual solo ejecuta tests, compila y archiva el JAR. No hay stages para construir una imagen Docker, pushearla a un registry (Docker Hub, ECR, etc.) ni desplegarla en ningún entorno.

## Criterios de aceptación

1. El pipeline tiene un stage `Docker Build` que construye la imagen usando el `Dockerfile` del proyecto.
2. El pipeline tiene un stage `Docker Push` que sube la imagen al registry configurado.
3. El pipeline tiene un stage `Deploy` que despliega la imagen en el entorno correspondiente.
4. La imagen Docker se taggea con el número de build y con `latest`.
5. Los stages de Docker son opcionales y se pueden saltar en builds de PR.
