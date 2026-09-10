package TurnosEnfermeria.modelo;

/**
 * Licencia medica, personal u otro tipo de ausencia justificada.
 * Las licencias no tienen horario (horaInicio y horaFin son vacios).
 * Implementa getResumen() con @Override (SIA-6: Override clase 2 de 3).
 */
public class Licencia extends Turno {

    private String motivo;
    private String tipoLicencia; // Medica | Personal | Maternidad | Paternidad | Estudio

    public Licencia(String id, String fecha, String motivo, String tipoLicencia) {
        super(id, fecha, motivo); // Sin horaInicio ni horaFin
        this.motivo       = motivo;
        this.tipoLicencia = tipoLicencia;
    }

    // ========== OVERRIDE (SIA-6) ==========

    @Override
    public String getResumen() {
        return "Licencia " + tipoLicencia + " el dia " + getFecha()
                + ". Motivo: " + motivo;
    }

    @Override
    public String getTipo() {
        return "LICENCIA";
    }

    // ========== GETTERS Y SETTERS (SIA-3) ==========

    public String getMotivo()              { return motivo; }
    public void setMotivo(String motivo) {
        setObservacion(motivo);
    }
    /**
    * Mantiene sincronizados el motivo y la observacion de la licencia.
    */
    @Override
    public void setObservacion(String observacion) {
        String texto = (observacion == null) ? "" : observacion;
        super.setObservacion(texto);
        this.motivo = texto;
    }

    public String getTipoLicencia()                  { return tipoLicencia; }
    public void   setTipoLicencia(String tipoLicencia) { this.tipoLicencia = tipoLicencia; }
}
