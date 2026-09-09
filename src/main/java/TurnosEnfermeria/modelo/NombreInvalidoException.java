package TurnosEnfermeria.modelo;

/** Excepcion lanzada cuando el nombre obligatorio esta vacio. */
public class NombreInvalidoException extends Exception {

    public NombreInvalidoException() {
        super("El nombre es obligatorio y no puede estar vacío.");
    }
}
