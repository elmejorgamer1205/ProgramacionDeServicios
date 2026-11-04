package ejercicio1_2;

/** Ejecuta los ejemplos de creación de hilos del ejercicio 1.2. */
public class Main {
    public static void main(String[] args) {
        Thread hiloHeredado = new HiloThread("Hilo-Thread");
        Thread hiloRunnable = new Thread(new HiloRunnable("Hilo-Runnable"));
        hiloHeredado.start();
        hiloRunnable.start();
    }
}
