package TurnosEnfermeria.modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

/**
 * Clase encargada de la persistencia batch en archivos CSV. (SIA-11)
 * Carga todos los datos al inicio del programa y los guarda todos al cerrarlo.
 *
 * Formato enfermeras.csv:
 *   RUT;Nombre;ApellidoP;ApellidoM;Edad;Especialidad;Area
 *
 * Formato turnos.csv:
 *   RUT;TIPO;ID;Fecha;HoraInicio;HoraFin;Observacion;DatoExtra
 *   - REGULAR : DatoExtra = TipoTurno (Manana/Tarde/Noche)
 *   - LICENCIA: DatoExtra = TipoLicencia; HoraInicio y HoraFin vacios
 *   - CAMBIO  : DatoExtra = RutSustituta|MotivoCambio (separados por |)
 *
 * Todos los metodos son estaticos. El delimitador es ";".
 */
public class GestorArchivos {

    private static final String CARPETA           = "resources";
    private static final String ARCHIVO_ENFERMERAS = CARPETA + File.separator + "enfermeras.csv";
    private static final String ARCHIVO_TURNOS     = CARPETA + File.separator + "turnos.csv";

    // ========== API PUBLICA ==========

    /**
     * Carga el registro global de enfermeras desde los archivos CSV.
     * Si los archivos no existen, llama a cargarDatosIniciales().
     * @return TreeMap&lt;String, Enfermera&gt; listo para usarse como registro global
     */
    public static TreeMap<String, Enfermera> cargarEnfermeras() {
        crearCarpetaResources();
        File archivoEnf = new File(ARCHIVO_ENFERMERAS);

        if (!archivoEnf.exists()) {
            System.out.println("[INFO] Archivos CSV no encontrados. Cargando datos iniciales...");
            return cargarDatosIniciales();
        }

        TreeMap<String, Enfermera> registro = new TreeMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivoEnf),
                        StandardCharsets.UTF_8))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; } // Saltar cabecera
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] campos = linea.split(";", -1);
                if (campos.length < 7) {
                    System.err.println("[WARN] Linea malformada en enfermeras.csv: " + linea);
                    continue;
                }
                try {
                    String rut        = campos[0].trim();
                    String nombre     = campos[1].trim();
                    String apellidoP  = campos[2].trim();
                    String apellidoM  = campos[3].trim();
                    int    edad       = Integer.parseInt(campos[4].trim());
                    String esp        = campos[5].trim();
                    String area       = campos[6].trim();
                    Enfermera e = new Enfermera(nombre, apellidoP, apellidoM,
                                               rut, edad, esp, area);
                    registro.put(rut, e);
                } catch (RutInvalidoException ex) {
                    System.err.println("[WARN] RUT invalido en CSV, linea ignorada: " + linea);
                } catch (NumberFormatException ex) {
                    System.err.println("[WARN] Edad invalida en CSV, linea ignorada: " + linea);
                }
            }
        } catch (Exception ex) {
            System.err.println("[ERROR] No se pudo leer enfermeras.csv: " + ex.getMessage());
        }

        cargarTurnos(registro);
        System.out.println("[INFO] " + registro.size()
                + " enfermeras cargadas desde CSV.");
        return registro;
    }

    /**
     * Guarda el registro completo de enfermeras y sus turnos en los archivos CSV.
     * Sobreescribe los archivos existentes.
     * @param registro TreeMap global a persistir
     */
    public static void guardarEnfermeras(TreeMap<String, Enfermera> registro) {
        crearCarpetaResources();
        guardarArchivoEnfermeras(registro);
        guardarTurnos(registro);
        System.out.println("[INFO] Datos guardados en CSV exitosamente.");
    }

    // ========== METODOS PRIVADOS DE LECTURA ==========

    /**
     * Lee turnos.csv y asocia cada turno a la enfermera correspondiente en el registro.
     */
    private static void cargarTurnos(TreeMap<String, Enfermera> registro) {
        File archivoTurnos = new File(ARCHIVO_TURNOS);
        if (!archivoTurnos.exists()) return;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivoTurnos),
                        StandardCharsets.UTF_8))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; }
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] c = linea.split(";", -1);
                if (c.length < 8) {
                    System.err.println("[WARN] Linea malformada en turnos.csv: " + linea);
                    continue;
                }
                String rutEnf   = c[0].trim();
                String tipo     = c[1].trim();
                String id       = c[2].trim();
                String fecha    = c[3].trim();
                String horaIni  = c[4].trim();
                String horaFin  = c[5].trim();
                String obs      = c[6].trim();
                String extra    = c[7].trim();

                Enfermera enfermera = registro.get(rutEnf);
                if (enfermera == null) {
                    System.err.println("[WARN] Turno sin enfermera correspondiente RUT="
                            + rutEnf + " ignorado.");
                    continue;
                }

                Turno turno = null;
                try {
                    switch (tipo.toUpperCase()) {
                        case "REGULAR":
                            turno = new TurnoRegular(id, fecha, horaIni,
                                                     horaFin, extra, obs);
                            break;
                        case "LICENCIA":
                            turno = new Licencia(id, fecha, obs, extra);
                            break;
                        case "CAMBIO":
                            String[] partes = extra.split("\\|", 2);
                            String rutSust  = partes.length > 0 ? partes[0] : "";
                            String motivo   = partes.length > 1 ? partes[1] : "";
                            turno = new CambioTurno(id, fecha, horaIni,
                                                    horaFin, rutSust, motivo, obs);
                            break;
                        default:
                            System.err.println("[WARN] Tipo de turno desconocido: " + tipo);
                    }
                    if (turno != null) {
                        enfermera.agregarTurno(turno); // Puede lanzar TurnoConflictoException
                    }
                } catch (TurnoConflictoException ex) {
                    System.err.println("[WARN] Conflicto al cargar turno "
                            + id + ": " + ex.getMessage() + " (ignorado)");
                }
            }
        } catch (Exception ex) {
            System.err.println("[ERROR] No se pudo leer turnos.csv: " + ex.getMessage());
        }
    }

    // ========== METODOS PRIVADOS DE ESCRITURA ==========

    private static void guardarArchivoEnfermeras(TreeMap<String, Enfermera> registro) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(ARCHIVO_ENFERMERAS),
                        StandardCharsets.UTF_8))) {
            bw.write("RUT;Nombre;ApellidoP;ApellidoM;Edad;Especialidad;Area");
            bw.newLine();
            for (Enfermera e : registro.values()) {
                bw.write(String.join(";",
                    e.getRut(), e.getNombre(), e.getApellidoP(),
                    e.getApellidoM(), String.valueOf(e.getEdad()),
                    e.getEspecialidad(), e.getAreaAsignada()));
                bw.newLine();
            }
        } catch (Exception ex) {
            System.err.println("[ERROR] No se pudo escribir enfermeras.csv: " + ex.getMessage());
        }
    }

    private static void guardarTurnos(TreeMap<String, Enfermera> registro) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(ARCHIVO_TURNOS),
                        StandardCharsets.UTF_8))) {
            bw.write("RUT;TIPO;ID;Fecha;HoraInicio;HoraFin;Observacion;DatoExtra");
            bw.newLine();
            for (Enfermera e : registro.values()) {
                for (Turno t : e.getListaTurnos()) {
                    String extra = "";
                    if (t instanceof TurnoRegular) {
                        extra = ((TurnoRegular) t).getTipoTurno();
                    } else if (t instanceof Licencia) {
                        extra = ((Licencia) t).getTipoLicencia();
                    } else if (t instanceof CambioTurno) {
                        CambioTurno ct = (CambioTurno) t;
                        extra = ct.getRutSustituta() + "|" + ct.getMotivoCambio();
                    }
                    bw.write(String.join(";",
                        e.getRut(), t.getTipo(), t.getId(), t.getFecha(),
                        t.getHoraInicio(), t.getHoraFin(),
                        t.getObservacion(), extra));
                    bw.newLine();
                }
            }
        } catch (Exception ex) {
            System.err.println("[ERROR] No se pudo escribir turnos.csv: " + ex.getMessage());
        }
    }

    /**
     * Crea la carpeta resources/ si no existe.
     */
    private static void crearCarpetaResources() {
        File carpeta = new File(CARPETA);
        if (!carpeta.exists()) {
            if (carpeta.mkdirs()) {
                System.out.println("[INFO] Carpeta '" + CARPETA + "' creada.");
            }
        }
    }

    // ========== DATOS INICIALES (SEMILLA) (SIA-3) ==========

    /**
     * Crea datos hardcodeados para que TODAS las funcionalidades puedan probarse
     * sin necesidad de ingresar datos manualmente. (SIA-3: Datos de prueba)
     * Incluye: 6 enfermeras, 3+ areas, 3 tipos de turno, datos para el filtro.
     */
    private static TreeMap<String, Enfermera> cargarDatosIniciales() {
        TreeMap<String, Enfermera> registro = new TreeMap<>();
        try {
            // ----- ENFERMERA 1: UCI - 3 turnos noche (supera el filtro) -----
            Enfermera e1 = new Enfermera("Maria", "Gonzalez", "Rojas",
                    "12345678-5", 32, "Cuidados Intensivos", "UCI");
            e1.agregarTurno(new TurnoRegular("T000001", "01/09/2026",
                    "23:00", "07:00", Utilidades.TURNO_NOCHE, "Guardia nocturna"));
            e1.agregarTurno(new TurnoRegular("T000002", "02/09/2026",
                    "23:00", "07:00", Utilidades.TURNO_NOCHE, "Guardia nocturna"));
            e1.agregarTurno(new TurnoRegular("T000003", "04/09/2026",
                    "23:00", "07:00", Utilidades.TURNO_NOCHE, "Guardia nocturna"));
            registro.put(e1.getRut(), e1);

            // ----- ENFERMERA 2: Urgencias - turno manana y tarde -----
            Enfermera e2 = new Enfermera("Carlos", "Munoz", "Soto",
                    "22222222-2", 28, "Urgencias y Emergencias", "Urgencias");
            e2.agregarTurno(new TurnoRegular("T000004", "01/09/2026",
                    "07:00", "15:00", Utilidades.TURNO_MANANA, "Sin novedad"));
            e2.agregarTurno(new TurnoRegular("T000005", "03/09/2026",
                    "15:00", "23:00", Utilidades.TURNO_TARDE, "Sin novedad"));
            registro.put(e2.getRut(), e2);

            // ----- ENFERMERA 3: Pediatria - licencia y turno manana -----
            Enfermera e3 = new Enfermera("Ana", "Lopez", "Silva",
                    "33333333-3", 35, "Pediatria", "Pediatria");
            e3.agregarTurno(new Licencia("T000006", "05/09/2026",
                    "Reposo medico", "Medica"));
            e3.agregarTurno(new TurnoRegular("T000007", "07/09/2026",
                    "07:00", "15:00", Utilidades.TURNO_MANANA, "Regreso de licencia"));
            registro.put(e3.getRut(), e3);

            // ----- ENFERMERA 4: Urgencias - cambio de turno + turno noche -----
            Enfermera e4 = new Enfermera("Pedro", "Ramirez", "Castro",
                    "44444444-4", 41, "Urgencias y Emergencias", "Urgencias");
            e4.agregarTurno(new CambioTurno("T000008", "01/09/2026",
                    "07:00", "15:00",
                    "22222222-2", "Compromiso personal", "Cubierto por Munoz"));
            e4.agregarTurno(new TurnoRegular("T000009", "04/09/2026",
                    "23:00", "07:00", Utilidades.TURNO_NOCHE, "Guardia"));
            registro.put(e4.getRut(), e4);

            // ----- ENFERMERA 5: UCI - turno manana, tarde y noche -----
            Enfermera e5 = new Enfermera("Sofia", "Vargas", "Morales",
                    "55555555-5", 26, "Enfermeria General", "UCI");
            e5.agregarTurno(new TurnoRegular("T000010", "01/09/2026",
                    "07:00", "15:00", Utilidades.TURNO_MANANA, "Primer dia"));
            e5.agregarTurno(new TurnoRegular("T000011", "03/09/2026",
                    "15:00", "23:00", Utilidades.TURNO_TARDE, ""));
            e5.agregarTurno(new TurnoRegular("T000012", "05/09/2026",
                    "23:00", "07:00", Utilidades.TURNO_NOCHE, ""));
            registro.put(e5.getRut(), e5);

            // ----- ENFERMERA 6: Cirugia - solo turno manana -----
            Enfermera e6 = new Enfermera("Laura", "Fernandez", "Perez",
                    "66666666-6", 38, "Cirugia", "Cirugia");
            e6.agregarTurno(new TurnoRegular("T000013", "02/09/2026",
                    "07:00", "15:00", Utilidades.TURNO_MANANA, "Preparacion quirurgica"));
            e6.agregarTurno(new Licencia("T000014", "08/09/2026",
                    "Vacaciones anuales", "Personal"));
            registro.put(e6.getRut(), e6);

        } catch (RutInvalidoException | TurnoConflictoException ex) {
            System.err.println("[ERROR CRITICO] Fallo al crear datos iniciales: "
                    + ex.getMessage());
        }
        System.out.println("[INFO] Datos iniciales cargados: "
                + registro.size() + " enfermeras.");
        return registro;
    }
}
