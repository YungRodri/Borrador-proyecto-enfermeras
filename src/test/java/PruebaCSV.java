import TurnosEnfermeria.modelo.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

/** Ejecutar en una carpeta temporal, sin los CSV del usuario. */
public class PruebaCSV {
    public static void main(String[] args) {
        try {
            TreeMap<String, Enfermera> registro = GestorArchivos.cargarEnfermeras();
            Enfermera e = registro.get("12345678-5");
            String texto = "Control; paciente \"estable\"\nSegunda linea";
            e.buscarTurno("T000001").setObservacion(texto);
            comprobar(GestorArchivos.guardarEnfermeras(registro), "Primer guardado");
            TreeMap<String, Enfermera> recuperado = GestorArchivos.cargarEnfermeras();
            comprobar(texto.equals(recuperado.get(e.getRut()).buscarTurno("T000001").getObservacion()),
                "No se conservaron separadores, comillas o saltos de linea");
            comprobar(GestorArchivos.guardarEnfermeras(recuperado), "Segundo guardado");
            comprobar(Files.isRegularFile(Paths.get("resources/turnos.csv.bak")), "Falta respaldo");
            Path archivo = Paths.get(GestorArchivos.getArchivoTurnos());
            byte[] original = Files.readAllBytes(archivo);
            Files.write(archivo, "FILA INCORRECTA\n".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            byte[] danado = Files.readAllBytes(archivo);
            boolean rechazado = false;
            try { GestorArchivos.cargarEnfermeras(); }
            catch (IllegalStateException ex) { rechazado = true; }
            comprobar(rechazado, "Se acepto una carga incompleta");
            comprobar(java.util.Arrays.equals(danado, Files.readAllBytes(archivo)), "Se modifico el CSV al leer");
            Files.write(archivo, original);
            // Fuerza un fallo al preparar el segundo archivo; los originales deben conservarse.
            Path temporal = Paths.get(GestorArchivos.getArchivoTurnos() + ".tmp");
            Files.createDirectory(temporal);
            comprobar(!GestorArchivos.guardarEnfermeras(recuperado), "Se esperaba fallo de guardado");
            comprobar(java.util.Arrays.equals(original, Files.readAllBytes(archivo)), "Se alteraron turnos tras fallo");
            System.out.println("PRUEBA CORRECTA: CSV, respaldo y carga incompleta.");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new IllegalStateException(mensaje);
    }
}
