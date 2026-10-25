-- =====================================================================
-- 01_esquema.sql · Base de datos del CRUD de funcionarios (PostgreSQL)
-- Caso de estudio: Recursos Humanos, IU Digital de Antioquia
-- Ejecutar desde WSL, en la carpeta del proyecto:
--   psql -h localhost -U crud_user -d funcionarios_db -f sql/01_esquema.sql
-- =====================================================================

-- Borra las tablas si ya existen, para poder ejecutar el script varias veces.
-- Orden: primero las "hijas" (tienen claves foráneas), luego las "padres".
DROP TABLE IF EXISTS formacion_academica;
DROP TABLE IF EXISTS grupo_familiar;
DROP TABLE IF EXISTS funcionarios;
DROP TABLE IF EXISTS estado_civil;
DROP TABLE IF EXISTS tipo_documento;

-- ---------------------------------------------------------------------
-- 1. CATÁLOGOS: listas cortas de valores posibles. No apuntan a nadie;
--    la tabla funcionarios apunta a ellos. Se crean PRIMERO.
-- ---------------------------------------------------------------------
CREATE TABLE tipo_documento (
    id_tipo_documento  SERIAL       PRIMARY KEY,     -- SERIAL: PostgreSQL asigna 1, 2, 3... solo
    nombre             VARCHAR(40)  NOT NULL UNIQUE  -- 'Cédula de ciudadanía', 'Pasaporte'... sin repetidos
);

CREATE TABLE estado_civil (
    id_estado_civil    SERIAL       PRIMARY KEY,
    nombre             VARCHAR(40)  NOT NULL UNIQUE  -- 'Soltero(a)', 'Casado(a)'... sin repetidos
);

-- ---------------------------------------------------------------------
-- 2. TABLA PRINCIPAL: muchos funcionarios -> un tipo de documento
--                     muchos funcionarios -> un estado civil
-- ---------------------------------------------------------------------
CREATE TABLE funcionarios (
    id_funcionario     SERIAL        PRIMARY KEY,
    id_tipo_documento  INT           NOT NULL REFERENCES tipo_documento(id_tipo_documento),
    numero_documento   VARCHAR(20)   NOT NULL,       -- texto: puede tener ceros iniciales o letras (pasaporte)
    nombres            VARCHAR(80)   NOT NULL,
    apellidos          VARCHAR(80)   NOT NULL,
    id_estado_civil    INT           NOT NULL REFERENCES estado_civil(id_estado_civil),
    sexo               CHAR(1)       NOT NULL CHECK (sexo IN ('M', 'F')),
    direccion          VARCHAR(150),
    telefono           VARCHAR(20),
    fecha_nacimiento   DATE          NOT NULL,
    -- Una misma persona no puede registrarse dos veces con el mismo documento
    CONSTRAINT uq_funcionario_documento UNIQUE (id_tipo_documento, numero_documento)
);

-- ---------------------------------------------------------------------
-- 3. TABLAS HIJAS: un funcionario -> muchos familiares / muchos estudios.
--    La clave foránea va aquí, en el lado "muchos".
--    ON DELETE CASCADE: si se elimina el funcionario, se eliminan sus filas aquí.
-- ---------------------------------------------------------------------
CREATE TABLE grupo_familiar (
    id_familiar        SERIAL        PRIMARY KEY,
    id_funcionario     INT           NOT NULL REFERENCES funcionarios(id_funcionario) ON DELETE CASCADE,
    nombres            VARCHAR(80)   NOT NULL,
    apellidos          VARCHAR(80)   NOT NULL,
    rol                VARCHAR(30)   NOT NULL        -- 'Hijo(a)', 'Cónyuge', 'Padre', 'Madre'...
);

CREATE TABLE formacion_academica (
    id_formacion       SERIAL        PRIMARY KEY,
    id_funcionario     INT           NOT NULL REFERENCES funcionarios(id_funcionario) ON DELETE CASCADE,
    universidad        VARCHAR(120)  NOT NULL,
    nivel_estudio      VARCHAR(40)   NOT NULL,       -- 'Pregrado', 'Especialización', 'Maestría'...
    titulo             VARCHAR(120)  NOT NULL
);
