package TurnosEnfermeria.vista;

import TurnosEnfermeria.controlador.EnfermeraControlador;
import TurnosEnfermeria.modelo.Enfermera;
import TurnosEnfermeria.modelo.Turno;
import TurnosEnfermeria.modelo.TurnoRegular;
import TurnosEnfermeria.modelo.Utilidades;
import TurnosEnfermeria.controlador.TurnoControlador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ventana de estadisticas y reportes visuales del sistema (SIA-O1).
 * Incluye:
 *   - Grafico de barras: Horas trabajadas por enfermera
 *   - Grafico de torta: Distribucion de tipos de turno
 *   - Panel de resumen de KPIs del hospital
 *
 * Todos los graficos son dibujados manualmente con Java2D (sin librerias externas).
 */
public class VentanaEstadisticas extends JFrame {

    public VentanaEstadisticas() {
        setTitle("Estadísticas y Reportes – Sistema de Turnos Hospital");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 680);
        setMinimumSize(new Dimension(800, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(EstilosGUI.getColorFondo());
        setLayout(new BorderLayout(0, 0));

        construirUI();
    }

    // =====================================================================
    //  CONSTRUCCION DE UI
    // =====================================================================

    private void construirUI() {
        add(crearPanelEncabezado(), BorderLayout.NORTH);

        // Panel central con graficos lado a lado
        JPanel panelGraficos = new JPanel(new GridLayout(1, 2, 16, 0));
        panelGraficos.setBackground(EstilosGUI.getColorFondo());
        panelGraficos.setBorder(new EmptyBorder(12, 16, 12, 16));
        panelGraficos.add(new PanelGraficoBarras());
        panelGraficos.add(new PanelGraficoTorta());

        // Panel inferior con KPIs
        add(panelGraficos,   BorderLayout.CENTER);
        add(crearPanelKPIs(), BorderLayout.SOUTH);
    }

    /** Panel de encabezado de la ventana. */
    private JPanel crearPanelEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EstilosGUI.getColorPanel());
        panel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel titulo = EstilosGUI.crearLabelTitulo("📊  Estadísticas del Sistema");
        JLabel subtitulo = EstilosGUI.crearLabel("Total de enfermeras registradas: " + EnfermeraControlador.totalRegistradas());
        subtitulo.setFont(EstilosGUI.FUENTE_NORMAL);

        JButton btnActualizar = EstilosGUI.crearBotonSecundario("🔄 Actualizar");
        btnActualizar.addActionListener(e -> {
            getContentPane().removeAll();
            construirUI();
            revalidate();
            repaint();
        });

        panel.add(titulo,        BorderLayout.WEST);
        panel.add(subtitulo,     BorderLayout.CENTER);
        panel.add(btnActualizar, BorderLayout.EAST);
        return panel;
    }

    /** Panel de KPIs (indicadores clave) del hospital. */
    private JPanel crearPanelKPIs() {
        List<Enfermera> todas = EnfermeraControlador.listar();

        int totalTurnos    = 0;
        int totalLicencias = 0;
        int totalCambios   = 0;
        double totalHoras  = 0;
        int turnosNoche    = 0;

        for (Enfermera e : todas) {
            totalTurnos    += e.contarTurnosRegulares();
            totalLicencias += e.contarLicencias();
            totalCambios   += e.contarCambios();
            totalHoras     += TurnoControlador.calcularHorasTrabajadas(e);
            for (Turno t : e.getListaTurnos()) {
                if (t instanceof TurnoRegular) {
                    TurnoRegular tr = (TurnoRegular) t;
                    if (Utilidades.getTurnoNoche().equals(tr.getTipoTurno())) turnosNoche++;
                }
            }
        }

        JPanel panel = new JPanel(new GridLayout(1, 5, 10, 0));
        panel.setBackground(EstilosGUI.getColorFondo());
        panel.setBorder(new EmptyBorder(0, 16, 16, 16));

        panel.add(crearKPI("👩‍⚕️  Enfermeras", String.valueOf(todas.size()), EstilosGUI.getColorAcento()));
        panel.add(crearKPI("📋  Turnos Regulares", String.valueOf(totalTurnos), EstilosGUI.getColorExito()));
        panel.add(crearKPI("🌙  Turnos Noche", String.valueOf(turnosNoche), EstilosGUI.getColorAcento2()));
        panel.add(crearKPI("📄  Licencias",String.valueOf(totalLicencias), EstilosGUI.getColorAdvertencia()));
        panel.add(crearKPI("⏱  Total Horas", String.format("%.1f h", totalHoras), EstilosGUI.getColorError()));
        return panel;
    }

    /** Crea una tarjeta de KPI con icono, valor y etiqueta. */
    private JPanel crearKPI(String etiqueta, String valor, Color color) {
        JPanel kpi = new JPanel(new BorderLayout(4, 4));
        kpi.setBackground(EstilosGUI.getColorTarjeta());
        kpi.setBorder(BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(color, 1), new EmptyBorder(10, 16, 10, 16)));

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValor.setForeground(color);

        JLabel lblEtiqueta = new JLabel("<html><center>" + etiqueta + "</center></html>", SwingConstants.CENTER);
        lblEtiqueta.setFont(EstilosGUI.FUENTE_PEQUENA);
        lblEtiqueta.setForeground(EstilosGUI.getColorTextoSecundario());

        kpi.add(lblValor,    BorderLayout.CENTER);
        kpi.add(lblEtiqueta, BorderLayout.SOUTH);
        return kpi;
    }

    // =====================================================================
    //  GRAFICO DE BARRAS: HORAS TRABAJADAS POR ENFERMERA
    // =====================================================================

    /**
     * Panel personalizado que dibuja un grafico de barras verticales
     * mostrando las horas trabajadas por cada enfermera registrada.
     * Pintado con Java2D puro (sin librerias externas).
     */
    private static class PanelGraficoBarras extends JPanel {

        public PanelGraficoBarras() {
            setBackground(EstilosGUI.getColorTarjeta());
            setBorder(BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(EstilosGUI.getColorBorde(), 1),
                new EmptyBorder(16, 16, 16, 16)
            ));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            List<Enfermera> todas = EnfermeraControlador.listar();

            int w = getWidth();
            int h = getHeight();
            int margenIzq  = 50;
            int margenDer  = 20;
            int margenSup  = 48;
            int margenInf  = 68;
            int anchoGraf  = w - margenIzq - margenDer;
            int altoGraf   = h - margenSup - margenInf;

            // --- Titulo ---
            g2.setFont(EstilosGUI.FUENTE_SUBTITULO);
            g2.setColor(EstilosGUI.getColorAcento());
            g2.drawString("Horas Trabajadas por Enfermera", margenIzq, 28);

            if (todas.isEmpty()) {
                g2.setFont(EstilosGUI.FUENTE_NORMAL);
                g2.setColor(EstilosGUI.getColorTextoSecundario());
                g2.drawString("Sin datos para mostrar", w / 2 - 70, h / 2);
                return;
            }

            // Calcular maximo para escala
            double maxHoras = 1;
            for (Enfermera e : todas) {
                if (TurnoControlador.calcularHorasTrabajadas(e) > maxHoras) maxHoras = TurnoControlador.calcularHorasTrabajadas(e);
            }

            // Dibujar lineas de guia horizontales
            g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{4f, 4f}, 0f));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            int guias = 5;
            for (int i = 0; i <= guias; i++) {
                double valorGuia = (maxHoras / guias) * i;
                int y = margenSup + altoGraf - (int)(altoGraf * i / guias);
                g2.setColor(EstilosGUI.getColorBorde());
                g2.drawLine(margenIzq, y, margenIzq + anchoGraf, y);
                g2.setColor(EstilosGUI.getColorTextoSecundario());
                g2.drawString(String.format("%.0f", valorGuia), 2, y + 4);
            }
            g2.setStroke(new BasicStroke(1f));

            // Dibujar eje Y
            g2.setColor(EstilosGUI.getColorBorde());
            g2.drawLine(margenIzq, margenSup, margenIzq, margenSup + altoGraf);
            g2.drawLine(margenIzq, margenSup + altoGraf, margenIzq + anchoGraf, margenSup + altoGraf);

            int n = todas.size();
            int separacion = 8;
            int anchoBarra = Math.max(12, (anchoGraf - separacion * (n + 1)) / Math.max(n, 1));
            anchoBarra = Math.min(anchoBarra, 60);

            for (int i = 0; i < n; i++) {
                Enfermera e = todas.get(i);
                double horas = TurnoControlador.calcularHorasTrabajadas(e);
                int altoBarra = (int)(altoGraf * horas / maxHoras);

                int x = margenIzq + separacion + i * (anchoBarra + separacion);
                int y = margenSup + altoGraf - altoBarra;

                // Barras negras sin efectos decorativos.
                g2.setColor(Color.BLACK);
                g2.fillRect(x, y, anchoBarra, altoBarra);

                // Valor encima de la barra
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(Color.BLACK);
                String valorStr = String.format("%.1f h", horas);
                FontMetrics fm = g2.getFontMetrics();
                int xVal = x + (anchoBarra - fm.stringWidth(valorStr)) / 2;
                if (altoBarra > 16) g2.drawString(valorStr, xVal, y - 4);

                // Nombre debajo del eje (apellido para ahorrar espacio)
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2.setColor(EstilosGUI.getColorTextoSecundario());
                String nombreCorto = e.getApellidoP().length() > 8 ? e.getApellidoP().substring(0, 7) + "." : e.getApellidoP();
                int xNom = x + (anchoBarra - fm.stringWidth(nombreCorto)) / 2;

                // Rotar texto del nombre 45 grados
                g2.translate(x + anchoBarra / 2, margenSup + altoGraf + 12);
                g2.rotate(Math.toRadians(35));
                g2.drawString(nombreCorto, 0, 0);
                g2.rotate(-Math.toRadians(35));
                g2.translate(-(x + anchoBarra / 2), -(margenSup + altoGraf + 12));
            }
        }
    }

    // =====================================================================
    //  GRAFICO DE TORTA: DISTRIBUCION DE TIPOS DE TURNO
    // =====================================================================

    /**
     * Panel personalizado que dibuja un grafico de torta
     * mostrando la distribucion de Turnos Regulares, Licencias y Cambios de turno.
     * Pintado con Java2D puro (sin librerias externas).
     */
    private static class PanelGraficoTorta extends JPanel {

        public PanelGraficoTorta() {
            setBackground(EstilosGUI.getColorTarjeta());
            setBorder(BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(EstilosGUI.getColorBorde(), 1),new EmptyBorder(16, 16, 16, 16)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // --- Titulo ---
            g2.setFont(EstilosGUI.FUENTE_SUBTITULO);
            g2.setColor(EstilosGUI.getColorAcento());
            g2.drawString("Distribución de Tipos de Evento", 16, 28);

            // Recopilar datos de TODAS las enfermeras
            List<Enfermera> todas = EnfermeraControlador.listar();
            int regulares = 0, licencias = 0, cambios = 0;
            for (Enfermera e : todas) {
                regulares += e.contarTurnosRegulares();
                licencias += e.contarLicencias();
                cambios += e.contarCambios();
            }
            int total = regulares + licencias + cambios;

            if (total == 0) {
                g2.setFont(EstilosGUI.FUENTE_NORMAL);
                g2.setColor(EstilosGUI.getColorTextoSecundario());
                g2.drawString("Sin datos para mostrar", w / 2 - 70, h / 2);
                return;
            }

            // Datos para el grafico de torta
            LinkedHashMap<String, Integer> datos = new LinkedHashMap<>();
            if (regulares > 0) datos.put("Turnos Regulares", regulares);
            if (licencias > 0) datos.put("Licencias", licencias);
            if (cambios > 0) datos.put("Cambios de Turno", cambios);

            Color[] colores = {
                Color.BLACK, new Color(110, 110, 110), new Color(190, 190, 190)
            };

            // Dimensiones de la torta
            int diametro = Math.min(w - 60, h - 130);
            diametro = Math.min(diametro, 220);
            int xCentro = w / 2 - diametro / 2;
            int yCentro = 44;

            double angulo = -90; // Empezar desde arriba
            int idx = 0;

            for (Map.Entry<String, Integer> entry : datos.entrySet()) {
                double porcion = (double) entry.getValue() / total * 360;
                Color color = colores[idx % colores.length];

                // Porcion de torta
                g2.setColor(color);
                g2.fill(new Arc2D.Double(xCentro, yCentro, diametro, diametro, angulo, porcion, Arc2D.PIE));

                // Borde de separacion
                g2.setColor(EstilosGUI.getColorTarjeta());
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new Arc2D.Double(xCentro, yCentro, diametro, diametro, angulo, porcion, Arc2D.PIE));
                g2.setStroke(new BasicStroke(1f));

                angulo += porcion;
                idx++;
            }

            // Circulo interior para efecto "donut" moderno
            g2.setColor(EstilosGUI.getColorTarjeta());
            int innerD = (int)(diametro * 0.45);
            g2.fillOval(xCentro + (diametro - innerD) / 2, yCentro + (diametro - innerD) / 2, innerD, innerD);

            // Texto central
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.setColor(EstilosGUI.getColorTexto());
            String textoTotal = String.valueOf(total);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(textoTotal, xCentro + diametro / 2 - fm.stringWidth(textoTotal) / 2, yCentro + diametro / 2 + 5);
            g2.setFont(EstilosGUI.FUENTE_PEQUENA);
            g2.setColor(EstilosGUI.getColorTextoSecundario());
            g2.drawString("eventos", xCentro + diametro / 2 - g2.getFontMetrics().stringWidth("eventos") / 2, yCentro + diametro / 2 + 18);

            // Leyenda
            int yLeyenda = yCentro + diametro + 16;
            int xLeyenda = 16;
            idx = 0;
            g2.setFont(EstilosGUI.FUENTE_NORMAL);
            for (Map.Entry<String, Integer> entry : datos.entrySet()) {
                Color color = colores[idx % colores.length];
                double pct = (double) entry.getValue() / total * 100;

                // Cuadro de color
                g2.setColor(color);
                g2.fillRoundRect(xLeyenda, yLeyenda + idx * 24 - 10, 12, 12, 3, 3);

                // Texto de leyenda
                g2.setColor(EstilosGUI.getColorTexto());
                g2.drawString(String.format("%s: %d (%.1f%%)", entry.getKey(), entry.getValue(), pct),
                    xLeyenda + 18, yLeyenda + idx * 24);
                idx++;
            }
        }
    }
}
