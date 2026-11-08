package co.edu.iudigital.funcionarios.modelo;

import co.edu.iudigital.funcionarios.seguridad.Rol;
import java.time.OffsetDateTime;

/** Una fila de la tabla usuarios (con el nombre de su rol traído por el JOIN). */
public class Usuario {

    private int idUsuario;
    private String username;
    private String claveHash;
    private int intentosFallidos;
    private OffsetDateTime bloqueadoHasta;
    private boolean activo;
    private Rol rol;

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getClaveHash() { return claveHash; }
    public void setClaveHash(String claveHash) { this.claveHash = claveHash; }

    public int getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(int intentosFallidos) { this.intentosFallidos = intentosFallidos; }

    public OffsetDateTime getBloqueadoHasta() { return bloqueadoHasta; }
    public void setBloqueadoHasta(OffsetDateTime bloqueadoHasta) { this.bloqueadoHasta = bloqueadoHasta; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}
