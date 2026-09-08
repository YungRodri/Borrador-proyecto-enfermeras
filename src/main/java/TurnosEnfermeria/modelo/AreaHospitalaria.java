package TurnosEnfermeria.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Agrupador logico de enfermeras por area hospitalaria.
 * Contiene el TreeMap principal del sistema (COLECCION 1 - SIA-4).
 *
 * Implementa sobrecarga del metodo listarEnfermeras() (SIA-5: Sobrecarga clase 2 de 2):
 *   - listarEnfermeras() -- lista todas las enfermeras del area
 *   - listarEnfermeras(String tipoTurno) -- solo las que tienen al menos 1 turno del tipo
 *
 * Todos los atributos son privados (SIA-3).
 */
public class AreaHospitalaria {

    private String nombre;
    private int    minimoEnfermeras;

    // COLECCION 1 (Mapa): registro de enfermeras ordenadas por RUT (SIA-4)
    private TreeMap<String, Enfermera> enfermeras;

    // ========== CONSTRUCTOR ==========

    public AreaHospitalaria(String nombre, int minimoEnfermeras) {
        this.nombre             = nombre;
        this.minimoEnfermeras   = minimoEnfermeras;
        this.enfermeras         = new TreeMap<>();
    }

    // ========== OPERACIONES DE POBLACION ==========

    /**
     * Carga el mapa de enfermeras desde el registro global,
     * filtrando solo las que pertenecen a esta area.
     */
    public void poblarArea(TreeMap<String, Enfermera> registroGlobal) {
        enfermeras.clear();
        for (Map.Entry<String, Enfermera> entrada : registroGlobal.entrySet()) {
            if (nombre.equalsIgnoreCase(entrada.getValue().getAreaAsignada())) {
                enfermeras.put(entrada.getKey(), entrada.getValue());
            }
        }
    }

    // ========== SOBRECARGA listarEnfermeras (SIA-5) ==========

    /**
     * [SOBRECARGA 1] Imprime en consola TODAS las enfermeras del area.
     * Muestra: RUT, nombre completo, especialidad y cantidad de turnos.
     */
    public void listarEnfermeras() {
        if (enfermeras.isEmpty()) {
            System.out.println("  (No hay enfermeras registradas en el area " + nombre + ")");
            return;
        }
        System.out.println("  Enfermeras en area: " + nombre
                + " [" + enfermeras.size() + " registradas]");
        Utilidades.imprimirLinea();
        for (Enfermera e : enfermeras.values()) {
            System.out.println("  " + e);
        }
    }

    /**
     * [SOBRECARGA 2] Imprime en consola las enfermeras del area que tienen
     * al menos un turno del tipo especificado (Manana, Tarde o Noche).
     * @param tipoTurno tipo de turno a filtrar
     */
    public void listarEnfermeras(String tipoTurno) {
        System.out.println("  Enfermeras en area " + nombre
                + " con turno tipo '" + tipoTurno + "':");
        Utilidades.imprimirLinea();
        int encontradas = 0;
        for (Enfermera e : enfermeras.values()) {
            boolean tieneTipo = false;
            for (Turno t : e.getListaTurnos()) {
                if (t instanceof TurnoRegular) {
                    TurnoRegular tr = (TurnoRegular) t;
                    if (tipoTurno.equalsIgnoreCase(tr.getTipoTurno())) {
                        tieneTipo = true;
                        break;
                    }
                }
            }
            if (tieneTipo) {
                System.out.println("  " + e);
                encontradas++;
            }
        }
        if (encontradas == 0) {
            System.out.println("  (Ninguna enfermera con ese tipo de turno)");
        }
    }

    // ========== OTROS METODOS ==========

    /**
     * Genera e imprime la lista de enfermeras con sus turnos detallados.
     */
    public void generarLista() {
        System.out.println("\n  === LISTA DETALLADA: " + nombre + " ===");
        for (Enfermera e : enfermeras.values()) {
            System.out.println("  " + e.getNombreCompleto()
                    + " (" + e.getRut() + ") - " + e.getEspecialidad());
            if (e.getListaTurnos().isEmpty()) {
                System.out.println("    (sin turnos registrados)");
            } else {
                for (Turno t : e.getListaTurnos()) {
                    System.out.println("    -> " + t.getResumen());
                }
            }
            System.out.println();
        }
    }

    /**
     * Verifica si el area tiene al menos el minimo de enfermeras requerido.
     * @return true si la cobertura es suficiente
     */
    public boolean verificarCobertura() {
        return enfermeras.size() >= minimoEnfermeras;
    }

    /**
     * Retorna una lista con las enfermeras que superan el limite de turnos noche
     * en un mes/anio dado.
     * @param limiteNoche numero maximo aceptable de turnos noche
     * @param mes         formato MM
     * @param anio        formato yyyy
     */
    public List<Enfermera> filtrarExcesoTurnosNoche(int limiteNoche,
                                                     String mes, String anio) {
        List<Enfermera> resultado = new ArrayList<>();
        for (Enfermera e : enfermeras.values()) {
            if (e.contarTurnosNocheMes(mes, anio) > limiteNoche) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    // ========== GETTERS Y SETTERS (SIA-3) ==========

    public String getNombre()               { return nombre; }
    public void   setNombre(String nombre)  { this.nombre = nombre; }

    public int  getMinimoEnfermeras()             { return minimoEnfermeras; }
    public void setMinimoEnfermeras(int minimo)   { this.minimoEnfermeras = minimo; }

    public TreeMap<String, Enfermera> getEnfermeras() { return enfermeras; }
    public void setEnfermeras(TreeMap<String, Enfermera> mapa) {
        this.enfermeras = new TreeMap<>(mapa);
    }

    @Override
    public String toString() {
        return "Area: " + nombre + " | Enfermeras: " + enfermeras.size()
                + "/" + minimoEnfermeras + " | Cobertura OK: " + verificarCobertura();
    }
}
