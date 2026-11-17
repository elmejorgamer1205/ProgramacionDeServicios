package CarreraDeHilos;

public class ControlCarrera {

    private String hiloGanador;
    private int posicionActual;

    /**
     * Inicializo el control de meta sin ganador y con las posiciones a cero.
     */
    public ControlCarrera() {
        this.hiloGanador = null;
        this.posicionActual = 0;
    }

    /**
     * Registro de forma sincronizada la llegada de un hilo a la meta.
     * Si es el primero en llegar, lo guardo como ganador.
     *
     * @param nombreHilo Nombre del hilo que ha completado todos los tramos.
     * @return Devuelvo el puesto (1º, 2º, 3º...) en el que ha finalizado.
     */
    public synchronized int registrarLlegada(String nombreHilo) {
        posicionActual++;
        if (hiloGanador == null) {
            hiloGanador = nombreHilo;
        }
        return posicionActual;
    }

    /**
     * Consulto el nombre del hilo que cruzó la meta en primer lugar.
     *
     * @return Devuelvo el nombre del ganador, o {@code null} si todavía no ha llegado ninguno.
     */
    public synchronized String getHiloGanador() {
        return hiloGanador;
    }
}
