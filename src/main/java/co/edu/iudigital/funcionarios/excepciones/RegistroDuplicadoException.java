package co.edu.iudigital.funcionarios.excepciones;

/**
 * Se lanza cuando ya existe un funcionario con el mismo tipo y número de documento
 * (la base de datos rechaza la fila por la restricción uq_funcionario_documento).
 * Es un caso particular de DAOException: hereda de ella.
 */
public class RegistroDuplicadoException extends DAOException {

    public RegistroDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
