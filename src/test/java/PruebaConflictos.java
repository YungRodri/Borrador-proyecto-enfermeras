import TurnosEnfermeria.modelo.Enfermera;
import TurnosEnfermeria.modelo.Licencia;
import TurnosEnfermeria.modelo.Turno;
import TurnosEnfermeria.modelo.TurnoRegular;
import TurnosEnfermeria.modelo.TurnoConflictoException;
import TurnosEnfermeria.modelo.RutInvalidoException;

public class PruebaConflictos {

    public static void main(String[] args) {
        try {
            Enfermera enfermera = crearEnfermera();

            enfermera.agregarTurno(new TurnoRegular(
                "N1", "10/09/2026", "23:00", "07:00", "Noche", ""
            ));

            comprobarRechazo(enfermera, new TurnoRegular(
                "M1", "11/09/2026", "06:00", "14:00", "Manana", ""
            ));

            // Comenzar justo cuando termina el anterior esta permitido.
            enfermera.agregarTurno(new TurnoRegular(
                "M2", "11/09/2026", "07:00", "15:00", "Manana", ""
            ));

            // La deteccion debe funcionar en ambos ordenes de ingreso.
            Enfermera ordenInverso = crearEnfermera();
            ordenInverso.agregarTurno(new TurnoRegular(
                "M3", "11/09/2026", "06:00", "14:00", "Manana", ""
            ));

            comprobarRechazo(ordenInverso, new TurnoRegular(
                "N2", "10/09/2026", "23:00", "07:00", "Noche", ""
            ));

            Enfermera conLicencia = crearEnfermera();
            conLicencia.agregarTurno(new Licencia(
                "L1", "11/09/2026", "Reposo", "Medica"
            ));

            comprobarRechazo(conLicencia, new TurnoRegular(
                "M4", "11/09/2026", "07:00", "15:00", "Manana", ""
            ));

            // Una noche que invade el dia de licencia tambien se rechaza.
            comprobarRechazo(conLicencia, new TurnoRegular(
                "N3", "10/09/2026", "23:00", "07:00", "Noche", ""
            ));

            // Tampoco se permite agregar una licencia sobre trabajo existente.
            comprobarRechazo(enfermera, new Licencia(
                "L2", "11/09/2026", "Reposo", "Medica"
            ));

            Enfermera sinTurnos = crearEnfermera();
            comprobarRechazo(sinTurnos, new TurnoRegular(
                "X1", "12/09/2026", "07:00", "07:00", "Manana", ""
            ));

            System.out.println(
                "PRUEBA CORRECTA: medianoche, turnos contiguos y licencias."
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static Enfermera crearEnfermera()
            throws RutInvalidoException {
        return new Enfermera(
            "Ana", "Perez", "Soto", "12345678-5",
            30, "Enfermeria General", "UCI"
        );
    }

    private static void comprobarRechazo(Enfermera enfermera, Turno turno) {
        int cantidadAnterior = enfermera.getListaTurnos().size();
        boolean rechazado = false;

        try {
            enfermera.agregarTurno(turno);
        } catch (TurnoConflictoException ex) {
            rechazado = true;
        }

        if (!rechazado) {
            throw new IllegalStateException(
                "Se acepto un evento incompatible: " + turno.getId()
            );
        }

        if (enfermera.getListaTurnos().size() != cantidadAnterior) {
            throw new IllegalStateException(
                "La lista cambio despues de rechazar el evento."
            );
        }
    }
}