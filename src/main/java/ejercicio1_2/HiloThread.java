package ejercicio1_2;

/** Hilo creado mediante herencia de {@link Thread}. */
public class HiloThread extends Thread {
    public HiloThread(String nombre) {
        super(nombre);
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(getName() + " (Thread), impresión " + (i + 1));
            if (i < 4) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    interrupt();
                    return;
                }
            }
        }
    }
}
