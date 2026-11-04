package ejercicio3_3;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

/** Simula ocho coches compartiendo tres plazas mediante un semáforo. */
public class Aparcamiento {
    public static void main(String[] args) throws InterruptedException {
        Semaphore plazas = new Semaphore(3, true);
        Thread[] coches = new Thread[8];
        for (int i = 0; i < coches.length; i++) {
            int id = i + 1;
            coches[i] = new Thread(() -> {
                boolean adquirido = false;
                try {
                    System.out.println("Coche " + id + " espera plaza.");
                    plazas.acquire();
                    adquirido = true;
                    System.out.println("Coche " + id + " entra.");
                    Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3001));
                    System.out.println("Coche " + id + " sale.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    if (adquirido) plazas.release();
                }
            }, "Coche-" + id);
            coches[i].start();
        }
        for (Thread coche : coches) coche.join();
    }
}
