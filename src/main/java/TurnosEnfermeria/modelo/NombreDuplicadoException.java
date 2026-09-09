package TurnosEnfermeria.modelo;

/** Excepcion lanzada cuando ya existe una enfermera con el mismo nombre. */
public class NombreDuplicadoException extends Exception {

    private final String nombreIngresado;

    public NombreDuplicadoException(String nombreIngresado) {
        super("Ya existe una enfermera con el nombre '" + nombreIngresado
            + "'. Los apellidos pueden repetirse, pero el nombre debe ser único.");
        this.nombreIngresado = nombreIngresado;
    }

    public String getNombreIngresado() {
        return nombreIngresado;
    }
}
