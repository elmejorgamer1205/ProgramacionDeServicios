package CarreraDeHilos;

import java.util.ArrayList;
import java.util.List;

public class SimulacionCarrera {

    private static final int NUM_CORREDORES = 4;
    private static final int TOTAL_TRAMOS = 5;

    public static void main(String[] args) {
        ControlCarrera controlCarrera = new ControlCarrera();
        List<Thread> hilosCorredores = new ArrayList<>();

        System.out.println("=== INICIO DE LA CARRERA (" + TOTAL_TRAMOS + " tramos) ===");

        // Creo e inicio los hilos de los corredores
        for (int i = 1; i <= NUM_CORREDORES; i++) {
            Runnable tareaCorredor = new Corredor(TOTAL_TRAMOS, controlCarrera);
            Thread hilo = new Thread(tareaCorredor, "Hilo-Corredor-" + i);
            hilosCorredores.add(hilo);
            hilo.start();
        }

        // Espero a que todos los corredores terminen su ejecución
        for (Thread hilo : hilosCorredores) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                System.err.println("El hilo principal fue interrumpido esperando a " + hilo.getName());
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("==========================================");
        System.out.println("EL HILO GANADOR ES: " + controlCarrera.getHiloGanador());
        System.out.println("==========================================");
    }
}
