package co.edu.iudigital.funcionarios.modelo;

import java.time.LocalDate;

/**
 * Representa una fila de la tabla funcionarios.
 * Guarda los ids de los catálogos (lo que va a la base de datos)
 * y sus nombres (lo que se muestra en pantalla, viene del JOIN).
 */
public class Funcionario {

    private int idFuncionario;
    private int idTipoDocumento;
    private String tipoDocumentoNombre;   // solo lectura: viene del JOIN con tipo_documento
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private int idEstadoCivil;
    private String estadoCivilNombre;     // solo lectura: viene del JOIN con estado_civil
    private String sexo;                  // "M" o "F"
    private String direccion;
    private String telefono;
    private LocalDate fechaNacimiento;

    public Funcionario() {
    }

    public int getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(int idFuncionario) { this.idFuncionario = idFuncionario; }

    public int getIdTipoDocumento() { return idTipoDocumento; }
    public void setIdTipoDocumento(int idTipoDocumento) { this.idTipoDocumento = idTipoDocumento; }

    public String getTipoDocumentoNombre() { return tipoDocumentoNombre; }
    public void setTipoDocumentoNombre(String tipoDocumentoNombre) { this.tipoDocumentoNombre = tipoDocumentoNombre; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public int getIdEstadoCivil() { return idEstadoCivil; }
    public void setIdEstadoCivil(int idEstadoCivil) { this.idEstadoCivil = idEstadoCivil; }

    public String getEstadoCivilNombre() { return estadoCivilNombre; }
    public void setEstadoCivilNombre(String estadoCivilNombre) { this.estadoCivilNombre = estadoCivilNombre; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    @Override
    public String toString() {
        return idFuncionario + " | " + tipoDocumentoNombre + " " + numeroDocumento + " | "
                + nombres + " " + apellidos + " | " + estadoCivilNombre + " | " + sexo
                + " | " + fechaNacimiento;
    }
}
