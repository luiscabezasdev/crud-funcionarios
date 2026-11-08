package co.edu.iudigital.funcionarios.emergencias;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demostración mínima de una CONDICIÓN DE CARRERA.
 * 4 hilos suman 1 un total de 250.000 veces cada uno: el resultado correcto es 1.000.000.
 *
 *   contador++ parece una sola operación, pero son tres: leer, sumar, escribir.
 *   Si dos hilos leen el mismo valor a la vez, ambos escriben el mismo resultado y se pierde un incremento.
 */
public class DemoCondicionCarrera {

    private static final int HILOS = 4;
    private static final int VECES = 250_000;

    private static int sinProteccion = 0;
    private static int conSynchronized = 0;
    private static final AtomicInteger conAtomic = new AtomicInteger();
    private static final Object CANDADO = new Object();

    public static void main(String[] args) throws InterruptedException {
        int esperado = HILOS * VECES;
        System.out.println("Resultado correcto: " + esperado + "\n");

        correr(() -> sinProteccion++);
        System.out.println("1) Sin protección:  " + sinProteccion
                + "   <- se perdieron " + (esperado - sinProteccion) + " incrementos");

        correr(() -> {
            synchronized (CANDADO) {               // solo un hilo a la vez entra aquí
                conSynchronized++;
            }
        });
        System.out.println("2) synchronized:    " + conSynchronized);

        correr(conAtomic::incrementAndGet);        // operación atómica hecha por el hardware
        System.out.println("3) AtomicInteger:   " + conAtomic.get());
    }

    private static void correr(Runnable incremento) throws InterruptedException {
        Thread[] hilos = new Thread[HILOS];
        for (int i = 0; i < HILOS; i++) {
            hilos[i] = new Thread(() -> {
                for (int j = 0; j < VECES; j++) {
                    incremento.run();
                }
            });
            hilos[i].start();
        }
        for (Thread h : hilos) {
            h.join();                              // esperar a que todos terminen
        }
    }
}
