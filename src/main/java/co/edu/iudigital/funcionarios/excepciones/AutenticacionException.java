package co.edu.iudigital.funcionarios.excepciones;

/**
 * Falló la autenticación (usuario inexistente, clave mala o cuenta bloqueada).
 * El mensaje es genérico a propósito: no revela si el usuario existe.
 */
public class AutenticacionException extends Exception {

    private static final long serialVersionUID = 1L;

    public AutenticacionException(String mensaje) {
        super(mensaje);
    }
}
