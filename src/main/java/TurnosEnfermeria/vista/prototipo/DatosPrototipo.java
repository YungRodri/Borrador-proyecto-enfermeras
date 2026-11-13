package TurnosEnfermeria.vista.prototipo;

/** Escenario fijo y totalmente ficticio. Los valores no son cálculos del sistema. */
final class DatosPrototipo {
    static final String PERIODO = "12–18 octubre 2026";
    static final String[] COL_PLAN = {"Turno", "Fecha", "Horario", "Responsable", "Estado"};
    static final String[][] PLAN = {
        {"T-001", "12 oct.", "07:00–15:00", "E-001 · Persona 01", "Ausencia registrada"},
        {"T-002", "12 oct.", "07:00–15:00", "E-002 · Persona 02", "Asignado"},
        {"T-003", "13 oct.", "15:00–23:00", "E-003 · Persona 03", "Asignado"},
        {"T-004", "13 oct.", "15:00–23:00", "E-004 · Persona 04", "Asignado"},
        {"T-005", "15–16 oct.", "23:00–07:00", "E-005 · Persona 05", "Asignado"},
        {"T-006", "15–16 oct.", "23:00–07:00", "E-006 · Persona 06", "Asignado"},
        {"T-007", "17 oct.", "07:00–15:00", "E-001 · Persona 01", "Asignado"},
        {"T-008", "17 oct.", "07:00–15:00", "E-003 · Persona 03", "Asignado"}
    };
    static final String[] COL_COBERTURA = {"Fecha", "Bloque", "Mínimo demo", "Asignados efectivos", "Estado"};
    static final String[][] COBERTURA = {
        {"12 oct.", "07:00–15:00", "2", "1", "Déficit: 1"},
        {"13 oct.", "15:00–23:00", "2", "2", "Completa"},
        {"15–16 oct.", "23:00–07:00", "2", "2", "Completa"},
        {"17 oct.", "07:00–15:00", "2", "2", "Completa"}
    };
    static final String[][] PERSONAL = {
        {"E-001", "Persona 01", "Área piloto A", "Activo"},
        {"E-002", "Persona 02", "Área piloto A", "Activo"},
        {"E-003", "Persona 03", "Área piloto A", "Activo"},
        {"E-004", "Persona 04", "Área piloto A", "Activo"},
        {"E-005", "Persona 05", "Área piloto A", "Activo"},
        {"E-006", "Persona 06", "Área piloto A", "Activo"}
    };
    private DatosPrototipo() { }
}
