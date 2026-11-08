package co.edu.iudigital.funcionarios.emergencias;

import co.edu.iudigital.funcionarios.excepciones.AccesoDenegadoException;
import co.edu.iudigital.funcionarios.excepciones.AutenticacionException;
import co.edu.iudigital.funcionarios.excepciones.DAOException;
import co.edu.iudigital.funcionarios.modelo.Usuario;
import co.edu.iudigital.funcionarios.seguridad.Permiso;
import co.edu.iudigital.funcionarios.seguridad.ServicioAuth;
import co.edu.iudigital.funcionarios.seguridad.Sesion;
import java.io.Console;
import java.util.Scanner;

/**
 * Demostración completa de la EA2: primero LOGIN (autenticación), luego se verifica el
 * permiso SIMULAR (autorización) y solo entonces corre el sistema con el monitor en vivo.
 * El rol ADMIN puede simular; el rol CONSULTA es rechazado.
 */
public class EmergenciasMain {

    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        ServicioAuth auth = new ServicioAuth();

        Usuario usuario = null;
        for (int intento = 1; intento <= 3 && usuario == null; intento++) {
            System.out.print("Usuario: ");
            String nombre = in.nextLine();
            char[] clave = leerClave(in);
            try {
                usuario = auth.autenticar(nombre, clave);
            } catch (AutenticacionException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (DAOException e) {
                System.out.println("No se pudo consultar la base de datos: " + e.getMessage());
                return;
            }
        }
        if (usuario == null) {
            System.out.println("Demasiados intentos. Fin.");
            return;
        }
        System.out.println("Sesión iniciada: " + usuario.getUsername() + " (" + usuario.getRol() + ")");

        try {
            Sesion.exigir(Permiso.SIMULAR);        // AUTORIZACIÓN
        } catch (AccesoDenegadoException e) {
            System.out.println("ACCESO DENEGADO: " + e.getMessage());
            Sesion.cerrar();
            return;
        }

        Simulacion.Resultado r = new Simulacion().ejecutar(new Simulacion.Config());
        System.out.println("\n=== RESUMEN ===");
        System.out.println("Emergencias atendidas: " + r.atendidas + "/" + r.config.emergencias);
        System.out.println("Tiempo total: " + r.duracionMs + " ms");
        System.out.printf("Espera media: %.1f ms | espera máxima: %d ms%n", r.esperaMediaMs, r.esperaMaxMs);
        System.out.println("Máx. ambulancias en uso a la vez: " + r.maxAmbulanciasEnUso
                + " de " + r.config.ambulancias);
        System.out.println("Máx. médicos en uso a la vez: " + r.maxMedicosEnUso + " de " + r.config.medicos);
        System.out.println("Comprobación de corrección: " + (r.correcto() ? "OK" : "FALLÓ"));
        Sesion.cerrar();
    }

    /** En la terminal oculta la clave; en el IDE (sin consola) la lee como texto normal. */
    private static char[] leerClave(Scanner in) {
        Console consola = System.console();
        if (consola != null) {
            return consola.readPassword("Contraseña: ");
        }
        System.out.print("Contraseña: ");
        return in.nextLine().toCharArray();
    }
}
