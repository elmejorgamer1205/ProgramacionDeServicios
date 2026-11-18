package examenes.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Mantengo y muestro de forma atómica el estado en tiempo real de los profesores del aula.
 */
public class MonitorAula {

    private final Map<String, String> estadoProfesores = new TreeMap<>();

    /**
     * Inicializo todos los profesores como disponibles.
     *
     * @param numProfesores Número total de profesores que gestiono en el pool.
     */
    public MonitorAula(int numProfesores) {
        for (int i = 1; i <= numProfesores; i++) {
            estadoProfesores.put("Profesor-" + i, "LIBRE");
        }
    }

    /**
     * Actualizo la asignación de un profesor e imprimo el estado del aula sin mezclar mensajes.
     *
     * @param profesor Nombre del hilo profesor.
     * @param estado   Estado actual ("Examinando Alumno-XX" o "LIBRE").
     * @param evento   Mensaje descriptivo del evento ocurrido.
     */
    public synchronized void registrarEvento(String profesor, String estado, String evento) {
        estadoProfesores.put(profesor, estado);
        System.out.println(evento);
        System.out.println("   -> [TIEMPO REAL] " + formatearEstadoAula());
    }

    private String formatearEstadoAula() {
        List<String> bloques = new ArrayList<>();
        for (Map.Entry<String, String> entrada : estadoProfesores.entrySet()) {
            bloques.add(entrada.getKey() + ": [" + entrada.getValue() + "]");
        }
        return String.join(" | ", bloques);
    }
}
