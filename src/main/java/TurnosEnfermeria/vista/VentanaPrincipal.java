package TurnosEnfermeria.vista;

import TurnosEnfermeria.controlador.EnfermeraControlador;
import TurnosEnfermeria.modelo.Enfermera;
import TurnosEnfermeria.modelo.Utilidades;
import java.util.function.BooleanSupplier;
import TurnosEnfermeria.controlador.TurnoControlador;
import TurnosEnfermeria.modelo.TurnoConflictoException;
import TurnosEnfermeria.modelo.TurnoRegular;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Ventana principal de la interfaz grafica del sistema (SIA-10).
 * Sirve como punto de entrada a todas las funcionalidades de la GUI:
 *   - Gestion de Enfermeras (Coleccion 1)
 *   - Gestion de Turnos por Enfermera (Coleccion 2 anidada)
 *   - Estadisticas y graficos (SIA-O1)
 *   - Filtros de negocio (SIA-9)
 *
 * Implementa el guardado de datos al cerrar la ventana (SIA-11 batch).
 */
public class VentanaPrincipal extends JFrame {

    /** Callback que se ejecuta al cerrar la ventana (para grabar datos a disco). */
    private final BooleanSupplier alCerrar;

    /**
     * Constructor principal.
     * @param alCerrar accion a ejecutar al cerrar la ventana (guardar datos CSV)
     */
    public VentanaPrincipal(BooleanSupplier alCerrar) {
        this.alCerrar = alCerrar;
        setTitle("Sistema de Gestión de Turnos de Enfermeras – Hospital Central");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(680, 480);
        setMinimumSize(new Dimension(620, 440));
        setLocationRelativeTo(null);
        getContentPane().setBackground(EstilosGUI.COLOR_FONDO);
        setLayout(new BorderLayout(0, 0));

        // Interceptar el cierre para guardar datos
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSistema();
            }
        });

        construirUI();
    }

    // =====================================================================
    //  CONSTRUCCION DE UI
    // =====================================================================

        private void construirUI() {
        add(crearPanelEncabezado(), BorderLayout.NORTH);
        add(crearPanelMenuCentral(), BorderLayout.CENTER);
        add(crearPanelPie(), BorderLayout.SOUTH);
    }

    /** Muestra el nombre del sistema. */
    private JPanel crearPanelEncabezado() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel titulo = new JLabel("Gestión de turnos de enfermería",SwingConstants.CENTER);
        titulo.setFont(new Font("Dialog", Font.BOLD, 20));
        titulo.setForeground(Color.BLACK);

        JLabel subtitulo = new JLabel("Seleccione una opción",SwingConstants.CENTER);
        subtitulo.setFont(new Font("Dialog", Font.PLAIN, 14));
        subtitulo.setForeground(Color.BLACK);

        panel.add(titulo);
        panel.add(subtitulo);
        return panel;
    }

    /** Organiza las opciones en cuatro filas y dos columnas. */
    private JPanel crearPanelMenuCentral() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        panel.add(crearBotonMenu("Gestión de enfermeras",e -> new VentanaEnfermeras().setVisible(true)));

        panel.add(crearBotonMenu("Gestión de turnos",e -> abrirGestionTurnos()));

        panel.add(crearBotonMenu("Asignación grupal",e -> mostrarAsignacionGrupal()));

        panel.add(crearBotonMenu("Consultar disponibilidad",e -> mostrarValidacionCobertura()));

        panel.add(crearBotonMenu("Filtrar por área",e -> mostrarFiltroPorArea()));

        panel.add(crearBotonMenu("Exceso de turnos por horario",e -> mostrarFiltroNocturnos()));

        panel.add(crearBotonMenu("Estadísticas",e -> new VentanaEstadisticas().setVisible(true)));

        panel.add(crearBotonMenu("Guardar y salir",e -> cerrarSistema()));

        return panel;
    }

    /** Crea un botón con texto negro y fondo blanco. */
    private JButton crearBotonMenu(
            String texto, java.awt.event.ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Dialog", Font.PLAIN, 14));
        boton.setBackground(Color.WHITE);
        boton.setForeground(Color.BLACK);
        boton.setOpaque(true);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK),
            new EmptyBorder(12, 12, 12, 12)
        ));
        boton.addActionListener(accion);
        return boton;
    }

    /** Indica cuándo se guardan los datos. */
    private JPanel crearPanelPie() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 10, 12, 10));

        JLabel informacion = new JLabel("Los datos se guardan al cerrar el sistema.");
        informacion.setFont(new Font("Dialog", Font.PLAIN, 12));
        informacion.setForeground(Color.BLACK);

        panel.add(informacion);
        return panel;
    }

    // =====================================================================
    //  FUNCIONALIDADES DE NEGOCIO (SIA-9)
    // =====================================================================

    /** Abre la ventana de gestion de turnos, pidiendo primero seleccionar una enfermera. */
    private void abrirGestionTurnos() {
        List<Enfermera> todas = EnfermeraControlador.listar();
        if (todas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay enfermeras registradas. Primero agregue una enfermera.",
                "Sin datos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] opciones = new String[todas.size()];
        for (int i = 0; i < todas.size(); i++) {
            opciones[i] = todas.get(i).getRut() + " – " + todas.get(i).getNombreCompleto();
        }

        String seleccion = (String) JOptionPane.showInputDialog(this,
            "Seleccione la enfermera para gestionar sus turnos:",
            "Seleccionar Enfermera", JOptionPane.QUESTION_MESSAGE,
            null, opciones, opciones[0]);

        if (seleccion == null) return;
        String rut = seleccion.split(" – ")[0];
        Enfermera e = EnfermeraControlador.obtener(rut);
        if (e != null) new VentanaTurnos(e, null).setVisible(true);
    }

        /** Consulta el exceso de turnos regulares por horario, mes y año. */
    private void mostrarFiltroNocturnos() {
        JComboBox<String> campoHorario =
            EstilosGUI.crearComboBox(Utilidades.getTiposTurno());
        JTextField campoMes = EstilosGUI.crearCampoTexto(4);
        JTextField campoAnio = EstilosGUI.crearCampoTexto(6);
        JTextField campoLimite = EstilosGUI.crearCampoTexto(4);

        campoHorario.setSelectedItem(Utilidades.TURNO_NOCHE);
        campoMes.setText("09");
        campoAnio.setText("2026");
        campoLimite.setText("3");

        JPanel formulario = new JPanel(new GridLayout(4, 2, 8, 8));
        formulario.setBackground(Color.WHITE);

        formulario.add(EstilosGUI.crearLabel("Horario:"));
        formulario.add(campoHorario);
        formulario.add(EstilosGUI.crearLabel("Mes (MM):"));
        formulario.add(campoMes);
        formulario.add(EstilosGUI.crearLabel("Año (yyyy):"));
        formulario.add(campoAnio);
        formulario.add(EstilosGUI.crearLabel("Máximo permitido:"));
        formulario.add(campoLimite);

        int respuesta = JOptionPane.showConfirmDialog(
            this, formulario, "Exceso de turnos por horario",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (respuesta != JOptionPane.OK_OPTION) return;

        try {
            String horario = (String) campoHorario.getSelectedItem();
            String mes = campoMes.getText().trim();
            String anio = campoAnio.getText().trim();
            int limite = Integer.parseInt(campoLimite.getText().trim());

            List<Enfermera> resultado =
                EnfermeraControlador.filtrarExcesoTurnosPorHorario(horario, limite, mes, anio);

            if (resultado.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Ninguna enfermera supera el límite indicado.",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            StringBuilder detalle = new StringBuilder();
            detalle.append("Horario: ").append(horario)
                   .append(" | Periodo: ").append(mes).append("/")
                   .append(anio)
                   .append("\nMás de ").append(limite)
                   .append(" turnos:\n\n");

            for (Enfermera enfermera : resultado) {
                detalle.append(enfermera.getNombreCompleto())
                       .append(" [").append(enfermera.getRut()).append("]")
                       .append(" | Área: ").append(enfermera.getAreaAsignada())
                       .append(" | Turnos: ")
                       .append(enfermera.contarTurnosPorHorarioMes(horario, mes, anio)).append("\n");
            }

            detalle.append("\nTotal: ").append(resultado.size()).append(" enfermera(s).");

            JTextArea texto = new JTextArea(detalle.toString(), 12, 50);
            texto.setEditable(false);
            texto.setLineWrap(true);
            texto.setWrapStyleWord(true);
            texto.setBackground(Color.WHITE);
            texto.setForeground(Color.BLACK);
            texto.setCaretPosition(0);

            JOptionPane.showMessageDialog(this, new JScrollPane(texto),"Resultado del filtro", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El máximo permitido debe ser un número entero.","Datos inválidos", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Datos inválidos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Muestra un dialogo de filtro de enfermeras por area hospitalaria. */
    private void mostrarFiltroPorArea() {
        JComboBox<String> comboArea = EstilosGUI.crearComboBox(Utilidades.getAreasHospitalarias());
        int res = JOptionPane.showConfirmDialog(this, comboArea,
            "Filtrar Enfermeras por Área Hospitalaria",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String area = (String) comboArea.getSelectedItem();
        List<Enfermera> resultado = EnfermeraControlador.listarPorArea(area);

        if (resultado.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay enfermeras asignadas al área: " + area,
                "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder("<html><b>Enfermeras en el área " + area + ":</b><br><br>");
            for (Enfermera e : resultado) {
                sb.append("• ").append(e.getNombreCompleto())
                  .append(" [").append(e.getRut()).append("] – ")
                  .append(e.getEspecialidad()).append("<br>");
            }
            sb.append("<br><i>Total: ").append(resultado.size()).append(" enfermera(s)</i></html>");
            JOptionPane.showMessageDialog(this, sb.toString(),
                "Área: " + area + " (" + resultado.size() + ")", JOptionPane.INFORMATION_MESSAGE);
        }
    }
   

    /**
 * Consulta disponibilidad sin registrar ni modificar turnos.
 */
    private void mostrarValidacionCobertura() {
        JComboBox<String> comboArea =
            EstilosGUI.crearComboBox(Utilidades.getAreasHospitalarias());

        JTextField campoFecha = EstilosGUI.crearCampoTexto(10);
        JTextField campoInicio = EstilosGUI.crearCampoTexto(5);
        JTextField campoFin = EstilosGUI.crearCampoTexto(5);
        JTextField campoCantidad = EstilosGUI.crearCampoTexto(4);

        campoInicio.setText("07:00");
        campoFin.setText("15:00");
        campoCantidad.setText("1");

        JPanel formulario = new JPanel(new GridLayout(5, 2, 8, 8));
        formulario.setBackground(EstilosGUI.COLOR_PANEL);

        formulario.add(EstilosGUI.crearLabel("Área:"));
        formulario.add(comboArea);
        formulario.add(EstilosGUI.crearLabel("Fecha (dd/MM/yyyy):"));
        formulario.add(campoFecha);
        formulario.add(EstilosGUI.crearLabel("Inicio (HH:mm):"));
        formulario.add(campoInicio);
        formulario.add(EstilosGUI.crearLabel("Fin (HH:mm):"));
        formulario.add(campoFin);
        formulario.add(EstilosGUI.crearLabel("Enfermeras necesarias:"));
        formulario.add(campoCantidad);

        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.setBackground(EstilosGUI.COLOR_PANEL);
        contenido.add(formulario, BorderLayout.CENTER);
        contenido.add(
            EstilosGUI.crearLabel(
                "<html>Si el fin es anterior al inicio, "
                + "el turno termina al día siguiente.</html>"
            ),
            BorderLayout.SOUTH
        );

        int respuesta = JOptionPane.showConfirmDialog(
            this,
            contenido,
            "Disponibilidad para nueva asignación",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int cantidad = Integer.parseInt(campoCantidad.getText().trim());

            boolean factible = TurnoControlador.validarFactibilidadCobertura(
                (String) comboArea.getSelectedItem(),
                campoFecha.getText().trim(),
                campoInicio.getText().trim(),
                campoFin.getText().trim(),
                cantidad
            );

            String mensaje;

            if (factible) {
                mensaje = "Hay suficientes enfermeras disponibles en el área.";
            } else {
                mensaje = "No hay suficientes enfermeras disponibles en el área.";
            }

            JOptionPane.showMessageDialog(
                this,
                mensaje + "\nNo se asignaron turnos.",
                "Resultado de disponibilidad",
                factible
                    ? JOptionPane.INFORMATION_MESSAGE
                    : JOptionPane.WARNING_MESSAGE
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "La cantidad debe ser un número entero.",
                "Datos inválidos",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (TurnoConflictoException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Datos inválidos",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void mostrarAsignacionGrupal() {
        JComboBox<String> campoArea = EstilosGUI.crearComboBox(Utilidades.getAreasHospitalarias());
        JComboBox<String> campoTipo =EstilosGUI.crearComboBox(Utilidades.getTiposTurno());
        JTextField campoFecha = EstilosGUI.crearCampoTexto(10);
        JTextField campoObs = EstilosGUI.crearCampoTexto(20);

        JPanel formulario = new JPanel(new GridLayout(4, 2, 8, 8));
        formulario.add(new JLabel("Área:"));
        formulario.add(campoArea);
        formulario.add(new JLabel("Fecha (dd/MM/yyyy):"));
        formulario.add(campoFecha);
        formulario.add(new JLabel("Tipo de turno:"));
        formulario.add(campoTipo);
        formulario.add(new JLabel("Observación:"));
        formulario.add(campoObs);

        int respuesta = JOptionPane.showConfirmDialog(this, formulario, "Asignación grupal por área",JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (respuesta != JOptionPane.OK_OPTION) return;

        String fecha = campoFecha.getText().trim();
        if (!Utilidades.validarFecha(fecha)) {
            JOptionPane.showMessageDialog( this, "Fecha inválida. Use dd/MM/yyyy.","Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String area = (String) campoArea.getSelectedItem();
        List<Enfermera> enfermeras = EnfermeraControlador.listarPorArea(area);

        if (enfermeras.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "No hay enfermeras en el área seleccionada.","Sin datos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String tipo = (String) campoTipo.getSelectedItem();
        String horaInicio = Utilidades.horaInicioPorTipo(tipo);
        String horaFin = Utilidades.horaFinPorTipo(tipo);

        int asignadas = 0;
        int conflictos = 0;
        StringBuilder detalle = new StringBuilder();

        for (Enfermera enfermera : enfermeras) {
            try {
                TurnoRegular turno = new TurnoRegular(
                    Utilidades.generarIdTurno(), fecha,
                    horaInicio, horaFin, tipo, campoObs.getText().trim());
                TurnoControlador.registrar(enfermera, turno);
                asignadas++;
            } catch (TurnoConflictoException ex) {
                conflictos++;
                detalle.append(enfermera.getNombreCompleto())
                       .append(": ").append(ex.getMessage()).append("\n");
            }
        }

        JTextArea resultado = new JTextArea("Turnos asignados: " + asignadas + "\nEnfermeras con conflicto: " + conflictos + "\n\n" + detalle.toString(), 10, 45);
        resultado.setEditable(false);
        resultado.setLineWrap(true);
        resultado.setWrapStyleWord(true);
        resultado.setCaretPosition(0);

        JOptionPane.showMessageDialog(
            this, new JScrollPane(resultado), "Resultado de asignación grupal",
            conflictos > 0
                ? JOptionPane.WARNING_MESSAGE
                : JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================================
    //  CIERRE DEL SISTEMA
    // =====================================================================

    /* Cierra el sistema solamente si el guardado fue exitoso.*/
    private void cerrarSistema() {
        int confirm = JOptionPane.showConfirmDialog(this,"¿Desea guardar los datos y cerrar el sistema?","Cerrar Sistema",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (alCerrar == null || !alCerrar.getAsBoolean()) {
                JOptionPane.showMessageDialog(
                    this,"No se completó el guardado.\n" + "El sistema seguirá abierto. Puede reintentar el cierre.", "Error al guardar",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,"No se pudo guardar: " + ex.getMessage() + "\nEl sistema seguirá abierto.", "Error al guardar", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dispose();
        System.exit(0);
    }
}
