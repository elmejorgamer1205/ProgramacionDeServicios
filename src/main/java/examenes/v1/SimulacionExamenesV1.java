package examenes.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Organizo la simulación de exámenes con un pool fijo de profesores
 * y tareas Callable que me devuelven resultados mediante Future.
 */
public class SimulacionExamenesV1 {

    private static final int NUM_ALUMNOS = 10;
    private static final int NUM_PROFESORES = 3;

    public static void main(String[] args) {
        System.out.printf("=== INICIO DE EXÁMENES (%d Alumnos | %d Profesores) ===%n%n",
                NUM_ALUMNOS, NUM_PROFESORES);

        // Creo una factoría personalizada para nombrar cada hilo del pool como "Profesor-X".
        ThreadFactory factoriaProfesores = new ThreadFactory() {
            private final AtomicInteger contador = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Profesor-" + contador.getAndIncrement());
            }
        };

        ExecutorService poolProfesores = Executors.newFixedThreadPool(NUM_PROFESORES, factoriaProfesores);
        List<Future<String>> listaFutures = new ArrayList<>();

        // Envío todas las tareas al pool de hilos.
        for (int i = 1; i <= NUM_ALUMNOS; i++) {
            Callable<String> examenAlumno = new TareaExamen(i);
            Future<String> futureResultado = poolProfesores.submit(examenAlumno);
            listaFutures.add(futureResultado);
        }

        // Cierro el envío de tareas; el pool terminará cuando se completen las que ya están en cola.
        poolProfesores.shutdown();

        // Recojo los resultados mediante los objetos Future.
        List<String> actasFinales = new ArrayList<>();
        for (Future<String> future : listaFutures) {
            try {
                actasFinales.add(future.get());
            } catch (InterruptedException e) {
                System.err.println("La espera de resultados fue interrumpida.");
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                System.err.println("Error durante la ejecución de un examen: " + e.getCause());
            }
        }

        System.out.println("\n=== RESULTADOS FINALES RECOGIDOS DE FUTURE ===");
        for (String acta : actasFinales) {
            System.out.println(acta);
        }
    }
}
