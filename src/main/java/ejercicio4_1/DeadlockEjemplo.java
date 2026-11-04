package ejercicio4_1;

/** Ejemplo de interbloqueo y corrección mediante orden global de adquisición. */
public class DeadlockEjemplo {
    private static final Object RECURSO_A = new Object();
    private static final Object RECURSO_B = new Object();

    /**
     * Forma el ciclo de espera: T1 retiene A y espera B, mientras T2 retiene B y espera A.
     * No se ejecuta desde {@link #main(String[])} porque los hilos quedarían bloqueados.
     */
    public static void ejecutarConDeadlock() {
        Thread t1 = new Thread(() -> {
            synchronized (RECURSO_A) {
                System.out.println("Hilo 1: bloqueó A");
                dormir(100);
                synchronized (RECURSO_B) { System.out.println("Hilo 1: bloqueó B"); }
            }
        }, "Hilo-1");
        Thread t2 = new Thread(() -> {
            synchronized (RECURSO_B) {
                System.out.println("Hilo 2: bloqueó B");
                dormir(100);
                synchronized (RECURSO_A) { System.out.println("Hilo 2: bloqueó A"); }
            }
        }, "Hilo-2");
        t1.start();
        t2.start();
    }

    /** Bloquea ambos recursos siempre en el orden A y después B, evitando ciclos de espera. */
    public static void ejecutarSinDeadlock() throws InterruptedException {
        Runnable tarea = () -> {
            synchronized (RECURSO_A) {
                System.out.println(Thread.currentThread().getName() + ": bloqueó A");
                dormir(100);
                synchronized (RECURSO_B) {
                    System.out.println(Thread.currentThread().getName() + ": bloqueó B");
                }
            }
        };
        Thread t1 = new Thread(tarea, "Hilo-1");
        Thread t2 = new Thread(tarea, "Hilo-2");
        t1.start(); t2.start();
        t1.join(); t2.join();
    }

    private static void dormir(long milisegundos) {
        try { Thread.sleep(milisegundos); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void main(String[] args) throws InterruptedException {
        ejecutarSinDeadlock();
    }
}
