package TurnosEnfermeria.vista.prototipo;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import static TurnosEnfermeria.vista.prototipo.TemaPrototipo.*;

/** Pantallas y formularios del diseño propuesto, con contenido de ejemplo. */
final class PantallasPrototipo {
    private PantallasPrototipo() { }

    static JPanel crear(String pagina, Consumer<String> abrir) {
        switch (pagina) {
            case "Mi agenda": return agenda(abrir);
            case "Mis solicitudes": return solicitudes(abrir);
            case "Resumen operativo": return resumen(abrir);
            case "Planificación": return planificacion(abrir);
            case "Personal": return personal(abrir);
            case "Ausencias y cobertura": return ausencias(abrir);
            case "Cobertura del servicio": return cobertura(abrir);
            case "Informes": return informes(abrir);
            default: throw new IllegalArgumentException("Pantalla desconocida: " + pagina);
        }
    }

    private static JPanel pagina(String title, String subtitle) {
        JPanel page = new Pagina();
        page.setBorder(new javax.swing.border.EmptyBorder(26, 28, 26, 28));
        agregar(page, texto(title, 28, true, TINTA));
        page.add(Box.createVerticalStrut(8));
        agregar(page, parrafo(subtitle, SUAVE));
        page.add(Box.createVerticalStrut(24));
        return page;
    }

    private static void bloque(JPanel page, JComponent component) {
        agregar(page, component);
        page.add(Box.createVerticalStrut(18));
    }

    private static JPanel agenda(Consumer<String> abrir) {
        JPanel page = pagina("Mi agenda", "Persona 01 · E-001. Revisa tus próximos turnos y cualquier cambio pendiente.");
        bloque(page, indicadores(new String[][]{
            {"PRÓXIMO TURNO", "17 oct.", "07:00–15:00 · Área piloto A"},
            {"HORAS ASIGNADAS", "8 h", "Un turno a realizar en el ejemplo"},
            {"COMUNICACIONES", "1", "En revisión por coordinación"}
        }));
        JPanel card = tarjeta("Semana del 12 al 18 de octubre", "Los turnos que cruzan medianoche muestran ambas fechas.");
        agregar(card, filtros());
        card.add(Box.createVerticalStrut(16));
        agregar(card, tabla(new String[]{"Turno", "Fecha", "Horario", "Área", "Estado"}, new String[][]{
            {"T-001", "12 oct.", "07:00–15:00", "Área piloto A", "Ausencia registrada"},
            {"T-007", "17 oct.", "07:00–15:00", "Área piloto A", "Asignado"}
        }));
        card.add(Box.createVerticalStrut(16));
        agregar(card, acciones(boton("Ver detalle de T-001", false, () -> abrir.accept("detalle-turno")),
                boton("Comunicar ausencia", true, () -> abrir.accept("solicitud"))));
        bloque(page, card);
        bloque(page, aviso("Una comunicación no cambia el turno automáticamente",
                "En el ejemplo, coordinación ya registró la ausencia del 12 de octubre y está buscando reemplazo. El turno que necesita cobertura se conserva."));
        return page;
    }

    private static JPanel solicitudes(Consumer<String> abrir) {
        JPanel page = pagina("Mis solicitudes", "Revisa el estado de las comunicaciones sobre tus turnos.");
        bloque(page, aviso("Canal por validar con el servicio",
                "Esta función es una propuesta. Su uso y los estados de resolución deben acordarse con las personas entrevistadas."));
        JPanel card = tarjeta("Comunicaciones de Persona 01", "Solicitud de ejemplo en revisión; no corresponde a un envío real.");
        agregar(card, tabla(new String[]{"Solicitud", "Turno", "Categoría", "Estado", "Envío demo"}, new String[][]{
            {"SOL-001", "T-001 · 12 oct.", "Ausencia", "En revisión", "11 oct. · 18:30"}
        }));
        card.add(Box.createVerticalStrut(18));
        agregar(card, acciones(boton("Nueva comunicación", true, () -> abrir.accept("solicitud")),
                boton("Ver estado", false, () -> abrir.accept("detalle-solicitud"))));
        bloque(page, card);
        JPanel steps = tarjeta("Recorrido previsto", "Envío → revisión de coordinación → resolución → consulta del turno vigente.");
        agregar(steps, parrafo("El personal podrá consultar sus solicitudes. La coordinación determinará la acción operativa; una solicitud por sí sola no aprueba un cambio.", TINTA));
        bloque(page, steps);
        return page;
    }

    private static JPanel resumen(Consumer<String> abrir) {
        JPanel page = pagina("Resumen operativo", "Lo que el equipo necesita revisar esta semana en el Área piloto A.");
        bloque(page, indicadores(new String[][]{
            {"PERSONAL DEL ÁREA", "6", "Dotación registrada en el ejemplo"},
            {"AUSENCIAS", "1", "Turno T-001 por revisar"},
            {"BLOQUES CON DÉFICIT", "1", "12 oct. · 07:00–15:00"}
        }));
        JPanel pending = tarjeta("Prioridad: revisar la ausencia del 12 de octubre", "El ejemplo conserva la asignación original y muestra una necesidad de cobertura.");
        agregar(pending, tabla(new String[]{"Caso", "Titular", "Turno afectado", "Situación"}, new String[][]{
            {"SOL-001", "E-001 · Persona 01", "T-001 · 07:00–15:00", "Pendiente de cobertura"}
        }));
        pending.add(Box.createVerticalStrut(16));
        agregar(pending, acciones(boton("Revisar ausencia", true, () -> abrir.accept("ausencia")),
                boton("Explorar sustitución", false, () -> abrir.accept("sustituir"))));
        bloque(page, pending);
        bloque(page, aviso("Dotación y cobertura son indicadores distintos",
                "Tener seis personas registradas no garantiza cubrir cada bloque. La cobertura futura se calculará sobre las asignaciones efectivas y las ausencias."));
        return page;
    }

    private static JPanel planificacion(Consumer<String> abrir) {
        JPanel page = pagina("Planificación de turnos", "Consulta las asignaciones del equipo y revisa posibles conflictos de horario.");
        JPanel card = tarjeta("Asignaciones del escenario", "Ocho registros: siete asignaciones efectivas y un turno afectado por ausencia.");
        agregar(card, filtros());
        card.add(Box.createVerticalStrut(16));
        agregar(card, tabla(DatosPrototipo.COL_PLAN, DatosPrototipo.PLAN));
        card.add(Box.createVerticalStrut(16));
        agregar(card, acciones(boton("Asignar turno", true, () -> abrir.accept("asignar")),
                boton("Editar asignación", false, () -> abrir.accept("editar-turno")),
                boton("Ver conflicto de ejemplo", false, () -> abrir.accept("conflicto"))));
        bloque(page, card);
        return page;
    }

    private static JPanel personal(Consumer<String> abrir) {
        JPanel page = pagina("Personal del área", "Consulta los integrantes del equipo. Los nombres que aparecen son de ejemplo.");
        JPanel card = tarjeta("Dotación · Área piloto A", "Las bajas futuras deberán conservar los turnos y referencias históricas.");
        agregar(card, tabla(new String[]{"Código", "Nombre de ejemplo", "Área", "Situación"}, DatosPrototipo.PERSONAL));
        card.add(Box.createVerticalStrut(18));
        agregar(card, acciones(boton("Registrar personal", true, () -> abrir.accept("personal")),
                boton("Editar registro", false, () -> abrir.accept("editar-personal"))));
        bloque(page, card);
        return page;
    }

    private static JPanel ausencias(Consumer<String> abrir) {
        JPanel page = pagina("Ausencias y cobertura", "Revisa las ausencias y encuentra turnos que necesitan reemplazo.");
        bloque(page, aviso("Bloque pendiente · 12 oct. · 07:00–15:00",
                "Titular: E-001. Mínimo del ejemplo: 2; asignados efectivos: 1; déficit: 1. Este mínimo es ficticio y debe validarse para el servicio."));
        JPanel caseCard = tarjeta("Ausencia vinculada a T-001", "La coordinación revisará la incidencia y buscará un reemplazo elegible.");
        agregar(caseCard, acciones(boton("Registrar / revisar ausencia", false, () -> abrir.accept("ausencia")),
                boton("Resolver solicitud", false, () -> abrir.accept("resolver"))));
        bloque(page, caseCard);
        JPanel candidates = tarjeta("Disponibilidad · ejemplo de candidatos", "Esta lista ilustra las comprobaciones futuras; no ejecuta el cálculo de disponibilidad.");
        agregar(candidates, tabla(new String[]{"Código", "Persona", "Situación", "Motivo de ejemplo"}, new String[][]{
            {"E-003", "Persona 03", "Disponible", "Sin asignación en el bloque"},
            {"E-004", "Persona 04", "Disponible", "Sin asignación en el bloque"},
            {"E-002", "Persona 02", "Ocupado", "T-002 · 07:00–15:00"},
            {"E-007", "Persona 07", "Por validar", "Pertenece a otra área"}
        }));
        candidates.add(Box.createVerticalStrut(16));
        agregar(candidates, acciones(boton("Preparar sustitución", true, () -> abrir.accept("sustituir"))));
        bloque(page, candidates);
        return page;
    }

    private static JPanel cobertura(Consumer<String> abrir) {
        JPanel page = pagina("Cobertura del servicio", "Consulta cómo está la cobertura de turnos del servicio.");
        bloque(page, indicadores(new String[][]{
            {"BLOQUES DEL EJEMPLO", "4", "Muestra ilustrativa del período"},
            {"COBERTURA COMPLETA", "3 / 4", "Según el mínimo ficticio de 2"},
            {"HORAS PLANIFICADAS", "56 h", "Siete asignaciones efectivas"}
        }));
        JPanel card = tarjeta("Cobertura por bloque", "Las horas son planificadas: no representan asistencia ni horas efectivamente trabajadas.");
        agregar(card, filtros());
        card.add(Box.createVerticalStrut(16));
        agregar(card, tabla(DatosPrototipo.COL_COBERTURA, DatosPrototipo.COBERTURA));
        card.add(Box.createVerticalStrut(16));
        agregar(card, acciones(boton("Ver incidencia del bloque", false, () -> abrir.accept("detalle-cobertura")),
                boton("Vista previa del informe", true, () -> abrir.accept("reporte"))));
        bloque(page, card);
        bloque(page, aviso("Vista propuesta de consulta",
                "La jefatura consulta cobertura e informes. En la implementación futura, la autorización deberá impedir modificar asignaciones desde este rol."));
        return page;
    }

    private static JPanel informes(Consumer<String> abrir) {
        JPanel page = pagina("Informes del período", "Resumen de los turnos de la semana. Los filtros y la exportación aún no están disponibles.");
        JPanel card = tarjeta("Horas planificadas del escenario", "56 horas distribuidas entre siete asignaciones efectivas de ocho horas.");
        agregar(card, tabla(new String[]{"Código", "Persona", "Turnos efectivos", "Horas planificadas"}, new String[][]{
            {"E-001", "Persona 01", "1", "8 h"}, {"E-002", "Persona 02", "1", "8 h"},
            {"E-003", "Persona 03", "2", "16 h"}, {"E-004", "Persona 04", "1", "8 h"},
            {"E-005", "Persona 05", "1", "8 h"}, {"E-006", "Persona 06", "1", "8 h"}
        }));
        card.add(Box.createVerticalStrut(16));
        agregar(card, acciones(boton("Vista previa del informe", true, () -> abrir.accept("reporte")),
                boton("Exportación prevista", false, () -> abrir.accept("exportar"))));
        bloque(page, card);
        bloque(page, aviso("Incidencia del ejemplo",
                "Una ausencia afecta T-001 y deja un bloque con déficit. La sustitución todavía no está confirmada; las pantallas comparten esta misma fotografía ficticia."));
        return page;
    }

    private static JPanel filtros() {
        JComboBox<String> area = new JComboBox<>(new String[]{"Área piloto A"});
        JComboBox<String> period = new JComboBox<>(new String[]{DatosPrototipo.PERIODO});
        area.setToolTipText("Control visual propuesto; no consulta datos reales.");
        period.setToolTipText("El bosquejo contiene un solo período ficticio.");
        return acciones(texto("ÁREA", 11, true, SUAVE), area,
                texto("PERÍODO", 11, true, SUAVE), period);
    }

    static String tituloFormulario(String id) {
        switch (id) {
            case "solicitud": return "Comunicar una ausencia";
            case "detalle-solicitud": return "Estado de SOL-001 · ejemplo";
            case "asignar": return "Asignar un turno";
            case "editar-turno": return "Editar un turno";
            case "conflicto": return "Conflicto de horario · ejemplo";
            case "personal": return "Registrar personal";
            case "editar-personal": return "Editar personal";
            case "ausencia": return "Revisar una ausencia";
            case "resolver": return "Resolver solicitud SOL-001";
            case "sustituir": return "Preparar una sustitución";
            case "reporte": return "Vista previa del informe · ejemplo";
            case "exportar": return "Exportación aún no disponible";
            case "detalle-cobertura": return "Detalle del déficit · ejemplo";
            default: return "Detalle de T-001 · ejemplo";
        }
    }

    static JPanel formulario(String id) {
        JPanel body = vertical();
        body.setBorder(new javax.swing.border.EmptyBorder(22, 24, 8, 24));
        bloque(body, aviso("Datos de demostración", "Puedes revisar los campos y las opciones. Esta versión no registra ni guarda cambios."));
        switch (id) {
            case "solicitud":
                agregar(body, campo("Turno propio", new JComboBox<>(new String[]{"T-001 · 12 oct. · 07:00–15:00", "T-007 · 17 oct. · 07:00–15:00"})));
                agregar(body, campo("Categoría operativa", new JComboBox<>(new String[]{"Ausencia", "Solicitud de cambio"})));
                agregar(body, campo("Período afectado", new JTextField("12/10/2026 · 07:00–15:00")));
                JTextArea note = new JTextArea("Información operativa breve. Evita datos personales, de pacientes o antecedentes clínicos.");
                note.setLineWrap(true); note.setWrapStyleWord(true);
                agregar(body, campo("Observación opcional", note));
                break;
            case "asignar": case "editar-turno":
                if (id.equals("editar-turno")) agregar(body, campo("Turno a editar", new JTextField("T-007 · E-001")));
                JComboBox<String> persona = new JComboBox<>(new String[]{"E-003 · Persona 03", "E-001 · Persona 01", "E-002 · Persona 02"});
                if (id.equals("editar-turno")) persona.setSelectedIndex(1);
                agregar(body, campo("Personal del área", persona));
                agregar(body, campo("Área", new JTextField("Área piloto A")));
                agregar(body, campo("Fecha inicial", new JTextField(id.equals("editar-turno") ? "17/10/2026" : "12/10/2026")));
                agregar(body, campo("Horario", new JComboBox<>(new String[]{"07:00–15:00", "15:00–23:00", "23:00–07:00 (día siguiente)"})));
                break;
            case "personal": case "editar-personal":
                agregar(body, campo("Código interno propuesto", new JTextField(id.equals("editar-personal") ? "E-001" : "E-008")));
                agregar(body, campo("Nombre de ejemplo", new JTextField(id.equals("editar-personal") ? "Persona 01" : "Persona 08")));
                agregar(body, campo("Área", new JComboBox<>(new String[]{"Área piloto A"})));
                agregar(body, campo("Estado", new JComboBox<>(new String[]{"Activo", "Inactivo"})));
                break;
            case "ausencia": case "resolver":
                agregar(body, campo("Turno original afectado", new JTextField("T-001 · E-001 · 12 oct. · 07:00–15:00")));
                agregar(body, campo("Categoría operativa", new JComboBox<>(new String[]{"Ausencia", "Cambio solicitado"})));
                agregar(body, campo("Resolución propuesta", new JComboBox<>(new String[]{"En revisión", "Requiere sustitución", "Rechazada"})));
                agregar(body, parrafo("La ausencia deberá conservar el turno que necesita cobertura. Resolver la solicitud y completar la cobertura son acciones distintas.", TINTA));
                break;
            case "sustituir":
                agregar(body, campo("Turno original", new JTextField("T-001 · 12/10/2026 · 07:00–15:00")));
                agregar(body, campo("Titular", new JTextField("E-001 · Persona 01")));
                agregar(body, campo("Reemplazante propuesto", new JComboBox<>(new String[]{"E-003 · Persona 03 (disponible en el ejemplo)", "E-004 · Persona 04 (disponible en el ejemplo)"})));
                agregar(body, parrafo("Antes de confirmar: verificar turno original, disponibilidad, pertenencia o elegibilidad para el área y ausencia de conflictos. Esta pantalla no ejecuta esas comprobaciones.", TINTA));
                break;
            case "conflicto":
                bloque(body, aviso("Asignación que deberá rechazarse", "E-002 ya tiene T-002 el 12 de octubre, de 07:00 a 15:00. No puede cubrir otro turno que intersecte ese intervalo."));
                agregar(body, parrafo("La futura operación debe explicar el conflicto y conservar los registros anteriores. Aquí solo se presenta ese estado de error.", TINTA));
                break;
            case "reporte": case "exportar":
                agregar(body, texto("Área piloto A · " + DatosPrototipo.PERIODO, 17, true, TINTA));
                body.add(Box.createVerticalStrut(18));
                agregar(body, tabla(new String[]{"Indicador ilustrativo", "Valor"}, new String[][]{
                    {"Bloques de la muestra", "4"}, {"Bloques completos", "3"},
                    {"Bloques con déficit", "1"}, {"Horas planificadas efectivas", "56 h"},
                    {"Ausencias pendientes de cobertura", "1"}
                }));
                body.add(Box.createVerticalStrut(16));
                agregar(body, parrafo("El informe futuro deberá usar los mismos filtros y cálculos que el panel. Este resumen es fijo; no genera archivos ni informa asistencia real.", TINTA));
                break;
            default:
                agregar(body, texto(id.equals("detalle-solicitud") ? "SOL-001 · En revisión" : "T-001 · Pendiente de cobertura", 20, true, TINTA));
                body.add(Box.createVerticalStrut(18));
                agregar(body, tabla(new String[]{"Dato del ejemplo", "Valor"}, new String[][]{
                    {"Titular", "E-001 · Persona 01"}, {"Área", "Área piloto A"},
                    {"Intervalo", "12/10/2026 · 07:00–15:00"}, {"Mínimo ficticio", "2"},
                    {"Asignados efectivos", "1"}, {"Déficit", "1"}
                }));
                break;
        }
        return body;
    }

    static String accionPrevista(String id) {
        switch (id) {
            case "solicitud": return "Registrar una comunicación pendiente vinculada a un turno propio, sin cambiar automáticamente la asignación. Necesidad institucional por validar.";
            case "asignar": case "editar-turno": return "Comprobar identidad, área, fechas y conflictos; confirmar solo una asignación válida y conservar el turno anterior si la edición falla.";
            case "personal": case "editar-personal": return "Validar identidad y unicidad; guardar los atributos mínimos y conservar referencias cuando se desactive personal.";
            case "sustituir": return "Volver a validar a la persona seleccionada, vincular la sustitución con T-001 y actualizar la agenda efectiva y la cobertura. Nada de esto se ejecuta en este bosquejo.";
            case "ausencia": case "resolver": return "Registrar la ausencia y su resolución operativa, preservando los turnos afectados y la necesidad de cobertura.";
            case "exportar": case "reporte": return "Calcular y exportar un informe del área y período usando asignaciones efectivas. La exportación todavía no está implementada.";
            default: return "Consultar el registro con los permisos del rol y presentar su situación vigente. Actualmente se muestra únicamente un ejemplo fijo.";
        }
    }

    /** Hace que las páginas sigan el ancho de la ventana y permitan desplazarse. */
    private static final class Pagina extends JPanel implements Scrollable {
        Pagina() { setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); setOpaque(false); }
        @Override public Dimension getPreferredScrollableViewportSize() { return new Dimension(920, 700); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 24; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return Math.max(24, r.height - 24); }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
