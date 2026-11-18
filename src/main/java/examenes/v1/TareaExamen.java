package examenes.v1;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represento el examen de un alumno como una tarea que devuelve un resumen al terminar.
 */
public class TareaExamen implements Callable<String> {

    private final int idAlumno;

    /**
     * Creo la tarea para el alumno indicado.
     *
     * @param idAlumno Identificador numérico del alumno al que evalúo.
     */
    public TareaExamen(int idAlumno) {
        this.idAlumno = idAlumno;
    }

    @Override
    public String call() throws Exception {
        String nombreProfesor = Thread.currentThread().getName();
        // Elijo una duración aleatoria entre 1 y 5 segundos (1000 a 5000 ms).
        int duracionMs = ThreadLocalRandom.current().nextInt(1000, 5001);
        double duracionSegundos = duracionMs / 1000.0;

        System.out.printf("[INICIO] [%s] atiende al Alumno-%02d (duración estimada: %.2f s)%n",
                nombreProfesor, idAlumno, duracionSegundos);

        // Simulo la realización del examen.
        Thread.sleep(duracionMs);

        // Asigno una calificación aleatoria entre 4.0 y 10.0.
        double nota = ThreadLocalRandom.current().nextDouble(4.0, 10.0);

        System.out.printf("[FIN]    El Alumno-%02d ha terminado su examen con [%s]%n",
                idAlumno, nombreProfesor);

        return String.format("Alumno-%02d | Evaluador: %-10s | Duración: %.2f s | Nota: %.2f",
                idAlumno, nombreProfesor, duracionSegundos, nota);
    }
}
