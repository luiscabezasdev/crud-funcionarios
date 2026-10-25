package co.edu.iudigital.funcionarios.dao;

import co.edu.iudigital.funcionarios.excepciones.DAOException;
import co.edu.iudigital.funcionarios.modelo.Funcionario;
import java.util.List;

/**
 * Contrato del patrón DAO: QUÉ operaciones existen sobre funcionarios.
 * No dice CÓMO se hacen (eso está en FuncionarioDAOImpl).
 * La pantalla solo conoce esta interfaz: nunca ve SQL.
 */
public interface FuncionarioDAO {

    List<Funcionario> listar() throws DAOException;

    Funcionario buscarPorId(int idFuncionario) throws DAOException;

    /** Inserta el funcionario y devuelve el id que le asignó la base de datos. */
    int crear(Funcionario funcionario) throws DAOException;

    void actualizar(Funcionario funcionario) throws DAOException;

    void eliminar(int idFuncionario) throws DAOException;
}
