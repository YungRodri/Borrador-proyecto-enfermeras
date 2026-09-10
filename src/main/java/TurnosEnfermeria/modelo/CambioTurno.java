package TurnosEnfermeria.modelo;

/**
 * Registro de un cambio de turno entre dos enfermeras.
 * Incluye datos de la enfermera que cubre (sustituta) y el motivo del cambio.
 * Implementa getResumen() con @Override (SIA-6: Override clase 3 de 3).
 */
public class CambioTurno extends Turno {

    private String rutSustituta;
    private String motivoCambio;

    public CambioTurno(String id, String fecha, String horaInicio, String horaFin,
                       String rutSustituta, String motivoCambio, String observacion) {
        super(id, fecha, horaInicio, horaFin, observacion);
        setRutSustituta(rutSustituta);
        this.motivoCambio = motivoCambio;
    }

    // ========== OVERRIDE (SIA-6) ==========

    @Override
    public String getResumen() {
        return "Cambio de turno el dia " + getFecha()
                + " de " + getHoraInicio() + " a " + getHoraFin()
                + ". Sustituta RUT: " + rutSustituta
                + ". Motivo: " + motivoCambio;
    }

    @Override
    public String getTipo() {
        return "CAMBIO";
    }

    // ========== GETTERS Y SETTERS (SIA-3) ==========

    public String getRutSustituta()                    { return rutSustituta; }
    public void setRutSustituta(String rutSustituta) {
        this.rutSustituta = Persona.normalizarRut(rutSustituta);
    }

    public String getMotivoCambio()                    { return motivoCambio; }
    public void   setMotivoCambio(String motivoCambio) { this.motivoCambio = motivoCambio; }
}
