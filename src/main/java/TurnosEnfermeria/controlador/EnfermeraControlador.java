package TurnosEnfermeria.controlador;

import TurnosEnfermeria.modelo.Enfermera;
import TurnosEnfermeria.modelo.RutInvalidoException;
import TurnosEnfermeria.Main;
import TurnosEnfermeria.modelo.Persona;

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
    **/
    public static boolean agregar(Enfermera enfermera) {
        return Main.registrarEnfermera(enfermera);
    }

    /* Busca una enfermera por su RUT. */
    public static Enfermera obtener(String rut) {
        return Main.getRegistroGlobal().get(Persona.normalizarRut(rut));
    }

    /*Elimina una enfermera del registro global por su RUT.*/
    public static boolean eliminar(String rut) {
        return Main.eliminarEnfermera(Persona.normalizarRut(rut));
    }

    /**
     * Edita los datos de una enfermera existente.
     * El RUT no se puede cambiar (es la clave del mapa).
     * @return true si se encontro y edito, false si el RUT no existe
     */
    public static boolean editar(String rut, String nombre, String apellidoP,
                             String apellidoM, int edad,
                             String especialidad, String area) {
        Enfermera e = obtener(rut);
            if (e == null) {
                return false;
            }

        // Valida y asigna la edad antes de modificar los demas campos.
        e.setEdad(edad);

        if (nombre != null && !nombre.isEmpty()) {
            e.setNombre(nombre);
        }
        if (apellidoP != null && !apellidoP.isEmpty()) {
            e.setApellidoP(apellidoP);
        }
        if (apellidoM != null && !apellidoM.isEmpty()) {
            e.setApellidoM(apellidoM);
        }
        if (especialidad != null && !especialidad.isEmpty()) {
            e.setEspecialidad(especialidad);
        }
        if (area != null && !area.isEmpty()) {
            e.setAreaAsignada(area);
        }

        return true;
    }

    /**
     * Retorna una lista con todas las enfermeras del registro.
     */
    public static List<Enfermera> listar() {
        return new ArrayList<>(Main.getRegistroGlobal().values());
    }

    /**
     * Retorna una lista con las enfermeras de un area especifica.
     */
    public static List<Enfermera> listarPorArea(String area) {
        List<Enfermera> resultado = new ArrayList<>();
        for (Enfermera e : Main.getRegistroGlobal().values()) {
            if (area.equalsIgnoreCase(e.getAreaAsignada())) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    /**
     * Retorna las enfermeras que superan el limite de turnos noche en un mes.
     * @param limiteNoche maximo aceptable de turnos noche
     * @param mes   formato MM (p.ej. "09")
     * @param anio  formato yyyy (p.ej. "2026")
     */
    public static List<Enfermera> filtrarExcesoTurnosNoche(int limiteNoche,
                                                            String mes, String anio) {
        List<Enfermera> resultado = new ArrayList<>();
        for (Enfermera e : Main.getRegistroGlobal().values()) {
            if (e.contarTurnosNocheMes(mes, anio) > limiteNoche) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    /**
     * Retorna el numero total de enfermeras registradas.
     */
    public static int totalRegistradas() {
        return Main.getRegistroGlobal().size();
    }
}
