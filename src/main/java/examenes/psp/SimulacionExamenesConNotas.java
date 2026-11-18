package examenes.psp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/** Ejecuto exámenes concurrentes, presento estadísticas y exporto los resultados a CSV. */
public class SimulacionExamenesConNotas {
    private static final String ARCHIVO_CSV = "resultados_examenes.csv";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== CONFIGURACIÓN DE EXÁMENES CONCURRENTES ===");
        int numAlumnos = leerEnteroPositivo(scanner, "Introduce el número de alumnos: ");
        int numProfesores = leerEnteroPositivo(scanner, "Introduce el número de profesores: ");
        scanner.close();
        MonitorAula monitor = new MonitorAula(numProfesores);

        // Creo una factoría personalizada para nombrar los hilos como «Profesor-X».
        ThreadFactory factoriaProfesores = new ThreadFactory() {
            private final AtomicInteger secuencia = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Profesor-" + secuencia.getAndIncrement());
            }
        };
        ExecutorService poolProfesores = Executors.newFixedThreadPool(numProfesores, factoriaProfesores);
        List<Future<ResultadoExamen>> listaFutures = new ArrayList<>();
        System.out.printf("%n=== INICIO DE LA SESIÓN (%d Alumnos | %d Profesores) ===%n%n", numAlumnos, numProfesores);
        long inicioNs = System.nanoTime();

        // Envío todas las tareas Callable al pool fijo.
        for (int i = 1; i <= numAlumnos; i++) listaFutures.add(poolProfesores.submit(new AlumnoCallable(i, monitor)));
        // Cierro el pool: ya no acepta tareas y completa las que están pendientes.
        poolProfesores.shutdown();

        // Recojo los resultados mediante Future.
        List<ResultadoExamen> resultados = new ArrayList<>();
        for (Future<ResultadoExamen> future : listaFutures) {
            try {
                resultados.add(future.get());
            } catch (InterruptedException e) {
                System.err.println("Hilo principal interrumpido esperando resultados.");
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                System.err.println("Error ejecutando un examen: " + e.getCause());
            }
        }
        double tiempoTotalSegundos = (System.nanoTime() - inicioNs) / 1_000_000_000.0;
        mostrarResultadosYEstadisticas(resultados, tiempoTotalSegundos);
        exportarResultadosCSV(resultados, ARCHIVO_CSV);
    }

    /** Presento las calificaciones, las estadísticas del grupo y el tiempo de ejecución. */
    private static void mostrarResultadosYEstadisticas(List<ResultadoExamen> resultados, double tiempoTotalSegundos) {
        if (resultados.isEmpty()) {
            System.out.println("No se recogieron resultados de exámenes.");
            return;
        }
        System.out.println("\n==========================================================");
        System.out.println("               ACTA FINAL DE CALIFICACIONES               ");
        System.out.println("==========================================================");
        System.out.printf("%-12s | %-12s | %-10s | %-8s | %-10s%n", "ALUMNO", "PROFESOR", "DURACIÓN", "NOTA", "ESTADO");
        System.out.println("----------------------------------------------------------");
        double sumaNotas = 0.0;
        ResultadoExamen examenMax = resultados.get(0), examenMin = resultados.get(0);
        int aprobados = 0;
        for (ResultadoExamen res : resultados) {
            double nota = res.getNota();
            sumaNotas += nota;
            if (nota > examenMax.getNota()) examenMax = res;
            if (nota < examenMin.getNota()) examenMin = res;
            if (res.estaAprobado()) aprobados++;
            String estado = res.estaAprobado() ? "APROBADO" : "SUSPENSO";
            System.out.printf("%-12s | %-12s | %7.2f s | %7.2f  | %-10s%n",
                    res.getNombreAlumno(), res.getNombreProfesor(), res.getDuracionSegundos(), nota, estado);
        }
        double notaMedia = sumaNotas / resultados.size();
        System.out.println("\n=== ESTADÍSTICAS DEL GRUPO ===");
        System.out.printf("Nota máxima: %.2f (%s)%n", examenMax.getNota(), examenMax.getNombreAlumno());
        System.out.printf("Nota mínima: %.2f (%s)%n", examenMin.getNota(), examenMin.getNombreAlumno());
        System.out.printf("Nota media:  %.2f (Aprobados: %d/%d)%n", notaMedia, aprobados, resultados.size());
        System.out.printf("Tiempo total de ejecución: %.3f segundos%n", tiempoTotalSegundos);
    }

    /** Guardo los resultados en un CSV para analizarlos posteriormente. */
    private static void exportarResultadosCSV(List<ResultadoExamen> resultados, String rutaArchivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo, StandardCharsets.UTF_8))) {
            writer.write("Alumno,Profesor,Duracion_Segundos,Nota,Estado");
            writer.newLine();
            for (ResultadoExamen res : resultados) {
                String estado = res.estaAprobado() ? "APROBADO" : "SUSPENSO";
                writer.write(String.format(Locale.US, "%s,%s,%.2f,%.2f,%s", res.getNombreAlumno(),
                        res.getNombreProfesor(), res.getDuracionSegundos(), res.getNota(), estado));
                writer.newLine();
            }
            System.out.printf("Resultados exportados correctamente a '%s'%n", rutaArchivo);
        } catch (IOException e) {
            System.err.printf("Error al escribir el archivo CSV '%s': %s%n", rutaArchivo, e.getMessage());
        }
    }

    /** Solicito un entero positivo y valido las entradas incorrectas. */
    private static int leerEnteroPositivo(Scanner scanner, String mensaje) {
        int valor = 0;
        while (valor <= 0) {
            System.out.print(mensaje);
            if (scanner.hasNextInt()) {
                valor = scanner.nextInt();
                if (valor <= 0) System.out.println("El número debe ser mayor que 0.");
            } else {
                System.out.println("Entrada inválida. Introduce un número entero.");
                scanner.next();
            }
        }
        return valor;
    }
}
