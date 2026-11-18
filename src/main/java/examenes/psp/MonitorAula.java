package examenes.psp;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/** Sincronizo el estado de los profesores y evito que se entrelacen las líneas de consola. */
public class MonitorAula {
    private final Map<String, String> estadoProfesores;

    /** Inicializo el monitor y registro todos los profesores como disponibles. */
    public MonitorAula(int numProfesores) {
        estadoProfesores = new TreeMap<>(
                Comparator.comparingInt(nombre -> Integer.parseInt(nombre.split("-")[1]))
        );
        for (int i = 1; i <= numProfesores; i++) estadoProfesores.put("Profesor-" + i, null);
    }

    /** Registro el inicio del examen y muestro el estado actualizado del aula. */
    public synchronized void notificarInicio(String nombreProfesor, String nombreAlumno, double duracionSeg) {
        estadoProfesores.put(nombreProfesor, nombreAlumno);
        System.out.printf("[INICIO] %s comienza a atender a %s (duración: %.2f s)%n",
                nombreProfesor, nombreAlumno, duracionSeg);
        imprimirPanelTiempoReal();
    }

    /** Registro el fin del examen, libero al profesor y muestro la nota obtenida. */
    public synchronized void notificarFin(ResultadoExamen resultado) {
        estadoProfesores.put(resultado.getNombreProfesor(), null);
        System.out.printf("[FIN]    %s termina su examen con %s (queda LIBRE) -> Nota: %.2f%n",
                resultado.getNombreAlumno(), resultado.getNombreProfesor(), resultado.getNota());
        imprimirPanelTiempoReal();
    }

    /** Imprimo de forma atómica la ocupación actual de todos los profesores. */
    private void imprimirPanelTiempoReal() {
        StringBuilder panel = new StringBuilder("   -> Estado Aula: ");
        int contador = 0;
        for (Map.Entry<String, String> entrada : estadoProfesores.entrySet()) {
            if (contador++ > 0) panel.append(" | ");
            String alumno = entrada.getValue();
            panel.append(entrada.getKey()).append(": ");
            if (alumno == null) panel.append("[LIBRE]");
            else panel.append("[").append(alumno).append("]");
        }
        System.out.println(panel);
    }
}
