package TurnosEnfermeria.controlador;

import TurnosEnfermeria.modelo.Enfermera;
import TurnosEnfermeria.modelo.Turno;
import TurnosEnfermeria.modelo.TurnoConflictoException;
import TurnosEnfermeria.Main;
import TurnosEnfermeria.modelo.CambioTurno;
import TurnosEnfermeria.modelo.Persona;
import TurnosEnfermeria.modelo.Utilidades;
import java.util.Map;
import TurnosEnfermeria.modelo.TurnoRegular;

/**
 * Controlador de la entidad Turno.
 * Gestiona el CRUD de turnos dentro de las enfermeras del registro global.
 * Implementa sobrecarga de buscarTurno() (puede buscarse por RUT+ID o solo por ID global).
 *
 * Todos los metodos son estaticos para simplificar el uso desde el menu CLI y la GUI.
 */
public class TurnoControlador {

   /**
 * Registra un turno usando el registro de la aplicacion.
 */
public static void registrar(Enfermera enfermera, Turno turno)
        throws TurnoConflictoException {
    registrar(enfermera, turno, Main.getRegistroGlobal());
}

/**
 * Registra un turno usando el registro recibido.
 * Permite aplicar las mismas validaciones durante la carga CSV.
 */
    public static void registrar(Enfermera enfermera, Turno turno,Map<String, Enfermera> registro)
        throws TurnoConflictoException {

        if (enfermera == null || turno == null || registro == null) {
            throw new TurnoConflictoException("Debe indicar una enfermera, un turno y un registro.");
        }

        if (registro.get(enfermera.getRut()) != enfermera) {
            throw new TurnoConflictoException("La enfermera titular no pertenece al registro.");
        }

        if (turno instanceof CambioTurno) {
            CambioTurno cambio = (CambioTurno) turno;
            String rutSustituta = cambio.getRutSustituta();

            if (!Persona.validarRut(rutSustituta)) {
                throw new TurnoConflictoException("El RUT de la sustituta no es valido.");
            }

            Enfermera sustituta = registro.get(rutSustituta);

            if (sustituta == null) {
                throw new TurnoConflictoException("La enfermera sustituta no esta registrada.");
            }

            if (enfermera.getRut().equals(sustituta.getRut())) {
                throw new TurnoConflictoException("La sustituta debe ser distinta de la enfermera titular.");
            }
        }

        Turno original = null;

        if (turno instanceof CambioTurno) {
            original = buscarTurnoOriginal(enfermera, (CambioTurno) turno);
        }

        comprobarDisponibilidad(enfermera, turno, registro);
        enfermera.agregarTurno(turno);

        // Solo retiramos el original cuando el cambio ya fue aceptado.
        if (original != null) {
            enfermera.eliminarTurno(original.getId());
        }
    }
   /* Comprueba la agenda de quien realiza el turno, incluyendo sustituciones registradas en otras enfermeras.*/
    private static void comprobarDisponibilidad(Enfermera titular, Turno nuevo,Map<String, Enfermera> registro)
        throws TurnoConflictoException {

        String rutResponsable = titular.getRut();

        if (nuevo instanceof CambioTurno) {
            rutResponsable = ((CambioTurno) nuevo).getRutSustituta();
        }   

        try {
            Utilidades.hayConflictoTurnos(nuevo, nuevo);

            for (Enfermera enfermera : registro.values()) {
                for (Turno existente : enfermera.getListaTurnos()) {
                    String rutExistente = enfermera.getRut();

                    if (existente instanceof CambioTurno) {
                        rutExistente = ((CambioTurno) existente).getRutSustituta();
                    }

                    if (rutResponsable.equals(rutExistente) && Utilidades.hayConflictoTurnos(existente, nuevo)) {
                        throw new TurnoConflictoException("La enfermera con RUT " + rutResponsable + " no esta disponible. Evento incompatible: " + existente.getResumen());
                    }
                }
            }
        } catch (java.time.DateTimeException ex) {
            throw new TurnoConflictoException("Revise la fecha y el horario del evento. " + ex.getMessage());
        }
    }
    
    /**
    * Evalua si hay suficientes enfermeras disponibles en un area
    * para una nueva asignacion. No modifica los registros.
    *
    * @param minimo cantidad de enfermeras necesarias
    * @return true si hay suficientes personas disponibles
    */
    public static boolean validarFactibilidadCobertura(
            String area, String fecha, String horaInicio,
            String horaFin, int minimo)
            throws TurnoConflictoException {

        if (area == null || area.trim().isEmpty()) {
            throw new TurnoConflictoException("Debe seleccionar un area.");
        }

        if (minimo <= 0) {
            throw new TurnoConflictoException("La cantidad requerida debe ser mayor que cero.");
        }

    // Evento temporal para comparar horarios. No se registra.
        TurnoRegular consulta = new TurnoRegular("CONSULTA", fecha, horaInicio, horaFin, "Consulta", "");

        // Validamos los datos antes de evaluar a las enfermeras.
        try {
            Utilidades.hayConflictoTurnos(consulta, consulta);
        } catch (java.time.DateTimeException ex) {
            throw new TurnoConflictoException("Fecha u horario invalido. Use dd/MM/yyyy y HH:mm.");
        }

        Map<String, Enfermera> registro = Main.getRegistroGlobal();
        int disponibles = 0;

        for (Enfermera enfermera : registro.values()) {
            if (area.trim().equalsIgnoreCase(enfermera.getAreaAsignada())) {
                try {
                    comprobarDisponibilidad(enfermera, consulta, registro);
                    disponibles++;
                } catch (TurnoConflictoException ex) {
                    // Tiene un evento incompatible y no se cuenta.
                }
            }
        }

        return disponibles >= minimo;
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
        Enfermera e = EnfermeraControlador.obtener(rut);
        if (e == null) return null;
        return e.buscarTurno(idTurno);
    }

    /**
     * [SOBRECARGA 2] Busca un turno en TODAS las enfermeras del registro global.
     * Retorna el primer turno encontrado con ese ID, o null si no existe.
     * @param idTurno ID del turno a buscar globalmente
     * @return array {Enfermera, Turno} o null si no se encuentra
     */
    public static Object[] buscarTurno(String idTurno){
        for (Enfermera e : Main.getRegistroGlobal().values()){
            Turno t = e.buscarTurno(idTurno);
            if (t != null) {
                return new Object[]{e, t};
            }
        }
        return null;
    }

    /* Suma los turnos propios y los cambios que cubre como sustituta.*/
    public static double calcularHorasTrabajadas(Enfermera enfermera) {
        double total = enfermera.getHorasTrabajadas();
        for (Enfermera titular : EnfermeraControlador.listar()) {
            for (Turno turno : titular.getListaTurnos()) {
                if (turno instanceof CambioTurno) {
                    CambioTurno cambio = (CambioTurno) turno;

                    if (enfermera.getRut().equals(cambio.getRutSustituta())) {
                        int ini = Utilidades.horaAMinutos(cambio.getHoraInicio());
                        int fin = Utilidades.horaAMinutos(cambio.getHoraFin());

                        if (ini < 0 || fin < 0) {
                            continue;
                        }

                        if (fin <= ini) {
                            fin += 24 * 60;
                        }

                        total += (fin - ini) / 60.0;
                    }
                }
            }
        }   

        return total;
    }


    /**
 * Busca el turno regular que sera reemplazado por el cambio.
 * Rechaza coincidencias parciales de horario.
 */
    private static Turno buscarTurnoOriginal(
        Enfermera titular, CambioTurno cambio)
        throws TurnoConflictoException {

        Turno original = null;

        try {
            Utilidades.hayConflictoTurnos(cambio, cambio);

            for (Turno existente : titular.getListaTurnos()) {
                if (existente instanceof TurnoRegular && Utilidades.hayConflictoTurnos(existente, cambio)) {
                    boolean mismoHorario = existente.getFecha().equals(cambio.getFecha()) && existente.getHoraInicio().equals(cambio.getHoraInicio()) && existente.getHoraFin().equals(cambio.getHoraFin());

                    if (!mismoHorario || original != null) {
                        throw new TurnoConflictoException("El cambio debe coincidir exactamente con un unico "+ "turno regular de la titular.");
                    }
                    original = existente;
                }
            }
        } catch (java.time.DateTimeException ex) {
            throw new TurnoConflictoException("Revise la fecha y el horario del cambio. " + ex.getMessage());
        }

        return original;
    }

}
