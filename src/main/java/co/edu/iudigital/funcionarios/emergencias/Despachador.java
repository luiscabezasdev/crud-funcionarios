package co.edu.iudigital.funcionarios.emergencias;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * CONSUMIDOR: toma la emergencia más urgente de la cola, reserva ambulancia + médico (+ equipo)
 * y la atiende. Varios despachadores compiten por los mismos recursos.
 */
public class Despachador implements Runnable {

    private final String nombre;
    private final BlockingQueue<Emergencia> cola;
    private final Recursos recursos;
    private final Estadisticas estadisticas;
    private final int atencionMinMs;
    private final int atencionMaxMs;
    private final boolean verbose;

    public Despachador(String nombre, BlockingQueue<Emergencia> cola, Recursos recursos,
                       Estadisticas estadisticas, int atencionMinMs, int atencionMaxMs,
                       boolean verbose) {
        this.nombre = nombre;
        this.cola = cola;
        this.recursos = recursos;
        this.estadisticas = estadisticas;
        this.atencionMinMs = atencionMinMs;
        this.atencionMaxMs = atencionMaxMs;
        this.verbose = verbose;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Emergencia e = cola.take();        // se bloquea si la cola está vacía
                if (e.esPildora()) {
                    return;                        // señal de fin
                }
                recursos.adquirir(e);              // se bloquea si no hay ambulancia/médico libre
                try {
                    e.marcarInicioAtencion();
                    estadisticas.empiezaAtencion();
                    if (verbose) {
                        System.out.println("[" + nombre + "] atiende " + e
                                + " tras esperar " + e.esperaMs() + " ms"
                                + (e.requiereEquipo() ? " (con equipo especial)" : ""));
                    }
                    Thread.sleep(ThreadLocalRandom.current()
                            .nextInt(atencionMinMs, atencionMaxMs + 1));   // simula la atención
                    estadisticas.terminaAtencion(e);
                } finally {
                    recursos.liberar(e);           // SIEMPRE se devuelven los recursos
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
