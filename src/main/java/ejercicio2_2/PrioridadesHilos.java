package ejercicio2_2;

/** Compara duración y orden de finalización con distintas prioridades. */
public class PrioridadesHilos {
    private static final class Trabajador extends Thread {
        private long duracionMs;

        private Trabajador(String nombre, int prioridad) {
            super(nombre);
            setPriority(prioridad);
        }

        @Override
        public void run() {
            long inicio = System.nanoTime();
            long contador = 0;
            for (int i = 0; i < 1_000_000; i++) contador++;
            duracionMs = (System.nanoTime() - inicio) / 1_000_000;
            System.out.println(getName() + " terminó (contador=" + contador + ").");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Trabajador minimo = new Trabajador("Prioridad mínima", Thread.MIN_PRIORITY);
        Trabajador normal = new Trabajador("Prioridad normal", Thread.NORM_PRIORITY);
        Trabajador maximo = new Trabajador("Prioridad máxima", Thread.MAX_PRIORITY);
        minimo.start(); normal.start(); maximo.start();
        minimo.join(); normal.join(); maximo.join();
        System.out.println(minimo.getName() + ": " + minimo.duracionMs + " ms");
        System.out.println(normal.getName() + ": " + normal.duracionMs + " ms");
        System.out.println(maximo.getName() + ": " + maximo.duracionMs + " ms");
        System.out.println("Las prioridades son sugerencias al planificador; no garantizan un orden.");
    }
}
