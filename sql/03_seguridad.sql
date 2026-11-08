-- =====================================================================
-- 03_seguridad.sql · Autenticación y autorización (roles y usuarios)
-- Ejecutar DESPUÉS de 01_esquema.sql:
--   psql -h localhost -p 5433 -U crud_user -d funcionarios_db -f sql/03_seguridad.sql
-- =====================================================================

DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS roles;

-- Catálogo de roles (mismo patrón que estado_civil y tipo_documento)
CREATE TABLE roles (
    id_rol  SERIAL       PRIMARY KEY,
    nombre  VARCHAR(20)  NOT NULL UNIQUE       -- 'ADMIN', 'CONSULTA' (coinciden con el enum Rol de Java)
);

-- Un rol tiene muchos usuarios: la FK va en usuarios (lado "muchos").
-- ESTA FK ES EL ENLACE entre autenticación y autorización:
-- el login identifica al usuario; su id_rol dice qué puede hacer.
CREATE TABLE usuarios (
    id_usuario         SERIAL        PRIMARY KEY,
    id_rol             INT           NOT NULL REFERENCES roles(id_rol),
    username           VARCHAR(30)   NOT NULL UNIQUE,
    clave_hash         VARCHAR(200)  NOT NULL,      -- NUNCA la clave: pbkdf2$iteraciones$salt$hash
    intentos_fallidos  INT           NOT NULL DEFAULT 0,
    bloqueado_hasta    TIMESTAMPTZ,                 -- NULL = no está bloqueado
    activo             BOOLEAN       NOT NULL DEFAULT TRUE
);

INSERT INTO roles (nombre) VALUES ('ADMIN'), ('CONSULTA');   -- id 1 y 2

-- Usuarios DE DEMOSTRACIÓN (contraseñas de ejemplo; en un sistema real se cambian al primer ingreso)
--   admin     / Admin#2026     -> rol ADMIN
--   consulta  / Consulta#2026  -> rol CONSULTA
INSERT INTO usuarios (id_rol, username, clave_hash) VALUES
    (1, 'admin',    'pbkdf2$120000$13o2xOlUMc9GEB7uB4dSug==$ewLRcXhk/aLQM1BUA2Yb6jImgKYHFlYITmMnYb+1ti8='),
    (2, 'consulta', 'pbkdf2$120000$JU0LfaZIzF8lgaKN0nHABA==$03Qu/MPbr5omPUvYwGXzE/5jeKCu7JH3KLND1iBqYk4=');

-- Verificación: muestra usuario y rol (la clave NO aparece, solo el comienzo de su hash)
SELECT u.username, r.nombre AS rol, LEFT(u.clave_hash, 25) || '...' AS clave_hash
FROM usuarios u JOIN roles r ON r.id_rol = u.id_rol ORDER BY u.id_usuario;
