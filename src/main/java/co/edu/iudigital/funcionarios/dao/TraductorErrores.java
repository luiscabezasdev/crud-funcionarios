package co.edu.iudigital.funcionarios.dao;

import co.edu.iudigital.funcionarios.excepciones.DAOException;
import co.edu.iudigital.funcionarios.excepciones.RegistroDuplicadoException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Convierte una SQLException (técnica) en una DAOException (mensaje para el usuario).
 * Regla de seguridad: el detalle técnico va al log; al usuario, un mensaje claro y genérico.
 * Se decide por el SQLState, un código estándar de 5 caracteres que trae toda SQLException.
 */
final class TraductorErrores {

    private static final Logger LOG = Logger.getLogger(TraductorErrores.class.getName());

    private TraductorErrores() {
    }

    static DAOException traducir(String accion, SQLException e) {
        String estado = e.getSQLState();
        LOG.log(Level.SEVERE, "Error al " + accion + " | SQLState=" + estado, e);

        if ("23505".equals(estado)) {          // unique_violation
            return new RegistroDuplicadoException(
                "Ya existe un funcionario con ese tipo y número de documento.", e);
        }
        if ("23503".equals(estado)) {          // foreign_key_violation
            return new DAOException(
                "El tipo de documento o el estado civil elegido no existe.", e);
        }
        if ("23514".equals(estado) || "23502".equals(estado)) {   // check_violation / not_null_violation
            return new DAOException(
                "Algún dato no cumple las reglas de la base de datos. Revisa el formulario.", e);
        }
        if ("28P01".equals(estado) || "28000".equals(estado)) {   // autenticación
            return new DAOException(
                "No se pudo iniciar sesión en la base de datos. Revisa db.properties.", e);
        }
        if (estado != null && estado.startsWith("08")) {          // errores de conexión
            return new DAOException(
                "No hay conexión con la base de datos. Verifica que PostgreSQL esté encendido.", e);
        }
        return new DAOException("No se pudo " + accion + ". Intenta de nuevo.", e);
    }
}
