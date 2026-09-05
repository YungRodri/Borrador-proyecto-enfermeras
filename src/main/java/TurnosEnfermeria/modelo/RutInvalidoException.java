package TurnosEnfermeria.modelo;

/**
 * Excepcion personalizada lanzada cuando el RUT ingresado no supera
 * la validacion del algoritmo Modulo 11. (SIA-12: Excepcion 1 de 2)
 */
public class RutInvalidoException extends Exception {

    private String rutIngresado;

    public RutInvalidoException(String rutIngresado) {
        super("El RUT ingresado no es valido: " + rutIngresado);
        this.rutIngresado = rutIngresado;
    }

    public String getRutIngresado() {
        return rutIngresado;
    }
}
