-- =====================================================================
-- 02_poblado.sql · Datos iniciales para que la aplicación arranque
-- Ejecutar DESPUÉS de 01_esquema.sql (que deja las tablas vacías y los
-- contadores SERIAL en 1):
--   psql -h localhost -p 5433 -U crud_user -d funcionarios_db -f sql/02_poblado.sql
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. CATÁLOGOS: sin estos datos, el formulario no tendría opciones
--    para elegir y ningún funcionario podría guardarse (sus FK fallarían).
-- ---------------------------------------------------------------------
INSERT INTO tipo_documento (nombre) VALUES
    ('Cédula de ciudadanía'),   -- id 1
    ('Cédula de extranjería'),  -- id 2
    ('Pasaporte'),              -- id 3
    ('Tarjeta de identidad');   -- id 4

INSERT INTO estado_civil (nombre) VALUES
    ('Soltero(a)'),             -- id 1
    ('Casado(a)'),              -- id 2
    ('Unión libre'),            -- id 3
    ('Divorciado(a)'),          -- id 4
    ('Viudo(a)');               -- id 5

-- ---------------------------------------------------------------------
-- 2. FUNCIONARIOS
--    Orden de columnas: tipo doc, número, nombres, apellidos, estado civil,
--                       sexo, dirección, teléfono, fecha de nacimiento
-- ---------------------------------------------------------------------

-- EJEMPLO RESUELTO: funcionario id 1 (Cédula, Casada)
INSERT INTO funcionarios (id_tipo_documento, numero_documento, nombres, apellidos,
                          id_estado_civil, sexo, direccion, telefono, fecha_nacimiento)
VALUES (1, '1036654321', 'Ana María', 'Restrepo Gómez',
        2, 'F', 'Calle 50 # 45-20, Medellín', '3001234567', '1988-03-14');

-- TODO 5: agrega 2 funcionarios más (serán el id 2 y el id 3).
--   Uno con Pasaporte (id 3) y estado civil Soltero(a) (id 1).
--   Otro con Cédula (id 1) y el estado civil que quieras.
--   Las fechas van como texto 'AAAA-MM-DD'.


-- ---------------------------------------------------------------------
-- 3. TABLAS HIJAS: cada fila dice a qué funcionario pertenece (id_funcionario)
-- ---------------------------------------------------------------------

-- EJEMPLO RESUELTO: los 2 hijos y la formación de Ana María (id_funcionario = 1)
INSERT INTO grupo_familiar (id_funcionario, nombres, apellidos, rol) VALUES
    (1, 'Samuel',  'Pérez Restrepo', 'Hijo'),
    (1, 'Valeria', 'Pérez Restrepo', 'Hija');

INSERT INTO formacion_academica (id_funcionario, universidad, nivel_estudio, titulo) VALUES
    (1, 'IU Digital de Antioquia', 'Pregrado', 'Tecnología en Desarrollo de Software');

-- TODO 6: agrega 1 familiar y 1 estudio para cada uno de tus 2 funcionarios.

INSERT INTO funcionarios (id_tipo_documento, numero_documento, nombres, apellidos,
                          id_estado_civil, sexo, direccion, telefono, fecha_nacimiento)
VALUES (1, '1025478541', 'Luis Enrique', 'Sanchez Suarez',
        1, 'M', 'Carrera 65 # 15-18, Medellín', '3205875412', '1986-08-26');


INSERT INTO funcionarios (id_tipo_documento, numero_documento, nombres, apellidos,
                          id_estado_civil, sexo, direccion, telefono, fecha_nacimiento)
VALUES (1, '1147524514', 'Maria del Carmen', 'Ortiz Diaz',
        1, 'F', 'Diagonal 23 # 116 - 20, San Antonio de Prado', '3118752142', '2000-01-16');

INSERT INTO grupo_familiar (id_funcionario, nombres, apellidos, rol) VALUES
    (2, 'Humberto',  'Sanchez Suarez', 'Hermano'),
    (3, 'Julio', 'Diaz Ortiz', 'Conyugue');

INSERT INTO formacion_academica (id_funcionario, universidad, nivel_estudio, titulo) VALUES
    (2, 'IU Digital de Antioquia', 'Pregrado', 'Administracion de Empresas'),
    (3, 'Universidad de Antioquia', 'Pregrado', 'Contaduría Pública');

-- ---------------------------------------------------------------------
-- 4. VERIFICACIÓN: une las tablas por sus claves y muestra los nombres
--    (no los números) del catálogo. Es lo mismo que hará la aplicación.
-- ---------------------------------------------------------------------
SELECT f.id_funcionario, td.nombre AS tipo_doc, f.numero_documento,
       f.nombres, f.apellidos, ec.nombre AS estado_civil,
       (SELECT COUNT(*) FROM grupo_familiar g WHERE g.id_funcionario = f.id_funcionario) AS familiares
FROM funcionarios f
JOIN tipo_documento td ON td.id_tipo_documento = f.id_tipo_documento
JOIN estado_civil  ec ON ec.id_estado_civil   = f.id_estado_civil
ORDER BY f.id_funcionario;
