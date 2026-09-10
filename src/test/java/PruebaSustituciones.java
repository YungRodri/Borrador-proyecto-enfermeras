import TurnosEnfermeria.Main;
import TurnosEnfermeria.controlador.TurnoControlador;
import TurnosEnfermeria.modelo.*;
import java.util.TreeMap;

public class PruebaSustituciones {

    public static void main(String[] args) {
        try {
            // La ejecucion debe comenzar en una carpeta temporal vacia.
            TreeMap<String, Enfermera> semilla =
                GestorArchivos.cargarEnfermeras();

            comprobar(semilla.size() == 6,
                "La semilla no contiene seis enfermeras.");

            Main.setRegistroGlobal(semilla);

            Enfermera carlos = semilla.get("22222222-2");
            Enfermera pedro = semilla.get("44444444-4");

            comprobar(carlos != null && pedro != null,
                "Faltan Carlos o Pedro en la semilla.");

            comprobar(pedro.buscarTurno("T000015") == null,
                "La semilla conserva el turno original reemplazado.");

            comprobar(pedro.buscarTurno("T000008") instanceof CambioTurno,
                "Falta el cambio de la semilla.");

            comprobar(TurnoControlador.calcularHorasTrabajadas(carlos) == 24,
                "Carlos debe tener 24 horas en la semilla.");

            comprobar(TurnoControlador.calcularHorasTrabajadas(pedro) == 8,
                "Pedro debe tener 8 horas en la semilla.");

            // Registro independiente para probar una sustitucion.
            Enfermera titular = new Enfermera(
                "Ana", "Perez", "Soto", "12345678-5",
                30, "Enfermeria General", "UCI"
            );

            Enfermera sustituta = new Enfermera(
                "Carlos", "Munoz", "Soto", "22222222-2",
                30, "Enfermeria General", "UCI"
            );

            TreeMap<String, Enfermera> registro = new TreeMap<>();
            registro.put(titular.getRut(), titular);
            registro.put(sustituta.getRut(), sustituta);
            Main.setRegistroGlobal(registro);

            TurnoRegular original = new TurnoRegular(
                "ORIGINAL", "10/09/2026", "23:00", "07:00", "Noche", ""
            );

            TurnoControlador.registrar(titular, original);

            TurnoControlador.registrar(sustituta, new TurnoRegular(
                "OCUPADA", "11/09/2026", "06:00", "14:00", "Manana", ""
            ));

            CambioTurno cambio = new CambioTurno(
                "CAMBIO", "10/09/2026", "23:00", "07:00",
                sustituta.getRut(), "Compromiso personal", ""
            );

            // La sustituta esta ocupada: debe conservarse el original.
            comprobarRechazo(titular, cambio);

            comprobar(titular.buscarTurno("ORIGINAL") == original,
                "Se perdio el original al rechazar el cambio.");

            comprobar(titular.buscarTurno("CAMBIO") == null,
                "Se agrego el cambio rechazado.");

            // Liberamos a la sustituta y repetimos.
            comprobar(TurnoControlador.eliminar(sustituta, "OCUPADA"),
                "No se pudo retirar el turno de prueba.");

            TurnoControlador.registrar(titular, cambio);

            comprobar(titular.buscarTurno("ORIGINAL") == null,
                "El original no fue reemplazado.");

            comprobar(titular.buscarTurno("CAMBIO") == cambio,
                "No se registro el cambio.");

            comprobarHoras(titular, sustituta);

            // Una asignacion posterior tampoco debe ocupar la cobertura.
            comprobarRechazo(sustituta, new TurnoRegular(
                "POSTERIOR", "11/09/2026", "06:00", "14:00", "Manana", ""
            ));

            // Guardamos y recuperamos los datos.
            GestorArchivos.guardarEnfermeras(registro);
            TreeMap<String, Enfermera> recuperado =
                GestorArchivos.cargarEnfermeras();

            comprobar(recuperado.size() == 2,
                "No se recuperaron las dos enfermeras.");

            Main.setRegistroGlobal(recuperado);
            titular = recuperado.get("12345678-5");
            sustituta = recuperado.get("22222222-2");

            comprobar(titular != null && sustituta != null,
                "Faltan enfermeras despues de cargar.");

            Turno recuperadoCambio = titular.buscarTurno("CAMBIO");

            comprobar(recuperadoCambio instanceof CambioTurno,
                "No se recupero el cambio desde CSV.");

            comprobar(sustituta.getRut().equals(
                ((CambioTurno) recuperadoCambio).getRutSustituta()),
                "No se conservo la sustituta.");

            comprobar(titular.buscarTurno("ORIGINAL") == null,
                "Reaparecio el original despues de cargar.");

            comprobarHoras(titular, sustituta);

            comprobarRechazo(sustituta, new TurnoRegular(
                "POSTERIOR-CSV", "11/09/2026",
                "06:00", "14:00", "Manana", ""
            ));

            System.out.println(
                "PRUEBA CORRECTA: sustituciones, horas, semilla y CSV."
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static void comprobarHoras(
            Enfermera titular, Enfermera sustituta) {
        comprobar(TurnoControlador.calcularHorasTrabajadas(titular) == 0,
            "La titular no debe sumar las horas cedidas.");

        comprobar(TurnoControlador.calcularHorasTrabajadas(sustituta) == 8,
            "La sustituta debe sumar ocho horas.");
    }

    private static void comprobarRechazo(Enfermera enfermera, Turno turno) {
        int cantidadAnterior = enfermera.getListaTurnos().size();
        boolean rechazado = false;

        try {
            TurnoControlador.registrar(enfermera, turno);
        } catch (TurnoConflictoException ex) {
            rechazado = true;
        }

        comprobar(rechazado, "Se acepto un evento incompatible.");
        comprobar(enfermera.getListaTurnos().size() == cantidadAnterior,
            "La lista cambio despues del rechazo.");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new IllegalStateException(mensaje);
        }
    }
}