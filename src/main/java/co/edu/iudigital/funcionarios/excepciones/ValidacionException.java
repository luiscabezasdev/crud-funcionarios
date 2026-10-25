package co.edu.iudigital.funcionarios.excepciones;

/**
 * Datos del formulario que no cumplen las reglas (campo vacío, fecha futura...).
 * Se lanza ANTES de ir a la base de datos.
 */
public class ValidacionException extends Exception {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
