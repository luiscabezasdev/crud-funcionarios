-- =====================================================================
-- 01_esquema.sql · Base de datos del CRUD de funcionarios (PostgreSQL)
-- Caso de estudio: Recursos Humanos, IU Digital de Antioquia
-- Ejecutar desde WSL, en la carpeta del proyecto:
--   psql -h localhost -U crud_user -d funcionarios_db -f sql/01_esquema.sql
-- =====================================================================

-- Borra las tablas si ya existen, para poder ejecutar el script varias veces.
-- Orden: primero las "hijas" (las que tienen claves foráneas), luego las "padres".
DROP TABLE IF EXISTS formacion_academica;
DROP TABLE IF EXISTS grupo_familiar;
DROP TABLE IF EXISTS funcionarios;
DROP TABLE IF EXISTS estado_civil;
DROP TABLE IF EXISTS tipo_documento;

-- ---------------------------------------------------------------------
-- 1. CATÁLOGOS (no apuntan a nadie; las demás tablas apuntan a ellos)
-- ---------------------------------------------------------------------

-- EJEMPLO RESUELTO: léelo línea por línea antes de escribir las tuyas.
CREATE TABLE tipo_documento (
    id_tipo_documento  SERIAL       PRIMARY KEY,     -- SERIAL: PostgreSQL asigna 1, 2, 3... solo
    nombre             VARCHAR(40)  NOT NULL UNIQUE  -- 'Cédula de ciudadanía', 'Pasaporte'... sin repetidos
);

-- TODO 1: escribe la tabla estado_civil (misma forma que tipo_documento).


-- ---------------------------------------------------------------------
-- 2. TABLA PRINCIPAL
-- ---------------------------------------------------------------------

-- TODO 2: escribe la tabla funcionarios con estas columnas:
--   id_funcionario      clave primaria autoincremental
--   id_tipo_documento   entero, obligatorio, CLAVE FORÁNEA -> ¿a qué tabla?
--   numero_documento    texto de hasta 20, obligatorio   (¿por qué texto y no INT? lo viste el 16-oct)
--   nombres             texto de hasta 80, obligatorio
--   apellidos           texto de hasta 80, obligatorio
--   id_estado_civil     entero, obligatorio, CLAVE FORÁNEA -> ¿a qué tabla?
--   sexo                CHAR(1), obligatorio, solo 'M' o 'F'  -> pista: CHECK (sexo IN ('M', 'F'))
--   direccion           texto de hasta 150 (opcional)
--   telefono            texto de hasta 20 (opcional)
--   fecha_nacimiento    DATE, obligatorio
--
-- Al final, antes del paréntesis de cierre, esta restricción (cópiala tal cual):
--   CONSTRAINT uq_funcionario_documento UNIQUE (id_tipo_documento, numero_documento)
--
-- Sintaxis de una clave foránea en PostgreSQL:
--   nombre_columna INT NOT NULL REFERENCES otra_tabla(columna_pk)


-- ---------------------------------------------------------------------
-- 3. TABLAS "HIJAS" DEL FUNCIONARIO (lado "muchos" de la relación)
-- ---------------------------------------------------------------------

-- TODO 3: grupo_familiar
--   id_familiar (PK), id_funcionario (FK), nombres, apellidos, rol ('Hijo', 'Cónyuge'...)
--   La FK termina así:  REFERENCES funcionarios(id_funcionario) ON DELETE CASCADE


-- TODO 4: formacion_academica
--   id_formacion (PK), id_funcionario (FK con ON DELETE CASCADE), universidad, nivel_estudio, titulo

