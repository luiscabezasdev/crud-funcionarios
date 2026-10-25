# CRUD de funcionarios · Patrón DAO y manejo de excepciones en Java

Evidencia de Aprendizaje 1 (IU Digital de Antioquia). Aplicación de escritorio en **Java Swing** que permite **listar, crear, editar y eliminar** funcionarios del área de Recursos Humanos, con base de datos **PostgreSQL**, **patrón DAO** y **excepciones propias**.

**Autor:** Luis Alberto Cabezas Sánchez
**Video explicativo:** _(enlace de YouTube)_

## Tecnologías

- Java 17+ (probado con JDK 21) · Swing
- PostgreSQL 12+ · driver JDBC `org.postgresql:postgresql`
- Maven · NetBeans 24

## Estructura

```
sql/
  01_esquema.sql      creación de las 5 tablas con claves primarias y foráneas
  02_poblado.sql      datos iniciales (catálogos y funcionarios de ejemplo)
docs/
  modelo.md           modelo relacional (diagrama y relaciones)
src/main/java/co/edu/iudigital/funcionarios/
  Main.java           abre la ventana
  PruebaConsola.java  prueba del CRUD y las excepciones sin interfaz
  conexion/           ConexionBD: abre conexiones JDBC leyendo db.properties
  modelo/             Funcionario, TipoDocumento, EstadoCivil
  dao/                interfaces DAO + implementaciones JDBC + TraductorErrores
  excepciones/        DAOException, RegistroDuplicadoException, ValidacionException
  util/               ValidadorFuncionario (validación con lista de permitidos)
  vista/              FuncionarioFrame (ventana Swing)
src/main/resources/
  db.properties.example   plantilla de credenciales (db.properties no se sube)
```

## Cómo ejecutarlo

1. **Crear el usuario y la base de datos** (como administrador de PostgreSQL):
   ```sql
   CREATE USER crud_user WITH PASSWORD 'tu_clave';
   CREATE DATABASE funcionarios_db OWNER crud_user;
   ```
2. **Crear las tablas y poblarlas:**
   ```bash
   psql -h localhost -U crud_user -d funcionarios_db -f sql/01_esquema.sql
   psql -h localhost -U crud_user -d funcionarios_db -f sql/02_poblado.sql
   ```
3. **Configurar la conexión:** copiar `src/main/resources/db.properties.example` como `db.properties` y poner la URL (puerto), el usuario y la clave.
4. **Ejecutar:** abrir la carpeta en NetBeans (proyecto Maven) y presionar F6, o `mvn compile exec:java -Dexec.mainClass=co.edu.iudigital.funcionarios.Main`.

## Patrón DAO

La ventana no contiene SQL. Habla con las **interfaces** `FuncionarioDAO` y `CatalogoDAO`; las clases `FuncionarioDAOImpl` y `CatalogoDAOImpl` son las únicas que conocen el SQL y JDBC. Si se cambia el motor de base de datos, solo cambian las implementaciones.

```
FuncionarioFrame (vista) ──► FuncionarioDAO (interfaz) ──► FuncionarioDAOImpl (JDBC) ──► PostgreSQL
```

Todas las consultas usan `PreparedStatement` con parámetros `?`, lo que evita la inyección SQL, y `try-with-resources`, que cierra conexiones, sentencias y resultados aunque ocurra un error.

## Manejo de excepciones

| Excepción | Cuándo ocurre | Qué ve el usuario |
|---|---|---|
| `ValidacionException` | Antes de ir a la base: campo vacío, nombre con números, fecha mal escrita, menor de edad | Lista de errores del formulario |
| `RegistroDuplicadoException` (hereda de `DAOException`) | PostgreSQL rechaza un documento repetido (SQLState `23505`) | "Ya existe un funcionario con ese tipo y número de documento." |
| `DAOException` | Cualquier otro error de base de datos: sin conexión, clave incorrecta, clave foránea inválida | Mensaje claro y genérico |

`TraductorErrores` convierte cada `SQLException` en una de estas excepciones según su **SQLState**. El detalle técnico se escribe en el log y al usuario solo le llega un mensaje comprensible, sin exponer información interna de la base de datos.
