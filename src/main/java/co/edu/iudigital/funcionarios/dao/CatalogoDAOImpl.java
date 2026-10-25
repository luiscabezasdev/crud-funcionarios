package co.edu.iudigital.funcionarios.dao;

import co.edu.iudigital.funcionarios.conexion.ConexionBD;
import co.edu.iudigital.funcionarios.excepciones.DAOException;
import co.edu.iudigital.funcionarios.modelo.EstadoCivil;
import co.edu.iudigital.funcionarios.modelo.TipoDocumento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC de CatalogoDAO: el mismo patrón de tu prueba del Bloque 3. */
public class CatalogoDAOImpl implements CatalogoDAO {

    @Override
    public List<TipoDocumento> listarTiposDocumento() throws DAOException {
        String sql = "SELECT id_tipo_documento, nombre FROM tipo_documento ORDER BY id_tipo_documento";
        List<TipoDocumento> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new TipoDocumento(rs.getInt("id_tipo_documento"), rs.getString("nombre")));
            }
            return lista;
        } catch (SQLException e) {
            throw TraductorErrores.traducir("listar los tipos de documento", e);
        }
    }

    @Override
    public List<EstadoCivil> listarEstadosCiviles() throws DAOException {
        String sql = "SELECT id_estado_civil, nombre FROM estado_civil ORDER BY id_estado_civil";
        List<EstadoCivil> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new EstadoCivil(rs.getInt("id_estado_civil"), rs.getString("nombre")));
            }
            return lista;
        } catch (SQLException e) {
            throw TraductorErrores.traducir("listar los estados civiles", e);
        }
    }
}
