package TurnosEnfermeria.controlador;

import TurnosEnfermeria.modelo.Enfermera;
import TurnosEnfermeria.modelo.Turno;
import TurnosEnfermeria.modelo.TurnoConflictoException;
import TurnosEnfermeria.Main;

/**
 * Controlador de la entidad Turno.
 * Gestiona el CRUD de turnos dentro de las enfermeras del registro global.
 * Implementa sobrecarga de buscarTurno() (puede buscarse por RUT+ID o solo por ID global).
 *
 * Todos los metodos son estaticos para simplificar el uso desde el menu CLI y la GUI.
 */
public class TurnoControlador {

    /**
     * Registra un turno en la enfermera especificada.
     * @param enfermera destinataria del turno
     * @param turno     turno a agregar (ya construido)
     * @throws TurnoConflictoException si hay superposicion de horario
     */
    public static void registrar(Enfermera enfermera, Turno turno)
            throws TurnoConflictoException {
        enfermera.agregarTurno(turno);
    }

    /**
     * Elimina un turno de una enfermera especifica por ID.
     * @return true si se elimino, false si no se encontro
     */
    public static boolean eliminar(Enfermera enfermera, String idTurno) {
        return enfermera.eliminarTurno(idTurno);
    }

    /**
     * Edita la observacion de un turno de una enfermera especifica.
     * @return true si se edito, false si no se encontro el turno
     */
    public static boolean editar(Enfermera enfermera, String idTurno,
                                 String nuevaObservacion) {
        return enfermera.editarTurno(idTurno, nuevaObservacion);
    }

    /**
     * [SOBRECARGA 1] Busca un turno en una enfermera especifica por ID.
     * @param rut     RUT de la enfermera
     * @param idTurno ID del turno
     * @return el Turno encontrado o null
     */
    public static Turno buscarTurno(String rut, String idTurno) {
        Enfermera e = Main.getRegistroGlobal().get(rut.trim().toUpperCase());        if (e == null) return null;
        return e.buscarTurno(idTurno);
    }

    /**
     * [SOBRECARGA 2] Busca un turno en TODAS las enfermeras del registro global.
     * Retorna el primer turno encontrado con ese ID, o null si no existe.
     * @param idTurno ID del turno a buscar globalmente
     * @return array {Enfermera, Turno} o null si no se encuentra
     */
    public static Object[] buscarTurno(String idTurno) {
        for (Enfermera e : Main.getRegistroGlobal().values()) {{
            Turno t = e.buscarTurno(idTurno);
            if (t != null) {
                return new Object[]{e, t};
            }
        }
        return null;
    }
}
