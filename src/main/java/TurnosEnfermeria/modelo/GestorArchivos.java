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

    public static String getCarpeta() { return "resources"; }
    public static String getArchivoEnfermeras() {
        return getCarpeta() + File.separator + "enfermeras.csv";
    }
    public static String getArchivoTurnos() {
        return getCarpeta() + File.separator + "turnos.csv";
    }

    // ========== API PUBLICA ==========

    /**
     * Carga el registro global de enfermeras desde los archivos CSV.
     * Si los archivos no existen, llama a cargarDatosIniciales().
     * @return TreeMap&lt;String, Enfermera&gt; listo para usarse como registro global
     */
    public static TreeMap<String, Enfermera> cargarEnfermeras() {
        crearCarpetaResources();
        File archivoEnf = new File(getArchivoEnfermeras());

        if (!archivoEnf.exists() && !new File(getArchivoTurnos()).exists()) {
            System.out.println("[INFO] Archivos CSV no encontrados. Cargando datos iniciales...");
            return cargarDatosIniciales();
        }

        TreeMap<String, Enfermera> registro = new TreeMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivoEnf),
                        StandardCharsets.UTF_8))) {
            String linea;
            boolean primera = true;
            while ((linea = leerRegistroCSV(br)) != null) {
                if (primera) {
                    validarCabecera(linea, "RUT;Nombre;ApellidoP;ApellidoM;Edad;Especialidad;Area");
                    primera = false; continue;
                }
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] campos = separarCSV(linea);
                if (campos.length != 7) {
                    throw new IllegalStateException("Fila incorrecta en enfermeras.csv.");
                }
                try {
                    String rut        = campos[0].trim();
                    String nombre     = campos[1];
                    String apellidoP  = campos[2];
                    String apellidoM  = campos[3];
                    int    edad       = Integer.parseInt(campos[4].trim());
                    String esp        = campos[5].trim();
                    String area       = campos[6].trim();
                    Enfermera e = new Enfermera(nombre, apellidoP, apellidoM,
                                               rut, edad, esp, area);
                    if (registro.putIfAbsent(e.getRut(), e) != null) {
                        throw new IllegalStateException("RUT duplicado en CSV: " + e.getRut());
                    }
                } catch (RutInvalidoException ex) {
                    throw new IllegalStateException("RUT invalido en CSV.", ex);
                } catch (NumberFormatException ex) {
                    throw new IllegalStateException("Edad no numerica en CSV.", ex);
                } catch (IllegalArgumentException ex) {
                    throw new IllegalStateException("Datos invalidos en CSV: " + ex.getMessage(), ex);
                }
            }
            if (primera) throw new IllegalStateException("Archivo CSV vacio.");
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo cargar enfermeras.csv: " + ex.getMessage(), ex);
        }

        cargarTurnos(registro);
        System.out.println("[INFO] " + registro.size()
                + " enfermeras cargadas desde CSV.");
        return registro;
    }

    /* Guarda las enfermeras y sus turnos.
    * @return true si ambos archivos se guardaron correctamente*/
    public static boolean guardarEnfermeras(TreeMap<String, Enfermera> registro) {
        File enfermeras = new File(getArchivoEnfermeras());
        File turnos = new File(getArchivoTurnos());
        File temporalEnf = new File(getArchivoEnfermeras() + ".tmp");
        File temporalTurnos = new File(getArchivoTurnos() + ".tmp");
        boolean existiaEnf = enfermeras.exists();
        boolean existiaTurnos = turnos.exists();
        boolean sustituyendo = false;
        try {
            java.nio.file.Files.createDirectories(new File(getCarpeta()).toPath());
            guardarArchivoEnfermeras(registro, temporalEnf);
            guardarTurnos(registro, temporalTurnos);
            if (existiaEnf) copiar(enfermeras, new File(enfermeras.getPath() + ".bak"));
            if (existiaTurnos) copiar(turnos, new File(turnos.getPath() + ".bak"));
            sustituyendo = true;
            copiar(temporalEnf, enfermeras);
            copiar(temporalTurnos, turnos);
            System.out.println("[INFO] Datos guardados en CSV exitosamente.");
            return true;
        } catch (Exception ex) {
            if (sustituyendo) {
                restaurar(enfermeras, existiaEnf);
                restaurar(turnos, existiaTurnos);
            }
            System.err.println("[ERROR] No se completo el guardado: " + ex.getMessage());
            return false;
        } finally {
            borrarTemporal(temporalEnf);
            borrarTemporal(temporalTurnos);
        }
    }

    private static void copiar(File origen, File destino) throws java.io.IOException {
        java.nio.file.Files.copy(origen.toPath(), destino.toPath(),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    private static void restaurar(File archivo, boolean existia) {
        try {
            if (existia) copiar(new File(archivo.getPath() + ".bak"), archivo);
            else java.nio.file.Files.deleteIfExists(archivo.toPath());
        } catch (Exception ex) {
            System.err.println("[ERROR] Revise el respaldo de " + archivo + ": " + ex.getMessage());
        }
    }

    private static void borrarTemporal(File archivo) {
        try { java.nio.file.Files.deleteIfExists(archivo.toPath()); }
        catch (Exception ex) { System.err.println("[WARN] No se retiro " + archivo); }
    }

    // ========== METODOS PRIVADOS DE LECTURA ==========

    /**
     * Lee turnos.csv y asocia cada turno a la enfermera correspondiente en el registro.
     */
    private static void cargarTurnos(TreeMap<String, Enfermera> registro) {
        File archivoTurnos = new File(getArchivoTurnos());
        if (!archivoTurnos.isFile()) throw new IllegalStateException("Falta turnos.csv.");

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivoTurnos),
                        StandardCharsets.UTF_8))) {
            String linea;
            boolean primera = true;
            while ((linea = leerRegistroCSV(br)) != null) {
                if (primera) {
                    validarCabecera(linea, "RUT;TIPO;ID;Fecha;HoraInicio;HoraFin;Observacion;DatoExtra");
                    primera = false; continue;
                }
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] c = separarCSV(linea);
                if (c.length != 8) {
                    throw new IllegalStateException("Fila incorrecta en turnos.csv.");
                }
                String rutEnf = Persona.normalizarRut(c[0]);
                String tipo     = c[1].trim();
                String id       = c[2].trim();
                String fecha    = c[3].trim();
                String horaIni  = c[4].trim();
                String horaFin  = c[5].trim();
                String obs      = c[6];
                String extra    = c[7];

                Enfermera enfermera = registro.get(rutEnf);
                if (enfermera == null) {
                    throw new IllegalStateException("Turno sin enfermera: " + rutEnf);
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
                            throw new IllegalStateException("Tipo desconocido: " + tipo);
                    }
                    if (turno != null) {
                        TurnoControlador.registrar(enfermera, turno, registro);
                    }
                } catch (TurnoConflictoException ex) {
                    throw new IllegalStateException("Conflicto al cargar " + id + ": " + ex.getMessage(), ex);
                }
            }
            if (primera) throw new IllegalStateException("Archivo CSV vacio.");
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo cargar turnos.csv: " + ex.getMessage(), ex);
        }
    }

    // ========== METODOS PRIVADOS DE ESCRITURA ==========

    private static void guardarArchivoEnfermeras(TreeMap<String, Enfermera> registro, File destino) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(destino),
                        StandardCharsets.UTF_8))) {
            bw.write("RUT;Nombre;ApellidoP;ApellidoM;Edad;Especialidad;Area");
            bw.newLine();
            for (Enfermera e : registro.values()) {
                bw.write(filaCSV(
                    e.getRut(), e.getNombre(), e.getApellidoP(),
                    e.getApellidoM(), String.valueOf(e.getEdad()),
                    e.getEspecialidad(), e.getAreaAsignada()));
                bw.newLine();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo escribir enfermeras.csv.", ex);
        }
    }

    private static void guardarTurnos(TreeMap<String, Enfermera> registro, File destino) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(destino),
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
                    bw.write(filaCSV(
                        e.getRut(), t.getTipo(), t.getId(), t.getFecha(),
                        t.getHoraInicio(), t.getHoraFin(),
                        t.getObservacion(), extra));
                    bw.newLine();
                }
            }
        } catch (Exception ex) {
           throw new IllegalStateException("No se pudo escribir turnos.csv.", ex);
        }
    }

    /**
     * Crea la carpeta resources/ si no existe.
     */
    private static void crearCarpetaResources() {
        File carpeta = new File(getCarpeta());
        if (!carpeta.exists()) {
            if (carpeta.mkdirs()) {
                System.out.println("[INFO] Carpeta '" + getCarpeta() + "' creada.");
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
            TurnoControlador.registrar(e1, new TurnoRegular("T000001", "01/09/2026", "23:00", "07:00",Utilidades.getTurnoNoche(), "Guardia nocturna"), registro);

            TurnoControlador.registrar(e1, new TurnoRegular("T000002", "02/09/2026", "23:00", "07:00",Utilidades.getTurnoNoche(), "Guardia nocturna"), registro);

            TurnoControlador.registrar(e1, new TurnoRegular("T000003", "04/09/2026", "23:00", "07:00",Utilidades.getTurnoNoche(), "Guardia nocturna"), registro);

            // Carlos: turnos propios.
            TurnoControlador.registrar(e2, new TurnoRegular("T000004", "01/09/2026", "07:00", "15:00",Utilidades.getTurnoManana(), "Sin novedad"), registro);

            TurnoControlador.registrar(e2, new TurnoRegular("T000005", "03/09/2026", "15:00", "23:00",Utilidades.getTurnoTarde(), "Sin novedad"), registro);

            // Ana: licencia y posterior regreso.
            TurnoControlador.registrar(e3, new Licencia("T000006", "05/09/2026", "Reposo medico", "Medica"), registro);

            TurnoControlador.registrar(e3, new TurnoRegular("T000007", "07/09/2026", "07:00", "15:00",Utilidades.getTurnoManana(), "Regreso de licencia"), registro);

            // Pedro: turno original que sera cubierto por Carlos.
            TurnoControlador.registrar(e4, new TurnoRegular("T000015", "02/09/2026", "07:00", "15:00",Utilidades.getTurnoManana(), "Asignacion original"), registro);

            // El cambio reemplaza T000015 y atribuye sus horas a Carlos.
            TurnoControlador.registrar(e4, new CambioTurno("T000008", "02/09/2026", "07:00", "15:00",e2.getRut(), "Compromiso personal", "Cubierto por Munoz"), registro);

            TurnoControlador.registrar(e4, new TurnoRegular("T000009", "04/09/2026", "23:00", "07:00",Utilidades.getTurnoNoche(), "Guardia"), registro);

            // Sofia: turnos de distintos tipos.
            TurnoControlador.registrar(e5, new TurnoRegular("T000010", "01/09/2026", "07:00", "15:00",Utilidades.getTurnoManana(), "Primer dia"), registro);

            TurnoControlador.registrar(e5, new TurnoRegular(
                "T000011", "03/09/2026", "15:00", "23:00",Utilidades.getTurnoTarde(), ""), registro);

            TurnoControlador.registrar(e5, new TurnoRegular(
                "T000012", "05/09/2026", "23:00", "07:00",Utilidades.getTurnoNoche(), ""), registro);

            // Laura: turno regular y licencia.
            TurnoControlador.registrar(e6, new TurnoRegular(
                "T000013", "02/09/2026", "07:00", "15:00",Utilidades.getTurnoManana(), "Preparacion quirurgica"), registro);

            TurnoControlador.registrar(e6, new Licencia("T000014", "08/09/2026", "Vacaciones anuales", "Personal"), registro);

        } catch (RutInvalidoException | TurnoConflictoException
                | IllegalArgumentException ex) {
            System.err.println("[ERROR CRITICO] Fallo al crear datos iniciales: " + ex.getMessage());
            throw new IllegalStateException("No se pudieron crear los datos iniciales.", ex);
        }

        System.out.println("[INFO] Datos iniciales cargados: " + registro.size() + " enfermeras.");
        return registro;
    }
    /** Conserva separadores, comillas y saltos de linea en los campos. */
    private static String filaCSV(String... campos) {
        StringBuilder fila = new StringBuilder();
        for (String campo : campos) {
            if (fila.length() > 0) fila.append(';');
            String valor = campo == null ? "" : campo;
            fila.append('"').append(valor.replace("\"", "\"\"")).append('"');
        }
        return fila.toString();
    }

    /** Lee una fila logica, que puede ocupar varias lineas entre comillas. */
    private static String leerRegistroCSV(BufferedReader lector) throws java.io.IOException {
        String linea = lector.readLine();
        if (linea == null) return null;
        StringBuilder registro = new StringBuilder(linea);
        while (comillasAbiertas(registro.toString())) {
            linea = lector.readLine();
            if (linea == null) throw new java.io.IOException("Comillas CSV sin cerrar.");
            registro.append('\n').append(linea);
        }
        return registro.toString();
    }

    private static boolean comillasAbiertas(String texto) {
        boolean abiertas = false;
        boolean inicioCampo = true;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (abiertas) {
                if (c == '"') {
                    if (i + 1 < texto.length() && texto.charAt(i + 1) == '"') i++;
                    else abiertas = false;
                }
            } else if (c == ';') inicioCampo = true;
            else {
                if (c == '"' && inicioCampo) abiertas = true;
                inicioCampo = false;
            }
        }
        return abiertas;
    }

    private static String[] separarCSV(String registro) {
        java.util.List<String> campos = new java.util.ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean abiertas = false;
        boolean cerradas = false;
        for (int i = 0; i < registro.length(); i++) {
            char c = registro.charAt(i);
            if (abiertas) {
                if (c == '"') {
                    if (i + 1 < registro.length() && registro.charAt(i + 1) == '"') {
                        campo.append('"'); i++;
                    } else { abiertas = false; cerradas = true; }
                } else campo.append(c);
            } else if (c == ';') {
                campos.add(campo.toString()); campo.setLength(0); cerradas = false;
            } else if (cerradas) {
                throw new IllegalArgumentException("Texto inesperado tras comillas CSV.");
            } else if (c == '"' && campo.length() == 0) abiertas = true;
            else campo.append(c);
        }
        if (abiertas) throw new IllegalArgumentException("Comillas CSV sin cerrar.");
        campos.add(campo.toString());
        return campos.toArray(new String[0]);
    }

    private static void validarCabecera(String actual, String esperada) {
        if (!java.util.Arrays.equals(separarCSV(actual), esperada.split(";"))) {
            throw new IllegalArgumentException("Cabecera CSV incorrecta.");
        }
    }
}
