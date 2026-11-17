package CarreraDeHilos;

import java.util.concurrent.ThreadLocalRandom;

public class Corredor implements Runnable {

    private final int totalTramos;
    private final ControlCarrera controlCarrera;

    /**
     * Creo una tarea de corredor asociada a un control de meta compartido.
     *
     * @param totalTramos    Número total de tramos que recorro en la carrera.
     * @param controlCarrera Monitor compartido que uso para registrar mi llegada.
     */
    public Corredor(int totalTramos, ControlCarrera controlCarrera) {
        this.totalTramos = totalTramos;
        this.controlCarrera = controlCarrera;
    }

    /**
     * Recorro los tramos uno a uno. Al terminar el último,
     * registro mi llegada y muestro por consola que he acabado.
     */
    @Override
    public void run() {
        String nombreHilo = Thread.currentThread().getName();

        try {
            for (int tramo = 1; tramo <= totalTramos; tramo++) {
                // Simulo el esfuerzo y el tiempo variable de cada tramo (entre 200 y 700 ms)
                int tiempoTramo = ThreadLocalRandom.current().nextInt(200, 701);
                Thread.sleep(tiempoTramo);

                System.out.printf("[%s] ha recorrido el tramo %d de %d%n",
                        nombreHilo, tramo, totalTramos);
            }

            int puesto = controlCarrera.registrarLlegada(nombreHilo);
            System.out.printf(">>> [%s] HA TERMINADO LA CARRERA (Puesto: %dº) <<<%n",
                    nombreHilo, puesto);

        } catch (InterruptedException e) {
            System.err.printf("[%s] fue interrumpido durante la carrera.%n", nombreHilo);
            Thread.currentThread().interrupt();
        }
    }
}
