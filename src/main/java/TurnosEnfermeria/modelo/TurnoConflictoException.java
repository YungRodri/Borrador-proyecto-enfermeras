package TurnosEnfermeria.modelo;

/**
 * Excepcion personalizada lanzada cuando se intenta asignar un turno
 * que se superpone en fecha y horario con otro turno ya existente.
 * (SIA-12: Excepcion 2 de 2)
 */
public class TurnoConflictoException extends Exception {

    private String descripcionConflicto;

    public TurnoConflictoException(String descripcionConflicto) {
        super("Conflicto de turno detectado: " + descripcionConflicto);
        this.descripcionConflicto = descripcionConflicto;
    }

    public String getDescripcionConflicto() {
        return descripcionConflicto;
    }
}
