package TurnosEnfermeria.modelo;

/** Indica que el RUT ingresado no es valido. */
public class RutInvalidoException extends Exception {

    private String rutIngresado;

    public RutInvalidoException(String rutIngresado) {
        setRutIngresado(rutIngresado);
    }

    public String getRutIngresado() {
        return rutIngresado;
    }

    public void setRutIngresado(String rutIngresado) {
        this.rutIngresado = rutIngresado;
    }

    @Override
    public String getMessage() {
        return "El RUT ingresado no es valido: " + rutIngresado;
    }
}