package co.edu.iudigital.funcionarios.seguridad;

import java.util.EnumSet;
import java.util.Set;

/**
 * Los roles y lo que cada uno puede hacer (RBAC: control de acceso por roles).
 * El nombre de cada constante coincide con roles.nombre en la base de datos.
 * Mínimo privilegio: CONSULTA solo lee.
 */
public enum Rol {
    ADMIN(EnumSet.allOf(Permiso.class)),
    CONSULTA(EnumSet.of(Permiso.LEER));

    private final Set<Permiso> permisos;

    Rol(Set<Permiso> permisos) {
        this.permisos = permisos;
    }

    public boolean permite(Permiso permiso) {
        return permisos.contains(permiso);
    }
}
