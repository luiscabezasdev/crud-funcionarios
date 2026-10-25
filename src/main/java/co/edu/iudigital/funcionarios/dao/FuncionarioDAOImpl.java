package co.edu.iudigital.funcionarios.dao;

import co.edu.iudigital.funcionarios.conexion.ConexionBD;
import co.edu.iudigital.funcionarios.excepciones.DAOException;
import co.edu.iudigital.funcionarios.modelo.Funcionario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del DAO de funcionarios: aquí (y solo aquí) está el SQL.
 * Todas las consultas usan PreparedStatement con "?": el dato del usuario viaja
 * separado del SQL, por eso no hay inyección SQL.
 */
public class FuncionarioDAOImpl implements FuncionarioDAO {

    // SELECT con JOIN: trae los ids y también los NOMBRES de los catálogos
    private static final String SQL_SELECT_BASE =
        "SELECT f.id_funcionario, f.id_tipo_documento, td.nombre AS tipo_documento, "
      + "       f.numero_documento, f.nombres, f.apellidos, "
      + "       f.id_estado_civil, ec.nombre AS estado_civil, "
      + "       f.sexo, f.direccion, f.telefono, f.fecha_nacimiento "
      + "FROM funcionarios f "
      + "JOIN tipo_documento td ON td.id_tipo_documento = f.id_tipo_documento "
      + "JOIN estado_civil  ec ON ec.id_estado_civil   = f.id_estado_civil ";

    private static final String SQL_LISTAR   = SQL_SELECT_BASE + "ORDER BY f.apellidos, f.nombres";
    private static final String SQL_BUSCAR   = SQL_SELECT_BASE + "WHERE f.id_funcionario = ?";

    private static final String SQL_INSERTAR =
        "INSERT INTO funcionarios (id_tipo_documento, numero_documento, nombres, apellidos, "
      + "id_estado_civil, sexo, direccion, telefono, fecha_nacimiento) "
      + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
        "UPDATE funcionarios SET id_tipo_documento = ?, numero_documento = ?, nombres = ?, "
      + "apellidos = ?, id_estado_civil = ?, sexo = ?, direccion = ?, telefono = ?, "
      + "fecha_nacimiento = ? WHERE id_funcionario = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM funcionarios WHERE id_funcionario = ?";

    // ------------------------------------------------------------------ LISTAR (Read)
    @Override
    public List<Funcionario> listar() throws DAOException {
        List<Funcionario> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearFila(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw TraductorErrores.traducir("listar los funcionarios", e);
        }
    }

    @Override
    public Funcionario buscarPorId(int idFuncionario) throws DAOException {
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR)) {
            ps.setInt(1, idFuncionario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearFila(rs);
                }
                throw new DAOException("No existe un funcionario con id " + idFuncionario + ".");
            }
        } catch (SQLException e) {
            throw TraductorErrores.traducir("buscar el funcionario", e);
        }
    }

    // ------------------------------------------------------------------ CREAR (Create)
    @Override
    public int crear(Funcionario f) throws DAOException {
        // El segundo argumento pide a PostgreSQL que devuelva el id generado por SERIAL
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERTAR, new String[] {"id_funcionario"})) {
            llenarParametros(ps, f);
            ps.executeUpdate();
            try (ResultSet llaves = ps.getGeneratedKeys()) {
                llaves.next();
                int idNuevo = llaves.getInt(1);
                f.setIdFuncionario(idNuevo);
                return idNuevo;
            }
        } catch (SQLException e) {
            throw TraductorErrores.traducir("crear el funcionario", e);
        }
    }

    // ------------------------------------------------------------------ EDITAR (Update)
    @Override
    public void actualizar(Funcionario f) throws DAOException {
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {
            llenarParametros(ps, f);                 // ? del 1 al 9
            ps.setInt(10, f.getIdFuncionario());     // ? número 10: el WHERE
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new DAOException("No existe un funcionario con id " + f.getIdFuncionario() + ".");
            }
        } catch (SQLException e) {
            throw TraductorErrores.traducir("actualizar el funcionario", e);
        }
    }

    // ------------------------------------------------------------------ ELIMINAR (Delete)
    @Override
    public void eliminar(int idFuncionario) throws DAOException {
        // ON DELETE CASCADE en el esquema borra también su grupo familiar y formación académica
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, idFuncionario);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new DAOException("No existe un funcionario con id " + idFuncionario + ".");
            }
        } catch (SQLException e) {
            throw TraductorErrores.traducir("eliminar el funcionario", e);
        }
    }

    // ------------------------------------------------------------------ AYUDANTES

    /** Pone los 9 valores del funcionario en los "?" del INSERT o del UPDATE (mismo orden). */
    private void llenarParametros(PreparedStatement ps, Funcionario f) throws SQLException {
        ps.setInt(1, f.getIdTipoDocumento());
        ps.setString(2, f.getNumeroDocumento());
        ps.setString(3, f.getNombres());
        ps.setString(4, f.getApellidos());
        ps.setInt(5, f.getIdEstadoCivil());
        ps.setString(6, f.getSexo());
        ps.setString(7, f.getDireccion());
        ps.setString(8, f.getTelefono());
        ps.setObject(9, f.getFechaNacimiento());   // LocalDate -> DATE
    }

    /** Convierte la fila actual del ResultSet en un objeto Funcionario. */
    private Funcionario mapearFila(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario();
        f.setIdFuncionario(rs.getInt("id_funcionario"));
        f.setIdTipoDocumento(rs.getInt("id_tipo_documento"));
        f.setTipoDocumentoNombre(rs.getString("tipo_documento"));
        f.setNumeroDocumento(rs.getString("numero_documento"));
        f.setNombres(rs.getString("nombres"));
        f.setApellidos(rs.getString("apellidos"));
        f.setIdEstadoCivil(rs.getInt("id_estado_civil"));
        f.setEstadoCivilNombre(rs.getString("estado_civil"));
        f.setSexo(rs.getString("sexo"));
        f.setDireccion(rs.getString("direccion"));
        f.setTelefono(rs.getString("telefono"));
        f.setFechaNacimiento(rs.getObject("fecha_nacimiento", LocalDate.class));
        return f;
    }
}
