package co.edu.iudigital.funcionarios.emergencias;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PRODUCTOR: simula a un operador que recibe llamadas y las deja en la cola.
 * Varios operadores trabajan al mismo tiempo, cada uno en su propio hilo.
 */
public class Operador implements Runnable {

    private final String nombre;
    private final int cantidad;
    private final int pausaMaxMs;
    private final AtomicInteger contadorIds;
    private final BlockingQueue<Emergencia> cola;
    private final Estadisticas estadisticas;
    private final boolean verbose;

    public Operador(String nombre, int cantidad, int pausaMaxMs, AtomicInteger contadorIds,
                    BlockingQueue<Emergencia> cola, Estadisticas estadisticas, boolean verbose) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.pausaMaxMs = pausaMaxMs;
        this.contadorIds = contadorIds;
        this.cola = cola;
        this.estadisticas = estadisticas;
        this.verbose = verbose;
    }

    @Override
    public void run() {
        ThreadLocalRandom azar = ThreadLocalRandom.current();
        try {
            for (int i = 0; i < cantidad; i++) {
                Emergencia e = new Emergencia(contadorIds.incrementAndGet(),
                        sortearGravedad(azar), 1 + azar.nextInt(30));
                cola.put(e);                       // put() es seguro entre hilos
                estadisticas.emergenciaCreada();
                if (verbose) {
                    System.out.println("[" + nombre + "] recibe emergencia " + e);
                }
                if (pausaMaxMs > 0) {
                    Thread.sleep(azar.nextInt(pausaMaxMs + 1));
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();    // buena práctica: conservar la interrupción
        }
    }

    /** Más casos de gravedad media que críticos, como en la vida real. */
    private int sortearGravedad(ThreadLocalRandom azar) {
        int r = azar.nextInt(100);
        if (r < 10) {
            return 1;
        }
        if (r < 35) {
            return 2;
        }
        if (r < 65) {
            return 3;
        }
        if (r < 90) {
            return 4;
        }
        return 5;
    }
}
