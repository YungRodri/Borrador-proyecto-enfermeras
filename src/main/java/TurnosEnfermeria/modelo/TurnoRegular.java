package TurnosEnfermeria.modelo;

/**
 * Turno de trabajo regular de una enfermera.
 * Tipo: Manana (07:00-15:00), Tarde (15:00-23:00) o Noche (23:00-07:00).
 * Implementa getResumen() con @Override (SIA-6: Override clase 1 de 3).
 */
public class TurnoRegular extends Turno {

    private String tipoTurno; // Manana | Tarde | Noche

    public TurnoRegular(String id, String fecha, String horaInicio,
                        String horaFin, String tipoTurno, String observacion) {
        super(id, fecha, horaInicio, horaFin, observacion);
        this.tipoTurno = tipoTurno;
    }

    // ========== OVERRIDE (SIA-6) ==========

    @Override
    public String getResumen() {
        return "Turno " + tipoTurno + " el dia " + getFecha()
                + " de " + getHoraInicio() + " a " + getHoraFin();
    }

    @Override
    public String getTipo() {
        return "REGULAR";
    }

    // ========== GETTERS Y SETTERS (SIA-3) ==========

    public String getTipoTurno()              { return tipoTurno; }
    public void   setTipoTurno(String tipo)   { this.tipoTurno = tipo; }
}
