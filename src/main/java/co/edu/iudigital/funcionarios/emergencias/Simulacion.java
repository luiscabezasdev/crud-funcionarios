package co.edu.iudigital.funcionarios.emergencias;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Arma y ejecuta todo el sistema:
 *
 *   Operadores (productores) --> PriorityBlockingQueue --> Despachadores (consumidores)
 *                                                               |
 *                                            Recursos (Semaphore: ambulancias, médicos, equipos)
 *   Monitor (hilo aparte) observa la cola, los recursos y los contadores.
 *
 * Patrones: Productor-Consumidor, Thread Pool (ExecutorService) y Poison Pill (fin ordenado).
 */
public class Simulacion {

    /** Parámetros de una corrida; tienen valores por defecto y se cambian por campo. */
    public static class Config {
        public int emergencias = 40;
        public int operadores = 3;
        public int despachadores = 4;
        public int ambulancias = 3;
        public int medicos = 2;
        public int equipos = 1;
        public int atencionMinMs = 150;
        public int atencionMaxMs = 400;
        public int pausaOperadorMs = 100;
        public boolean verbose = true;
        public boolean monitor = true;
    }

    /** Lo que se midió en una corrida. */
    public static class Resultado {
        public final Config config;
        public final int creadas;
        public final int atendidas;
        public final long duracionMs;
        public final double throughput;         // emergencias atendidas por segundo
        public final double esperaMediaMs;
        public final long esperaMaxMs;
        public final int maxAmbulanciasEnUso;
        public final int maxMedicosEnUso;
        public final double[] esperaMediaPorGravedad = new double[6];

        Resultado(Config config, Estadisticas est, Recursos recursos, long duracionMs) {
            this.config = config;
            this.creadas = est.getCreadas();
            this.atendidas = est.getAtendidas();
            this.duracionMs = duracionMs;
            this.throughput = duracionMs == 0 ? 0 : atendidas * 1000.0 / duracionMs;
            this.esperaMediaMs = atendidas == 0 ? 0 : (double) est.getEsperaTotalMs() / atendidas;
            this.esperaMaxMs = est.getEsperaMaxMs();
            this.maxAmbulanciasEnUso = recursos.getMaxAmbulanciasEnUso();
            this.maxMedicosEnUso = recursos.getMaxMedicosEnUso();
            for (int g = 1; g <= 5; g++) {
                esperaMediaPorGravedad[g] = est.esperaMediaGravedad(g);
            }
        }

        /** Las dos comprobaciones de corrección: no se perdió nada y nunca se sobrepasó la capacidad. */
        public boolean correcto() {
            return atendidas == config.emergencias
                    && creadas == config.emergencias
                    && maxAmbulanciasEnUso <= config.ambulancias
                    && maxMedicosEnUso <= config.medicos;
        }
    }

    public Resultado ejecutar(Config cfg) throws InterruptedException {
        PriorityBlockingQueue<Emergencia> cola = new PriorityBlockingQueue<>();
        Recursos recursos = new Recursos(cfg.ambulancias, cfg.medicos, cfg.equipos);
        Estadisticas estadisticas = new Estadisticas();
        AtomicInteger ids = new AtomicInteger();

        long inicio = System.nanoTime();

        Thread monitor = null;
        if (cfg.monitor) {
            monitor = new Thread(new Monitor(cola, recursos, estadisticas, cfg.emergencias, 500),
                    "monitor");
            monitor.setDaemon(true);
            monitor.start();
        }

        // 1) Los despachadores arrancan primero y esperan trabajo
        ExecutorService poolDespachadores = Executors.newFixedThreadPool(cfg.despachadores);
        for (int i = 1; i <= cfg.despachadores; i++) {
            poolDespachadores.execute(new Despachador("Despachador-" + i, cola, recursos,
                    estadisticas, cfg.atencionMinMs, cfg.atencionMaxMs, cfg.verbose));
        }

        // 2) Los operadores generan las emergencias (reparto del total entre ellos)
        ExecutorService poolOperadores = Executors.newFixedThreadPool(cfg.operadores);
        int base = cfg.emergencias / cfg.operadores;
        int resto = cfg.emergencias % cfg.operadores;
        for (int i = 1; i <= cfg.operadores; i++) {
            int cantidad = base + (i <= resto ? 1 : 0);
            poolOperadores.execute(new Operador("Operador-" + i, cantidad, cfg.pausaOperadorMs, ids,
                    cola, estadisticas, cfg.verbose));
        }
        poolOperadores.shutdown();
        poolOperadores.awaitTermination(5, TimeUnit.MINUTES);

        // 3) Ya no llegan más: una "píldora" por despachador para que terminen ordenadamente
        for (int i = 0; i < cfg.despachadores; i++) {
            cola.put(Emergencia.pildora());
        }
        poolDespachadores.shutdown();
        poolDespachadores.awaitTermination(5, TimeUnit.MINUTES);

        long duracionMs = (System.nanoTime() - inicio) / 1_000_000;
        if (monitor != null) {
            monitor.interrupt();
        }
        return new Resultado(cfg, estadisticas, recursos, duracionMs);
    }
}
