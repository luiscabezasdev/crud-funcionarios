package co.edu.iudigital.funcionarios.emergencias;

import java.util.concurrent.BlockingQueue;

/**
 * MONITOREO en tiempo real: un hilo aparte que cada cierto tiempo imprime el estado.
 * Solo OBSERVA (lee contadores); no modifica nada. Las lecturas son aproximadas porque
 * los demás hilos siguen trabajando mientras se imprime, y para un panel eso es suficiente.
 */
public class Monitor implements Runnable {

    private final BlockingQueue<Emergencia> cola;
    private final Recursos recursos;
    private final Estadisticas estadisticas;
    private final int total;
    private final int periodoMs;

    public Monitor(BlockingQueue<Emergencia> cola, Recursos recursos, Estadisticas estadisticas,
                   int total, int periodoMs) {
        this.cola = cola;
        this.recursos = recursos;
        this.estadisticas = estadisticas;
        this.total = total;
        this.periodoMs = periodoMs;
    }

    @Override
    public void run() {
        long inicio = System.nanoTime();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(periodoMs);
                double seg = (System.nanoTime() - inicio) / 1_000_000_000.0;
                System.out.printf("[MONITOR %5.1fs] en cola: %3d | en atención: %2d | atendidas: %3d/%d"
                                + " | libres -> ambulancias %d/%d, médicos %d/%d, equipos %d/%d%n",
                        seg, cola.size(), estadisticas.getEnAtencion(), estadisticas.getAtendidas(),
                        total, recursos.ambulanciasLibres(), recursos.getTotalAmbulancias(),
                        recursos.medicosLibres(), recursos.getTotalMedicos(),
                        recursos.equiposLibres(), recursos.getTotalEquipos());
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
