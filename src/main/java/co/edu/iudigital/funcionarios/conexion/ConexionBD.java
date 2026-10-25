package co.edu.iudigital.funcionarios.conexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Entrega conexiones a PostgreSQL.
 * Los datos de acceso se leen de src/main/resources/db.properties
 * (ese archivo NO se sube a GitHub: está en .gitignore).
 */
public final class ConexionBD {

    private static final Properties CONFIG = cargarConfiguracion();

    private ConexionBD() {
        // Clase de utilidad: no se crean objetos de ella.
    }

    /** Lee db.properties una sola vez, cuando la clase se usa por primera vez. */
    private static Properties cargarConfiguracion() {
        Properties props = new Properties();
        try (InputStream in = ConexionBD.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                    "No se encontró db.properties. Copia db.properties.example como db.properties y pon tu contraseña.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer db.properties", e);
        }
        return props;
    }

    /**
     * Abre una conexión nueva. Quien la pide debe cerrarla (usar try-with-resources).
     * @throws SQLException si la base no responde o el usuario/contraseña son incorrectos
     */
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(
            CONFIG.getProperty("db.url"),
            CONFIG.getProperty("db.usuario"),
            CONFIG.getProperty("db.clave"));
    }
}
