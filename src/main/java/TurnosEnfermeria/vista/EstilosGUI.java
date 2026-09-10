package TurnosEnfermeria.vista;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Clase utilitaria de estilos y constantes visuales para toda la GUI.
 * Centraliza colores, fuentes y metodos de fabrica de componentes estandarizados.
 * Usado por todas las vistas (VentanaEnfermeras, VentanaTurnos, VentanaPrincipal, etc.)
 */
public final class EstilosGUI {

    // =====================================================================
    //  PALETA DE COLORES DEL SISTEMA
    // =====================================================================

    public static Color getColorFondo() {
        return Color.WHITE;
    }

    public static Color getColorPanel() {
        return Color.WHITE;
    }

    public static Color getColorTarjeta() {
        return Color.WHITE;
    }

    public static Color getColorAcento() {
        return Color.BLACK;
    }

    public static Color getColorAcento2() {
        return Color.BLACK;
    }

    public static Color getColorTexto() {
        return Color.BLACK;
    }

    public static Color getColorTextoSecundario() {
        return Color.BLACK;
    }

    public static Color getColorExito() {
        return Color.BLACK;
    }

    public static Color getColorError() {
        return Color.BLACK;
    }

    public static Color getColorAdvertencia() {
        return Color.BLACK;
    }

    public static Color getColorBorde() {
        return Color.BLACK;
    }

    public static Color getColorFilaAlternada() {
        return Color.WHITE;
    }

    public static Color getColorSeleccion() {
        return Color.BLACK;
    }

    // =====================================================================
    //  FUENTES
    // =====================================================================

    public static final Font FUENTE_TITULO    = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_NORMAL    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_PEQUENA   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FUENTE_BOTON     = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FUENTE_TABLA     = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FUENTE_ENCABEZADO= new Font("Segoe UI", Font.BOLD, 12);

    // Constructor privado - solo metodos estaticos
    private EstilosGUI() {}

    // =====================================================================
    //  METODOS DE FABRICA DE COMPONENTES
    // =====================================================================

    /**
     * Configura el Look and Feel del sistema al estilo oscuro nativo.
     */
    public static void aplicarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        // Personalizar componentes globales de Swing
        UIManager.put("Panel.background", getColorFondo());
        UIManager.put("OptionPane.background", getColorPanel());
        UIManager.put("OptionPane.messageForeground", getColorTexto());
        UIManager.put("Button.background", Color.WHITE);
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("TextField.background", getColorTarjeta());
        UIManager.put("TextField.foreground", getColorTexto());
        UIManager.put("TextField.caretForeground", getColorAcento());
        UIManager.put("ComboBox.background", getColorTarjeta());
        UIManager.put("ComboBox.foreground", getColorTexto());
    }

    /**
     * Crea un JButton con el estilo principal (acento azul cian).
     */
    public static JButton crearBotonPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FUENTE_BOTON);
        btn.setBackground(getColorAcento());
        btn.setForeground(getColorFondo());
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setOpaque(true);
        return btn;
    }

    /**
     * Crea un JButton con el estilo secundario (contorno).
     */
    public static JButton crearBotonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FUENTE_BOTON);
        btn.setBackground(getColorTarjeta());
        btn.setForeground(getColorAcento());
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(getColorAcento(), 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(getColorAcento(), 1),
            new EmptyBorder(7, 16, 7, 16)
        ));
        btn.setOpaque(true);
        return btn;
    }

    /**
     * Crea un JButton con el estilo de peligro (rojo).
     */
    public static JButton crearBotonPeligro(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FUENTE_BOTON);
        btn.setBackground(getColorError());
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setOpaque(true);
        return btn;
    }

    /**
     * Crea un JTextField estilizado con el tema oscuro.
     */
    public static JTextField crearCampoTexto(int columnas) {
        JTextField campo = new JTextField(columnas);
        campo.setFont(FUENTE_NORMAL);
        campo.setBackground(getColorTarjeta());
        campo.setForeground(getColorTexto());
        campo.setCaretColor(getColorAcento());
        campo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(getColorBorde(), 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        campo.setOpaque(true);
        return campo;
    }

    /**
     * Crea un JComboBox estilizado.
     */
    public static JComboBox<String> crearComboBox(String[] opciones) {
        JComboBox<String> combo = new JComboBox<>(opciones);
        combo.setFont(FUENTE_NORMAL);
        combo.setBackground(getColorTarjeta());
        combo.setForeground(getColorTexto());
        combo.setBorder(new LineBorder(getColorBorde(), 1));
        return combo;
    }

    /**
     * Crea un JLabel de titulo con estilo de encabezado.
     */
    public static JLabel crearLabelTitulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FUENTE_TITULO);
        lbl.setForeground(getColorAcento());
        lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    /**
     * Crea un JLabel normal de formulario.
     */
    public static JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FUENTE_NORMAL);
        lbl.setForeground(getColorTextoSecundario());
        return lbl;
    }

    /**
     * Aplica estilos a un JTable con tema oscuro.
     */
    public static void estilizarTabla(JTable tabla) {
        tabla.setFont(FUENTE_TABLA);
        tabla.setForeground(getColorTexto());
        tabla.setBackground(getColorPanel());
        tabla.setSelectionBackground(getColorSeleccion());
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setGridColor(getColorBorde());
        tabla.setRowHeight(28);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setFillsViewportHeight(true);
        tabla.setOpaque(true);

        // Estilizar encabezado
        JTableHeader header = tabla.getTableHeader();
        header.setFont(FUENTE_ENCABEZADO);
        header.setBackground(getColorTarjeta());
        header.setForeground(getColorAcento());
        header.setBorder(new LineBorder(getColorBorde(), 1));
        header.setReorderingAllowed(false);

        // Renderer con filas alternadas
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setFont(FUENTE_TABLA);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (sel) {
                    setBackground(getColorSeleccion());
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? getColorPanel() : getColorFilaAlternada());
                    setForeground(getColorTexto());
                }
                setOpaque(true);
                return this;
            }
        });
    }

    /**
     * Crea un JPanel con fondo de tarjeta y borde redondeado simulado.
     */
    public static JPanel crearPanelTarjeta() {
        JPanel panel = new JPanel();
        panel.setBackground(getColorTarjeta());
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(getColorBorde(), 1),
            new EmptyBorder(16, 20, 16, 20)
        ));
        return panel;
    }

    /**
     * Configura un JScrollPane con estilo oscuro.
     */
    public static JScrollPane crearScrollPane(Component comp) {
        JScrollPane scroll = new JScrollPane(comp);
        scroll.setBackground(getColorPanel());
        scroll.getViewport().setBackground(getColorPanel());
        scroll.setBorder(new LineBorder(getColorBorde(), 1));
        scroll.getVerticalScrollBar().setBackground(getColorPanel());
        return scroll;
    }

    /**
     * Crea un borde de titulo con el color del acento.
     */
    public static Border crearBordeTitulo(String titulo) {
        return BorderFactory.createTitledBorder(new LineBorder(getColorBorde(), 1),titulo,0,0,FUENTE_SUBTITULO,getColorAcento());
    }
}
