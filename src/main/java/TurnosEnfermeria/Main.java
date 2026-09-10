package TurnosEnfermeria;

import TurnosEnfermeria.controlador.EnfermeraControlador;
import TurnosEnfermeria.controlador.TurnoControlador;
import TurnosEnfermeria.modelo.AreaHospitalaria;
import TurnosEnfermeria.modelo.CambioTurno;
import TurnosEnfermeria.modelo.Enfermera;
import TurnosEnfermeria.modelo.GestorArchivos;
import TurnosEnfermeria.modelo.Licencia;
import TurnosEnfermeria.modelo.RutInvalidoException;
import TurnosEnfermeria.modelo.Turno;
import TurnosEnfermeria.modelo.TurnoConflictoException;
import TurnosEnfermeria.modelo.TurnoRegular;
import TurnosEnfermeria.modelo.Utilidades;
import TurnosEnfermeria.vista.EstilosGUI;
import TurnosEnfermeria.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Punto de entrada del Sistema de Gestion de Turnos de Enfermeras.
 *
 * Al iniciar:
 *   1. Carga datos desde CSV (o semilla si no existen) - SIA-11
 *   2. Ofrece seleccion de interfaz: Consola o GUI - SIA-10
 *
 * registroGlobal es el TreeMap&lt;RUT, Enfermera&gt; que actua como base de datos en memoria.
 * SOLO los Controladores deben acceder a este campo.
 */
public class Main {

    /** COLECCION 1 (Mapa principal del sistema) - SIA-4 */
    private static TreeMap<String, Enfermera> registroGlobal = new TreeMap<>();

    public static java.util.Map<String, Enfermera> getRegistroGlobal() {
        return java.util.Collections.unmodifiableMap(registroGlobal);
    }

    public static void setRegistroGlobal(TreeMap<String, Enfermera> registro) {
        registroGlobal = new TreeMap<>(registro);
    }

    public static boolean registrarEnfermera(Enfermera enfermera) {
        String rut = enfermera.getRut();

        if (registroGlobal.containsKey(rut)) {
            return false;
        }

        registroGlobal.put(rut, enfermera);
            return true;
    }

    public static boolean eliminarEnfermera(String rut) {
        return registroGlobal.remove(rut) != null;
    }

    private static Scanner sc = new Scanner(System.in);

    // ===================================================================
    //  PUNTO DE ENTRADA
    // ===================================================================

    public static void main(String[] args) {
        // Cargar datos batch al inicio (SIA-11)
        setRegistroGlobal(GestorArchivos.cargarEnfermeras());

        Utilidades.imprimirSeparador();
        System.out.println("  SISTEMA DE GESTION DE TURNOS DE ENFERMERAS");
        System.out.println("  Hospital Central - v1.0");
        Utilidades.imprimirSeparador();
        System.out.println("  Seleccione el modo de interfaz:");
        System.out.println("    1. Interfaz de Consola (CLI)");
        System.out.println("    2. Interfaz Grafica  (GUI)");
        System.out.print("  Opcion: ");

        int modo = Utilidades.leerEntero(sc);
        if (modo == 2) {
            // Lanzar la GUI en el Event Dispatch Thread de Swing (SIA-10)
            EstilosGUI.aplicarLookAndFeel();
            SwingUtilities.invokeLater(() -> {
                VentanaPrincipal ventana = new VentanaPrincipal(
                    // Callback de cierre: guardar datos CSV (SIA-11)
                    () -> GestorArchivos.guardarEnfermeras(registroGlobal)
                );
                ventana.setVisible(true);
            });
        } else {
            menuConsola();
        }
    }

    // ===================================================================
    //  MENU PRINCIPAL DE CONSOLA (SIA-7, SIA-8, SIA-9)
    // ===================================================================

    private static void menuConsola() {
        int opcion = -1;
        do {
            imprimirMenuPrincipal();
            System.out.print("  Opcion: ");
            opcion = Utilidades.leerEntero(sc);

            switch (opcion) {
                // ── ENFERMERAS (Coleccion 1) ──────────────────────────
                case 1:  opcionAgregarEnfermera();    break;
                case 2:  opcionListarEnfermeras();    break;
                case 3:  opcionBuscarEnfermera();     break;
                case 4:  opcionEditarEnfermera();     break;
                case 5:  opcionEliminarEnfermera();   break;
                // ── TURNOS (Coleccion 2 anidada) ──────────────────────
                case 6:  opcionRegistrarTurno();      break;
                case 7:  opcionVerHistorialTurnos();  break;
                case 8:  opcionBuscarTurnoPorId();    break;
                case 9:  opcionEditarTurno();         break;
                case 10: opcionEliminarTurno();       break;
                // ── REPORTES Y FILTROS ────────────────────────────────
                case 11: opcionAsignacionGrupal();    break;
                case 12: opcionFiltroExcesoNoche();   break; // SIA-9
                case 13: opcionResumenPorArea();      break;
                // ── SISTEMA ───────────────────────────────────────────
                case 0:
                    GestorArchivos.guardarEnfermeras(registroGlobal); // SIA-11
                    System.out.println("\n  Hasta luego. Datos guardados correctamente.");
                    break;
                default:
                    System.out.println("  [!] Opcion invalida. Ingrese un numero del 0 al 13.");
            }

            if (opcion != 0) pausar();

        } while (opcion != 0);

        sc.close();
    }

    // ===================================================================
    //  PANTALLA DEL MENU
    // ===================================================================

    private static void imprimirMenuPrincipal() {
        System.out.println();
        Utilidades.imprimirSeparador();
        System.out.println("  MENU PRINCIPAL");
        Utilidades.imprimirSeparador();
        System.out.println("  -- ENFERMERAS (Coleccion 1) --");
        System.out.println("   1. Agregar Enfermera");
        System.out.println("   2. Listar Enfermeras");
        System.out.println("   3. Buscar Enfermera por RUT");
        System.out.println("   4. Editar Enfermera");
        System.out.println("   5. Eliminar Enfermera");
        Utilidades.imprimirLinea();
        System.out.println("  -- TURNOS (Coleccion 2 anidada) --");
        System.out.println("   6. Registrar Turno");
        System.out.println("   7. Ver Historial de Turnos");
        System.out.println("   8. Buscar Turno por ID");
        System.out.println("   9. Editar Observacion de Turno");
        System.out.println("  10. Eliminar Turno");
        Utilidades.imprimirLinea();
        System.out.println("  -- REPORTES Y FILTROS --");
        System.out.println("  11. Asignacion Grupal de Turnos por Area");
        System.out.println("  12. Filtro: Enfermeras con exceso turnos Noche");
        System.out.println("  13. Resumen por Area Hospitalaria");
        Utilidades.imprimirLinea();
        System.out.println("   0. Guardar y Salir");
        Utilidades.imprimirSeparador();
    }

    // ===================================================================
    //  OPCIONES - ENFERMERAS (CRUD COLECCION 1) - SIA-7
    // ===================================================================

    /** Opcion 1: Agregar Enfermera */
    private static void opcionAgregarEnfermera() {
        System.out.println("\n  === AGREGAR ENFERMERA ===");
        try {
            System.out.print("  RUT (formato 12345678-5): ");
            String rut = sc.nextLine().trim();

            System.out.print("  Nombre: ");
            String nombre = sc.nextLine().trim();
            if (!Utilidades.noEsVacio(nombre)) {
                System.out.println("  [!] El nombre no puede estar vacio.");
                return;
            }

            System.out.print("  Apellido Paterno: ");
            String apP = sc.nextLine().trim();

            System.out.print("  Apellido Materno: ");
            String apM = sc.nextLine().trim();

            System.out.print("  Edad: ");
            int edad = Utilidades.leerEntero(sc);
            if (!Utilidades.validarEdad(edad)) {
                System.out.println("  [!] Edad invalida (debe estar entre 18 y 70).");
                return;
            }

            System.out.println("  Especialidad:");
            int espIdx = Utilidades.seleccionarOpcion(sc, Utilidades.ESPECIALIDADES);
            if (espIdx < 1) { System.out.println("  [!] Especialidad invalida."); return; }
            String especialidad = Utilidades.ESPECIALIDADES[espIdx - 1];

            System.out.println("  Area Hospitalaria:");
            int areaIdx = Utilidades.seleccionarOpcion(sc, Utilidades.AREAS_HOSPITALARIAS);
            if (areaIdx < 1) { System.out.println("  [!] Area invalida."); return; }
            String area = Utilidades.AREAS_HOSPITALARIAS[areaIdx - 1];

            Enfermera nueva = new Enfermera(nombre, apP, apM, rut, edad, especialidad, area);
            if (EnfermeraControlador.agregar(nueva)) {
                System.out.println("  [OK] Enfermera registrada: " + nueva.getNombreCompleto());
            } else {
                System.out.println("  [!] Ya existe una enfermera con RUT " + rut);
            }
        } catch (RutInvalidoException ex) {
            System.out.println("  [!] RUT invalido: " + ex.getRutIngresado()
                    + " - Verifique el digito verificador.");
        }
    }

    /** Opcion 2: Listar Enfermeras */
    private static void opcionListarEnfermeras() {
        System.out.println("\n  === LISTA DE ENFERMERAS ===");
        List<Enfermera> lista = EnfermeraControlador.listar();
        if (lista.isEmpty()) {
            System.out.println("  (No hay enfermeras registradas)");
            return;
        }
        System.out.printf("  %-15s %-25s %-22s %-18s %s%n",
                "RUT", "NOMBRE COMPLETO", "ESPECIALIDAD", "AREA", "TURNOS");
        Utilidades.imprimirLinea();
        for (Enfermera e : lista) {
            System.out.printf("  %-15s %-25s %-22s %-18s %d%n",
                    e.getRut(), e.getNombreCompleto(),
                    e.getEspecialidad(), e.getAreaAsignada(),
                    e.getListaTurnos().size());
        }
        System.out.println("\n  Total: " + lista.size() + " enfermeras registradas.");
    }

    /** Opcion 3: Buscar Enfermera por RUT */
    private static void opcionBuscarEnfermera() {
        System.out.println("\n  === BUSCAR ENFERMERA ===");
        System.out.print("  RUT a buscar: ");
        String rut = sc.nextLine().trim();
        Enfermera e = EnfermeraControlador.obtener(rut);
        if (e == null) {
            System.out.println("  [!] No se encontro enfermera con RUT: " + rut);
        } else {
            System.out.println("\n  Datos de la enfermera:");
            Utilidades.imprimirLinea();
            System.out.println("  RUT          : " + e.getRut());
            System.out.println("  Nombre       : " + e.getNombreCompleto());
            System.out.println("  Edad         : " + e.getEdad());
            System.out.println("  Especialidad : " + e.getEspecialidad());
            System.out.println("  Area         : " + e.getAreaAsignada());
            System.out.println("  Turnos reg.  : " + e.contarTurnosRegulares());
            System.out.println("  Licencias    : " + e.contarLicencias());
            System.out.println("  Cambios      : " + e.contarCambios());
            System.out.printf( "  Horas trab.  : %.1f h%n", e.getHorasTrabajadas());
        }
    }

    /** Opcion 4: Editar Enfermera */
    private static void opcionEditarEnfermera() {
        System.out.println("\n  === EDITAR ENFERMERA ===");
        System.out.print("  RUT de la enfermera a editar: ");
        String rut = sc.nextLine().trim();
        Enfermera e = EnfermeraControlador.obtener(rut);
        if (e == null) {
            System.out.println("  [!] No se encontro enfermera con RUT: " + rut);
            return;
        }
        System.out.println("  Datos actuales: " + e);
        System.out.println("  (Deje vacio para no modificar el campo)");

        System.out.print("  Nuevo nombre [" + e.getNombre() + "]: ");
        String nombre = sc.nextLine().trim();

        System.out.print("  Nuevo ap. paterno [" + e.getApellidoP() + "]: ");
        String apP = sc.nextLine().trim();

        System.out.print("  Nuevo ap. materno [" + e.getApellidoM() + "]: ");
        String apM = sc.nextLine().trim();

        System.out.print("  Nueva edad [" + e.getEdad() + "]: ");
        String edadStr = sc.nextLine().trim();
        int edad;
        try {
            edad = edadStr.isEmpty()
                ? e.getEdad()
                : Integer.parseInt(edadStr);
        } catch (NumberFormatException ex) {
                System.out.println("  [!] La edad debe ser un numero entero.");
                return;
        }

        System.out.println("  Nueva especialidad (0 = no cambiar):");
        System.out.println("  0. Mantener actual: " + e.getEspecialidad());
        int espIdx = Utilidades.seleccionarOpcion(sc, Utilidades.ESPECIALIDADES);
        String esp = (espIdx < 1) ? "" : Utilidades.ESPECIALIDADES[espIdx - 1];

        System.out.println("  Nueva area (0 = no cambiar):");
        System.out.println("  0. Mantener actual: " + e.getAreaAsignada());
        int areaIdx = Utilidades.seleccionarOpcion(sc, Utilidades.AREAS_HOSPITALARIAS);
        String area = (areaIdx < 1) ? "" : Utilidades.AREAS_HOSPITALARIAS[areaIdx - 1];

        try {
            boolean actualizado = EnfermeraControlador.editar(rut, nombre, apP, apM, edad, esp, area);
            if (actualizado) {
                System.out.println("  [OK] Enfermera actualizada: " + e.getNombreCompleto());
            } else {
                System.out.println("  [!] No se encontro la enfermera.");
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("  [!] " + ex.getMessage());
            }
        }

    /** Opcion 5: Eliminar Enfermera */
    private static void opcionEliminarEnfermera() {
        System.out.println("\n  === ELIMINAR ENFERMERA ===");
        System.out.print("  RUT a eliminar: ");
        String rut = sc.nextLine().trim();
        Enfermera e = EnfermeraControlador.obtener(rut);
        if (e == null) {
            System.out.println("  [!] No se encontro enfermera con RUT: " + rut);
            return;
        }
        System.out.println("  Enfermera encontrada: " + e.getNombreCompleto());
        System.out.print("  Confirmar eliminacion (s/n): ");
        String conf = sc.nextLine().trim().toLowerCase();
        if ("s".equals(conf)) {
            EnfermeraControlador.eliminar(rut);
            System.out.println("  [OK] Enfermera eliminada del sistema.");
        } else {
            System.out.println("  Operacion cancelada.");
        }
    }

    // ===================================================================
    //  OPCIONES - TURNOS (CRUD COLECCION 2 ANIDADA) - SIA-8
    // ===================================================================

    /** Opcion 6: Registrar Turno */
    private static void opcionRegistrarTurno() {
        System.out.println("\n  === REGISTRAR TURNO ===");
        System.out.print("  RUT de la enfermera: ");
        String rut = sc.nextLine().trim();
        Enfermera e = EnfermeraControlador.obtener(rut);
        if (e == null) {
            System.out.println("  [!] No se encontro enfermera con RUT: " + rut);
            return;
        }
        System.out.println("  Enfermera: " + e.getNombreCompleto());
        System.out.println("  Tipo de evento:");
        String[] tiposEvento = {"Turno Regular", "Licencia", "Cambio de Turno"};
        int tipoIdx = Utilidades.seleccionarOpcion(sc, tiposEvento);
        if (tipoIdx < 1) { System.out.println("  [!] Tipo invalido."); return; }

        System.out.print("  Fecha (dd/MM/yyyy): ");
        String fecha = sc.nextLine().trim();
        if (!Utilidades.validarFecha(fecha)) {
            System.out.println("  [!] Fecha invalida. Use el formato dd/MM/yyyy.");
            return;
        }

        String id = Utilidades.generarIdTurno();
        Turno nuevoTurno = null;
        try {
            switch (tipoIdx) {
                case 1: // Turno Regular
                    System.out.println("  Tipo de turno:");
                    int ttIdx = Utilidades.seleccionarOpcion(sc, Utilidades.TIPOS_TURNO);
                    if (ttIdx < 1) { System.out.println("  [!] Tipo invalido."); return; }
                    String tipoTurno = Utilidades.TIPOS_TURNO[ttIdx - 1];
                    String horaIni = Utilidades.horaInicioPorTipo(tipoTurno);
                    String horaFin = Utilidades.horaFinPorTipo(tipoTurno);
                    System.out.print("  Observacion (opcional): ");
                    String obsR = sc.nextLine().trim();
                    nuevoTurno = new TurnoRegular(id, fecha, horaIni, horaFin, tipoTurno, obsR);
                    break;

                case 2: // Licencia
                    System.out.println("  Tipo de licencia:");
                    int tlIdx = Utilidades.seleccionarOpcion(sc, Utilidades.TIPOS_LICENCIA);
                    if (tlIdx < 1) { System.out.println("  [!] Tipo invalido."); return; }
                    String tipoLic = Utilidades.TIPOS_LICENCIA[tlIdx - 1];
                    System.out.print("  Motivo: ");
                    String motivo = sc.nextLine().trim();
                    nuevoTurno = new Licencia(id, fecha, motivo, tipoLic);
                    break;

                case 3: // Cambio de Turno
                    System.out.print("  RUT de la enfermera sustituta: ");
                    String rutSust = sc.nextLine().trim();
                    System.out.println("  Tipo de turno del cambio:");
                    int ctIdx = Utilidades.seleccionarOpcion(sc, Utilidades.TIPOS_TURNO);
                    if (ctIdx < 1) { System.out.println("  [!] Tipo invalido."); return; }
                    String tipoC = Utilidades.TIPOS_TURNO[ctIdx - 1];
                    String hIni = Utilidades.horaInicioPorTipo(tipoC);
                    String hFin = Utilidades.horaFinPorTipo(tipoC);
                    System.out.print("  Motivo del cambio: ");
                    String motivoC = sc.nextLine().trim();
                    System.out.print("  Observacion (opcional): ");
                    String obsC = sc.nextLine().trim();
                    nuevoTurno = new CambioTurno(id, fecha, hIni, hFin, rutSust, motivoC, obsC);
                    break;
            }

            if (nuevoTurno != null) {
                TurnoControlador.registrar(e, nuevoTurno);
                System.out.println("  [OK] Turno registrado con ID: " + id);
                System.out.println("  Resumen: " + nuevoTurno.getResumen());
            }
        } catch (TurnoConflictoException ex) {
            System.out.println("  [!] " + ex.getMessage());
        }
    }

    /** Opcion 7: Ver Historial de Turnos */
    private static void opcionVerHistorialTurnos() {
        System.out.println("\n  === HISTORIAL DE TURNOS ===");
        System.out.print("  RUT de la enfermera: ");
        String rut = sc.nextLine().trim();
        Enfermera e = EnfermeraControlador.obtener(rut);
        if (e == null) {
            System.out.println("  [!] No se encontro enfermera con RUT: " + rut);
            return;
        }
        System.out.println("\n  Historial de: " + e.getNombreCompleto());
        Utilidades.imprimirLinea();
        if (e.getListaTurnos().isEmpty()) {
            System.out.println("  (No tiene turnos registrados)");
            return;
        }
        for (Turno t : e.getListaTurnos()) {
            System.out.println("  [" + t.getId() + "] [" + t.getTipo() + "] "
                    + t.getResumen());
            if (!t.getObservacion().isEmpty()) {
                System.out.println("    Obs: " + t.getObservacion());
            }
        }
        System.out.println("\n  Regulares: " + e.contarTurnosRegulares()
                + " | Licencias: " + e.contarLicencias()
                + " | Cambios: " + e.contarCambios()
                + " | Horas: " + String.format("%.1f", e.getHorasTrabajadas()));
    }

    /** Opcion 8: Buscar Turno por ID */
    private static void opcionBuscarTurnoPorId() {
        System.out.println("\n  === BUSCAR TURNO POR ID ===");
        System.out.print("  ID del turno: ");
        String idTurno = sc.nextLine().trim();
        // Usar la sobrecarga 2: buscar en todas las enfermeras
        Object[] resultado = TurnoControlador.buscarTurno(idTurno);
        if (resultado == null) {
            System.out.println("  [!] No se encontro turno con ID: " + idTurno);
        } else {
            Enfermera e = (Enfermera) resultado[0];
            Turno t     = (Turno)     resultado[1];
            System.out.println("\n  Turno encontrado:");
            Utilidades.imprimirLinea();
            System.out.println("  ID      : " + t.getId());
            System.out.println("  Tipo    : " + t.getTipo());
            System.out.println("  Resumen : " + t.getResumen());
            System.out.println("  Obs     : " + t.getObservacion());
            System.out.println("  Enferm. : " + e.getNombreCompleto()
                    + " (" + e.getRut() + ")");
        }
    }

    /** Opcion 9: Editar Observacion de Turno */
    private static void opcionEditarTurno() {
        System.out.println("\n  === EDITAR TURNO ===");
        System.out.print("  RUT de la enfermera: ");
        String rut = sc.nextLine().trim();
        Enfermera e = EnfermeraControlador.obtener(rut);
        if (e == null) {
            System.out.println("  [!] No se encontro enfermera con RUT: " + rut);
            return;
        }
        System.out.print("  ID del turno a editar: ");
        String idTurno = sc.nextLine().trim();
        Turno t = e.buscarTurno(idTurno);
        if (t == null) {
            System.out.println("  [!] Turno no encontrado.");
            return;
        }
        System.out.println("  Turno: " + t.getResumen());
        System.out.println("  Observacion actual: " + t.getObservacion());
        System.out.print("  Nueva observacion: ");
        String obs = sc.nextLine().trim();
        TurnoControlador.editar(e, idTurno, obs);
        System.out.println("  [OK] Observacion actualizada.");
    }

    /** Opcion 10: Eliminar Turno */
    private static void opcionEliminarTurno() {
        System.out.println("\n  === ELIMINAR TURNO ===");
        System.out.print("  RUT de la enfermera: ");
        String rut = sc.nextLine().trim();
        Enfermera e = EnfermeraControlador.obtener(rut);
        if (e == null) {
            System.out.println("  [!] No se encontro enfermera con RUT: " + rut);
            return;
        }
        System.out.print("  ID del turno a eliminar: ");
        String idTurno = sc.nextLine().trim();
        Turno t = e.buscarTurno(idTurno);
        if (t == null) {
            System.out.println("  [!] Turno no encontrado con ID: " + idTurno);
            return;
        }
        System.out.println("  Turno: " + t.getResumen());
        System.out.print("  Confirmar eliminacion (s/n): ");
        String conf = sc.nextLine().trim().toLowerCase();
        if ("s".equals(conf)) {
            TurnoControlador.eliminar(e, idTurno);
            System.out.println("  [OK] Turno eliminado.");
        } else {
            System.out.println("  Operacion cancelada.");
        }
    }

    // ===================================================================
    //  OPCIONES - REPORTES Y FILTROS
    // ===================================================================

    /** Opcion 11: Asignacion Grupal de Turnos por Area */
    private static void opcionAsignacionGrupal() {
        System.out.println("\n  === ASIGNACION GRUPAL DE TURNOS POR AREA ===");
        System.out.println("  Seleccione el area:");
        int areaIdx = Utilidades.seleccionarOpcion(sc, Utilidades.AREAS_HOSPITALARIAS);
        if (areaIdx < 1) { System.out.println("  [!] Area invalida."); return; }
        String areaNombre = Utilidades.AREAS_HOSPITALARIAS[areaIdx - 1];

        AreaHospitalaria area = new AreaHospitalaria(areaNombre, 2);
        area.poblarArea(registroGlobal);

        if (area.getEnfermeras().isEmpty()) {
            System.out.println("  [!] No hay enfermeras en el area: " + areaNombre);
            return;
        }

        System.out.println("\n  Enfermeras en " + areaNombre + ":");
        area.listarEnfermeras(); // Usar sobrecarga 1 de AreaHospitalaria (SIA-5)

        System.out.print("\n  Fecha para el turno grupal (dd/MM/yyyy): ");
        String fecha = sc.nextLine().trim();
        if (!Utilidades.validarFecha(fecha)) {
            System.out.println("  [!] Fecha invalida.");
            return;
        }

        System.out.println("  Tipo de turno a asignar:");
        int ttIdx = Utilidades.seleccionarOpcion(sc, Utilidades.TIPOS_TURNO);
        if (ttIdx < 1) { System.out.println("  [!] Tipo invalido."); return; }
        String tipoTurno = Utilidades.TIPOS_TURNO[ttIdx - 1];
        String horaIni   = Utilidades.horaInicioPorTipo(tipoTurno);
        String horaFin   = Utilidades.horaFinPorTipo(tipoTurno);

        System.out.print("  Observacion (opcional): ");
        String obs = sc.nextLine().trim();

        int asignadas = 0;
        int conflictos = 0;
        for (Enfermera e : area.getEnfermeras().values()) {
            try {
                String id = Utilidades.generarIdTurno();
                TurnoRegular t = new TurnoRegular(id, fecha, horaIni, horaFin, tipoTurno, obs);
                TurnoControlador.registrar(e, t);
                System.out.println("  [OK] Turno asignado a: " + e.getNombreCompleto());
                asignadas++;
            } catch (TurnoConflictoException ex) {
                System.out.println("  [CONFLICTO] " + e.getNombreCompleto()
                        + ": " + ex.getMessage());
                conflictos++;
            }
        }
        System.out.println("\n  Resultado: " + asignadas + " asignadas, "
                + conflictos + " con conflicto.");
    }

    /** Opcion 12: Filtro - Enfermeras con exceso de turnos Noche (SIA-9) */
    private static void opcionFiltroExcesoNoche() {
        System.out.println("\n  === FILTRO: ENFERMERAS CON EXCESO DE TURNOS NOCHE ===");
        System.out.print("  Mes a evaluar (MM, p.ej. 09): ");
        String mes = sc.nextLine().trim();

        System.out.print("  Anio a evaluar (yyyy, p.ej. 2026): ");
        String anio = sc.nextLine().trim();

        System.out.print("  Maximo de turnos noche permitidos por mes: ");
        int limite = Utilidades.leerEntero(sc);
        if (limite < 0) {
            System.out.println("  [!] Limite invalido.");
            return;
        }

        List<Enfermera> exceso = EnfermeraControlador
                .filtrarExcesoTurnosNoche(limite, mes, anio);

        System.out.println("\n  Enfermeras con mas de " + limite
                + " turno(s) noche en " + mes + "/" + anio + ":");
        Utilidades.imprimirLinea();

        if (exceso.isEmpty()) {
            System.out.println("  (Ninguna enfermera supera el limite establecido)");
        } else {
            for (Enfermera e : exceso) {
                int cant = e.contarTurnosNocheMes(mes, anio);
                System.out.println("  " + e.getNombreCompleto()
                        + " (" + e.getRut() + ")"
                        + " - Area: " + e.getAreaAsignada()
                        + " - Turnos noche: " + cant);
            }
            System.out.println("\n  Total: " + exceso.size() + " enfermera(s) con exceso.");
        }
    }

    /** Opcion 13: Resumen por Area Hospitalaria */
    private static void opcionResumenPorArea() {
        System.out.println("\n  === RESUMEN POR AREA HOSPITALARIA ===");
        System.out.println("  Seleccione el area:");
        int areaIdx = Utilidades.seleccionarOpcion(sc, Utilidades.AREAS_HOSPITALARIAS);
        if (areaIdx < 1) { System.out.println("  [!] Area invalida."); return; }
        String areaNombre = Utilidades.AREAS_HOSPITALARIAS[areaIdx - 1];

        AreaHospitalaria area = new AreaHospitalaria(areaNombre, 2);
        area.poblarArea(registroGlobal);

        System.out.println("\n  " + area);
        Utilidades.imprimirLinea();

        if (area.getEnfermeras().isEmpty()) {
            System.out.println("  (No hay enfermeras en esta area)");
            return;
        }

        // Tabla de resumen por enfermera
        System.out.printf("  %-25s %-14s %-8s %-8s %-8s %s%n",
                "NOMBRE", "RUT", "REG.", "LIC.", "CAMB.", "HORAS");
        Utilidades.imprimirLinea();
        for (Enfermera e : area.getEnfermeras().values()) {
            System.out.printf("  %-25s %-14s %-8d %-8d %-8d %.1f h%n",
                    e.getNombreCompleto(), e.getRut(),
                    e.contarTurnosRegulares(), e.contarLicencias(),
                    e.contarCambios(), e.getHorasTrabajadas());
        }
        Utilidades.imprimirLinea();
        System.out.println("  Cobertura minima: "
                + (area.verificarCobertura() ? "[OK] Suficiente" : "[!] INSUFICIENTE"));

        System.out.println("\n  Listado con filtro de tipo de turno:");
        System.out.println("  Tipo de turno a mostrar:");
        int ttIdx = Utilidades.seleccionarOpcion(sc, Utilidades.TIPOS_TURNO);
        if (ttIdx >= 1) {
            // Usar sobrecarga 2 de AreaHospitalaria (SIA-5)
            area.listarEnfermeras(Utilidades.TIPOS_TURNO[ttIdx - 1]);
        }
    }

    // ===================================================================
    //  UTIL DE CONSOLA
    // ===================================================================

    private static void pausar() {
        System.out.print("\n  Presione ENTER para continuar...");
        sc.nextLine();
    }
}
