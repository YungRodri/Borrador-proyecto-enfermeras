package TurnosEnfermeria.modelo;

/**
 * Clase abstracta que representa un turno o evento de agenda de una enfermera.
 * Subclases concretas: TurnoRegular, Licencia, CambioTurno.
 * Todos los atributos son privados (SIA-3).
 * Define el contrato de getResumen() para polimorfismo (SIA-6).
 */
public abstract class Turno {

    private String id;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String observacion;

    /**
     * Constructor para turnos con horario definido (TurnoRegular, CambioTurno).
     */
    public Turno(String id, String fecha, String horaInicio,
                 String horaFin, String observacion) {
        this.id          = id;
        this.fecha       = fecha;
        this.horaInicio  = (horaInicio  == null) ? "" : horaInicio;
        this.horaFin     = (horaFin     == null) ? "" : horaFin;
        this.observacion = (observacion == null) ? "" : observacion;
    }

    /**
     * Constructor para licencias (sin horario, solo fecha y observacion).
     */
    public Turno(String id, String fecha, String observacion) {
        this(id, fecha, "", "", observacion);
    }

    // ========== METODOS ABSTRACTOS ==========

    /**
     * Retorna un resumen legible y especifico del turno. (SIA-6: Override en subclases)
     */
    public abstract String getResumen();

    /**
     * Retorna el tipo de turno como String para persistencia CSV (REGULAR/LICENCIA/CAMBIO).
     */
    public abstract String getTipo();

    // ========== GETTERS Y SETTERS (SIA-3) ==========

    public String getId()             { return id; }
    public void   setId(String id)    { this.id = id; }

    public String getFecha()          { return fecha; }
    public void   setFecha(String f)  { this.fecha = f; }

    public String getHoraInicio()               { return horaInicio; }
    public void   setHoraInicio(String horaIni) { this.horaInicio = horaIni; }

    public String getHoraFin()                  { return horaFin; }
    public void   setHoraFin(String horaFin)    { this.horaFin = horaFin; }

    public String getObservacion()              { return observacion; }
    public void   setObservacion(String obs)    { this.observacion = obs; }

    @Override
    public String toString() {
        return "[" + id + "] " + getResumen();
    }
}
