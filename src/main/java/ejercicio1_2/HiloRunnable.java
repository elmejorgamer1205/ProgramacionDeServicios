package ejercicio1_2;

/** Tarea de hilo creada mediante la interfaz {@link Runnable}. */
public class HiloRunnable implements Runnable {
    private final String nombre;

    public HiloRunnable(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(nombre + " (Runnable), impresión " + (i + 1));
            if (i < 4) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
