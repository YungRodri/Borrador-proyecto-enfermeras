package TurnosEnfermeria.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidad principal del sistema. Representa a una enfermera del hospital.
 * Extiende Persona (herencia) y contiene una lista de turnos (coleccion anidada SIA-4).
 *
 * Implementa sobrecarga del metodo agregarTurno() (SIA-5: Sobrecarga clase 1 de 2):
 *   - agregarTurno(Turno turno)
 *   - agregarTurno(String fecha, String horaInicio, String horaFin, String tipoTurno)
 *
 * Todos los atributos son privados (SIA-3).
 */
public class Enfermera extends Persona {

    private String especialidad;
    private String areaAsignada;

    // COLECCION 2 (anidada): historial de turnos asignados a esta enfermera (SIA-4)
    private ArrayList<Turno> listaTurnos;

    // ========== CONSTRUCTOR ==========

    public Enfermera(String nombre, String apellidoP, String apellidoM, String rut, int edad, String especialidad, String areaAsignada) throws RutInvalidoException {
        super(nombre, apellidoP, apellidoM, rut, edad);
        this.especialidad  = especialidad;
        this.areaAsignada  = areaAsignada;
        this.listaTurnos   = new ArrayList<>();
    }

    // ========== SOBRECARGA agregarTurno (SIA-5) ==========

    /**
     * [SOBRECARGA 1] Agrega un objeto Turno ya construido a la lista.
     * Verifica conflictos de horario en la misma fecha antes de agregar.
     * @param turno turno a agregar
     * @throws TurnoConflictoException si hay superposicion de horario en la misma fecha
     */
    public void agregarTurno(Turno turno) throws TurnoConflictoException {
        if (turno == null) {
            throw new TurnoConflictoException("El turno no puede ser nulo.");
        }

        if (buscarTurno(turno.getId()) != null) {
            throw new TurnoConflictoException("La enfermera ya tiene un turno con ID: " + turno.getId());
        }

        try {
        // Comprueba fecha y horario incluso si la lista esta vacia.
            Utilidades.hayConflictoTurnos(turno, turno);
            for (Turno existente : listaTurnos) {
                if (!(existente instanceof CambioTurno) && !(turno instanceof CambioTurno)&& Utilidades.hayConflictoTurnos(existente, turno)) {
                    throw new TurnoConflictoException("La enfermera " + getNombreCompleto() + " tiene un evento incompatible: " + existente.getResumen());
                }
            }
        } catch (java.time.DateTimeException ex) {
            throw new TurnoConflictoException("Revise las fechas y horas. Use dd/MM/yyyy y HH:mm. " + ex.getMessage());
        }

        listaTurnos.add(turno);
    }

    /**
     * [SOBRECARGA 2] Crea un TurnoRegular a partir de datos primitivos y lo agrega.
     * Las horas predefinidas por tipo se asignan automaticamente.
     * @param fecha      fecha del turno (dd/MM/yyyy)
     * @param horaInicio hora de inicio (HH:mm)
     * @param horaFin    hora de fin    (HH:mm)
     * @param tipoTurno  tipo: Manana | Tarde | Noche
     * @throws TurnoConflictoException si hay superposicion de horario
     */
    public void agregarTurno(String fecha, String horaInicio, String horaFin, String tipoTurno)
            throws TurnoConflictoException {
        String id = Utilidades.generarIdTurno();
        TurnoRegular nuevo = new TurnoRegular(id, fecha, horaInicio, horaFin, tipoTurno, "");
        agregarTurno(nuevo); // Delega a la sobrecarga 1 para reutilizar validacion
    }

    // ========== CRUD DE TURNOS ==========

    /**
     * Busca un turno por su ID en la lista de esta enfermera.
     * @return el turno encontrado o null si no existe
     */
    public Turno buscarTurno(String id) {
        for (Turno t : listaTurnos) {
            if (t.getId().equalsIgnoreCase(id)) return t;
        }
        return null;
    }

    /**
     * Elimina un turno por su ID.
     * @return true si se encontro y elimino, false si no existia
     */
    public boolean eliminarTurno(String id) {
        return listaTurnos.removeIf(t -> t.getId().equalsIgnoreCase(id));
    }

    /**
     * Edita la observacion de un turno identificado por ID.
     * @return true si se encontro y edito, false si no existia
     */
    public boolean editarTurno(String id, String nuevaObservacion) {
        Turno t = buscarTurno(id);
        if (t == null) return false;
        t.setObservacion(nuevaObservacion);
        return true;
    }

    // ========== ESTADISTICAS ==========

    /** Cuenta la cantidad de TurnoRegular en la lista. */
    public int contarTurnosRegulares() {
        int c = 0;
        for (Turno t : listaTurnos) if (t instanceof TurnoRegular) c++;
        return c;
    }

    /** Cuenta la cantidad de Licencia en la lista. */
    public int contarLicencias() {
        int c = 0;
        for (Turno t : listaTurnos) if (t instanceof Licencia) c++;
        return c;
    }

    /** Cuenta la cantidad de CambioTurno en la lista. */
    public int contarCambios() {
        int c = 0;
        for (Turno t : listaTurnos) if (t instanceof CambioTurno) c++;
        return c;
    }

    /**
    * Calcula las horas de los turnos regulares propios.
    * Las sustituciones recibidas se suman en el controlador.
    */
    public double getHorasTrabajadas() {
        double total = 0;

        for (Turno t : listaTurnos) {
            if (t instanceof TurnoRegular) {
                int ini = Utilidades.horaAMinutos(t.getHoraInicio());
                int fin = Utilidades.horaAMinutos(t.getHoraFin());

                if (ini < 0 || fin < 0) {
                    continue;
                }

                if (fin <= ini) {
                    fin += 24 * 60;
                }

                total += (fin - ini) / 60.0;
            }
        }

        return total;
    }

    /**
     * Cuenta cuantos turnos de tipo Noche tiene esta enfermera en un mes dado.
     * @param mes formato MM (p.ej. "09")
     * @param anio formato yyyy (p.ej. "2026")
     */
        /** Cuenta turnos regulares de un horario en el mes indicado. */
    public int contarTurnosPorHorarioMes(
            String horario, String mes, String anio) {
        int cantidad = 0;

        for (Turno turno : listaTurnos) {
            if (turno instanceof TurnoRegular) {
                TurnoRegular regular = (TurnoRegular) turno;

                if (regular.getTipoTurno().equalsIgnoreCase(horario)) {
                    String[] partes = regular.getFecha().split("/");

                    if (partes.length == 3
                            && partes[1].equals(mes)
                            && partes[2].equals(anio)) {
                        cantidad++;
                    }
                }
            }
        }

        return cantidad;
    }

    /** Cuenta los turnos nocturnos usando el conteo por horario. */
    public int contarTurnosNocheMes(String mes, String anio) {
        return contarTurnosPorHorarioMes(
            Utilidades.TURNO_NOCHE, mes, anio
        );
    }

    // ========== GETTERS Y SETTERS (SIA-3) ==========

    public String getEspecialidad()                { return especialidad; }
    public void   setEspecialidad(String esp)      { this.especialidad = esp; }

    public String getAreaAsignada()                { return areaAsignada; }
    public void   setAreaAsignada(String area)     { this.areaAsignada = area; }

    /**
     * Retorna vista inmutable de la lista de turnos (encapsulacion defensiva).
     */
    public List<Turno> getListaTurnos() {
        return Collections.unmodifiableList(listaTurnos);
    }

    /**
     * Asigna la lista de turnos realizando una copia defensiva.
     */
    public void setListaTurnos(ArrayList<Turno> lista) {
        this.listaTurnos = new ArrayList<>(lista);
    }

    @Override
    public String toString() {
        return "[" + getRut() + "] " + getNombreCompleto()
                + " | " + especialidad + " | Area: " + areaAsignada
                + " | Turnos: " + listaTurnos.size();
    }
}
