package TurnosEnfermeria.vista.prototipo;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Componentes visuales compartidos por el bosquejo; no accede al modelo real. */
final class TemaPrototipo {
    // Colores tranquilos, pensados para trabajar durante horas sin fatiga visual.
    static final Color FONDO = new Color(247, 249, 248);
    static final Color TINTA = new Color(36, 51, 51);
    static final Color SUAVE = new Color(100, 117, 114);
    static final Color BORDE = new Color(220, 228, 224);
    static final Color AZUL = new Color(38, 67, 65);
    static final Color ACENTO = new Color(42, 108, 94);
    static final Color VERDE = new Color(42, 111, 73);
    static final Color AMBAR = new Color(142, 99, 37);
    static final Color ROJO = new Color(171, 76, 73);
    static final Color PALIDO = new Color(232, 243, 238);
    static final Color CREMA = new Color(251, 247, 238);

    private TemaPrototipo() { }

    static void configurar() {
        // El aspecto nativo evita elementos rígidos propios del tema Metal.
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { /* El tema disponible sigue funcionando. */ }
        Font normal = fuente(13, false);
        for (String key : new String[]{"Label.font", "Button.font", "ComboBox.font", "TextField.font",
                "TextArea.font", "Table.font", "TableHeader.font", "OptionPane.messageFont"}) {
            UIManager.put(key, normal);
        }
        UIManager.put("Panel.background", FONDO);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextArea.background", Color.WHITE);
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ScrollBar.width", 12);
    }

    static Font fuente(int size, boolean bold) {
        return new Font(Font.SANS_SERIF, bold ? Font.BOLD : Font.PLAIN, size);
    }

    static JLabel texto(String text, int size, boolean bold, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(fuente(size, bold));
        label.setForeground(color);
        return label;
    }

    static JPanel vertical() {
        JPanel panel = new JPanel() {
            @Override public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        return panel;
    }

    /** Panel de bordes suaves con relleno liso, sin degradados ni sombras artificiales. */
    static class PanelSuave extends JPanel {
        private final Color relleno;
        private final Color contorno;
        private final int radio;

        PanelSuave(Color relleno, Color contorno, int radio) {
            this.relleno = relleno;
            this.contorno = contorno;
            this.radio = radio;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(relleno);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
            if (contorno != null) {
                g.setColor(contorno);
                g.setStroke(new BasicStroke(1f));
                g.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, radio, radio);
            }
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    static JPanel tarjeta(String title, String subtitle) {
        JPanel panel = new PanelSuave(Color.WHITE, BORDE, 18) {
            @Override public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(21, 23, 21, 23));
        if (title != null) {
            agregar(panel, texto(title, 17, true, TINTA));
            if (subtitle != null) {
                panel.add(Box.createVerticalStrut(7));
                agregar(panel, parrafo(subtitle, SUAVE));
            }
            panel.add(Box.createVerticalStrut(18));
        }
        return panel;
    }

    static void agregar(JPanel panel, JComponent child) {
        child.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(child);
    }

    static JTextArea parrafo(String value, Color color) {
        JTextArea area = new JTextArea(value) {
            @Override public Dimension getPreferredSize() {
                int width = getWidth();
                if (width <= 0 && getParent() != null) {
                    Insets insets = getParent().getInsets();
                    width = getParent().getWidth() - insets.left - insets.right;
                }
                if (width <= 0) width = 600;
                FontMetrics metrics = getFontMetrics(getFont());
                int lines = 0;
                for (String paragraph : getText().split("\\n", -1)) {
                    int used = 0;
                    lines++;
                    for (String word : paragraph.split(" ")) {
                        int length = metrics.stringWidth(word + " ");
                        if (used > 0 && used + length > width) { lines++; used = 0; }
                        used += length;
                    }
                }
                return new Dimension(width, lines * metrics.getHeight() + 3);
            }
            @Override public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
            @Override public Dimension getMinimumSize() {
                return new Dimension(40, getPreferredSize().height);
            }
        };
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setFont(fuente(13, false));
        area.setForeground(color);
        area.setFocusable(false);
        return area;
    }

    static final class BotonSuave extends JButton {
        private final boolean principal;

        BotonSuave(String title, boolean principal) {
            super(title);
            this.principal = principal;
            setFont(fuente(13, true));
            setForeground(principal ? Color.WHITE : ACENTO);
            setBackground(principal ? ACENTO : Color.WHITE);
            setOpaque(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setRolloverEnabled(true);
            setBorder(new EmptyBorder(11, 16, 11, 16));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base = getBackground();
            boolean over = getModel().isRollover();
            if (over && isEnabled()) {
                if (principal) base = base.brighter();
                else if (base.equals(Color.WHITE)) base = PALIDO;
                else base = base.darker();
            }
            g.setColor(base);
            g.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
            if (!principal && base.equals(Color.WHITE)) {
                g.setColor(BORDE);
                g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
            }
            if (hasFocus() && isFocusOwner()) {
                g.setColor(ACENTO);
                g.setStroke(new BasicStroke(1.5f));
                g.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 11, 11);
            }
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    static JButton boton(String title, boolean primary, Runnable action) {
        JButton button = new BotonSuave(title, primary);
        button.addActionListener(e -> action.run());
        return button;
    }

    static JPanel acciones(JComponent... components) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        for (JComponent component : components) panel.add(component);
        return panel;
    }

    static JPanel aviso(String title, String message) {
        JPanel panel = new PanelSuave(CREMA, new Color(234, 225, 203), 14);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(14, 17, 14, 17));
        agregar(panel, texto(title, 13, true, AMBAR));
        panel.add(Box.createVerticalStrut(6));
        agregar(panel, parrafo(message, TINTA));
        return panel;
    }

    static JPanel indicadores(String[][] values) {
        JPanel row = new JPanel(new GridLayout(1, values.length, 13, 0));
        row.setOpaque(false);
        for (String[] value : values) {
            JPanel card = tarjeta(null, null);
            agregar(card, texto(value[0], 11, true, SUAVE));
            card.add(Box.createVerticalStrut(10));
            agregar(card, texto(value[1], 27, true, TINTA));
            card.add(Box.createVerticalStrut(7));
            agregar(card, parrafo(value[2], SUAVE));
            row.add(card);
        }
        return row;
    }

    static JScrollPane tabla(String[] columns, String[][] rows) {
        DefaultTableModel model = new DefaultTableModel(rows, columns) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(fuente(13, false));
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(233, 238, 236));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(PALIDO);
        table.setSelectionForeground(TINTA);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(fuente(12, true));
        table.getTableHeader().setBackground(new Color(240, 244, 242));
        table.getTableHeader().setForeground(SUAVE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                    boolean focused, int row, int column) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(t, value, selected, focused, row, column);
                cell.setBorder(new EmptyBorder(0, 12, 0, 8));
                cell.setFont(fuente(13, false));
                cell.setForeground(TINTA);
                if (!selected) cell.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 251, 250));
                String text = String.valueOf(value);
                if (text.contains("Pendiente") || text.contains("Ausencia") || text.contains("Por validar") || text.equals("En revisión")) {
                    cell.setForeground(AMBAR);
                } else if (text.contains("Ocupado") || text.contains("Déficit")) {
                    cell.setForeground(ROJO);
                } else if (text.equals("Disponible") || text.equals("Completa") || text.equals("Asignado")) {
                    cell.setForeground(VERDE);
                }
                cell.setToolTipText(text);
                return cell;
            }
        });
        int height = 38 + Math.min(8, rows.length) * 40 + 3;
        JScrollPane scroll = new JScrollPane(table);
        scroll.setColumnHeaderView(table.getTableHeader());
        scroll.setBorder(new LineBorder(BORDE));
        scroll.setPreferredSize(new Dimension(820, height));
        scroll.setMinimumSize(new Dimension(100, height));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return scroll;
    }

    static JPanel campo(String title, JComponent input) {
        JPanel panel = vertical();
        agregar(panel, texto(title, 12, true, SUAVE));
        panel.add(Box.createVerticalStrut(7));
        input.setFont(fuente(14, false));
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, input instanceof JTextArea ? 95 : 38));
        input.setPreferredSize(new Dimension(360, input instanceof JTextArea ? 95 : 38));
        input.setBorder(new CompoundBorder(new LineBorder(BORDE), new EmptyBorder(7, 9, 7, 9)));
        agregar(panel, input);
        panel.add(Box.createVerticalStrut(16));
        return panel;
    }
}
