package co.edu.iudigital.funcionarios.seguridad;

import co.edu.iudigital.funcionarios.dao.UsuarioDAO;
import co.edu.iudigital.funcionarios.dao.UsuarioDAOImpl;
import co.edu.iudigital.funcionarios.excepciones.AutenticacionException;
import co.edu.iudigital.funcionarios.excepciones.DAOException;
import co.edu.iudigital.funcionarios.modelo.Usuario;
import java.time.OffsetDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/** AUTENTICACIÓN: comprueba quién eres. Si acierta, abre la sesión. */
public class ServicioAuth {

    private static final Logger LOG = Logger.getLogger(ServicioAuth.class.getName());
    private static final int MAX_INTENTOS = 3;
    private static final int MINUTOS_BLOQUEO = 5;
    private static final String MENSAJE_GENERICO = "Usuario o contraseña incorrectos.";

    // Hash de relleno: se verifica cuando el usuario no existe, para que tarde igual
    private static final String HASH_RELLENO = HasherClave.hashear("relleno".toCharArray());

    private final UsuarioDAO usuarioDAO;

    public ServicioAuth() {
        this(new UsuarioDAOImpl());
    }

    public ServicioAuth(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario autenticar(String username, char[] clave) throws AutenticacionException, DAOException {
        Usuario u = usuarioDAO.buscarPorUsername(username == null ? "" : username.trim());

        if (u == null || !u.isActivo()) {
            HasherClave.verificar(clave, HASH_RELLENO);          // mismo tiempo que un usuario real
            LOG.warning("Login fallido: usuario inexistente o inactivo");
            throw new AutenticacionException(MENSAJE_GENERICO);
        }

        OffsetDateTime ahora = OffsetDateTime.now();
        if (u.getBloqueadoHasta() != null) {
            if (u.getBloqueadoHasta().isAfter(ahora)) {
                LOG.warning("Login rechazado: cuenta bloqueada (" + u.getUsername() + ")");
                throw new AutenticacionException(
                    "Demasiados intentos fallidos. Cuenta bloqueada temporalmente; intenta más tarde.");
            }
            usuarioDAO.reiniciarIntentos(u.getIdUsuario());      // el bloqueo ya venció
        }

        if (!HasherClave.verificar(clave, u.getClaveHash())) {
            usuarioDAO.registrarFallo(u.getIdUsuario(), MAX_INTENTOS, MINUTOS_BLOQUEO);
            LOG.log(Level.WARNING, "Login fallido: clave incorrecta ({0})", u.getUsername());
            throw new AutenticacionException(MENSAJE_GENERICO);
        }

        usuarioDAO.reiniciarIntentos(u.getIdUsuario());
        Sesion.iniciar(u);
        LOG.log(Level.INFO, "Login correcto: {0} ({1})", new Object[] {u.getUsername(), u.getRol()});
        return u;
    }
}
