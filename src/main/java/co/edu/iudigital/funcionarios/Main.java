package co.edu.iudigital.funcionarios;

import co.edu.iudigital.funcionarios.vista.FuncionarioFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Punto de entrada: abre la ventana del CRUD (F6 en NetBeans). */
public class Main {

    public static void main(String[] args) {
        try {
            // Aspecto de ventana "Nimbus", incluido en Java; si falla, se usa el de siempre
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // no es grave: la app funciona igual con el aspecto por defecto
        }
        // Swing pide crear las ventanas en su propio hilo de eventos
        SwingUtilities.invokeLater(() -> new FuncionarioFrame().setVisible(true));
    }
}
