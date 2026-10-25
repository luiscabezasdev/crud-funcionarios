package co.edu.iudigital.funcionarios.modelo;

/**
 * Una opción del catálogo (fila de la tabla estado_civil).
 * toString() devuelve el nombre: así el JComboBox de Swing muestra el texto, no el objeto.
 */
public class EstadoCivil {

    private final int id;
    private final String nombre;

    public EstadoCivil(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
