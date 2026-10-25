package co.edu.iudigital.funcionarios.vista;

import co.edu.iudigital.funcionarios.dao.CatalogoDAO;
import co.edu.iudigital.funcionarios.dao.CatalogoDAOImpl;
import co.edu.iudigital.funcionarios.dao.FuncionarioDAO;
import co.edu.iudigital.funcionarios.dao.FuncionarioDAOImpl;
import co.edu.iudigital.funcionarios.excepciones.DAOException;
import co.edu.iudigital.funcionarios.excepciones.RegistroDuplicadoException;
import co.edu.iudigital.funcionarios.excepciones.ValidacionException;
import co.edu.iudigital.funcionarios.modelo.EstadoCivil;
import co.edu.iudigital.funcionarios.modelo.Funcionario;
import co.edu.iudigital.funcionarios.modelo.TipoDocumento;
import co.edu.iudigital.funcionarios.util.ValidadorFuncionario;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana del CRUD de funcionarios.
 * La vista NO tiene SQL: solo llama a los métodos del DAO y muestra
 * los mensajes de las excepciones.
 */
public class FuncionarioFrame extends JFrame {

    // La vista depende de las INTERFACES del DAO, no de las clases con SQL
    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAOImpl();
    private final CatalogoDAO catalogoDAO = new CatalogoDAOImpl();

    // ---- Formulario
    private final JComboBox<TipoDocumento> cbTipoDocumento = new JComboBox<>();
    private final JTextField txtNumeroDocumento = new JTextField(15);
    private final JTextField txtNombres = new JTextField(15);
    private final JTextField txtApellidos = new JTextField(15);
    private final JComboBox<EstadoCivil> cbEstadoCivil = new JComboBox<>();
    private final JComboBox<String> cbSexo = new JComboBox<>(new String[] {"M", "F"});
    private final JTextField txtDireccion = new JTextField(15);
    private final JTextField txtTelefono = new JTextField(15);
    private final JTextField txtFechaNacimiento = new JTextField(15);

    // ---- Botones (uno por operación del CRUD)
    private final JButton btnCrear = new JButton("Crear");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JButton btnRefrescar = new JButton("Refrescar lista");

    // ---- Tabla (Listar)
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
        new String[] {"Id", "Tipo doc.", "Número", "Nombres", "Apellidos",
                      "Estado civil", "Sexo", "Teléfono", "Nacimiento"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;   // se edita en el formulario, no en la tabla
        }
    };
    private final JTable tabla = new JTable(modeloTabla);

    /** Id del funcionario seleccionado en la tabla; 0 = ninguno (modo "crear"). */
    private int idSeleccionado = 0;

    public FuncionarioFrame() {
        super("CRUD Funcionarios · IU Digital de Antioquia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        construirPantalla();
        conectarEventos();
        cargarCatalogos();
        cargarTabla();
        limpiarFormulario();
        setSize(1100, 560);
        setLocationRelativeTo(null);
    }

    // ================================================================ PANTALLA

    private void construirPantalla() {
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del funcionario"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        agregarCampo(formulario, c, 0, "Tipo de documento:", cbTipoDocumento);
        agregarCampo(formulario, c, 1, "Número de documento:", txtNumeroDocumento);
        agregarCampo(formulario, c, 2, "Nombres:", txtNombres);
        agregarCampo(formulario, c, 3, "Apellidos:", txtApellidos);
        agregarCampo(formulario, c, 4, "Estado civil:", cbEstadoCivil);
        agregarCampo(formulario, c, 5, "Sexo:", cbSexo);
        agregarCampo(formulario, c, 6, "Dirección:", txtDireccion);
        agregarCampo(formulario, c, 7, "Teléfono:", txtTelefono);
        agregarCampo(formulario, c, 8, "Nacimiento (AAAA-MM-DD):", txtFechaNacimiento);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnCrear);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        botones.add(btnRefrescar);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane panelTabla = new JScrollPane(tabla);
        panelTabla.setBorder(BorderFactory.createTitledBorder("Funcionarios registrados"));

        setLayout(new BorderLayout(8, 8));
        add(formulario, BorderLayout.WEST);
        add(panelTabla, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints c, int fila, String texto,
                              java.awt.Component campo) {
        c.gridx = 0;
        c.gridy = fila;
        c.weightx = 0;
        panel.add(new JLabel(texto), c);
        c.gridx = 1;
        c.weightx = 1;
        panel.add(campo, c);
    }

    private void conectarEventos() {
        btnCrear.addActionListener(e -> crear());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRefrescar.addActionListener(e -> cargarTabla());

        // Al hacer clic en una fila, sus datos pasan al formulario (para editar o eliminar)
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                int id = (int) modeloTabla.getValueAt(tabla.getSelectedRow(), 0);
                cargarEnFormulario(id);
            }
        });
    }

    // ================================================================ CRUD

    /** LISTAR: pide la lista al DAO y la pinta en la tabla. */
    private void cargarTabla() {
        try {
            List<Funcionario> lista = funcionarioDAO.listar();
            modeloTabla.setRowCount(0);
            for (Funcionario f : lista) {
                modeloTabla.addRow(new Object[] {
                    f.getIdFuncionario(), f.getTipoDocumentoNombre(), f.getNumeroDocumento(),
                    f.getNombres(), f.getApellidos(), f.getEstadoCivilNombre(),
                    f.getSexo(), f.getTelefono(), f.getFechaNacimiento()
                });
            }
        } catch (DAOException e) {
            mostrarError(e.getMessage());
        }
    }

    /** CREAR */
    private void crear() {
        try {
            Funcionario f = leerFormulario();
            ValidadorFuncionario.validar(f);          // 1. ¿los datos son válidos?
            int idNuevo = funcionarioDAO.crear(f);    // 2. guardarlos
            cargarTabla();
            limpiarFormulario();
            mostrarInfo("Funcionario creado con id " + idNuevo + ".");
        } catch (ValidacionException e) {
            mostrarAdvertencia("Revisa el formulario:\n" + e.getMessage());
        } catch (RegistroDuplicadoException e) {     // la más específica va ANTES que DAOException
            mostrarAdvertencia(e.getMessage());
        } catch (DAOException e) {
            mostrarError(e.getMessage());
        }
    }

    /** EDITAR */
    private void actualizar() {
        if (idSeleccionado == 0) {
            mostrarAdvertencia("Primero selecciona en la tabla el funcionario que quieres editar.");
            return;
        }
        try {
            Funcionario f = leerFormulario();
            f.setIdFuncionario(idSeleccionado);
            ValidadorFuncionario.validar(f);
            funcionarioDAO.actualizar(f);
            cargarTabla();
            limpiarFormulario();
            mostrarInfo("Funcionario actualizado.");
        } catch (ValidacionException e) {
            mostrarAdvertencia("Revisa el formulario:\n" + e.getMessage());
        } catch (RegistroDuplicadoException e) {
            mostrarAdvertencia(e.getMessage());
        } catch (DAOException e) {
            mostrarError(e.getMessage());
        }
    }

    /** ELIMINAR */
    private void eliminar() {
        if (idSeleccionado == 0) {
            mostrarAdvertencia("Primero selecciona en la tabla el funcionario que quieres eliminar.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Eliminar al funcionario " + idSeleccionado + "?\n"
          + "También se eliminarán su grupo familiar y su formación académica.",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            funcionarioDAO.eliminar(idSeleccionado);
            cargarTabla();
            limpiarFormulario();
            mostrarInfo("Funcionario eliminado.");
        } catch (DAOException e) {
            mostrarError(e.getMessage());
        }
    }

    // ================================================================ AYUDANTES

    /** Llena las listas desplegables desde las tablas catálogo (aquí se ven las relaciones). */
    private void cargarCatalogos() {
        try {
            for (TipoDocumento t : catalogoDAO.listarTiposDocumento()) {
                cbTipoDocumento.addItem(t);
            }
            for (EstadoCivil ec : catalogoDAO.listarEstadosCiviles()) {
                cbEstadoCivil.addItem(ec);
            }
        } catch (DAOException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Pasa lo escrito en el formulario a un objeto Funcionario. */
    private Funcionario leerFormulario() throws ValidacionException {
        Funcionario f = new Funcionario();
        TipoDocumento tipo = (TipoDocumento) cbTipoDocumento.getSelectedItem();
        EstadoCivil estado = (EstadoCivil) cbEstadoCivil.getSelectedItem();
        f.setIdTipoDocumento(tipo == null ? 0 : tipo.getId());
        f.setNumeroDocumento(txtNumeroDocumento.getText().trim());
        f.setNombres(txtNombres.getText().trim());
        f.setApellidos(txtApellidos.getText().trim());
        f.setIdEstadoCivil(estado == null ? 0 : estado.getId());
        f.setSexo((String) cbSexo.getSelectedItem());
        f.setDireccion(textoONulo(txtDireccion));
        f.setTelefono(textoONulo(txtTelefono));
        String fecha = txtFechaNacimiento.getText().trim();
        if (!fecha.isEmpty()) {
            try {
                f.setFechaNacimiento(LocalDate.parse(fecha));   // espera AAAA-MM-DD
            } catch (DateTimeParseException e) {
                throw new ValidacionException("La fecha debe tener el formato AAAA-MM-DD, por ejemplo 1990-05-20.");
            }
        }
        return f;
    }

    /** Busca el funcionario por id y pone sus datos en el formulario. */
    private void cargarEnFormulario(int id) {
        try {
            Funcionario f = funcionarioDAO.buscarPorId(id);
            idSeleccionado = f.getIdFuncionario();
            seleccionarPorId(cbTipoDocumento, f.getIdTipoDocumento());
            txtNumeroDocumento.setText(f.getNumeroDocumento());
            txtNombres.setText(f.getNombres());
            txtApellidos.setText(f.getApellidos());
            seleccionarPorId(cbEstadoCivil, f.getIdEstadoCivil());
            cbSexo.setSelectedItem(f.getSexo());
            txtDireccion.setText(f.getDireccion() == null ? "" : f.getDireccion());
            txtTelefono.setText(f.getTelefono() == null ? "" : f.getTelefono());
            txtFechaNacimiento.setText(f.getFechaNacimiento() == null ? "" : f.getFechaNacimiento().toString());
            btnCrear.setEnabled(false);
            btnActualizar.setEnabled(true);
            btnEliminar.setEnabled(true);
        } catch (DAOException e) {
            mostrarError(e.getMessage());
        }
    }

    private void limpiarFormulario() {
        idSeleccionado = 0;
        tabla.clearSelection();
        txtNumeroDocumento.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtFechaNacimiento.setText("");
        if (cbTipoDocumento.getItemCount() > 0) cbTipoDocumento.setSelectedIndex(0);
        if (cbEstadoCivil.getItemCount() > 0) cbEstadoCivil.setSelectedIndex(0);
        cbSexo.setSelectedIndex(0);
        btnCrear.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private void seleccionarPorId(JComboBox<?> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            int idItem = (item instanceof TipoDocumento t) ? t.getId() : ((EstadoCivil) item).getId();
            if (idItem == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private String textoONulo(JTextField campo) {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? null : texto;
    }

    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Listo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
