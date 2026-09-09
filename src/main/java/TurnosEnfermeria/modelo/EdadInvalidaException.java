package TurnosEnfermeria.modelo;

/** Excepcion lanzada cuando la edad esta fuera del rango laboral permitido. */
public class EdadInvalidaException extends Exception {

    private final int edadIngresada;

    public EdadInvalidaException(int edadIngresada) {
        super("La edad debe estar entre 18 y 65 años. Valor ingresado: " + edadIngresada);
        this.edadIngresada = edadIngresada;
    }

    public int getEdadIngresada() {
        return edadIngresada;
    }
}
