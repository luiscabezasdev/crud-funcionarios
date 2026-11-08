package co.edu.iudigital.funcionarios.emergencias;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Recursos COMPARTIDOS: ambulancias, médicos y equipos especiales.
 * Un Semaphore es un contador de "permisos": acquire() toma uno (y espera si no hay),
 * release() lo devuelve. Con N permisos, nunca habrá más de N hilos usando el recurso.
 *
 * Para evitar un interbloqueo (deadlock) todos los hilos piden los recursos SIEMPRE en el
 * mismo orden: ambulancia -> médico -> equipo.
 */
public class Recursos {

    private final Semaphore ambulancias;
    private final Semaphore medicos;
    private final Semaphore equipos;
    private final int totalAmbulancias;
    private final int totalMedicos;
    private final int totalEquipos;

    // Contadores atómicos: sirven para COMPROBAR que nunca se pasó de la capacidad
    private final AtomicInteger ambulanciasEnUso = new AtomicInteger();
    private final AtomicInteger maxAmbulanciasEnUso = new AtomicInteger();
    private final AtomicInteger medicosEnUso = new AtomicInteger();
    private final AtomicInteger maxMedicosEnUso = new AtomicInteger();

    public Recursos(int ambulancias, int medicos, int equipos) {
        this.totalAmbulancias = ambulancias;
        this.totalMedicos = medicos;
        this.totalEquipos = equipos;
        // true = semáforo "justo": quien espera más, entra primero
        this.ambulancias = new Semaphore(ambulancias, true);
        this.medicos = new Semaphore(medicos, true);
        this.equipos = new Semaphore(equipos, true);
    }

    public void adquirir(Emergencia e) throws InterruptedException {
        boolean tieneAmbulancia = false;
        boolean tieneMedico = false;
        try {
            ambulancias.acquire();
            tieneAmbulancia = true;
            medicos.acquire();
            tieneMedico = true;
            if (e.requiereEquipo()) {
                equipos.acquire();
            }
        } catch (InterruptedException ex) {
            // Si nos interrumpen a medias, devolvemos lo que ya teníamos
            if (tieneMedico) {
                medicos.release();
            }
            if (tieneAmbulancia) {
                ambulancias.release();
            }
            throw ex;
        }
        maxAmbulanciasEnUso.accumulateAndGet(ambulanciasEnUso.incrementAndGet(), Math::max);
        maxMedicosEnUso.accumulateAndGet(medicosEnUso.incrementAndGet(), Math::max);
    }

    public void liberar(Emergencia e) {
        // Primero bajamos el contador y luego devolvemos el permiso (así el contador nunca excede)
        ambulanciasEnUso.decrementAndGet();
        medicosEnUso.decrementAndGet();
        if (e.requiereEquipo()) {
            equipos.release();
        }
        medicos.release();
        ambulancias.release();
    }

    public int ambulanciasLibres() {
        return ambulancias.availablePermits();
    }

    public int medicosLibres() {
        return medicos.availablePermits();
    }

    public int equiposLibres() {
        return equipos.availablePermits();
    }

    public int getTotalAmbulancias() {
        return totalAmbulancias;
    }

    public int getTotalMedicos() {
        return totalMedicos;
    }

    public int getTotalEquipos() {
        return totalEquipos;
    }

    public int getMaxAmbulanciasEnUso() {
        return maxAmbulanciasEnUso.get();
    }

    public int getMaxMedicosEnUso() {
        return maxMedicosEnUso.get();
    }
}
