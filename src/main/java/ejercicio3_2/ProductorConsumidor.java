package ejercicio3_2;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

/** Productor y consumidor coordinados por wait/notifyAll sobre un buffer acotado. */
public class ProductorConsumidor {
    private static final int CAPACIDAD = 5;
    private static final int ELEMENTOS = 20;

    private static final class Buffer {
        private final Queue<Integer> cola = new ArrayDeque<>();

        /** Espera mientras no haya espacio; el monitor protege la cola. */
        synchronized void poner(int valor) throws InterruptedException {
            while (cola.size() == CAPACIDAD) wait();
            cola.add(valor);
            System.out.println("Produce " + valor + " (buffer " + cola.size() + "/" + CAPACIDAD + ")");
            notifyAll();
        }

        /** Espera mientras el buffer esté vacío y devuelve el elemento más antiguo. */
        synchronized int tomar() throws InterruptedException {
            while (cola.isEmpty()) wait();
            int valor = cola.remove();
            System.out.println("Consume " + valor + " (buffer " + cola.size() + "/" + CAPACIDAD + ")");
            notifyAll();
            return valor;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Buffer buffer = new Buffer();
        Thread productor = new Thread(() -> {
            try {
                for (int i = 0; i < ELEMENTOS; i++) {
                    buffer.poner(ThreadLocalRandom.current().nextInt(100));
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "Productor");
        Thread consumidor = new Thread(() -> {
            try {
                for (int i = 0; i < ELEMENTOS; i++) {
                    buffer.tomar();
                    Thread.sleep(150);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "Consumidor");
        productor.start(); consumidor.start();
        productor.join(); consumidor.join();
    }
}
