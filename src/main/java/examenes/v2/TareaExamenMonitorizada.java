package examenes.v2;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represento la evaluación individual de un alumno como una tarea Callable.
 */
public class TareaExamenMonitorizada implements Callable<String> {

    private final int idAlumno;
    private final MonitorAula monitor;

    public TareaExamenMonitorizada(int idAlumno, MonitorAula monitor) {
        this.idAlumno = idAlumno;
        this.monitor = monitor;
    }

    @Override
    public String call() throws Exception {
        String nombreProfesor = Thread.currentThread().getName();
        String etiquetaAlumno = String.format("Alumno-%02d", idAlumno);
        int duracionMs = ThreadLocalRandom.current().nextInt(1000, 5001);
        double duracionSegundos = duracionMs / 1000.0;

        monitor.registrarEvento(
                nombreProfesor,
                "Examinando " + etiquetaAlumno,
                String.format("[INICIO] %s comienza el examen de %s (%.2f s)",
                        nombreProfesor, etiquetaAlumno, duracionSegundos)
        );

        Thread.sleep(duracionMs);

        double nota = ThreadLocalRandom.current().nextDouble(4.0, 10.0);

        monitor.registrarEvento(
                nombreProfesor,
                "LIBRE",
                String.format("[FIN]    %s ha terminado el examen con %s",
                        etiquetaAlumno, nombreProfesor)
        );

        return String.format("%s | Evaluador: %-10s | Duración: %.2f s | Nota: %.2f",
                etiquetaAlumno, nombreProfesor, duracionSegundos, nota);
    }
}
