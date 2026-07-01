# HU-53 — Eliminar consulta redundante en UserService.delete()

**Como** desarrollador  
**Quiero** eliminar la llamada `existsById()` redundante en `UserService.delete()`  
**Para** reducir a la mitad las consultas a la base de datos en esta operación.

## Descripción

En `UserService.java:114-119`:
```java
public User delete(Long id) {
    if (!userRepository.existsById(id)) {
        throw new UserNotFoundException("User not found: " + id);
    }
    return userRepository.delete(id);
}
```

El método `delete(id)` del adaptador también verifica la existencia mediante `findById()` y lanza `UserNotFoundException` si no encuentra el usuario. La llamada `existsById()` previa es redundante y genera dos consultas SQL donde una es suficiente.

## Criterios de aceptación

1. Se elimina la llamada `existsById()` de `UserService.delete()`.
2. Se mantiene el comportamiento: si el usuario no existe, se lanza `UserNotFoundException`.
3. Todos los tests existentes continúan pasando.
