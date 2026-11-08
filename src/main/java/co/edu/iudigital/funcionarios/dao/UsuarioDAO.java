package co.edu.iudigital.funcionarios.dao;

import co.edu.iudigital.funcionarios.excepciones.DAOException;
import co.edu.iudigital.funcionarios.modelo.Usuario;

/** Acceso a la tabla usuarios (siempre con PreparedStatement). */
public interface UsuarioDAO {

    /** @return el usuario, o null si no existe */
    Usuario buscarPorUsername(String username) throws DAOException;

    /** Suma un intento fallido y, al llegar al máximo, bloquea la cuenta (todo en un solo UPDATE). */
    void registrarFallo(int idUsuario, int maxIntentos, int minutosBloqueo) throws DAOException;

    void reiniciarIntentos(int idUsuario) throws DAOException;
}
