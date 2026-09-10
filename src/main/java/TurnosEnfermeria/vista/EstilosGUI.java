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

    public static final Color COLOR_FONDO       = Color.WHITE;
    public static final Color COLOR_PANEL       = Color.WHITE;
    public static final Color COLOR_TARJETA     = Color.WHITE;

    public static final Color COLOR_ACENTO      = Color.BLACK;
    public static final Color COLOR_ACENTO2     = Color.BLACK;
    public static final Color COLOR_TEXTO       = Color.BLACK;
    public static final Color COLOR_TEXTO_SEC   = Color.BLACK;

    public static final Color COLOR_EXITO       = Color.BLACK;
    public static final Color COLOR_ERROR       = Color.BLACK;
    public static final Color COLOR_ADVERTENCIA = Color.BLACK;
    public static final Color COLOR_BORDE       = Color.BLACK;

    public static final Color COLOR_FILA_ALT    = Color.WHITE;
    public static final Color COLOR_SELECCION   = Color.BLACK;

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
        UIManager.put("Panel.background", COLOR_FONDO);
        UIManager.put("OptionPane.background", COLOR_PANEL);
        UIManager.put("OptionPane.messageForeground", COLOR_TEXTO);
        UIManager.put("Button.background", COLOR_ACENTO);
        UIManager.put("Button.foreground", COLOR_FONDO);
        UIManager.put("TextField.background", COLOR_TARJETA);
        UIManager.put("TextField.foreground", COLOR_TEXTO);
        UIManager.put("TextField.caretForeground", COLOR_ACENTO);
        UIManager.put("ComboBox.background", COLOR_TARJETA);
        UIManager.put("ComboBox.foreground", COLOR_TEXTO);
    }

    /**
     * Crea un JButton con el estilo principal (acento azul cian).
     */
    public static JButton crearBotonPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FUENTE_BOTON);
        btn.setBackground(COLOR_ACENTO);
        btn.setForeground(COLOR_FONDO);
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
        btn.setBackground(COLOR_TARJETA);
        btn.setForeground(COLOR_ACENTO);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(COLOR_ACENTO, 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_ACENTO, 1),
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
        btn.setBackground(COLOR_ERROR);
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
        campo.setBackground(COLOR_TARJETA);
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_ACENTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDE, 1),
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
        combo.setBackground(COLOR_TARJETA);
        combo.setForeground(COLOR_TEXTO);
        combo.setBorder(new LineBorder(COLOR_BORDE, 1));
        return combo;
    }

    /**
     * Crea un JLabel de titulo con estilo de encabezado.
     */
    public static JLabel crearLabelTitulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FUENTE_TITULO);
        lbl.setForeground(COLOR_ACENTO);
        lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    /**
     * Crea un JLabel normal de formulario.
     */
    public static JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FUENTE_NORMAL);
        lbl.setForeground(COLOR_TEXTO_SEC);
        return lbl;
    }

    /**
     * Aplica estilos a un JTable con tema oscuro.
     */
    public static void estilizarTabla(JTable tabla) {
        tabla.setFont(FUENTE_TABLA);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setBackground(COLOR_PANEL);
        tabla.setSelectionBackground(COLOR_SELECCION);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setGridColor(COLOR_BORDE);
        tabla.setRowHeight(28);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setFillsViewportHeight(true);
        tabla.setOpaque(true);

        // Estilizar encabezado
        JTableHeader header = tabla.getTableHeader();
        header.setFont(FUENTE_ENCABEZADO);
        header.setBackground(COLOR_TARJETA);
        header.setForeground(COLOR_ACENTO);
        header.setBorder(new LineBorder(COLOR_BORDE, 1));
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
                    setBackground(COLOR_SELECCION);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? COLOR_PANEL : COLOR_FILA_ALT);
                    setForeground(COLOR_TEXTO);
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
        panel.setBackground(COLOR_TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDE, 1),
            new EmptyBorder(16, 20, 16, 20)
        ));
        return panel;
    }

    /**
     * Configura un JScrollPane con estilo oscuro.
     */
    public static JScrollPane crearScrollPane(Component comp) {
        JScrollPane scroll = new JScrollPane(comp);
        scroll.setBackground(COLOR_PANEL);
        scroll.getViewport().setBackground(COLOR_PANEL);
        scroll.setBorder(new LineBorder(COLOR_BORDE, 1));
        scroll.getVerticalScrollBar().setBackground(COLOR_PANEL);
        return scroll;
    }

    /**
     * Crea un borde de titulo con el color del acento.
     */
    public static Border crearBordeTitulo(String titulo) {
        return BorderFactory.createTitledBorder(
            new LineBorder(COLOR_BORDE, 1),
            titulo,
            0, 0,
            FUENTE_SUBTITULO,
            COLOR_ACENTO
        );
    }
}
