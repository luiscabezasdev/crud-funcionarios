package co.edu.iudigital.funcionarios.excepciones;

/**
 * Error de acceso a datos con un mensaje apto para el usuario.
 * El detalle técnico (la SQLException original) viaja como "causa" y se escribe en el log,
 * nunca en la pantalla.
 */
public class DAOException extends Exception {

    public DAOException(String mensaje) {
        super(mensaje);
    }

    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
