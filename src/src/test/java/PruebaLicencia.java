import TurnosEnfermeria.modelo.Enfermera;
import TurnosEnfermeria.modelo.GestorArchivos;
import TurnosEnfermeria.modelo.Licencia;
import TurnosEnfermeria.modelo.Turno;
import java.util.TreeMap;

public class PruebaLicencia {

    public static void main(String[] args) {
        try {
            Enfermera enfermera = new Enfermera(
                "Ana", "Perez", "Soto", "12345678-5",
                30, "Enfermeria General", "UCI"
            );

            Licencia licencia = new Licencia(
                "PRUEBA-01", "10/09/2026", "Motivo original", "Medica"
            );

            enfermera.agregarTurno(licencia);

            String nuevoMotivo = "Reposo actualizado";

            if (!enfermera.editarTurno("PRUEBA-01", nuevoMotivo)) {
                throw new IllegalStateException("No se encontro la licencia.");
            }

            comprobarLicencia(licencia, nuevoMotivo);

            TreeMap<String, Enfermera> registro = new TreeMap<>();
            registro.put(enfermera.getRut(), enfermera);
            GestorArchivos.guardarEnfermeras(registro);

            TreeMap<String, Enfermera> recuperado =
                GestorArchivos.cargarEnfermeras();

            Enfermera enfermeraRecuperada = recuperado.get(enfermera.getRut());

            if (enfermeraRecuperada == null) {
                throw new IllegalStateException("No se recupero la enfermera.");
            }

            Turno turnoRecuperado =
                enfermeraRecuperada.buscarTurno("PRUEBA-01");

            if (!(turnoRecuperado instanceof Licencia)) {
                throw new IllegalStateException("No se recupero una licencia.");
            }

            comprobarLicencia((Licencia) turnoRecuperado, nuevoMotivo);

            System.out.println(
                "PRUEBA CORRECTA: motivo y observacion se conservan."
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static void comprobarLicencia(Licencia licencia, String esperado) {
        if (!esperado.equals(licencia.getMotivo())
                || !esperado.equals(licencia.getObservacion())) {
            throw new IllegalStateException(
                "El motivo y la observacion no coinciden con el texto editado."
            );
        }
    }
}