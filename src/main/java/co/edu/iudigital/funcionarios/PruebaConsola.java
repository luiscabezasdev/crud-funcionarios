package co.edu.iudigital.funcionarios;

import co.edu.iudigital.funcionarios.dao.FuncionarioDAO;
import co.edu.iudigital.funcionarios.dao.FuncionarioDAOImpl;
import co.edu.iudigital.funcionarios.excepciones.DAOException;
import co.edu.iudigital.funcionarios.excepciones.RegistroDuplicadoException;
import co.edu.iudigital.funcionarios.excepciones.ValidacionException;
import co.edu.iudigital.funcionarios.modelo.Funcionario;
import co.edu.iudigital.funcionarios.util.ValidadorFuncionario;
import java.time.LocalDate;

/**
 * PRUEBA DEL BLOQUE 4: las 4 operaciones del CRUD y las 3 excepciones, sin ventana.
 * Ejecutar con clic derecho sobre el archivo -> Run File (Shift + F6).
 */
public class PruebaConsola {

    public static void main(String[] args) {
        FuncionarioDAO dao = new FuncionarioDAOImpl();   // la variable es de tipo INTERFAZ

        try {
            System.out.println("== 1. LISTAR ==");
            dao.listar().forEach(System.out::println);

            System.out.println("\n== 2. CREAR ==");
            Funcionario nuevo = new Funcionario();
            nuevo.setIdTipoDocumento(1);
            nuevo.setNumeroDocumento("80123456");
            nuevo.setNombres("Pedro");
            nuevo.setApellidos("Prueba Consola");
            nuevo.setIdEstadoCivil(1);
            nuevo.setSexo("M");
            nuevo.setTelefono("3001112233");
            nuevo.setFechaNacimiento(LocalDate.of(1990, 5, 20));
            ValidadorFuncionario.validar(nuevo);
            int id = dao.crear(nuevo);
            System.out.println("Creado con id " + id);

            System.out.println("\n== 3. EDITAR ==");
            nuevo.setIdEstadoCivil(2);                    // Pedro se casó
            dao.actualizar(nuevo);
            System.out.println("Después de editar: " + dao.buscarPorId(id));

            System.out.println("\n== 4. EXCEPCIÓN: documento duplicado ==");
            try {
                dao.crear(nuevo);                         // mismo tipo y número otra vez
            } catch (RegistroDuplicadoException e) {
                System.out.println("Mensaje para el usuario: " + e.getMessage());
            }

            System.out.println("\n== 5. EXCEPCIÓN: validación ==");
            try {
                Funcionario malo = new Funcionario();
                malo.setNombres("P3dr0");
                ValidadorFuncionario.validar(malo);
            } catch (ValidacionException e) {
                System.out.println("Errores del formulario:\n" + e.getMessage());
            }

            System.out.println("\n== 6. ELIMINAR ==");
            dao.eliminar(id);
            System.out.println("Eliminado el id " + id + ". Lista final:");
            dao.listar().forEach(System.out::println);

        } catch (DAOException | ValidacionException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
