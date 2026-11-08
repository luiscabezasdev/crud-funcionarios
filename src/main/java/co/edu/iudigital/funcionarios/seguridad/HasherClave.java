package co.edu.iudigital.funcionarios.seguridad;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Guarda contraseñas como HASH con SALT (PBKDF2, incluido en Java).
 *  - Hash: función de una sola vía; de "a9f3..." no se recupera la clave.
 *  - Salt: texto aleatorio distinto por usuario; dos claves iguales dan hashes distintos.
 *  - Iteraciones: hacen el cálculo deliberadamente lento, para frenar la fuerza bruta.
 * Formato guardado en la BD (una sola columna):  pbkdf2$ITERACIONES$SALT$HASH
 */
public final class HasherClave {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 120_000;
    private static final int BYTES_SALT = 16;
    private static final int BITS_HASH = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private HasherClave() {
    }

    public static String hashear(char[] clave) {
        byte[] salt = new byte[BYTES_SALT];
        ALEATORIO.nextBytes(salt);
        return formatear(ITERACIONES, salt, calcular(clave, salt, ITERACIONES));
    }

    /** Recalcula el hash con el salt guardado y lo compara con el guardado. */
    public static boolean verificar(char[] clave, String guardado) {
        String[] partes = guardado.split("\\$");
        if (partes.length != 4 || !"pbkdf2".equals(partes[0])) {
            return false;
        }
        int iteraciones = Integer.parseInt(partes[1]);
        byte[] salt = Base64.getDecoder().decode(partes[2]);
        byte[] esperado = Base64.getDecoder().decode(partes[3]);
        byte[] calculado = calcular(clave, salt, iteraciones);
        // isEqual tarda lo mismo acierte o falle: evita filtrar información por el tiempo
        return MessageDigest.isEqual(esperado, calculado);
    }

    private static byte[] calcular(char[] clave, byte[] salt, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(clave, salt, iteraciones, BITS_HASH);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 no disponible en esta JVM", e);
        } finally {
            spec.clearPassword();
        }
    }

    private static String formatear(int iteraciones, byte[] salt, byte[] hash) {
        Base64.Encoder b64 = Base64.getEncoder();
        return "pbkdf2$" + iteraciones + "$" + b64.encodeToString(salt) + "$" + b64.encodeToString(hash);
    }
}
