package TurnosEnfermeria.modelo;

/** Indica que un turno no puede registrarse por un conflicto. */
public class TurnoConflictoException extends Exception {

    private String descripcionConflicto;

    public TurnoConflictoException(String descripcionConflicto) {
        setDescripcionConflicto(descripcionConflicto);
    }

    public String getDescripcionConflicto() {
        return descripcionConflicto;
    }

    public void setDescripcionConflicto(String descripcionConflicto) {
        this.descripcionConflicto = descripcionConflicto;
    }

    @Override
    public String getMessage() {
        return "Conflicto de turno detectado: " + descripcionConflicto;
    }
}