package TurnosEnfermeria.modelo;

/**
 * Excepcion personalizada lanzada cuando el RUT ingresado no supera
 * la validacion del algoritmo Modulo 11. (SIA-12: Excepcion 1 de 2)
 */
public class RutInvalidoException extends Exception {

    private String rutIngresado;

    public RutInvalidoException(String rutIngresado) {
        this(rutIngresado,
            "RUT inválido. Use el formato 12345678-5 o 10000013-K, sin puntos.");
    }

    public RutInvalidoException(String rutIngresado, String mensaje) {
        super(mensaje);
        this.rutIngresado = rutIngresado;
    }

    public String getRutIngresado() {
        return rutIngresado;
    }
}
