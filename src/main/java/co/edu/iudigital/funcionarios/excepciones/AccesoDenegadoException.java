package co.edu.iudigital.funcionarios.excepciones;

/**
 * El usuario YA está autenticado, pero su rol no tiene el permiso pedido (autorización).
 * Hereda de DAOException para poder viajar por la interfaz FuncionarioDAO.
 */
public class AccesoDenegadoException extends DAOException {

    private static final long serialVersionUID = 1L;

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
