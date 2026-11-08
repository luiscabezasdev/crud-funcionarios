package co.edu.iudigital.funcionarios;

import co.edu.iudigital.funcionarios.vista.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Punto de entrada: primero la puerta (login); el CRUD se abre solo si el login es correcto. */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // no es grave: la app funciona igual con el aspecto por defecto
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
