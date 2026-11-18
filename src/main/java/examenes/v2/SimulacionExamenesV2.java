package examenes.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Amplío la simulación para configurar alumnos y profesores, mostrar su estado
 * en tiempo real y medir el tiempo total de ejecución.
 */
public class SimulacionExamenesV2 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int numAlumnos = leerEnteroPositivo(scanner, "Introduce el número de alumnos: ");
        int numProfesores = leerEnteroPositivo(scanner, "Introduce el número de profesores: ");
        scanner.close();

        MonitorAula monitor = new MonitorAula(numProfesores);

        // Creo una factoría personalizada para nombrar cada hilo como "Profesor-X".
        ThreadFactory factoriaProfesores = new ThreadFactory() {
            private final AtomicInteger contador = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Profesor-" + contador.getAndIncrement());
            }
        };

        ExecutorService poolProfesores = Executors.newFixedThreadPool(numProfesores, factoriaProfesores);
        List<Future<String>> listaFutures = new ArrayList<>();

        System.out.printf("%n=== INICIANDO SIMULACIÓN (%d Alumnos, %d Profesores) ===%n%n",
                numAlumnos, numProfesores);

        // Inicio la medición del tiempo total de ejecución.
        long tiempoInicioNs = System.nanoTime();

        for (int i = 1; i <= numAlumnos; i++) {
            listaFutures.add(poolProfesores.submit(new TareaExamenMonitorizada(i, monitor)));
        }

        poolProfesores.shutdown();

        List<String> resultados = new ArrayList<>();
        for (Future<String> future : listaFutures) {
            try {
                resultados.add(future.get());
            } catch (InterruptedException e) {
                System.err.println("Hilo principal interrumpido.");
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                System.err.println("Fallo en ejecución de examen: " + e.getCause());
            }
        }

        // Finalizo la medición cuando ya he completado todos los Future.
        long tiempoFinNs = System.nanoTime();
        double tiempoTotalSegundos = (tiempoFinNs - tiempoInicioNs) / 1_000_000_000.0;

        System.out.println("\n=== ACTA FINAL DE CALIFICACIONES (FUTURE) ===");
        for (String res : resultados) {
            System.out.println(res);
        }

        System.out.printf("%n>>> TIEMPO TOTAL DE EJECUCIÓN DE TODOS LOS EXÁMENES: %.3f segundos <<<%n",
                tiempoTotalSegundos);
    }

    /**
     * Solicito por consola un entero positivo y valido la entrada.
     */
    private static int leerEnteroPositivo(Scanner scanner, String mensaje) {
        int valor = 0;
        while (valor <= 0) {
            System.out.print(mensaje);
            if (scanner.hasNextInt()) {
                valor = scanner.nextInt();
                if (valor <= 0) {
                    System.out.println("El valor debe ser mayor que 0.");
                }
            } else {
                System.out.println("Entrada no válida. Introduce un número entero.");
                scanner.next();
            }
        }
        return valor;
    }
}
