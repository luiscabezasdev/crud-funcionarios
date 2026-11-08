package co.edu.iudigital.funcionarios.dao;

import co.edu.iudigital.funcionarios.excepciones.AccesoDenegadoException;
import co.edu.iudigital.funcionarios.excepciones.DAOException;
import co.edu.iudigital.funcionarios.modelo.Funcionario;
import co.edu.iudigital.funcionarios.seguridad.Permiso;
import co.edu.iudigital.funcionarios.seguridad.Sesion;
import java.util.List;

/**
 * AUTORIZACIÓN. Patrón Decorator: envuelve al DAO real y, antes de cada operación,
 * pregunta a la sesión si el rol tiene el permiso. Si no, el DAO real NUNCA se ejecuta.
 *
 * Defensa en profundidad: la ventana desactiva botones (comodidad), pero la regla
 * real vive aquí. Aunque alguien llamara al DAO sin pasar por la ventana, se aplica igual.
 */
public class FuncionarioDAOSeguro implements FuncionarioDAO {

    private final FuncionarioDAO real;

    public FuncionarioDAOSeguro(FuncionarioDAO real) {
        this.real = real;
    }

    @Override
    public List<Funcionario> listar() throws DAOException {
        Sesion.exigir(Permiso.LEER);
        return real.listar();
    }

    @Override
    public Funcionario buscarPorId(int idFuncionario) throws DAOException {
        Sesion.exigir(Permiso.LEER);
        return real.buscarPorId(idFuncionario);
    }

    @Override
    public int crear(Funcionario funcionario) throws DAOException {
        Sesion.exigir(Permiso.CREAR);
        return real.crear(funcionario);
    }

    @Override
    public void actualizar(Funcionario funcionario) throws DAOException {
        Sesion.exigir(Permiso.EDITAR);
        real.actualizar(funcionario);
    }

    @Override
    public void eliminar(int idFuncionario) throws DAOException {
        Sesion.exigir(Permiso.ELIMINAR);
        real.eliminar(idFuncionario);
    }
}
