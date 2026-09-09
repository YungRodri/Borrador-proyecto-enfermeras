package TurnosEnfermeria;

import TurnosEnfermeria.controlador.EnfermeraControlador;
import TurnosEnfermeria.controlador.TurnoControlador;
import TurnosEnfermeria.modelo.AreaHospitalaria;
import TurnosEnfermeria.modelo.CambioTurno;
import TurnosEnfermeria.modelo.Enfermera;
import TurnosEnfermeria.modelo.EdadInvalidaException;
import TurnosEnfermeria.modelo.GestorArchivos;
import TurnosEnfermeria.modelo.Licencia;
import TurnosEnfermeria.modelo.NombreDuplicadoException;
import TurnosEnfermeria.modelo.NombreInvalidoException;
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
    public static TreeMap<String, Enfermera> registroGlobal;

    private static Scanner sc = new Scanner(System.in);

    // ===================================================================
    //  PUNTO DE ENTRADA
    // ===================================================================

    public static void main(String[] args)
    {
        // Cargar datos batch al inicio (SIA-11)
        registroGlobal = GestorArchivos.cargarEnfermeras();

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
                case 12: opcionFiltroPorHorario();    break; // SIA-9
                case 13: opcionResumenPorArea();      break;
                case 14: opcionEstadisticas();        break;
                // ── SISTEMA ───────────────────────────────────────────
                case 0:
                    GestorArchivos.guardarEnfermeras(registroGlobal); // SIA-11
                    System.out.println("\n  Hasta luego. Datos guardados correctamente.");
                    break;
                default:
                    System.out.println("  [!] Opcion invalida. Ingrese un numero del 0 al 14.");
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
        System.out.println("  12. Filtro: Enfermeras por tipo de horario");
        System.out.println("  13. Resumen por Area Hospitalaria");
        System.out.println("  14. Estadisticas y Reportes");
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
            System.out.print("  RUT (ej. 12345678-5 o 10000013-K): ");
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
                System.out.println("  [!] Edad inválida: debe estar entre 18 y 65 años.");
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
            System.out.println("  [!] " + ex.getMessage());
        } catch (EdadInvalidaException | NombreDuplicadoException
                | NombreInvalidoException ex) {
            System.out.println("  [!] " + ex.getMessage());
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

    /** Opcion 3: Buscar Enfermera por RUT o nombre */
    private static void opcionBuscarEnfermera() {
        System.out.println("\n  === BUSCAR ENFERMERA ===");
        System.out.print("  RUT o Nombre a buscar: ");
        String busqueda = sc.nextLine().trim();

        Enfermera e = EnfermeraControlador.obtener(busqueda);

        if (e != null) {
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
            return;
        }

        List<Enfermera> resultado = new java.util.ArrayList<>();

        for (Enfermera enfermera : EnfermeraControlador.listar()){
            if(enfermera.getNombreCompleto().toLowerCase().contains(busqueda.toLowerCase())){
                resultado.add(enfermera);
            }
        }
        if (resultado.isEmpty()) {
            System.out.println(
                    "  [!] No se encontraron enfermeras con el criterio: "
                            + busqueda);
            return;
        }

        System.out.println("\n  Enfermeras encontradas:");
        Utilidades.imprimirLinea();
        System.out.printf("  %-15s %-25s %-22s %-18s %s%n", "RUT", "NOMBRE COMPLETO", "ESPECIALIDAD", "AREA", "TURNOS");
        Utilidades.imprimirLinea();

        for (Enfermera enfermera : resultado) {
            System.out.printf("  %-15s %-25s %-22s %-18s %d%n",
                    enfermera.getRut(),
                    enfermera.getNombreCompleto(),
                    enfermera.getEspecialidad(),
                    enfermera.getAreaAsignada(),
                    enfermera.getListaTurnos().size());
        }

        System.out.println("\n  Total: " + resultado.size()
                + " coincidencia(s).");

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
            edad = edadStr.isEmpty() ? e.getEdad() : Integer.parseInt(edadStr);
        } catch (NumberFormatException ex) {
            System.out.println("  [!] La edad debe ser un número entero entre 18 y 65.");
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
            String nombreFinal = nombre.isEmpty() ? e.getNombre() : nombre;
            EnfermeraControlador.editar(rut, nombreFinal, apP, apM, edad, esp, area);
            System.out.println("  [OK] Enfermera actualizada: " + e.getNombreCompleto());
        } catch (EdadInvalidaException | NombreDuplicadoException
                | NombreInvalidoException ex) {
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
                    System.out.println("  Especialidad para este turno:");
                    int espTurnoIdx = Utilidades.seleccionarOpcion(sc, Utilidades.ESPECIALIDADES);
                    if (espTurnoIdx < 1) { System.out.println("  [!] Especialidad invalida."); return; }
                    nuevoTurno.setEspecialidad(Utilidades.ESPECIALIDADES[espTurnoIdx - 1]);
                    break;

                case 2: // Licencia
                    System.out.println("  Tipo de licencia:");
                    int tlIdx = Utilidades.seleccionarOpcion(sc, Utilidades.TIPOS_LICENCIA);
                    if (tlIdx < 1) { System.out.println("  [!] Tipo invalido."); return; }
                    String tipoLic = Utilidades.TIPOS_LICENCIA[tlIdx - 1];
                    System.out.print("  Motivo: ");
                    String motivo = sc.nextLine().trim();
                    if (motivo.isEmpty()) {
                        System.out.println("  [!] El motivo de la licencia es obligatorio.");
                        return;
                    }
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
                    System.out.println("  Especialidad para este turno:");
                    int espCambioIdx = Utilidades.seleccionarOpcion(sc, Utilidades.ESPECIALIDADES);
                    if (espCambioIdx < 1) { System.out.println("  [!] Especialidad invalida."); return; }
                    nuevoTurno.setEspecialidad(Utilidades.ESPECIALIDADES[espCambioIdx - 1]);
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
                    + "[" + t.getEspecialidad() + "] " + t.getResumen());
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
            System.out.println("  Especialidad: " + t.getEspecialidad());
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
        System.out.println("  Nueva especialidad (0 = mantener "
                + t.getEspecialidad() + "):");
        int espIdx = Utilidades.seleccionarOpcion(sc, Utilidades.ESPECIALIDADES);
        String especialidad = espIdx < 1
            ? t.getEspecialidad() : Utilidades.ESPECIALIDADES[espIdx - 1];
        TurnoControlador.editar(e, idTurno, obs, especialidad);
        System.out.println("  [OK] Turno actualizado.");
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

        System.out.println("  Especialidad para estos turnos:");
        int espIdx = Utilidades.seleccionarOpcion(sc, Utilidades.ESPECIALIDADES);
        if (espIdx < 1) { System.out.println("  [!] Especialidad invalida."); return; }
        String especialidadTurno = Utilidades.ESPECIALIDADES[espIdx - 1];

        System.out.print("  Observacion (opcional): ");
        String obs = sc.nextLine().trim();

        int asignadas = 0;
        int conflictos = 0;
        for (Enfermera e : area.getEnfermeras().values()) {
            try {
                String id = Utilidades.generarIdTurno();
                TurnoRegular t = new TurnoRegular(id, fecha, horaIni, horaFin, tipoTurno, obs);
                t.setEspecialidad(especialidadTurno);
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

    /** Opcion 12: Filtro por cualquier tipo de horario (SIA-9). */
    private static void opcionFiltroPorHorario() {
        System.out.println("\n  === FILTRO DE TURNOS POR HORARIO ===");
        String[] horarios = {"Todos", "Mañana", "Tarde", "Noche"};
        System.out.println("  Horario a filtrar:");
        int horarioIdx = Utilidades.seleccionarOpcion(sc, horarios);
        if (horarioIdx < 1) {
            System.out.println("  [!] Horario invalido.");
            return;
        }
        String horarioVisible = horarios[horarioIdx - 1];
        String horario = "Mañana".equals(horarioVisible)
            ? Utilidades.TURNO_MANANA : horarioVisible;
        System.out.print("  Mes a evaluar (MM, p.ej. 09): ");
        String mes = sc.nextLine().trim();

        System.out.print("  Anio a evaluar (yyyy, p.ej. 2026): ");
        String anio = sc.nextLine().trim();
        if (!mes.matches("0[1-9]|1[0-2]") || !anio.matches("\\d{4}")) {
            System.out.println("  [!] Ingrese un mes entre 01 y 12 y un año de cuatro dígitos.");
            return;
        }

        System.out.print("  Maximo de turnos permitidos por mes: ");
        int limite = Utilidades.leerEntero(sc);
        if (limite < 0) {
            System.out.println("  [!] Limite invalido.");
            return;
        }

        List<Enfermera> exceso = EnfermeraControlador
                .filtrarExcesoTurnosPorHorario(horario, limite, mes, anio);

        System.out.println("\n  Enfermeras con mas de " + limite
                + " turno(s) " + horarioVisible.toLowerCase()
                + " en " + mes + "/" + anio + ":");
        Utilidades.imprimirLinea();

        if (exceso.isEmpty()) {
            System.out.println("  (Ninguna enfermera supera el limite establecido)");
        } else {
            for (Enfermera e : exceso) {
                int cant = e.contarTurnosPorHorarioMes(horario, mes, anio);
                System.out.println("  " + e.getNombreCompleto()
                        + " (" + e.getRut() + ")"
                        + " - Area: " + e.getAreaAsignada()
                        + " - Turnos " + horarioVisible.toLowerCase() + ": " + cant);
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
        String[] horariosArea = {"Todos", "Mañana", "Tarde", "Noche"};
        int ttIdx = Utilidades.seleccionarOpcion(sc, horariosArea);
        if (ttIdx > 1) {
            String horario = "Mañana".equals(horariosArea[ttIdx - 1])
                ? Utilidades.TURNO_MANANA : horariosArea[ttIdx - 1];
            // Usar sobrecarga 2 de AreaHospitalaria (SIA-5)
            area.listarEnfermeras(horario);
        } else if (ttIdx < 1) {
            System.out.println("  [!] Horario invalido.");
        }
    }

    /** Opcion 14: muestra en consola los mismos indicadores de la interfaz grafica. */
    private static void opcionEstadisticas() {
        int regulares = 0;
        int licencias = 0;
        int cambios = 0;
        int noches = 0;
        double horas = 0;

        System.out.println("\n  === ESTADISTICAS DEL SISTEMA ===");
        for (Enfermera enfermera : EnfermeraControlador.listar()) {
            regulares += enfermera.contarTurnosRegulares();
            licencias += enfermera.contarLicencias();
            cambios += enfermera.contarCambios();
            horas += enfermera.getHorasTrabajadas();
            for (Turno turno : enfermera.getListaTurnos()) {
                if (turno instanceof TurnoRegular
                        && Utilidades.TURNO_NOCHE.equals(
                            ((TurnoRegular) turno).getTipoTurno())) {
                    noches++;
                }
            }
        }

        System.out.println("  Enfermeras registradas : " + EnfermeraControlador.totalRegistradas());
        System.out.println("  Turnos regulares       : " + regulares);
        System.out.println("  Turnos noche           : " + noches);
        System.out.println("  Licencias              : " + licencias);
        System.out.println("  Cambios de turno       : " + cambios);
        System.out.printf("  Horas trabajadas       : %.1f h%n", horas);
        System.out.println("  Total de eventos       : " + (regulares + licencias + cambios));
    }

    // ===================================================================
    //  UTIL DE CONSOLA
    // ===================================================================

    private static void pausar() {
        System.out.print("\n  Presione ENTER para continuar...");
        sc.nextLine();
    }
}
