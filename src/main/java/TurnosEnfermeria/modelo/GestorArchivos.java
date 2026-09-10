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
import TurnosEnfermeria.controlador.TurnoControlador;

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
                    registro.put(e.getRut(), e);
                } catch (RutInvalidoException ex) {
                    System.err.println("[WARN] RUT invalido en CSV, linea ignorada: " + linea);
                } catch (NumberFormatException ex) {
                    System.err.println("[WARN] Edad no numerica en CSV, linea ignorada: " + linea);
                } catch (IllegalArgumentException ex) {
                    System.err.println("[WARN] " + ex.getMessage() + " Linea ignorada: " + linea);
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

    /* Guarda las enfermeras y sus turnos.
    * @return true si ambos archivos se guardaron correctamente*/
    public static boolean guardarEnfermeras(TreeMap<String, Enfermera> registro) {
        try {
            crearCarpetaResources();
            guardarArchivoEnfermeras(registro);
            guardarTurnos(registro);

            System.out.println("[INFO] Datos guardados en CSV exitosamente.");
            return true;
        } catch (Exception ex) {
            System.err.println("[ERROR] No se pudo completar el guardado: " + ex.getMessage());
            return false;
        }
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
                String rutEnf = Persona.normalizarRut(c[0]);
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
                            String rutSust = partes.length > 0 ? Persona.normalizarRut(partes[0]) : "";
                            String motivo   = partes.length > 1 ? partes[1] : "";
                            turno = new CambioTurno(id, fecha, horaIni,
                                                    horaFin, rutSust, motivo, obs);
                            break;
                        default:
                            System.err.println("[WARN] Tipo de turno desconocido: " + tipo);
                    }
                    if (turno != null) {
                        TurnoControlador.registrar(enfermera, turno, registro);
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
            throw new IllegalStateException("No se pudo escribir enfermeras.csv.", ex);
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
           throw new IllegalStateException("No se pudo escribir turnos.csv.", eX);
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
    * Crea seis enfermeras y eventos iniciales.
    * Usa las mismas validaciones que el registro desde las interfaces.
    */
    private static TreeMap<String, Enfermera> cargarDatosIniciales() {
        TreeMap<String, Enfermera> registro = new TreeMap<>();

        try {
            // Primero registramos a todas las enfermeras.
            Enfermera e1 = new Enfermera(
                "Maria", "Gonzalez", "Rojas", "12345678-5",
                32, "Cuidados Intensivos", "UCI"
            );

            Enfermera e2 = new Enfermera(
                "Carlos", "Munoz", "Soto", "22222222-2",
                28, "Urgencias y Emergencias", "Urgencias"
            );

            Enfermera e3 = new Enfermera(
                "Ana", "Lopez", "Silva", "33333333-3",
                35, "Pediatria", "Pediatria"
            );

            Enfermera e4 = new Enfermera(
                "Pedro", "Ramirez", "Castro", "44444444-4",
                41, "Urgencias y Emergencias", "Urgencias"
            );

            Enfermera e5 = new Enfermera(
                "Sofia", "Vargas", "Morales", "55555555-5",
                26, "Enfermeria General", "UCI"
            );

            Enfermera e6 = new Enfermera(
                "Laura", "Fernandez", "Perez", "66666666-6",
                38, "Cirugia", "Cirugia"
            );

            registro.put(e1.getRut(), e1);
            registro.put(e2.getRut(), e2);
            registro.put(e3.getRut(), e3);
            registro.put(e4.getRut(), e4);
            registro.put(e5.getRut(), e5);
            registro.put(e6.getRut(), e6);

            // Maria: tres turnos nocturnos.
            TurnoControlador.registrar(e1, new TurnoRegular(
                "T000001", "01/09/2026", "23:00", "07:00",
                Utilidades.TURNO_NOCHE, "Guardia nocturna"
            ), registro);

            TurnoControlador.registrar(e1, new TurnoRegular(
                "T000002", "02/09/2026", "23:00", "07:00",
                Utilidades.TURNO_NOCHE, "Guardia nocturna"
            ), registro);

            TurnoControlador.registrar(e1, new TurnoRegular(
                "T000003", "04/09/2026", "23:00", "07:00",
                Utilidades.TURNO_NOCHE, "Guardia nocturna"
            ), registro);

            // Carlos: turnos propios.
            TurnoControlador.registrar(e2, new TurnoRegular(
                "T000004", "01/09/2026", "07:00", "15:00",
                Utilidades.TURNO_MANANA, "Sin novedad"
            ), registro);

            TurnoControlador.registrar(e2, new TurnoRegular(
                "T000005", "03/09/2026", "15:00", "23:00",
                Utilidades.TURNO_TARDE, "Sin novedad"
            ), registro);

            // Ana: licencia y posterior regreso.
            TurnoControlador.registrar(e3, new Licencia(
                "T000006", "05/09/2026", "Reposo medico", "Medica"
            ), registro);

            TurnoControlador.registrar(e3, new TurnoRegular(
                "T000007", "07/09/2026", "07:00", "15:00",
                Utilidades.TURNO_MANANA, "Regreso de licencia"
            ), registro);

            // Pedro: turno original que sera cubierto por Carlos.
            TurnoControlador.registrar(e4, new TurnoRegular(
                "T000015", "02/09/2026", "07:00", "15:00",
                Utilidades.TURNO_MANANA, "Asignacion original"
            ), registro);

            // El cambio reemplaza T000015 y atribuye sus horas a Carlos.
            TurnoControlador.registrar(e4, new CambioTurno(
                "T000008", "02/09/2026", "07:00", "15:00",
                e2.getRut(), "Compromiso personal", "Cubierto por Munoz"
            ), registro);

            TurnoControlador.registrar(e4, new TurnoRegular(
                "T000009", "04/09/2026", "23:00", "07:00",
                Utilidades.TURNO_NOCHE, "Guardia"
            ), registro);

            // Sofia: turnos de distintos tipos.
            TurnoControlador.registrar(e5, new TurnoRegular(
                "T000010", "01/09/2026", "07:00", "15:00",
                Utilidades.TURNO_MANANA, "Primer dia"
            ), registro);

            TurnoControlador.registrar(e5, new TurnoRegular(
                "T000011", "03/09/2026", "15:00", "23:00",
                Utilidades.TURNO_TARDE, ""
            ), registro);

            TurnoControlador.registrar(e5, new TurnoRegular(
                "T000012", "05/09/2026", "23:00", "07:00",
                Utilidades.TURNO_NOCHE, ""
            ), registro);

            // Laura: turno regular y licencia.
            TurnoControlador.registrar(e6, new TurnoRegular(
                "T000013", "02/09/2026", "07:00", "15:00",
                Utilidades.TURNO_MANANA, "Preparacion quirurgica"
            ), registro);

            TurnoControlador.registrar(e6, new Licencia(
                "T000014", "08/09/2026", "Vacaciones anuales", "Personal"
            ), registro);

        } catch (RutInvalidoException | TurnoConflictoException
                | IllegalArgumentException ex) {
            System.err.println(
                "[ERROR CRITICO] Fallo al crear datos iniciales: "
                + ex.getMessage()
            );
            registro.clear();
            return registro;
        }

        System.out.println(
            "[INFO] Datos iniciales cargados: "
            + registro.size() + " enfermeras."
        );
        return registro;
    }
}
