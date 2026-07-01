# HU-58 — Agregar tests unitarios para DefaultPasswordPolicy y PasswordHasherAdapter

**Como** desarrollador  
**Quiero** agregar tests unitarios para la política de contraseñas y el adaptador de hasheo  
**Para** garantizar que las validaciones de seguridad de contraseñas funcionan correctamente.

## Descripción

No existen tests para `DefaultPasswordPolicy.validate()` ni para `PasswordHasherAdapter.encode()`. La política de contraseñas valida que la contraseña tenga al menos una letra y un dígito. El adaptador delega en `BCryptPasswordEncoder`. Deben existir tests unitarios que verifiquen estos comportamientos.

## Criterios de aceptación

1. `DefaultPasswordPolicyTest` cubre: contraseña válida (letra + dígito), contraseña sin letra → lanza excepción, contraseña sin dígito → lanza excepción.
2. `PasswordHasherAdapterTest` cubre: encode retorna hash no nulo, encode retorna hash diferente del texto original, dos llamadas con misma contraseña retornan hashes diferentes (BCrypt es salteado).
