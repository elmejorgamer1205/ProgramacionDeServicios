package examenes.psp;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

/** Represento a un alumno que realiza su examen como una tarea concurrente. */
public class AlumnoCallable implements Callable<ResultadoExamen> {
    private final int idAlumno;
    private final MonitorAula monitor;

    /** Creo la tarea asociada a un alumno. */
    public AlumnoCallable(int idAlumno, MonitorAula monitor) {
        this.idAlumno = idAlumno;
        this.monitor = monitor;
    }

    @Override
    public ResultadoExamen call() throws Exception {
        String nombreProfesor = Thread.currentThread().getName();
        String nombreAlumno = String.format("Alumno-%02d", idAlumno);
        // Genero una duración aleatoria entre 1 y 5 segundos.
        int duracionMs = ThreadLocalRandom.current().nextInt(1000, 5001);
        double duracionSegundos = duracionMs / 1000.0;
        monitor.notificarInicio(nombreProfesor, nombreAlumno, duracionSegundos);
        // Simulo el tiempo que tarda el alumno en completar el examen.
        Thread.sleep(duracionMs);
        // Genero una nota entre 0 y 10 y la redondeo a dos decimales.
        double notaBruta = ThreadLocalRandom.current().nextDouble(0.0, 10.001);
        double notaFinal = Math.min(10.0, Math.round(notaBruta * 100.0) / 100.0);
        ResultadoExamen resultado = new ResultadoExamen(idAlumno, nombreAlumno, nombreProfesor, duracionSegundos, notaFinal);
        monitor.notificarFin(resultado);
        return resultado;
    }
}
