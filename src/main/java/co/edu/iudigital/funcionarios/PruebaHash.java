package co.edu.iudigital.funcionarios;

import co.edu.iudigital.funcionarios.seguridad.HasherClave;

/**
 * EXPERIMENTO DEL HASH (sin base de datos): Shift+F6 en NetBeans.
 * Antes de ejecutar, predice: ¿los dos hashes de la misma clave salen iguales o distintos? ¿Por qué?
 */
public class PruebaHash {

    public static void main(String[] args) {
        char[] clave = "crud2026".toCharArray();

        String h1 = HasherClave.hashear(clave);
        String h2 = HasherClave.hashear(clave);
        System.out.println("Hash 1: " + h1);
        System.out.println("Hash 2: " + h2);
        System.out.println("¿Son iguales? " + h1.equals(h2));

        System.out.println("\nClave correcta contra hash 1:   " + HasherClave.verificar("crud2026".toCharArray(), h1));
        System.out.println("Clave correcta contra hash 2:   " + HasherClave.verificar("crud2026".toCharArray(), h2));
        System.out.println("Clave incorrecta contra hash 1: " + HasherClave.verificar("crud2027".toCharArray(), h1));
    }
}
