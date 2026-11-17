package CarreraDeHilos;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Representa a un corredor que avanza por una serie de tramos de manera concurrente.
 */
public class Corredor implements Runnable {

    private final int totalTramos;
    private final ControlCarrera controlCarrera;

    /**
     * Construye una tarea de corredor asociada a un control de meta compartido.
     *
     * @param totalTramos    Número total de tramos que componen la carrera.
     * @param controlCarrera Monitor compartido para registrar la llegada a meta.
     */
    public Corredor(int totalTramos, ControlCarrera controlCarrera) {
        this.totalTramos = totalTramos;
        this.controlCarrera = controlCarrera;
    }

    /**
     * Ejecuta el recorrido tramo a tramo. Al finalizar el último tramo,
     * notifica su llegada y muestra por consola que ha terminado.
     */
    @Override
    public void run() {
        String nombreHilo = Thread.currentThread().getName();

        try {
            for (int tramo = 1; tramo <= totalTramos; tramo++) {
                // Simula el esfuerzo y tiempo variable en recorrer cada tramo (entre 200 y 700 ms)
                int tiempoTramo = ThreadLocalRandom.current().nextInt(200, 701);
                Thread.sleep(tiempoTramo);

                System.out.printf("[%s] ha recorrido el tramo %d de %d%n",
                        nombreHilo, tramo, totalTramos);
            }

            // Al completar todos los tramos, registra su llegada en el monitor
            int puesto = controlCarrera.registrarLlegada(nombreHilo);
            System.out.printf(">>> [%s] HA TERMINADO LA CARRERA (Puesto: %dº) <<<%n",
                    nombreHilo, puesto);

        } catch (InterruptedException e) {
            System.err.printf("[%s] fue interrumpido durante la carrera.%n", nombreHilo);
            Thread.currentThread().interrupt();
        }
    }
}
