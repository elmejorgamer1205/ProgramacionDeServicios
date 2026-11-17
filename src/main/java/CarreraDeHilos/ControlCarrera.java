package CarreraDeHilos;

/**
 * Monitor encargado de gestionar la línea de meta de forma segura entre hilos.
 * Garantiza la exclusión mutua al registrar el orden de llegada y determinar el ganador.
 */
public class ControlCarrera {

    private String hiloGanador;
    private int posicionActual;

    /**
     * Inicializa el control de meta sin ganador y con el contador de posiciones a cero.
     */
    public ControlCarrera() {
        this.hiloGanador = null;
        this.posicionActual = 0;
    }

    /**
     * Registra la llegada de un hilo a la meta de forma sincronizada.
     * El primer hilo en acceder a este método queda registrado como ganador.
     *
     * @param nombreHilo Nombre del hilo que acaba de completar todos los tramos.
     * @return El puesto (1º, 2º, 3º...) en el que ha finalizado el hilo.
     */
    public synchronized int registrarLlegada(String nombreHilo) {
        posicionActual++;
        if (hiloGanador == null) {
            hiloGanador = nombreHilo;
        }
        return posicionActual;
    }

    /**
     * Devuelve el nombre del hilo que cruzó la meta en primer lugar.
     *
     * @return Nombre del hilo ganador, o {@code null} si aún no ha llegado ninguno.
     */
    public synchronized String getHiloGanador() {
        return hiloGanador;
    }
}
