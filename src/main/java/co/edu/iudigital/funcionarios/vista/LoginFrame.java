package co.edu.iudigital.funcionarios.vista;

import co.edu.iudigital.funcionarios.excepciones.AutenticacionException;
import co.edu.iudigital.funcionarios.excepciones.DAOException;
import co.edu.iudigital.funcionarios.seguridad.ServicioAuth;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** Ventana de inicio de sesión: la "puerta". Solo habla con ServicioAuth. */
public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final transient ServicioAuth servicioAuth = new ServicioAuth();
    private final JTextField txtUsuario = new JTextField(16);
    private final JPasswordField txtClave = new JPasswordField(16);   // oculta lo que se escribe
    private final JButton btnEntrar = new JButton("Entrar");

    public LoginFrame() {
        super("Iniciar sesión · CRUD Funcionarios");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; panel.add(new JLabel("Usuario:"), c);
        c.gridx = 1; panel.add(txtUsuario, c);
        c.gridx = 0; c.gridy = 1; panel.add(new JLabel("Contraseña:"), c);
        c.gridx = 1; panel.add(txtClave, c);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; panel.add(btnEntrar, c);

        add(panel);
        getRootPane().setDefaultButton(btnEntrar);                    // Enter = Entrar
        btnEntrar.addActionListener(e -> entrar());
        pack();
        setLocationRelativeTo(null);
    }

    private void entrar() {
        char[] clave = txtClave.getPassword();
        try {
            servicioAuth.autenticar(txtUsuario.getText(), clave);
            new FuncionarioFrame().setVisible(true);                  // pasó la puerta
            dispose();
        } catch (AutenticacionException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Acceso denegado", JOptionPane.WARNING_MESSAGE);
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(clave, '\0');                                 // borra la clave de la memoria
            txtClave.setText("");
        }
    }
}
