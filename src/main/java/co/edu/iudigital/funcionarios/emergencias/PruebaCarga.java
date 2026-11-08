package co.edu.iudigital.funcionarios.emergencias;

/**
 * Análisis de rendimiento: corre el sistema con 10, 50 y 200 emergencias y mide tiempos.
 * Al final agrega una corrida de 200 con el DOBLE de recursos para ver cómo escala.
 */
public class PruebaCarga {

    public static void main(String[] args) throws InterruptedException {
        Simulacion sim = new Simulacion();

        sim.ejecutar(config(20, 3, 2, 1, 4));      // calentamiento de la JVM (no se reporta)

        System.out.println("Atención de 10 a 30 ms por emergencia, 3 operadores\n");
        System.out.printf("%-12s %-22s %10s %12s %12s %12s  %s%n", "Emergencias",
                "Amb/Méd/Equipos/Desp", "Total ms", "Atend./seg", "Espera med.", "Espera máx.", "Correcto");

        Simulacion.Resultado ultimo = null;
        int[] cargas = {10, 50, 200};
        for (int n : cargas) {
            ultimo = imprimir(sim.ejecutar(config(n, 3, 2, 1, 4)));
        }
        Simulacion.Resultado doble = imprimir(sim.ejecutar(config(200, 6, 4, 2, 8)));

        System.out.println("\nEspera media (ms) por gravedad en la corrida de 200 con recursos base:");
        for (int g = 1; g <= 5; g++) {
            System.out.printf("  gravedad %d: %8.1f ms%n", g, ultimo.esperaMediaPorGravedad[g]);
        }
        System.out.printf("%nDuplicar recursos (200 emergencias): %d ms -> %d ms%n",
                ultimo.duracionMs, doble.duracionMs);
    }

    private static Simulacion.Config config(int n, int amb, int med, int eq, int desp) {
        Simulacion.Config c = new Simulacion.Config();
        c.emergencias = n;
        c.ambulancias = amb;
        c.medicos = med;
        c.equipos = eq;
        c.despachadores = desp;
        c.atencionMinMs = 10;
        c.atencionMaxMs = 30;
        c.pausaOperadorMs = 2;
        c.verbose = false;
        c.monitor = false;
        return c;
    }

    private static Simulacion.Resultado imprimir(Simulacion.Resultado r) {
        Simulacion.Config c = r.config;
        System.out.printf("%-12d %-22s %10d %12.1f %12.1f %12d  %s%n", c.emergencias,
                c.ambulancias + "/" + c.medicos + "/" + c.equipos + "/" + c.despachadores,
                r.duracionMs, r.throughput, r.esperaMediaMs, r.esperaMaxMs,
                r.correcto() ? "SÍ" : "NO");
        return r;
    }
}
