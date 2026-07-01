# HU-55 — Reemplazar orElseThrow sin mensaje en MenuRepositoryAdapter

**Como** desarrollador  
**Quiero** reemplazar `orElseThrow()` sin argumentos por una excepción con mensaje descriptivo  
**Para** facilitar el debugging cuando no se encuentra un módulo asociado a un menú.

## Descripción

En `MenuRepositoryAdapter.java:35`:
```java
entity.setModule(moduleJpaRepository.findById(menu.getModuleId()).orElseThrow());
```

Si el `moduleId` no existe, se lanza `java.util.NoSuchElementException` sin mensaje, lo que dificulta identificar la causa del error. Debe lanzarse `ModuleNotFoundException` con un mensaje descriptivo.

## Criterios de aceptación

1. `MenuRepositoryAdapter.save()` lanza `ModuleNotFoundException` con mensaje si el `moduleId` no existe.
2. `MenuRepositoryAdapter.update()` también maneja este caso si se cambia el módulo.
3. El `GlobalExceptionHandler` ya tiene el handler para `ModuleNotFoundException` que responde **404 Not Found**.
4. Todos los tests existentes continúan pasando.
