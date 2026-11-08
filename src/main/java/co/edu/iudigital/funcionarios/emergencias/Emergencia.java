package co.edu.iudigital.funcionarios.emergencias;

/**
 * Una emergencia médica que espera ser atendida.
 *
 * Implementa Comparable para que la PriorityBlockingQueue la ordene sola:
 * la más urgente sale primero.
 *
 * PRIORIZACIÓN (3 factores):
 *   puntaje = 100 * gravedad  -  2 * distanciaKm  +  100 * segundosEsperando
 * El último término es el "envejecimiento": cada segundo de espera suma lo mismo que un nivel
 * de gravedad, así una emergencia leve no espera para siempre (evita la inanición).
 *
 * Truco: la diferencia de puntaje entre dos emergencias NO cambia con el tiempo (las dos
 * envejecen igual). Por eso basta calcular una "clave" fija al crearla y la cola no necesita
 * reordenarse.
 */
public class Emergencia implements Comparable<Emergencia> {

    public static final int PESO_GRAVEDAD = 100;
    public static final int PESO_DISTANCIA = 2;
    public static final int PESO_ESPERA_POR_SEGUNDO = 100;

    private static final long ORIGEN_NS = System.nanoTime();

    private final int id;
    private final int gravedad;      // 1 (leve) a 5 (crítica)
    private final int distanciaKm;
    private final long creadaNs;
    private final double clave;
    private volatile long inicioAtencionNs;

    public Emergencia(int id, int gravedad, int distanciaKm) {
        this.id = id;
        this.gravedad = gravedad;
        this.distanciaKm = distanciaKm;
        this.creadaNs = System.nanoTime();
        double segundos = (creadaNs - ORIGEN_NS) / 1_000_000_000.0;
        this.clave = PESO_GRAVEDAD * gravedad - PESO_DISTANCIA * distanciaKm
                - PESO_ESPERA_POR_SEGUNDO * segundos;
    }

    /** Constructor privado: solo para la "píldora venenosa" (ver pildora()). */
    private Emergencia() {
        this.id = -1;
        this.gravedad = 0;
        this.distanciaKm = 0;
        this.creadaNs = 0;
        this.clave = Double.NEGATIVE_INFINITY;
    }

    /**
     * Señal de fin: tiene la prioridad más baja, así que sale de la cola cuando ya no queda
     * ninguna emergencia real. Cada despachador que la recibe termina su trabajo.
     */
    public static Emergencia pildora() {
        return new Emergencia();
    }

    public boolean esPildora() {
        return id < 0;
    }

    /** Las graves necesitan además un equipo especial (desfibrilador, ventilador...). */
    public boolean requiereEquipo() {
        return gravedad >= 4;
    }

    public void marcarInicioAtencion() {
        this.inicioAtencionNs = System.nanoTime();
    }

    public long esperaMs() {
        return (inicioAtencionNs - creadaNs) / 1_000_000;
    }

    public int getId() {
        return id;
    }

    public int getGravedad() {
        return gravedad;
    }

    public int getDistanciaKm() {
        return distanciaKm;
    }

    @Override
    public int compareTo(Emergencia otra) {
        int c = Double.compare(otra.clave, this.clave);   // mayor clave = sale primero
        return c != 0 ? c : Integer.compare(this.id, otra.id);
    }

    @Override
    public String toString() {
        return "#" + id + " (gravedad " + gravedad + ", " + distanciaKm + " km)";
    }
}
