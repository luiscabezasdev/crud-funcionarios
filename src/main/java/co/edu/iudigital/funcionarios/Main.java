package co.edu.iudigital.funcionarios;

import co.edu.iudigital.funcionarios.conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        // PRUEBA DEL BLOQUE 3: conectarse y leer el catálogo tipo_documento.
        // (Más adelante este main abrirá la ventana Swing.)
        String sql = "SELECT id_tipo_documento, nombre FROM tipo_documento ORDER BY id_tipo_documento";

        try (Connection con = ConexionBD.obtenerConexion();          // 1. abrir la llamada
             PreparedStatement ps = con.prepareStatement(sql);       // 2. preparar la consulta
             ResultSet rs = ps.executeQuery()) {                     // 3. ejecutarla: rs = las filas

            System.out.println("Conectado a: " + con.getMetaData().getURL());

            // TODO 7: recorre las filas con while (rs.next()) { ... }
            //   y en cada vuelta imprime el id y el nombre.
            //   Pista: rs.getInt("id_tipo_documento") y rs.getString("nombre").
            //   (Es el mismo patrón del método imprimir() de tu Paso2.java.)
            
             while (rs.next()) {                                   // ¿hay otra fila? baja el dedo
                int id = rs.getInt("id_tipo_documento");          // lee la celda "id" de esa fila
                String nombre = rs.getString("nombre");           // lee la celda "nombre"
                System.out.println(id + " - " + nombre);          // la muestra en pantalla
            }

        } catch (SQLException e) {
            // 4. si algo falla (base apagada, clave mala, tabla inexistente) llega aquí
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }
}
