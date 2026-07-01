# HU-51 — Corregir typo en nombre de interfaz ModuleUseCase

**Como** desarrollador  
**Quiero** renombrar la interfaz `ModuleUserCase` a `ModuleUseCase`  
**Para** corregir el error tipográfico y mantener consistencia en la nomenclatura del proyecto.

## Descripción

El archivo `domain/port/in/ModuleUserCase.java` tiene el nombre mal escrito: `ModuleUserCase` en lugar de `ModuleUseCase` (falta la letra 's'). Aunque el nombre se usa consistentemente en todo el proyecto, es un error tipográfico que debe corregirse para mantener calidad de código.

## Criterios de aceptación

1. El archivo se renombra a `ModuleUseCase.java`.
2. El nombre de la interfaz cambia a `ModuleUseCase`.
3. Todas las referencias a `ModuleUserCase` en el proyecto se actualizan a `ModuleUseCase`.
4. El proyecto compila sin errores.
5. Todos los tests existentes continúan pasando.
