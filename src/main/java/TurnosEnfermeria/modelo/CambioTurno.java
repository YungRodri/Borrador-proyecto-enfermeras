package TurnosEnfermeria.modelo;

/**
 * Registro de un cambio de turno entre dos enfermeras.
 * Incluye datos de la enfermera que cubre (sustituta) y el motivo del cambio.
 * Implementa getResumen() con @Override (SIA-6: Override clase 3 de 3).
 */
public class CambioTurno extends Turno {

    private String rutSustituta;
    private String motivoCambio;

    /**
     * Crea un registro de cambio de turno entre dos enfermeras.
     * @param id           identificador unico del registro
     * @param fecha        fecha del cambio (dd/MM/yyyy)
     * @param horaInicio   hora de inicio del turno cubierto (HH:mm)
     * @param horaFin      hora de fin del turno cubierto (HH:mm)
     * @param rutSustituta RUT de la enfermera que cubre el turno
     * @param motivoCambio razon del cambio
     * @param observacion  nota adicional opcional
     */
    public CambioTurno(String id, String fecha, String horaInicio, String horaFin,
                       String rutSustituta, String motivoCambio, String observacion) {
        super(id, fecha, horaInicio, horaFin, observacion);
        setRutSustituta(rutSustituta);
        this.motivoCambio = motivoCambio;
    }

    // ========== OVERRIDE (SIA-6) ==========

    /** Retorna un resumen del cambio con la fecha, horario, sustituta y motivo. */
    @Override
    public String getResumen() {
        return "Cambio de turno el dia " + getFecha()
                + " de " + getHoraInicio() + " a " + getHoraFin()
                + ". Sustituta RUT: " + rutSustituta
                + ". Motivo: " + motivoCambio;
    }

    /** Retorna la clave CSV que identifica este tipo de turno. */
    @Override
    public String getTipo() {
        return "CAMBIO";
    }

    // ========== GETTERS Y SETTERS (SIA-3) ==========

    /** Retorna el RUT normalizado de la enfermera que cubre el turno. */
    public String getRutSustituta()                    { return rutSustituta; }
    /** Asigna el RUT de la sustituta, normalizandolo automaticamente. */
    public void setRutSustituta(String rutSustituta) {
        this.rutSustituta = Persona.normalizarRut(rutSustituta);
    }

    /** Retorna el motivo del cambio de turno. */
    public String getMotivoCambio()                    { return motivoCambio; }
    /** Asigna el motivo del cambio de turno. */
    public void   setMotivoCambio(String motivoCambio) { this.motivoCambio = motivoCambio; }
}
