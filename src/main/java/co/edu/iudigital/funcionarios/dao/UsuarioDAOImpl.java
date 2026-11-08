package co.edu.iudigital.funcionarios.dao;

import co.edu.iudigital.funcionarios.conexion.ConexionBD;
import co.edu.iudigital.funcionarios.excepciones.DAOException;
import co.edu.iudigital.funcionarios.modelo.Usuario;
import co.edu.iudigital.funcionarios.seguridad.Rol;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String SQL_BUSCAR =
        "SELECT u.id_usuario, u.username, u.clave_hash, u.intentos_fallidos, "
      + "       u.bloqueado_hasta, u.activo, r.nombre AS rol "
      + "FROM usuarios u JOIN roles r ON r.id_rol = u.id_rol "
      + "WHERE u.username = ?";

    // En un UPDATE de PostgreSQL, el lado derecho usa los valores ANTERIORES de la fila
    private static final String SQL_FALLO =
        "UPDATE usuarios SET intentos_fallidos = intentos_fallidos + 1, "
      + "bloqueado_hasta = CASE WHEN intentos_fallidos + 1 >= ? "
      + "                       THEN NOW() + (? * INTERVAL '1 minute') ELSE bloqueado_hasta END "
      + "WHERE id_usuario = ?";

    private static final String SQL_REINICIAR =
        "UPDATE usuarios SET intentos_fallidos = 0, bloqueado_hasta = NULL WHERE id_usuario = ?";

    @Override
    public Usuario buscarPorUsername(String username) throws DAOException {
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setUsername(rs.getString("username"));
                u.setClaveHash(rs.getString("clave_hash"));
                u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
                u.setBloqueadoHasta(rs.getObject("bloqueado_hasta", OffsetDateTime.class));
                u.setActivo(rs.getBoolean("activo"));
                u.setRol(Rol.valueOf(rs.getString("rol")));
                return u;
            }
        } catch (SQLException e) {
            throw TraductorErrores.traducir("consultar el usuario", e);
        }
    }

    @Override
    public void registrarFallo(int idUsuario, int maxIntentos, int minutosBloqueo) throws DAOException {
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_FALLO)) {
            ps.setInt(1, maxIntentos);
            ps.setInt(2, minutosBloqueo);
            ps.setInt(3, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw TraductorErrores.traducir("registrar el intento fallido", e);
        }
    }

    @Override
    public void reiniciarIntentos(int idUsuario) throws DAOException {
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_REINICIAR)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw TraductorErrores.traducir("reiniciar los intentos", e);
        }
    }
}
