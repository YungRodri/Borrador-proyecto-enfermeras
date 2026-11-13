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
            case "Consulta personal": return consultaPersonal(abrir); // Nueva vista solo lectura para Coordinador
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
        JPanel page = pagina("Mi agenda", "Persona 01 · E-001. Revisa tus próximos turnos, asistencia y disponibilidad.");
        bloque(page, indicadores(new String[][]{
                {"PRÓXIMO TURNO", "17 oct.", "07:00–15:00 · Área piloto A"},
                {"RESUMEN DE ASISTENCIA", "0 · 1 · 1", "0 presentes · 1 ausente · 1 pendiente"},
                {"COMUNICACIONES", "1", "SOL-001 aprobada · reemplazo pendiente"}
        }));
        JPanel card = tarjeta("Semana del 12 al 18 de octubre", "Los turnos que cruzan medianoche muestran ambas fechas.");
        agregar(card, filtros());
        card.add(Box.createVerticalStrut(16));
        agregar(card, tabla(new String[]{"Turno", "Fecha", "Horario", "Área", "Estado Asistencia", "Observación"}, new String[][]{
                {"T-001", "12 oct.", "07:00–15:00", "Área piloto A", "Ausente", "Ausencia justificada · SOL-001 aprobada"},
                {"T-007", "17 oct.", "07:00–15:00", "Área piloto A", "Pendiente de registro", "—"}
        }));
        card.add(Box.createVerticalStrut(16));
        agregar(card, acciones(boton("Comunicar ausencia o cambio", true, () -> abrir.accept("solicitud")),
                boton("Solicitar corrección", false, () -> abrir.accept("correccion")),
                boton("Comunicar disponibilidad", false, () -> abrir.accept("disponibilidad"))));
        bloque(page, card);
        bloque(page, aviso("Una comunicación no cambia el turno automáticamente",
                "En el ejemplo, coordinación aprobó la ausencia del 12 de octubre y sigue buscando reemplazo. El turno que necesita cobertura se conserva."));
        return page;
    }

    private static JPanel solicitudes(Consumer<String> abrir) {
        JPanel page = pagina("Mis solicitudes", "Revisa el estado de las comunicaciones sobre tus turnos.");
        bloque(page, aviso("Canal de solicitudes y revisiones",
                "La enfermera solicita los cambios de turno o ausencias; quien los revisa y resuelve es la coordinación."));
        JPanel card = tarjeta("Comunicaciones de Persona 01", "Solicitud de ejemplo aprobada; el reemplazo del turno sigue pendiente.");
        agregar(card, tabla(new String[]{"Solicitud", "Turno", "Categoría", "Estado", "Motivo de resolución", "Envío demo"}, new String[][]{
                {"SOL-001", "T-001 · 12 oct.", "Ausencia", "Aprobada", "Ausencia justificada", "11 oct. · 18:30"}
        }));
        card.add(Box.createVerticalStrut(18));
        agregar(card, acciones(boton("Nueva comunicación", true, () -> abrir.accept("solicitud")),
                boton("Ver estado", false, () -> abrir.accept("detalle-solicitud"))));
        bloque(page, card);
        JPanel steps = tarjeta("Recorrido previsto", "Envío → revisión de coordinación → resolución → consulta del turno vigente.");
        agregar(steps, parrafo("El personal podrá consultar sus solicitudes. La coordinación determinará la acción operativa; una solicitud por sí sola no aprueba un cambio ni confirma un reemplazo.", TINTA));
        bloque(page, steps);
        return page;
    }

    private static JPanel resumen(Consumer<String> abrir) {
        JPanel page = pagina("Resumen operativo", "Lo que el equipo necesita revisar esta semana en el Área piloto A.");
        bloque(page, indicadores(new String[][]{
                {"PERSONAL DEL ÁREA", "6", "Dotación activa registrada en el ejemplo"},
                {"AUSENCIAS", "1", "Registrada en T-001 · 12 oct."},
                {"BLOQUES CON DÉFICIT", "1", "12 oct. · 07:00–15:00"}
        }));
        JPanel pending = tarjeta("Prioridad: cubrir el turno T-001", "La ausencia de E-001 está registrada y la solicitud fue aprobada; el turno sigue sin reemplazo confirmado.");
        agregar(pending, tabla(new String[]{"Caso", "Titular", "Turno afectado", "Situación"}, new String[][]{
                {"SOL-001", "E-001 · Persona 01", "T-001 · 07:00–15:00", "Pendiente de cobertura"}
        }));
        pending.add(Box.createVerticalStrut(16));
        agregar(pending, acciones(boton("Ver ausencia registrada", true, () -> abrir.accept("ausencia")),
                boton("Buscar reemplazo", false, () -> abrir.accept("sustituir"))));
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
        JPanel page = pagina("Personal del área", "Administración del sistema: gestiona altas, bajas, cuentas y áreas de trabajo.");
        JPanel card = tarjeta("Dotación · Área piloto A", "Las bajas futuras (Inactivo) deberán conservar los turnos y referencias históricas.");
        agregar(card, tabla(new String[]{"Código", "Nombre de ejemplo", "Área", "Estado"}, DatosPrototipo.PERSONAL));
        card.add(Box.createVerticalStrut(18));
        agregar(card, acciones(boton("Registrar personal", true, () -> abrir.accept("personal")),
                boton("Editar registro", false, () -> abrir.accept("editar-personal")),
                boton("Cuentas y perfiles", false, () -> abrir.accept("cuentas")),
                boton("Áreas de trabajo", false, () -> abrir.accept("areas"))));
        bloque(page, card);
        return page;
    }

    private static JPanel consultaPersonal(Consumer<String> abrir) {
        JPanel page = pagina("Consulta de personal", "Organización de turnos y disponibilidad del equipo (Solo lectura).");
        JPanel card = tarjeta("Dotación · Área piloto A", "El administrador del sistema gestiona las altas y bajas.");
        agregar(card, tabla(new String[]{"Código", "Nombre de ejemplo", "Área", "Estado"}, DatosPrototipo.PERSONAL));
        bloque(page, card);
        return page;
    }

    private static JPanel ausencias(Consumer<String> abrir) {
        JPanel page = pagina("Ausencias, asistencia y cobertura", "Control de asistencia, revisión de ausencias y turnos que necesitan reemplazo.");

        JPanel asistencia = tarjeta("Registro de asistencia e historial", "Distinción de estados operativos diarios. La ausencia no se marca automáticamente por falta de registro.");
        agregar(asistencia, tabla(new String[]{"Turno", "Persona", "Fecha", "Estado de Asistencia"}, new String[][]{
                {"T-002", "E-002", "12 oct.", "Presente"},
                {"T-005", "E-005", "15–16 oct.", "Pendiente de registro"},
                {"T-001", "E-001", "12 oct.", "Ausente"}
        }));
        asistencia.add(Box.createVerticalStrut(16));
        agregar(asistencia, acciones(boton("Registrar asistencia", true, () -> abrir.accept("asistencia")),
                boton("Revisar y resolver corrección", false, () -> abrir.accept("resolver-correccion")),
                boton("Ver historial completo", false, () -> abrir.accept("historial"))));
        bloque(page, asistencia);

        bloque(page, aviso("Bloque pendiente de cobertura · 12 oct. · 07:00–15:00",
                "Titular: E-001. Mínimo del ejemplo: 2; asignados efectivos: 1; déficit: 1. Este mínimo es ficticio y debe validarse para el servicio."));

        JPanel caseCard = tarjeta("Ausencia vinculada a T-001", "La ausencia está registrada y la solicitud SOL-001 fue aprobada; falta confirmar un reemplazo elegible.");
        agregar(caseCard, acciones(boton("Registrar ausencia", false, () -> abrir.accept("ausencia")),
                boton("Resolver solicitud", false, () -> abrir.accept("resolver"))));
        bloque(page, caseCard);

        JPanel candidates = tarjeta("Disponibilidad · ejemplo de candidatos", "Esta lista ilustra las comprobaciones futuras; no ejecuta el cálculo real.");
        agregar(candidates, tabla(new String[]{"Código", "Persona", "Situación", "Motivo de ejemplo"}, new String[][]{
                {"E-003", "Persona 03", "Disponible", "Sin asignación en el bloque"},
                {"E-004", "Persona 04", "Disponible", "Sin asignación en el bloque"},
                {"E-002", "Persona 02", "Ocupado", "T-002 · 07:00–15:00"}
        }));
        candidates.add(Box.createVerticalStrut(16));
        agregar(candidates, acciones(boton("Confirmar reemplazo", true, () -> abrir.accept("sustituir"))));
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
        bloque(page, aviso("Vista de Coordinación",
                "La coordinación analiza la cobertura e informes. La autorización debe impedir modificar asignaciones estructurales desde estas vistas analíticas."));
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
                "Una ausencia afecta T-001 y deja un bloque con déficit. La sustitución todavía no está confirmada."));
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
            case "solicitud": return "Comunicar ausencia o solicitar cambio de turno";
            case "detalle-solicitud": return "Estado de SOL-001 · ejemplo";
            case "asignar": return "Asignar un turno";
            case "editar-turno": return "Editar un turno";
            case "conflicto": return "Conflicto de horario · ejemplo";
            case "personal": return "Registrar personal";
            case "editar-personal": return "Editar personal";
            case "ausencia": return "Registrar ausencia";
            case "resolver": return "Resolver solicitud SOL-001";
            case "sustituir": return "Confirmar reemplazo del turno T-001";
            case "asistencia": return "Registrar asistencia";
            case "reporte": return "Vista previa del informe · ejemplo";
            case "exportar": return "Exportación aún no disponible";
            case "detalle-cobertura": return "Detalle del déficit · ejemplo";
            case "correccion": return "Solicitar corrección de asistencia";
            case "resolver-correccion": return "Revisar y resolver corrección";
            case "disponibilidad": return "Comunicar disponibilidad";
            case "historial": return "Historial de asistencia";
            case "cuentas": return "Cuentas y perfiles";
            case "areas": return "Áreas de trabajo";
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
                agregar(body, campo("Categoría operativa", new JComboBox<>(new String[]{"Ausencia", "Cambio de turno"})));
                agregar(body, campo("Período afectado", new JTextField("12/10/2026 · 07:00–15:00")));
                agregar(body, campo("Motivo de la solicitud", new JTextField("")));
                JTextArea note = new JTextArea("Información operativa breve. Evita datos personales, de pacientes o antecedentes clínicos.");
                note.setLineWrap(true); note.setWrapStyleWord(true);
                agregar(body, campo("Observación opcional", note));
                break;
            case "detalle-solicitud":
                agregar(body, campo("Identificador", new JTextField("SOL-001")));
                agregar(body, campo("Tipo de solicitud", new JTextField("Ausencia")));
                agregar(body, campo("Turno o intervalo", new JTextField("T-001 · 12 oct. · 07:00–15:00")));
                agregar(body, campo("Estado", new JTextField("Aprobada")));
                agregar(body, campo("Resolución", new JTextField("Aprobada por coordinación · 11 oct. · 19:10")));
                agregar(body, campo("Motivo de la resolución", new JTextField("Ausencia justificada")));
                agregar(body, parrafo("La solicitud está aprobada y la ausencia registrada. La cobertura del turno sigue pendiente de reemplazo.", TINTA));
                break;
            case "correccion":
                agregar(body, campo("Turno propio", new JComboBox<>(new String[]{"T-007 · 17 oct. · 07:00–15:00", "T-001 · 12 oct. · 07:00–15:00"})));
                agregar(body, campo("Asistencia actual", new JTextField("Pendiente de registro")));
                agregar(body, campo("Estado solicitado", new JComboBox<>(new String[]{"Presente", "Ausente"})));
                agregar(body, campo("Motivo", new JTextField("")));
                agregar(body, parrafo("La coordinación revisará la corrección y la aprobará o rechazará indicando el motivo de su decisión.", TINTA));
                break;
            case "resolver-correccion":
                agregar(body, campo("Corrección", new JTextField("COR-001 · enviada por E-005 · Persona 05")));
                agregar(body, campo("Registro actual", new JTextField("T-005 · 15–16 oct. · Pendiente de registro")));
                agregar(body, campo("Cambio solicitado", new JTextField("Presente")));
                agregar(body, campo("Decisión", new JComboBox<>(new String[]{"Pendiente", "Aprobada", "Rechazada"})));
                agregar(body, campo("Motivo de la decisión", new JTextField("")));
                break;
            case "disponibilidad":
                agregar(body, campo("Fecha y hora de inicio", new JTextField("18/10/2026 · 07:00")));
                agregar(body, campo("Fecha y hora de término", new JTextField("18/10/2026 · 15:00")));
                agregar(body, parrafo("Coordinación podrá considerar este intervalo al buscar reemplazos para los bloques con déficit.", TINTA));
                break;
            case "historial":
                agregar(body, texto("Movimientos del escenario", 15, true, TINTA));
                body.add(Box.createVerticalStrut(12));
                agregar(body, tabla(new String[]{"Fecha", "Responsable", "Operación", "Registro afectado"}, new String[][]{
                        {"11 oct. · 18:30", "E-001 · Persona 01", "Comunicó ausencia", "SOL-001 (T-001)"},
                        {"11 oct. · 19:10", "Coordinación", "Aprobó solicitud", "SOL-001"},
                        {"12 oct. · 07:45", "Coordinación", "Registró asistencia", "T-002 · E-002"},
                        {"12 oct. · 15:05", "Coordinación", "Registró ausencia", "T-001 · E-001"}
                }));
                body.add(Box.createVerticalStrut(18));
                agregar(body, texto("Correcciones de asistencia", 15, true, TINTA));
                body.add(Box.createVerticalStrut(12));
                agregar(body, tabla(new String[]{"Fecha", "Registro", "Valor anterior", "Valor nuevo", "Motivo"}, new String[][]{
                        {"12 oct. · 15:05", "T-001 · E-001", "Pendiente de registro", "Ausente", "Ausencia aprobada (SOL-001)"}
                }));
                break;
            case "detalle-cobertura":
                agregar(body, campo("Área", new JTextField("Área piloto A")));
                agregar(body, campo("Intervalo", new JTextField("12 oct. · 07:00–15:00")));
                agregar(body, campo("Mínimo ficticio", new JTextField("2 personas")));
                agregar(body, campo("Asignados efectivos", new JTextField("1 · E-002 (T-002)")));
                agregar(body, campo("Déficit", new JTextField("1 persona")));
                agregar(body, parrafo("La cobertura es información de consulta: se actualiza al confirmar un reemplazo, no desde esta ventana.", TINTA));
                break;
            case "cuentas":
                agregar(body, tabla(new String[]{"Cuenta", "Persona asociada", "Perfil", "Estado"}, new String[][]{
                        {"U-001", "E-001 · Persona 01", "Enfermera", "Activa"},
                        {"U-002", "E-002 · Persona 02", "Enfermera", "Activa"},
                        {"U-003", "E-005 · Persona 05", "Enfermera", "Desactivada"},
                        {"U-010", "Coordinación · Área piloto A", "Coordinador", "Activa"},
                        {"U-020", "Administración central", "Administrador", "Activa"}
                }));
                body.add(Box.createVerticalStrut(18));
                agregar(body, acciones(botonVisual("Crear cuenta"), botonVisual("Desactivar cuenta"), botonVisual("Asignar perfil")));
                body.add(Box.createVerticalStrut(16));
                agregar(body, parrafo("Cada cuenta se asocia a una persona y a un perfil: Enfermera, Coordinador o Administrador. Desactivar una cuenta no elimina a la persona ni su historial.", TINTA));
                break;
            case "areas":
                agregar(body, tabla(new String[]{"Identificador", "Área", "Personal asociado"}, new String[][]{
                        {"A-01", "Área piloto A", "6 personas · E-001 a E-006"}
                }));
                body.add(Box.createVerticalStrut(18));
                agregar(body, acciones(botonVisual("Nueva área"), botonVisual("Editar área"), botonVisual("Vincular personal")));
                body.add(Box.createVerticalStrut(16));
                agregar(body, parrafo("El mantenimiento de áreas corresponde al perfil Administrador. Las altas o cambios se reflejarán en la planificación del servicio.", TINTA));
                break;
            case "asistencia":
                agregar(body, campo("Turno", new JComboBox<>(new String[]{"T-002 · 12 oct. · 07:00–15:00", "T-005 · 15–16 oct. · 23:00–07:00", "T-001 · 12 oct. · 07:00–15:00"})));
                agregar(body, campo("Persona", new JTextField("E-002 · Persona 02")));
                agregar(body, campo("Estado de asistencia", new JComboBox<>(new String[]{"Presente", "Ausente"})));
                agregar(body, parrafo("Sin registro, el estado mostrado sigue siendo Pendiente de registro: la ausencia no se marca automáticamente.", TINTA));
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
                agregar(body, campo("Estado del personal", new JComboBox<>(new String[]{"Activo", "Inactivo (Conserva historial)"})));
                break;
            case "ausencia":
                agregar(body, campo("Persona", new JComboBox<>(new String[]{"E-001 · Persona 01", "E-002 · Persona 02", "E-003 · Persona 03",
                        "E-004 · Persona 04", "E-005 · Persona 05", "E-006 · Persona 06"})));
                agregar(body, campo("Intervalo de la ausencia", new JTextField("12/10/2026 · 07:00–15:00")));
                agregar(body, campo("Turnos afectados", new JTextField("T-001 · 12 oct. · 07:00–15:00")));
                agregar(body, campo("Motivo", new JTextField("")));
                agregar(body, parrafo("El registro es válido aunque no existan turnos afectados: la ausencia queda asociada a la persona y al intervalo.", TINTA));
                break;
            case "resolver":
                agregar(body, campo("Solicitud", new JTextField("SOL-001 · Ausencia · E-001 · Persona 01")));
                agregar(body, campo("Turno afectado", new JTextField("T-001 · 12 oct. · 07:00–15:00")));
                JComboBox<String> estadoSolicitud = new JComboBox<>(new String[]{"Pendiente", "Aprobada", "Rechazada"});
                estadoSolicitud.setSelectedIndex(1);
                agregar(body, campo("Estado de la solicitud", estadoSolicitud));
                agregar(body, campo("Motivo de la decisión", new JTextField("Ausencia justificada por coordinación")));
                JTextField coberturaConsulta = new JTextField("Pendiente de reemplazo");
                coberturaConsulta.setEditable(false);
                agregar(body, campo("Cobertura del turno (solo consulta)", coberturaConsulta));
                agregar(body, parrafo("Resolver la solicitud y asegurar la cobertura son estados distintos: la cobertura se actualiza al confirmar un reemplazo en su propia ventana.", TINTA));
                break;
            case "sustituir":
                agregar(body, campo("Turno original", new JTextField("T-001 · 12/10/2026")));
                agregar(body, campo("Intervalo", new JTextField("07:00–15:00")));
                agregar(body, campo("Titular ausente", new JTextField("E-001 · Persona 01")));
                agregar(body, campo("Sustituta propuesta", new JComboBox<>(new String[]{"E-003 · Persona 03 (disponible en el ejemplo)", "E-004 · Persona 04 (disponible en el ejemplo)"})));
                agregar(body, parrafo("Antes de confirmar: verificar turno original, disponibilidad, pertenencia o elegibilidad para el área y ausencia de conflictos. Al confirmarse el reemplazo, la cobertura del bloque dejará de mostrar déficit.", TINTA));
                break;
            case "conflicto":
                bloque(body, aviso("Asignación que deberá rechazarse", "E-002 ya tiene T-002 el 12 de octubre, de 07:00 a 15:00. No puede cubrir otro turno que intersecte ese intervalo."));
                break;
            case "reporte": case "exportar":
                agregar(body, texto("Área piloto A · " + DatosPrototipo.PERIODO, 17, true, TINTA));
                body.add(Box.createVerticalStrut(18));
                agregar(body, tabla(new String[]{"Indicador ilustrativo", "Valor"}, new String[][]{
                        {"Bloques de la muestra", "4"}, {"Bloques completos", "3"},
                        {"Bloques con déficit", "1"}, {"Horas planificadas efectivas", "56 h"}
                }));
                body.add(Box.createVerticalStrut(16));
                agregar(body, parrafo("El informe futuro deberá usar los mismos filtros y cálculos que el panel. Este resumen es fijo; no genera archivos ni informa asistencia real.", TINTA));
                break;
            default:
                agregar(body, texto("Ejemplo genérico de formulario / detalle", 20, true, TINTA));
                break;
        }
        return body;
    }

    static String accionPrevista(String id) {
        switch (id) {
            case "solicitud": return "Registrar una comunicación pendiente vinculada a un turno propio indicando el motivo. Necesidad institucional por validar por coordinación.";
            case "detalle-solicitud": return "Consultar el estado de la solicitud, su resolución y el motivo registrado por coordinación.";
            case "asignar": case "editar-turno": return "Comprobar identidad, área, fechas y conflictos; confirmar solo una asignación válida.";
            case "personal": case "editar-personal": return "Validar identidad y unicidad; guardar los atributos mínimos. El estado Inactivo distingue al personal dado de baja conservando su historial.";
            case "asistencia": return "Marcar la asistencia del turno como Presente o Ausente. Sin registro, el estado mostrado seguirá siendo Pendiente de registro.";
            case "ausencia": return "Registrar directamente la ausencia de una persona en un intervalo, con su motivo, aunque no existan turnos afectados.";
            case "resolver": return "Resolver la solicitud con estado Pendiente, Aprobada o Rechazada, dejando constancia del motivo de la decisión. La cobertura se gestiona por separado.";
            case "correccion": return "Enviar una solicitud de corrección sobre la asistencia de un turno propio; la coordinación la revisará y resolverá.";
            case "resolver-correccion": return "Comparar el registro actual con el cambio solicitado y resolver la corrección con decisión y motivo.";
            case "disponibilidad": return "Comunicar un intervalo de disponibilidad para que coordinación lo considere al buscar reemplazos.";
            case "sustituir": return "Confirmar la sustituta del turno original en su intervalo; al confirmarse, la cobertura dejará de mostrar déficit.";
            case "cuentas": return "Crear cuentas, desactivarlas y asignar perfiles; cada cuenta se asocia a una persona registrada.";
            case "areas": return "Mantener las áreas de trabajo y el personal asociado a cada una.";
            case "exportar": case "reporte": return "Calcular y exportar un informe del área y período usando asignaciones efectivas.";
            default: return "Consultar el registro con los permisos del rol y presentar su situación vigente.";
        }
    }

    /** Botón ilustrativo para acciones que el bosquejo solo representa visualmente. */
    private static JButton botonVisual(String title) {
        JButton[] ref = new JButton[1];
        ref[0] = boton(title, false, () -> JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(ref[0]),
                "Acción visual del bosquejo: estará disponible en una versión futura; esta demostración no ejecuta cambios.",
                "Acción visual", JOptionPane.INFORMATION_MESSAGE));
        return ref[0];
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
