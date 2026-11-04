package ejercicio2_1;

/** Muestra los estados principales de un hilo durante su ciclo de vida. */
public class EstadosHilo {
    public static void main(String[] args) throws InterruptedException {
        Thread hilo = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("Iteración " + (i + 1));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "Hilo-Estados");

        System.out.println("Recién creado: " + hilo.getState());
        hilo.start();
        System.out.println("Tras start(): " + hilo.getState());
        while (hilo.getState() != Thread.State.TIMED_WAITING) Thread.yield();
        System.out.println("Durante sleep(): " + hilo.getState());
        hilo.join();
        System.out.println("Al finalizar: " + hilo.getState());
    }
}
