package examenes.psp;

/** Almaceno el resultado de un examen finalizado. */
public class ResultadoExamen {
    private final int idAlumno;
    private final String nombreAlumno;
    private final String nombreProfesor;
    private final double duracionSegundos;
    private final double nota;

    /** Construyo el resultado con los datos del examen evaluado. */
    public ResultadoExamen(int idAlumno, String nombreAlumno, String nombreProfesor,
                           double duracionSegundos, double nota) {
        this.idAlumno = idAlumno;
        this.nombreAlumno = nombreAlumno;
        this.nombreProfesor = nombreProfesor;
        this.duracionSegundos = duracionSegundos;
        this.nota = nota;
    }

    public int getIdAlumno() { return idAlumno; }
    public String getNombreAlumno() { return nombreAlumno; }
    public String getNombreProfesor() { return nombreProfesor; }
    public double getDuracionSegundos() { return duracionSegundos; }
    public double getNota() { return nota; }
    public boolean estaAprobado() { return nota >= 5.0; }
}
