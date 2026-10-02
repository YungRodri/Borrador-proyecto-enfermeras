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

    /** Retorna el identificador unico del turno. */
    public String getId()             { return id; }
    /** Asigna el identificador del turno. */
    public void   setId(String id)    { this.id = id; }

    /** Retorna la fecha del turno en formato dd/MM/yyyy. */
    public String getFecha()          { return fecha; }
    /** Asigna la fecha del turno. */
    public void   setFecha(String f)  { this.fecha = f; }

    /** Retorna la hora de inicio del turno en formato HH:mm. */
    public String getHoraInicio()               { return horaInicio; }
    /** Asigna la hora de inicio del turno. */
    public void   setHoraInicio(String horaIni) { this.horaInicio = horaIni; }

    /** Retorna la hora de fin del turno en formato HH:mm. */
    public String getHoraFin()                  { return horaFin; }
    /** Asigna la hora de fin del turno. */
    public void   setHoraFin(String horaFin)    { this.horaFin = horaFin; }

    /** Retorna la observacion o nota adicional del turno. */
    public String getObservacion()              { return observacion; }
    /** Asigna una observacion o nota adicional al turno. */
    public void   setObservacion(String obs)    { this.observacion = obs; }

    /** Representacion de texto del turno usando su resumen especifico. */
    @Override
    public String toString() {
        return "[" + id + "] " + getResumen();
    }
}
