package co.edu.iudigital.funcionarios.seguridad;

import co.edu.iudigital.funcionarios.excepciones.AccesoDenegadoException;
import co.edu.iudigital.funcionarios.modelo.Usuario;

/**
 * Guarda QUIÉN está conectado después del login. Es el puente entre las dos ideas:
 * la autenticación llena la sesión; la autorización la consulta.
 */
public final class Sesion {

    private static Usuario actual;

    private Sesion() {
    }

    public static void iniciar(Usuario usuario) {
        actual = usuario;
    }

    public static void cerrar() {
        actual = null;
    }

    public static Usuario usuario() {
        return actual;
    }

    public static boolean tiene(Permiso permiso) {
        return actual != null && actual.getRol().permite(permiso);
    }

    /** Autorización: si el rol no tiene el permiso, lanza la excepción. */
    public static void exigir(Permiso permiso) throws AccesoDenegadoException {
        if (actual == null) {
            throw new AccesoDenegadoException("Debes iniciar sesión.");
        }
        if (!actual.getRol().permite(permiso)) {
            throw new AccesoDenegadoException(
                "Tu rol (" + actual.getRol() + ") no tiene permiso para esta acción.");
        }
    }
}
