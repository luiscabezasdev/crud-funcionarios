package co.edu.iudigital.funcionarios.util;

import co.edu.iudigital.funcionarios.excepciones.ValidacionException;
import co.edu.iudigital.funcionarios.modelo.Funcionario;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Revisa los datos ANTES de enviarlos a la base de datos.
 * Usa "lista de permitidos" (regex de lo que SÍ es válido), como en Software Seguro.
 */
public final class ValidadorFuncionario {

    private static final String REGEX_DOCUMENTO = "^[A-Za-z0-9]{4,20}$";        // letras y números
    private static final String REGEX_NOMBRE    = "^[\\p{L} .'-]{2,80}$";        // letras (con tildes), espacios
    private static final String REGEX_TELEFONO  = "^[0-9+ ]{7,20}$";

    private ValidadorFuncionario() {
    }

    public static void validar(Funcionario f) throws ValidacionException {
        List<String> errores = new ArrayList<>();

        if (f.getIdTipoDocumento() <= 0) {
            errores.add("Elige un tipo de documento.");
        }
        if (vacio(f.getNumeroDocumento()) || !f.getNumeroDocumento().matches(REGEX_DOCUMENTO)) {
            errores.add("El número de documento debe tener de 4 a 20 letras o números, sin espacios.");
        }
        if (vacio(f.getNombres()) || !f.getNombres().matches(REGEX_NOMBRE)) {
            errores.add("Los nombres son obligatorios y solo pueden tener letras.");
        }
        if (vacio(f.getApellidos()) || !f.getApellidos().matches(REGEX_NOMBRE)) {
            errores.add("Los apellidos son obligatorios y solo pueden tener letras.");
        }
        if (f.getIdEstadoCivil() <= 0) {
            errores.add("Elige un estado civil.");
        }
        if (!"M".equals(f.getSexo()) && !"F".equals(f.getSexo())) {
            errores.add("Elige el sexo (M o F).");
        }
        if (!vacio(f.getTelefono()) && !f.getTelefono().matches(REGEX_TELEFONO)) {
            errores.add("El teléfono solo puede tener números (7 a 20).");
        }
        if (f.getDireccion() != null && f.getDireccion().length() > 150) {
            errores.add("La dirección no puede pasar de 150 caracteres.");
        }
        if (f.getFechaNacimiento() == null) {
            errores.add("La fecha de nacimiento es obligatoria (formato AAAA-MM-DD).");
        } else if (Period.between(f.getFechaNacimiento(), LocalDate.now()).getYears() < 18) {
            errores.add("El funcionario debe ser mayor de edad.");
        }

        if (!errores.isEmpty()) {
            throw new ValidacionException(String.join("\n", errores));
        }
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
