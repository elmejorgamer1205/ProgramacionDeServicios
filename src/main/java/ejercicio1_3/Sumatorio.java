package ejercicio1_3;

import java.util.Scanner;

/** Calcula dos sumatorios concurrentemente y espera ambos resultados con join. */
public class Sumatorio {
    private static final class Calculador extends Thread {
        private final long limite;
        private long resultado;

        private Calculador(String nombre, long limite) {
            super(nombre);
            this.limite = limite;
        }

        @Override
        public void run() {
            long suma = 0;
            for (long i = 1; i <= limite; i++) suma += i;
            resultado = suma;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Introduce N1: ");
            long n1 = scanner.nextLong();
            System.out.print("Introduce N2: ");
            long n2 = scanner.nextLong();
            if (n1 < 0 || n2 < 0) throw new IllegalArgumentException("N debe ser >= 0");

            long inicio = System.nanoTime();
            Calculador hilo1 = new Calculador("Sumatorio-N1", n1);
            Calculador hilo2 = new Calculador("Sumatorio-N2", n2);
            hilo1.start();
            hilo2.start();
            hilo1.join();
            hilo2.join();
            long milisegundos = (System.nanoTime() - inicio) / 1_000_000;
            System.out.println("Suma de 1 a " + n1 + ": " + hilo1.resultado);
            System.out.println("Suma de 1 a " + n2 + ": " + hilo2.resultado);
            System.out.println("Tiempo total: " + milisegundos + " ms");
        }
    }
}
