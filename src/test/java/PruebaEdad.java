import TurnosEnfermeria.Main;
import TurnosEnfermeria.controlador.EnfermeraControlador;
import TurnosEnfermeria.modelo.Enfermera;
import TurnosEnfermeria.modelo.RutInvalidoException;

public class PruebaEdad {

    public static void main(String[] args) {
        try {
            // Los limites del rango deben aceptarse.
            Enfermera enfermera = crearEnfermera(18);
            comprobar(enfermera.getEdad() == 18,
                "No se acepto la edad de 18.");

            enfermera.setEdad(70);
            comprobar(enfermera.getEdad() == 70,
                "No se acepto la edad de 70.");

            // Las edades fuera del rango deben rechazarse al crear.
            comprobarCreacionRechazada(17);
            comprobarCreacionRechazada(71);

            // Una asignacion invalida debe conservar la edad anterior.
            comprobarAsignacionRechazada(enfermera, 17);
            comprobarAsignacionRechazada(enfermera, 71);

            // Una edicion invalida no debe modificar nombre ni edad.
            enfermera.setEdad(30);
            comprobar(EnfermeraControlador.agregar(enfermera),
                "No se pudo registrar la enfermera de prueba.");

            comprobarEdicionRechazada(enfermera, 17);
            comprobarEdicionRechazada(enfermera, 71);

            System.out.println(
                "PRUEBA CORRECTA: edades validadas y datos conservados."
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static Enfermera crearEnfermera(int edad)
            throws RutInvalidoException {
        return new Enfermera(
            "Ana", "Perez", "Soto", "12345678-5",
            edad, "Enfermeria General", "UCI"
        );
    }

    private static void comprobarCreacionRechazada(int edad)
            throws RutInvalidoException {
        boolean rechazada = false;

        try {
            crearEnfermera(edad);
        } catch (IllegalArgumentException ex) {
            rechazada = true;
        }

        comprobar(rechazada,
            "Se permitio crear una enfermera con edad " + edad);
    }

    private static void comprobarAsignacionRechazada(
            Enfermera enfermera, int edad) {
        int edadAnterior = enfermera.getEdad();
        boolean rechazada = false;

        try {
            enfermera.setEdad(edad);
        } catch (IllegalArgumentException ex) {
            rechazada = true;
        }

        comprobar(rechazada, "Se permitio asignar la edad " + edad);
        comprobar(enfermera.getEdad() == edadAnterior,
            "La edad cambio despues del rechazo.");
    }

    private static void comprobarEdicionRechazada(
            Enfermera enfermera, int edad) {
        String nombreAnterior = enfermera.getNombre();
        int edadAnterior = enfermera.getEdad();
        boolean rechazada = false;

        try {
            EnfermeraControlador.editar(
                enfermera.getRut(), "Nombre modificado",
                "Perez", "Soto", edad, "Enfermeria General", "UCI"
            );
        } catch (IllegalArgumentException ex) {
            rechazada = true;
        }

        comprobar(rechazada, "Se permitio editar con edad " + edad);
        comprobar(nombreAnterior.equals(enfermera.getNombre()),
            "El nombre cambio aunque la edicion fue rechazada.");
        comprobar(enfermera.getEdad() == edadAnterior,
            "La edad cambio aunque la edicion fue rechazada.");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new IllegalStateException(mensaje);
        }
    }
}