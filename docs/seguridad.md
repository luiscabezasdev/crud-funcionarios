# Autenticación y autorización

## Las dos ideas

| | Pregunta | En la aplicación |
|---|---|---|
| **Autenticación** | ¿Quién eres? | `LoginFrame` → `ServicioAuth` (usuario y contraseña) |
| **Autorización** | ¿Qué puedes hacer? | `Sesion` + `Rol` + `FuncionarioDAOSeguro` |

**El enlace entre las dos es la identidad:** la autenticación llena la `Sesion` con el usuario; la autorización consulta el rol de ese usuario (`usuarios.id_rol → roles`).

## Modelo

`roles (id_rol, nombre)` 1 ──< `usuarios (id_usuario, id_rol FK, username, clave_hash, intentos_fallidos, bloqueado_hasta, activo)`

## Autenticación

- La contraseña **nunca** se guarda: se guarda `pbkdf2$120000$salt$hash` (PBKDF2-HMAC-SHA256, incluido en Java, salt aleatorio de 16 bytes por usuario).
- El login recalcula el hash con el salt guardado y lo compara en tiempo constante (`MessageDigest.isEqual`).
- Mensaje de error **genérico**: "Usuario o contraseña incorrectos." No revela si el usuario existe; si no existe se calcula igual un hash de relleno para que tarde lo mismo.
- 3 intentos fallidos bloquean la cuenta 5 minutos (frena la fuerza bruta). El contador se actualiza con un solo `UPDATE` atómico.
- Todas las consultas usan `PreparedStatement`: el login no es vulnerable a `admin'--`.

## Autorización (RBAC)

| Rol | LEER | CREAR | EDITAR | ELIMINAR |
|---|:-:|:-:|:-:|:-:|
| ADMIN | sí | sí | sí | sí |
| CONSULTA | sí | no | no | no |

Mínimo privilegio: cada rol solo tiene lo que necesita.

**Defensa en profundidad (dos capas):**
1. La ventana desactiva los botones según el rol (comodidad, no seguridad).
2. `FuncionarioDAOSeguro` (patrón **Decorator**) envuelve al DAO real y exige el permiso en cada operación; si el rol no lo tiene lanza `AccesoDenegadoException` y el DAO real no se ejecuta.

## Excepciones nuevas

| Excepción | Cuándo |
|---|---|
| `AutenticacionException` | Usuario o clave incorrectos, o cuenta bloqueada |
| `AccesoDenegadoException` (hereda de `DAOException`) | El rol no tiene el permiso pedido |

## Usuarios de demostración

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `Admin#2026` | ADMIN |
| `consulta` | `Consulta#2026` | CONSULTA |

Son contraseñas de ejemplo solo para la demostración.

## Mejoras posibles (no implementadas)

Tabla de auditoría (quién creó, editó o eliminó y cuándo), cambio obligatorio de clave al primer ingreso y permisos de rol guardados en la base de datos en vez de en el enum.
