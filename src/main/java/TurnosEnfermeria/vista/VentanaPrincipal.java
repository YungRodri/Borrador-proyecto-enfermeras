package TurnosEnfermeria.vista;

import TurnosEnfermeria.controlador.EnfermeraControlador;
import TurnosEnfermeria.modelo.Enfermera;
import TurnosEnfermeria.modelo.Utilidades;

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
    private final Runnable alCerrar;

    /**
     * Constructor principal.
     * @param alCerrar accion a ejecutar al cerrar la ventana (guardar datos CSV)
     */
    public VentanaPrincipal(Runnable alCerrar) {
        this.alCerrar = alCerrar;
        setTitle("Sistema de Gestión de Turnos de Enfermeras – Hospital Central");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(780, 580);
        setMinimumSize(new Dimension(680, 500));
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
        add(crearPanelPie(),        BorderLayout.SOUTH);
    }

    /** Panel superior con logo, nombre del sistema y estadisticas rapidas. */
    private JPanel crearPanelEncabezado() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBackground(EstilosGUI.COLOR_PANEL);
        panel.setBorder(new EmptyBorder(20, 32, 20, 32));

        // Columna izquierda: icono + titulo
        JPanel izq = new JPanel(new BorderLayout(10, 4));
        izq.setOpaque(false);

        JLabel icono = new JLabel("🏥");
        icono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        icono.setVerticalAlignment(SwingConstants.CENTER);

        JPanel textoTitulo = new JPanel(new GridLayout(2, 1, 0, 2));
        textoTitulo.setOpaque(false);

        JLabel titulo = new JLabel("Sistema de Turnos de Enfermería");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(EstilosGUI.COLOR_ACENTO);

        JLabel subtitulo = EstilosGUI.crearLabel("Hospital Central  ·  v1.0");
        subtitulo.setFont(EstilosGUI.FUENTE_NORMAL);

        textoTitulo.add(titulo);
        textoTitulo.add(subtitulo);

        izq.add(icono,       BorderLayout.WEST);
        izq.add(textoTitulo, BorderLayout.CENTER);

        // Columna derecha: resumen de enfermeras registradas
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        der.setOpaque(false);

        int total = EnfermeraControlador.totalRegistradas();
        der.add(crearIndicador("Enfermeras\nRegistradas", String.valueOf(total), EstilosGUI.COLOR_EXITO));

        panel.add(izq, BorderLayout.WEST);
        panel.add(der, BorderLayout.EAST);
        return panel;
    }

    /** Crea un indicador numerico para el encabezado. */
    private JPanel crearIndicador(String etiqueta, String valor, Color color) {
        JPanel ind = new JPanel(new BorderLayout(0, 4));
        ind.setBackground(EstilosGUI.COLOR_TARJETA);
        ind.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(color, 1),
            new EmptyBorder(8, 18, 8, 18)
        ));
        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(color);
        JLabel lblEtiqueta = new JLabel("<html><center>" + etiqueta.replace("\n", "<br>") + "</center></html>", SwingConstants.CENTER);
        lblEtiqueta.setFont(EstilosGUI.FUENTE_PEQUENA);
        lblEtiqueta.setForeground(EstilosGUI.COLOR_TEXTO_SEC);
        ind.add(lblValor, BorderLayout.CENTER);
        ind.add(lblEtiqueta, BorderLayout.SOUTH);
        return ind;
    }

    /** Panel central con los botones del menu principal agrupados en tarjetas. */
    private JPanel crearPanelMenuCentral() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(EstilosGUI.COLOR_FONDO);
        wrapper.setBorder(new EmptyBorder(24, 48, 24, 48));

        JPanel grid = new JPanel(new GridLayout(2, 3, 18, 18));
        grid.setBackground(EstilosGUI.COLOR_FONDO);

        grid.add(crearTarjetaMenu("👩‍⚕️", "Gestión de\nEnfermeras",
            "Agregar, editar, eliminar y\nbuscar enfermeras registradas.",
            EstilosGUI.COLOR_ACENTO,
            e -> new VentanaEnfermeras().setVisible(true)));

        grid.add(crearTarjetaMenu("📋", "Gestión de\nTurnos",
            "Administrar turnos de una\nenfermera seleccionada.",
            EstilosGUI.COLOR_ACENTO2,
            e -> abrirGestionTurnos()));

        grid.add(crearTarjetaMenu("📊", "Estadísticas\ny Reportes",
            "Gráficos de horas y\ndistribución de eventos.",
            EstilosGUI.COLOR_EXITO,
            e -> new VentanaEstadisticas().setVisible(true)));

        grid.add(crearTarjetaMenu("🌙", "Filtrar Turnos\nNocturnos",
            "Ver enfermeras con exceso\nde turnos noche en el mes.",
            EstilosGUI.COLOR_ADVERTENCIA,
            e -> mostrarFiltroNocturnos()));

        grid.add(crearTarjetaMenu("🏥", "Filtrar por\nÁrea",
            "Listar enfermeras según\nel área hospitalaria.",
            new Color(236, 72, 153),
            e -> mostrarFiltroPorArea()));

        grid.add(crearTarjetaMenu("🚪", "Cerrar\nSistema",
            "Guardar datos y salir\ndel sistema de forma segura.",
            EstilosGUI.COLOR_ERROR,
            e -> cerrarSistema()));

        wrapper.add(grid, new GridBagConstraints());
        return wrapper;
    }

    /**
     * Crea una tarjeta de menu con icono, titulo, descripcion y accion.
     */
    private JPanel crearTarjetaMenu(String icono, String titulo, String descripcion,
                                    Color colorAcento,
                                    java.awt.event.ActionListener accion) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setBackground(EstilosGUI.COLOR_TARJETA);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(
                colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 60), 1),
            new EmptyBorder(18, 16, 14, 16)
        ));
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);

        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JLabel lblTitulo = new JLabel("<html>" + titulo.replace("\n", "<br>") + "</html>");
        lblTitulo.setFont(EstilosGUI.FUENTE_SUBTITULO);
        lblTitulo.setForeground(colorAcento);

        top.add(lblIcono);
        top.add(lblTitulo);

        JLabel lblDesc = new JLabel("<html><small>" + descripcion.replace("\n", "<br>") + "</small></html>");
        lblDesc.setFont(EstilosGUI.FUENTE_PEQUENA);
        lblDesc.setForeground(EstilosGUI.COLOR_TEXTO_SEC);
        lblDesc.setBorder(new EmptyBorder(0, 4, 0, 0));

        // Boton de accion
        JButton btn = new JButton("Abrir →");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(colorAcento);
        btn.setBackground(EstilosGUI.COLOR_FONDO);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(colorAcento, 1),
            new EmptyBorder(5, 12, 5, 12)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addActionListener(accion);

        // Hover de la tarjeta
        tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                tarjeta.setBackground(EstilosGUI.COLOR_PANEL);
                tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    new javax.swing.border.LineBorder(colorAcento, 1),
                    new EmptyBorder(18, 16, 14, 16)
                ));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                tarjeta.setBackground(EstilosGUI.COLOR_TARJETA);
                tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    new javax.swing.border.LineBorder(new Color(
                        colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 60), 1),
                    new EmptyBorder(18, 16, 14, 16)
                ));
            }
        });

        JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botPanel.setOpaque(false);
        botPanel.add(btn);

        tarjeta.add(top,      BorderLayout.NORTH);
        tarjeta.add(lblDesc,  BorderLayout.CENTER);
        tarjeta.add(botPanel, BorderLayout.SOUTH);
        return tarjeta;
    }

    /** Panel inferior con informacion de version y botones de acceso rapido. */
    private JPanel crearPanelPie() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EstilosGUI.COLOR_PANEL);
        panel.setBorder(new EmptyBorder(8, 24, 8, 24));

        JLabel info = new JLabel("Sistema de Gestión de Turnos  ·  Programación Avanzada 2026  ·  Datos guardados automáticamente al cerrar");
        info.setFont(EstilosGUI.FUENTE_PEQUENA);
        info.setForeground(EstilosGUI.COLOR_TEXTO_SEC);

        panel.add(info, BorderLayout.WEST);
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

    /** Muestra un dialogo de filtro de turnos nocturnos por mes y anio. */
    private void mostrarFiltroNocturnos() {
        JTextField campoMes  = EstilosGUI.crearCampoTexto(4);
        JTextField campoAnio = EstilosGUI.crearCampoTexto(6);
        JTextField campoLim  = EstilosGUI.crearCampoTexto(4);

        campoMes.setText("09");
        campoAnio.setText("2026");
        campoLim.setText("3");

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBackground(EstilosGUI.COLOR_PANEL);
        form.add(EstilosGUI.crearLabel("Mes (MM):")); form.add(campoMes);
        form.add(EstilosGUI.crearLabel("Año (yyyy):")); form.add(campoAnio);
        form.add(EstilosGUI.crearLabel("Límite de turnos noche:")); form.add(campoLim);

        int res = JOptionPane.showConfirmDialog(this, form,
            "Filtrar Exceso de Turnos Nocturnos",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            int limite = Integer.parseInt(campoLim.getText().trim());
            String mes = campoMes.getText().trim();
            String anio= campoAnio.getText().trim();
            List<Enfermera> resultado = EnfermeraControlador.filtrarExcesoTurnosNoche(limite, mes, anio);

            if (resultado.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Ninguna enfermera supera el límite de " + limite + " turnos noche en " + mes + "/" + anio,
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder sb = new StringBuilder("<html><b>Enfermeras con más de " + limite + " turnos noche en " + mes + "/" + anio + ":</b><br><br>");
                for (Enfermera e : resultado) {
                    int n = e.contarTurnosNocheMes(mes, anio);
                    sb.append("• ").append(e.getNombreCompleto())
                      .append(" [").append(e.getRut()).append("]")
                      .append(" → ").append(n).append(" turnos noche<br>");
                }
                sb.append("</html>");
                JOptionPane.showMessageDialog(this, sb.toString(),
                    "Resultado del Filtro (" + resultado.size() + " enfermeras)",
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El límite debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Muestra un dialogo de filtro de enfermeras por area hospitalaria. */
    private void mostrarFiltroPorArea() {
        JComboBox<String> comboArea = EstilosGUI.crearComboBox(Utilidades.AREAS_HOSPITALARIAS);
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

    // =====================================================================
    //  CIERRE DEL SISTEMA
    // =====================================================================

    /** Confirma el cierre, ejecuta el callback de guardado y cierra la ventana. */
    private void cerrarSistema() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Desea cerrar el sistema?\nLos datos se guardarán automáticamente.",
            "Cerrar Sistema", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (alCerrar != null) alCerrar.run();
            dispose();
            System.exit(0);
        }
    }
}
