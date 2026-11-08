package co.edu.iudigital.funcionarios.emergencias;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Contadores compartidos por todos los hilos. Se usan clases "atómicas" (AtomicInteger,
 * LongAdder...) en vez de int normales: así no se pierden incrementos (ver DemoCondicionCarrera).
 */
public class Estadisticas {

    private final AtomicInteger creadas = new AtomicInteger();
    private final AtomicInteger enAtencion = new AtomicInteger();
    private final AtomicInteger atendidas = new AtomicInteger();
    private final LongAdder esperaTotalMs = new LongAdder();
    private final AtomicLong esperaMaxMs = new AtomicLong();
    private final LongAdder[] esperaPorGravedad = new LongAdder[6];
    private final LongAdder[] cantidadPorGravedad = new LongAdder[6];
    private final ConcurrentLinkedQueue<Integer> ordenAtencion = new ConcurrentLinkedQueue<>();

    public Estadisticas() {
        for (int i = 0; i < 6; i++) {
            esperaPorGravedad[i] = new LongAdder();
            cantidadPorGravedad[i] = new LongAdder();
        }
    }

    public void emergenciaCreada() {
        creadas.incrementAndGet();
    }

    public void empiezaAtencion() {
        enAtencion.incrementAndGet();
    }

    public void terminaAtencion(Emergencia e) {
        long espera = e.esperaMs();
        esperaTotalMs.add(espera);
        esperaMaxMs.accumulateAndGet(espera, Math::max);
        esperaPorGravedad[e.getGravedad()].add(espera);
        cantidadPorGravedad[e.getGravedad()].increment();
        ordenAtencion.add(e.getId());
        enAtencion.decrementAndGet();
        atendidas.incrementAndGet();
    }

    public int getCreadas() {
        return creadas.get();
    }

    public int getEnAtencion() {
        return enAtencion.get();
    }

    public int getAtendidas() {
        return atendidas.get();
    }

    public long getEsperaTotalMs() {
        return esperaTotalMs.sum();
    }

    public long getEsperaMaxMs() {
        return esperaMaxMs.get();
    }

    /** Espera media (ms) de las emergencias de una gravedad dada; 0 si no hubo ninguna. */
    public double esperaMediaGravedad(int gravedad) {
        long n = cantidadPorGravedad[gravedad].sum();
        return n == 0 ? 0 : (double) esperaPorGravedad[gravedad].sum() / n;
    }

    public ConcurrentLinkedQueue<Integer> getOrdenAtencion() {
        return ordenAtencion;
    }
}
