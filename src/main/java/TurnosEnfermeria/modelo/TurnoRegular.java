package TurnosEnfermeria.modelo;

/**
 * Turno de trabajo regular de una enfermera.
 * Tipo: Manana (07:00-15:00), Tarde (15:00-23:00) o Noche (23:00-07:00).
 * Implementa getResumen() con @Override (SIA-6: Override clase 1 de 3).
 */
public class TurnoRegular extends Turno {

    private String tipoTurno; // Manana | Tarde | Noche

    /**
     * Crea un turno regular con todos sus datos.
     * @param id          identificador unico del turno
     * @param fecha       fecha del turno (dd/MM/yyyy)
     * @param horaInicio  hora de inicio (HH:mm)
     * @param horaFin     hora de fin (HH:mm)
     * @param tipoTurno   tipo: Manana, Tarde o Noche
     * @param observacion nota adicional opcional
     */
    public TurnoRegular(String id, String fecha, String horaInicio,
                        String horaFin, String tipoTurno, String observacion) {
        super(id, fecha, horaInicio, horaFin, observacion);
        this.tipoTurno = tipoTurno;
    }

    // ========== OVERRIDE (SIA-6) ==========

    /** Retorna un resumen legible del turno indicando tipo y horario. */
    @Override
    public String getResumen() {
        return "Turno " + tipoTurno + " el dia " + getFecha()
                + " de " + getHoraInicio() + " a " + getHoraFin();
    }

    /** Retorna la clave CSV que identifica este tipo de turno. */
    @Override
    public String getTipo() {
        return "REGULAR";
    }

    // ========== GETTERS Y SETTERS (SIA-3) ==========

    /** Retorna el tipo de horario: Manana, Tarde o Noche. */
    public String getTipoTurno()              { return tipoTurno; }
    /** Asigna el tipo de horario del turno. */
    public void   setTipoTurno(String tipo)   { this.tipoTurno = tipo; }
}
