package TurnosEnfermeria.modelo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * Clase de utilidades con metodos estaticos para validaciones y operaciones comunes.
 * Centraliza TODA la logica de validacion para evitar duplicacion entre Modelo y Vista.
 * Ninguna clase de Vista debe validar directamente; debe usar esta clase.
 */
public class Utilidades {

    // ---- Constantes de tipos de turno ----
    public static final String TURNO_MANANA = "Manana";
    public static final String TURNO_TARDE  = "Tarde";
    public static final String TURNO_NOCHE  = "Noche";

    public static final String HORA_MANANA_INI = "07:00";
    public static final String HORA_MANANA_FIN = "15:00";
    public static final String HORA_TARDE_INI  = "15:00";
    public static final String HORA_TARDE_FIN  = "23:00";
    public static final String HORA_NOCHE_INI  = "23:00";
    public static final String HORA_NOCHE_FIN  = "07:00";

       /** Devuelve las areas disponibles. */
    public static String[] getAreasHospitalarias() {
        return new String[]{
            "UCI", "Urgencias", "Pediatria", "Cirugia",
            "Maternidad", "Medicina General",
            "Traumatologia", "Oncologia"
        };
    }

    /** Devuelve las especialidades disponibles. */
    public static String[] getEspecialidades() {
        return new String[]{
            "Enfermeria General", "Cuidados Intensivos",
            "Urgencias y Emergencias", "Pediatria",
            "Cirugia", "Maternidad", "Oncologia", "Traumatologia"
        };
    }

    /** Devuelve los tipos de licencia disponibles. */
    public static String[] getTiposLicencia() {
        return new String[]{
            "Medica", "Personal", "Maternidad",
            "Paternidad", "Estudio"
        };
    }

    /** Devuelve los horarios disponibles. */
    public static String[] getTiposTurno() {
        return new String[]{
            TURNO_MANANA, TURNO_TARDE, TURNO_NOCHE
        };
    }

    // Formatos de fecha y hora (no lenient para validacion estricta)
    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat FORMATO_HORA  = new SimpleDateFormat("HH:mm");

    static {
        FORMATO_FECHA.setLenient(false);
        FORMATO_HORA.setLenient(false);
    }

    /**
     * Valida que una cadena tenga el formato de fecha dd/MM/yyyy y sea una fecha real.
     * @param fecha cadena a validar
     * @return true si es una fecha valida
     */
    public static boolean validarFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) return false;
        try {
            FORMATO_FECHA.parse(fecha.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Valida que una cadena tenga el formato de hora HH:mm en formato 24h.
     * @param hora cadena a validar
     * @return true si es una hora valida
     */
    public static boolean validarHora(String hora) {
        if (hora == null || hora.trim().isEmpty()) return false;
        try {
            FORMATO_HORA.parse(hora.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Compara dos fechas en formato dd/MM/yyyy.
     * @return negativo si a es antes que b, 0 si iguales, positivo si a es despues
     */
    public static int compararFechas(String a, String b) {
        try {
            Date da = FORMATO_FECHA.parse(a.trim());
            Date db = FORMATO_FECHA.parse(b.trim());
            return da.compareTo(db);
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * Convierte una hora HH:mm a minutos desde medianoche para comparacion numerica.
     */
    public static int horaAMinutos(String hora) {
        if (hora == null || hora.trim().isEmpty()) return -1;
        try {
            String[] partes = hora.trim().split(":");
            return Integer.parseInt(partes[0]) * 60 + Integer.parseInt(partes[1]);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Verifica si dos intervalos de tiempo se solapan.
     * Maneja correctamente los turnos nocturnos (horaFin &lt; horaInicio).
     */
    public static boolean hayConflictoHorario(String ini1, String fin1,
                                              String ini2, String fin2) {
        if (ini1 == null || ini1.isEmpty() || ini2 == null || ini2.isEmpty()) return false;
        int a = horaAMinutos(ini1);
        int b = horaAMinutos(fin1);
        int c = horaAMinutos(ini2);
        int d = horaAMinutos(fin2);
        if (a < 0 || b < 0 || c < 0 || d < 0) return false;
        // Normalizar turnos nocturnos (cuando fin < inicio, cruzar medianoche)
        if (b <= a) b += 24 * 60;
        if (d <= c) d += 24 * 60;
        return a < d && c < b;
    }

    /**
     * Lee una linea del Scanner, retornando -1 si la entrada no es un numero entero.
     */
    public static int leerEntero(Scanner sc) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Muestra un catalogo numerado y retorna la opcion elegida (1-based).
     * Retorna -1 si la opcion no es valida.
     */
    public static int seleccionarOpcion(Scanner sc, String[] opciones) {
        for (int i = 0; i < opciones.length; i++) {
            System.out.println("  " + (i + 1) + ". " + opciones[i]);
        }
        System.out.print("  Seleccione opcion: ");
        int op = leerEntero(sc);
        if (op < 1 || op > opciones.length) return -1;
        return op;
    }

    /**
     * Valida que una cadena no sea nula ni vacia (tras hacer trim).
     */
    public static boolean noEsVacio(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /**
      Valida que la edad sea razonable para una enfermera (18-70).
     */
    public static boolean validarEdad(int edad) {
        return edad >= 18 && edad <= 70;
    }

    /* Genera un identificador de turno usando UUID del JDK.*/
    public static String generarIdTurno() {
        return "T" + java.util.UUID.randomUUID().toString();
    }

    /**
     * Retorna la hora de inicio predefinida para un tipo de turno.
     */
    public static String horaInicioPorTipo(String tipoTurno) {
        switch (tipoTurno) {
            case TURNO_MANANA: return HORA_MANANA_INI;
            case TURNO_TARDE:  return HORA_TARDE_INI;
            case TURNO_NOCHE:  return HORA_NOCHE_INI;
            default:           return "";
        }
    }

    /**
     * Retorna la hora de fin predefinida para un tipo de turno.
     */
    public static String horaFinPorTipo(String tipoTurno) {
        switch (tipoTurno) {
            case TURNO_MANANA: return HORA_MANANA_FIN;
            case TURNO_TARDE:  return HORA_TARDE_FIN;
            case TURNO_NOCHE:  return HORA_NOCHE_FIN;
            default:           return "";
        }
    }

    /**
     * Imprime una linea divisoria para el menu de consola.
     */
    public static void imprimirSeparador() {
        System.out.println("═══════════════════════════════════════════════════");
    }

    /**
     * Imprime una linea divisoria corta.
     */
    public static void imprimirLinea() {
        System.out.println("---------------------------------------------------");
    }

    /**
 * Obtiene el inicio del evento.
 * Una licencia comienza a las 00:00 del dia indicado.
 */
private static LocalDateTime obtenerInicio(Turno turno) {
    DateTimeFormatter formatoFecha = DateTimeFormatter
        .ofPattern("dd/MM/uuuu")
        .withResolverStyle(ResolverStyle.STRICT);

    LocalDate fecha = LocalDate.parse(turno.getFecha(), formatoFecha);

    if (turno instanceof Licencia) {
        return fecha.atStartOfDay();
    }

    DateTimeFormatter formatoHora = DateTimeFormatter
        .ofPattern("HH:mm")
        .withResolverStyle(ResolverStyle.STRICT);

    LocalTime hora = LocalTime.parse(turno.getHoraInicio(), formatoHora);
    return fecha.atTime(hora);
}

/**
 * Obtiene el fin del evento.
 * Si la hora final es anterior a la inicial, termina al dia siguiente.
 */
private static LocalDateTime obtenerFin(Turno turno, LocalDateTime inicio) {
    if (turno instanceof Licencia) {
        return inicio.plusDays(1);
    }

    DateTimeFormatter formatoHora = DateTimeFormatter
        .ofPattern("HH:mm")
        .withResolverStyle(ResolverStyle.STRICT);

    LocalTime horaFin = LocalTime.parse(turno.getHoraFin(), formatoHora);

    if (horaFin.equals(inicio.toLocalTime())) {
        throw new java.time.DateTimeException(
            "La hora de inicio y fin no pueden ser iguales."
        );
    }

    LocalDateTime fin = inicio.toLocalDate().atTime(horaFin);

    if (fin.isBefore(inicio)) {
        fin = fin.plusDays(1);
    }

    return fin;
}

    /**
    * Comprueba solapamiento usando fechas y horas completas.
    * Dos eventos contiguos no se consideran en conflicto.
    */
    public static boolean hayConflictoTurnos(Turno primero, Turno segundo) {
        LocalDateTime inicioPrimero = obtenerInicio(primero);
        LocalDateTime finPrimero = obtenerFin(primero, inicioPrimero);

        LocalDateTime inicioSegundo = obtenerInicio(segundo);
        LocalDateTime finSegundo = obtenerFin(segundo, inicioSegundo);

        return inicioPrimero.isBefore(finSegundo)
            && inicioSegundo.isBefore(finPrimero);
    }
}
