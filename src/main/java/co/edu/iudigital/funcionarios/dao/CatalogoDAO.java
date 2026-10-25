package co.edu.iudigital.funcionarios.dao;

import co.edu.iudigital.funcionarios.excepciones.DAOException;
import co.edu.iudigital.funcionarios.modelo.EstadoCivil;
import co.edu.iudigital.funcionarios.modelo.TipoDocumento;
import java.util.List;

/** Lectura de los catálogos para llenar las listas desplegables del formulario. */
public interface CatalogoDAO {

    List<TipoDocumento> listarTiposDocumento() throws DAOException;

    List<EstadoCivil> listarEstadosCiviles() throws DAOException;
}
