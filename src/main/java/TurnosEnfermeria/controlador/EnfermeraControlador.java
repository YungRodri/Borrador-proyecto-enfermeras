package TurnosEnfermeria.controlador;

import TurnosEnfermeria.modelo.Enfermera;
import TurnosEnfermeria.modelo.EdadInvalidaException;
import TurnosEnfermeria.modelo.GestorArchivos;
import TurnosEnfermeria.modelo.NombreDuplicadoException;
import TurnosEnfermeria.modelo.NombreInvalidoException;
import TurnosEnfermeria.modelo.Utilidades;
import TurnosEnfermeria.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Controlador de la entidad Enfermera.
 * Es el UNICO punto de acceso al registro global desde la capa de Vista.
 * Ninguna clase de vista/ debe acceder directamente a Main.registroGlobal.
 *
 * Todos los metodos son estaticos para simplificar el uso desde el menu CLI y la GUI.
 */
public class EnfermeraControlador {

    /**
     * Agrega una nueva enfermera al registro global.
     * @param enfermera enfermera ya construida y validada
     * @return true si se agrego, false si ya existe un RUT igual
     */
    public static boolean agregar(Enfermera enfermera)
            throws NombreDuplicadoException {
        String rut = enfermera.getRut();
        if (Main.registroGlobal.containsKey(rut)) {
            return false;
        }
        validarNombreUnico(enfermera.getNombre(), null);
        Main.registroGlobal.put(rut, enfermera);
        GestorArchivos.guardarEnfermeras(Main.registroGlobal);
        return true;
    }

    /**
     * Busca una enfermera por su RUT.
     * @return la Enfermera encontrada o null si no existe
     */
    public static Enfermera obtener(String rut) {
        return Main.registroGlobal.get(rut.trim().toUpperCase());
    }

    /**
     * Elimina una enfermera del registro global por su RUT.
     * @return true si se elimino, false si no existia
     */
    public static boolean eliminar(String rut) {
        boolean eliminado = Main.registroGlobal.remove(rut.trim().toUpperCase()) != null;
        if (eliminado) {
            GestorArchivos.guardarEnfermeras(Main.registroGlobal);
        }
        return eliminado;
    }

    /**
     * Edita los datos de una enfermera existente.
     * El RUT no se puede cambiar (es la clave del mapa).
     * @return true si se encontro y edito, false si el RUT no existe
     */
    public static boolean editar(String rut, String nombre, String apellidoP,
                                 String apellidoM, int edad,
                                 String especialidad, String area)
            throws NombreDuplicadoException, EdadInvalidaException,
                   NombreInvalidoException {
        Enfermera e = obtener(rut);
        if (e == null) return false;
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new NombreInvalidoException();
        }
        String nombreFinal = nombre.trim();
        validarNombreUnico(nombreFinal, e.getRut());
        if (!Utilidades.validarEdad(edad)) {
            throw new EdadInvalidaException(edad);
        }
        if (nombre     != null && !nombre.isEmpty())     e.setNombre(nombre);
        if (apellidoP  != null && !apellidoP.isEmpty())  e.setApellidoP(apellidoP);
        if (apellidoM  != null && !apellidoM.isEmpty())  e.setApellidoM(apellidoM);
        if (edad > 0)                                    e.setEdad(edad);
        if (especialidad != null && !especialidad.isEmpty()) e.setEspecialidad(especialidad);
        if (area != null && !area.isEmpty())             e.setAreaAsignada(area);
        GestorArchivos.guardarEnfermeras(Main.registroGlobal);
        return true;
    }

    /** Verifica que ninguna otra enfermera tenga el mismo nombre. */
    private static void validarNombreUnico(String nombre, String rutIgnorado)
            throws NombreDuplicadoException {
        String nombreBuscado = normalizarNombre(nombre);
        for (Enfermera existente : Main.registroGlobal.values()) {
            boolean mismaEnfermera = rutIgnorado != null
                && existente.getRut().equalsIgnoreCase(rutIgnorado);
            if (!mismaEnfermera
                    && normalizarNombre(existente.getNombre()).equals(nombreBuscado)) {
                throw new NombreDuplicadoException(nombre.trim());
            }
        }
    }

    /** Compara nombres ignorando mayusculas, tildes y espacios repetidos. */
    private static String normalizarNombre(String nombre) {
        String sinTildes = java.text.Normalizer.normalize(
            nombre == null ? "" : nombre.trim(), java.text.Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase(java.util.Locale.ROOT)
            .replaceAll("\\s+", " ");
    }

    /**
     * Retorna una lista con todas las enfermeras del registro.
     */
    public static List<Enfermera> listar() {
        return new ArrayList<>(Main.registroGlobal.values());
    }

    /**
     * Retorna una lista con las enfermeras de un area especifica.
     */
    public static List<Enfermera> listarPorArea(String area) {
        List<Enfermera> resultado = new ArrayList<>();
        for (Enfermera e : Main.registroGlobal.values()) {
            if (area.equalsIgnoreCase(e.getAreaAsignada())) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    /** Retorna las enfermeras que superan el limite de un horario en un mes. */
    public static List<Enfermera> filtrarExcesoTurnosPorHorario(
            String horario, int limite, String mes, String anio) {
        List<Enfermera> resultado = new ArrayList<>();
        for (Enfermera e : Main.registroGlobal.values()) {
            if (e.contarTurnosPorHorarioMes(horario, mes, anio) > limite) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    /** Conserva el filtro anterior para compatibilidad. */
    public static List<Enfermera> filtrarExcesoTurnosNoche(
            int limiteNoche, String mes, String anio) {
        return filtrarExcesoTurnosPorHorario(
            Utilidades.TURNO_NOCHE, limiteNoche, mes, anio);
    }

    /**
     * Retorna el numero total de enfermeras registradas.
     */
    public static int totalRegistradas() {
        return Main.registroGlobal.size();
    }
}
