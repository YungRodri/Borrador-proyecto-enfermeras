package TurnosEnfermeria.modelo;

/**
 * Licencia medica, personal u otro tipo de ausencia justificada.
 * Las licencias no tienen horario (horaInicio y horaFin son vacios).
 * Implementa getResumen() con @Override (SIA-6: Override clase 2 de 3).
 */
public class Licencia extends Turno {

    private String motivo;
    private String tipoLicencia; // Medica | Personal | Maternidad | Paternidad | Estudio

    /**
     * Crea una licencia registrada en el historial de una enfermera.
     * @param id           identificador unico del registro
     * @param fecha        fecha de la licencia (dd/MM/yyyy)
     * @param motivo       descripcion del motivo de la ausencia
     * @param tipoLicencia categoria: Medica, Personal, Maternidad, Paternidad o Estudio
     */
    public Licencia(String id, String fecha, String motivo, String tipoLicencia) {
        super(id, fecha, motivo); // Sin horaInicio ni horaFin
        this.motivo       = motivo;
        this.tipoLicencia = tipoLicencia;
    }

    // ========== OVERRIDE (SIA-6) ==========

    /** Retorna un resumen que describe el tipo y motivo de la licencia. */
    @Override
    public String getResumen() {
        return "Licencia " + tipoLicencia + " el dia " + getFecha()
                + ". Motivo: " + motivo;
    }

    /** Retorna la clave CSV que identifica este tipo de turno. */
    @Override
    public String getTipo() {
        return "LICENCIA";
    }

    // ========== GETTERS Y SETTERS (SIA-3) ==========

    /** Retorna el motivo de la licencia. */
    public String getMotivo()              { return motivo; }
    /**
     * Asigna el motivo de la licencia delegando en setObservacion
     * para mantener ambos campos sincronizados.
     */
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

    /** Retorna la categoria de licencia. */
    public String getTipoLicencia()                    { return tipoLicencia; }
    /** Asigna la categoria de licencia. */
    public void   setTipoLicencia(String tipoLicencia) { this.tipoLicencia = tipoLicencia; }
}
