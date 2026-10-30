package TurnosEnfermeria.vista.prototipo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import static TurnosEnfermeria.vista.prototipo.TemaPrototipo.*;

/** Interfaz de demostración. La selección de perfil no autentica ni concede permisos. */
public final class PrototipoPanel extends JPanel {
    private final CardLayout cards = new CardLayout();
    private final JPanel vistas = new JPanel(cards);
    private final JPanel espacio = new JPanel(new BorderLayout());
    private final JPanel menu = vertical();
    private final JLabel contexto = texto("", 14, true, TINTA);
    private final JLabel identidad = texto("", 12, false, SUAVE);
    private final Map<String, JButton> botones = new LinkedHashMap<>();
    private String rol;
    private String paginaActual;

    public PrototipoPanel() {
        super(new BorderLayout());
        setBackground(FONDO);
        vistas.setOpaque(false);

        // Aviso discreto, pero visible: es una demostración, no una aplicación conectada.
        JPanel avisoDemo = new JPanel(new BorderLayout());
        avisoDemo.setBackground(new Color(239, 246, 242));
        avisoDemo.setBorder(new EmptyBorder(10, 29, 10, 29));
        avisoDemo.add(texto("Versión de demostración  ·  Datos de ejemplo", 12, true, ACENTO), BorderLayout.WEST);
        avisoDemo.add(texto("Los cambios no se guardan", 12, false, SUAVE), BorderLayout.EAST);
        add(avisoDemo, BorderLayout.NORTH);

        JScrollPane inicio = new JScrollPane(selector());
        inicio.setBorder(null);
        inicio.getViewport().setBackground(FONDO);
        inicio.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inicio.getVerticalScrollBar().setUnitIncrement(24);
        vistas.add(inicio, "selector");
        vistas.add(escritorio(), "escritorio");
        add(vistas, BorderLayout.CENTER);
        cards.show(vistas, "selector");
    }

    private JPanel selector() {
        JPanel page = new SelectorPanel();
        page.setBackground(FONDO);
        page.setBorder(new EmptyBorder(30, 42, 30, 42));

        JPanel marca = new JPanel(new FlowLayout(FlowLayout.LEFT, 11, 0));
        marca.setOpaque(false);
        marca.add(new Marca(34));
        JLabel name = texto("Turnos de enfermería", 17, true, AZUL);
        name.setBorder(new EmptyBorder(6, 0, 0, 0));
        marca.add(name);
        page.add(marca, BorderLayout.NORTH);

        JPanel main = new JPanel(new GridBagLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(30, 0, 18, 0));

        JPanel presentacion = vertical();
        presentacion.add(Box.createVerticalStrut(18));
        JLabel eyebrow = texto("ORGANIZACIÓN DEL SERVICIO", 11, true, ACENTO);
        agregar(presentacion, eyebrow);
        presentacion.add(Box.createVerticalStrut(20));
        JLabel titulo = texto("<html>Los turnos, más<br>claros para todos.</html>", 34, true, TINTA);
        agregar(presentacion, titulo);
        presentacion.add(Box.createVerticalStrut(20));
        JLabel intro = texto("<html><div style='width:280px'>Un espacio para consultar turnos, organizar reemplazos y ver cómo está el equipo durante la semana.</div></html>", 15, false, SUAVE);
        agregar(presentacion, intro);
        presentacion.add(Box.createVerticalStrut(36));

        JPanel dato = new PanelSuave(new Color(236, 243, 239), null, 15);
        dato.setLayout(new BoxLayout(dato, BoxLayout.Y_AXIS));
        dato.setBorder(new EmptyBorder(17, 18, 17, 18));
        agregar(dato, texto("Área piloto A", 14, true, AZUL));
        dato.add(Box.createVerticalStrut(7));
        agregar(dato, texto("Semana de ejemplo: " + DatosPrototipo.PERIODO, 12, false, SUAVE));
        dato.add(Box.createVerticalStrut(11));
        agregar(dato, parrafo("Las tres vistas utilizan la misma información para que puedas probar cada tarea.", SUAVE));
        agregar(presentacion, dato);

        JPanel eleccion = vertical();
        agregar(eleccion, texto("¿Con qué perfil quieres entrar?", 22, true, TINTA));
        eleccion.add(Box.createVerticalStrut(8));
        agregar(eleccion, texto("Elige una opción para ver sus tareas.", 13, false, SUAVE));
        eleccion.add(Box.createVerticalStrut(22));
        agregar(eleccion, opcion("Enfermera", "Mis turnos y solicitudes", "EN",
                "Consultar turnos propios", "Comunicar ausencias (por validar)", "Seguir solicitudes",
                "enfermeria"));
        eleccion.add(Box.createVerticalStrut(12));
        agregar(eleccion, opcion("Coordinador", "Organización del equipo", "CO",
                "Organizar las asignaciones", "Revisar ausencias y conflictos", "Preparar sustituciones",
                "coordinacion"));
        eleccion.add(Box.createVerticalStrut(12));
        agregar(eleccion, opcion("Administrador", "Seguimiento del servicio", "AD",
                "Consultar cobertura por bloque", "Revisar déficits e incidencias", "Consultar informes del período",
                "jefatura"));

        GridBagConstraints left = new GridBagConstraints();
        left.gridx = 0; left.gridy = 0; left.weightx = .37; left.weighty = 1;
        left.fill = GridBagConstraints.BOTH;
        left.insets = new Insets(0, 0, 0, 35);
        main.add(presentacion, left);
        GridBagConstraints right = new GridBagConstraints();
        right.gridx = 1; right.gridy = 0; right.weightx = .63; right.weighty = 1;
        right.fill = GridBagConstraints.BOTH;
        main.add(eleccion, right);
        page.add(main, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDE));
        JLabel note = texto("Proyecto académico · Ingeniería de Software · PUCV", 12, false, SUAVE);
        note.setBorder(new EmptyBorder(14, 0, 0, 0));
        footer.add(note, BorderLayout.WEST);
        page.add(footer, BorderLayout.SOUTH);
        return page;
    }

    private JPanel opcion(String nombre, String subtitulo, String iniciales, String primera,
                          String segunda, String tercera, String claveRol) {
        JPanel card = new PanelSuave(Color.WHITE, BORDE, 16);
        card.setLayout(new BorderLayout(14, 0));
        card.setBorder(new EmptyBorder(16, 17, 16, 17));
        card.setPreferredSize(new Dimension(540, 158));
        card.setMinimumSize(new Dimension(335, 158));

        JLabel badge = texto(iniciales, 12, true, ACENTO);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(PALIDO);
        badge.setPreferredSize(new Dimension(45, 45));
        JPanel badgeWrap = new JPanel(new BorderLayout());
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge, BorderLayout.NORTH);
        card.add(badgeWrap, BorderLayout.WEST);

        JPanel info = vertical();
        agregar(info, texto(nombre, 19, true, TINTA));
        info.add(Box.createVerticalStrut(4));
        agregar(info, texto(subtitulo, 12, false, SUAVE));
        info.add(Box.createVerticalStrut(12));
        JLabel lista = texto("<html>• " + primera + "<br>• " + segunda + "<br>• " + tercera + "</html>",
                12, false, TINTA);
        agregar(info, lista);
        card.add(info, BorderLayout.CENTER);

        JPanel controles = new JPanel(new BorderLayout());
        controles.setOpaque(false);
        JButton entrar = boton("Entrar  →", false, () -> mostrarRol(claveRol));
        entrar.setToolTipText("Abrir vista de " + nombre);
        controles.add(entrar, BorderLayout.SOUTH);
        card.add(controles, BorderLayout.EAST);
        return card;
    }

    private JPanel escritorio() {
        JPanel desk = new JPanel(new BorderLayout());
        desk.setBackground(FONDO);
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(253, 0));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, BORDE));

        JPanel interior = new JPanel(new BorderLayout());
        interior.setOpaque(false);
        interior.setBorder(new EmptyBorder(26, 17, 20, 17));
        JPanel brand = vertical();
        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandRow.setOpaque(false);
        brandRow.add(new Marca(33));
        JLabel brandName = texto("Turnos", 19, true, AZUL);
        brandName.setBorder(new EmptyBorder(5, 0, 0, 0));
        brandRow.add(brandName);
        agregar(brand, brandRow);
        brand.add(Box.createVerticalStrut(11));
        agregar(brand, texto("Enfermería  /  Área piloto A", 12, false, SUAVE));
        brand.add(Box.createVerticalStrut(43));
        agregar(brand, texto("MENÚ PRINCIPAL", 11, true, SUAVE));
        brand.add(Box.createVerticalStrut(15));
        interior.add(brand, BorderLayout.NORTH);

        JPanel listado = new JPanel(new BorderLayout());
        listado.setOpaque(false);
        listado.add(menu, BorderLayout.NORTH);
        interior.add(listado, BorderLayout.CENTER);

        JPanel bottom = vertical();
        agregar(bottom, texto("Modo de prueba", 12, true, AZUL));
        bottom.add(Box.createVerticalStrut(6));
        agregar(bottom, parrafo("Los formularios muestran cómo funcionaría cada tarea, pero no guardan cambios.", SUAVE));
        bottom.add(Box.createVerticalStrut(16));
        agregar(bottom, boton("←  Cambiar de perfil", false, this::mostrarSelector));
        interior.add(bottom, BorderLayout.SOUTH);
        sidebar.add(interior, BorderLayout.CENTER);
        desk.add(sidebar, BorderLayout.WEST);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(FONDO);
        JPanel top = new JPanel(new BorderLayout(18, 0));
        top.setBackground(Color.WHITE);
        top.setBorder(new javax.swing.border.CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDE), new EmptyBorder(20, 29, 20, 29)));
        top.add(contexto, BorderLayout.WEST);
        top.add(identidad, BorderLayout.EAST);
        body.add(top, BorderLayout.NORTH);
        body.add(espacio, BorderLayout.CENTER);
        JLabel foot = texto("Información de ejemplo · Sin registro de cambios", 11, false, SUAVE);
        foot.setBorder(new EmptyBorder(9, 28, 12, 28));
        body.add(foot, BorderLayout.SOUTH);
        desk.add(body, BorderLayout.CENTER);
        return desk;
    }

    void mostrarRol(String role) {
        rol = role;
        menu.removeAll();
        botones.clear();
        String[] pages;
        switch (role) {
            case "enfermeria":
                pages = new String[]{"Mi agenda", "Mis solicitudes"};
                contexto.setText("Enfermera  /  " + DatosPrototipo.PERIODO);
                identidad.setText("E-001 · Persona 01");
                break;
            case "coordinacion":
                pages = new String[]{"Resumen operativo", "Planificación", "Personal", "Ausencias y cobertura"};
                contexto.setText("Coordinador  /  " + DatosPrototipo.PERIODO);
                identidad.setText("Área piloto A");
                break;
            case "jefatura":
                pages = new String[]{"Cobertura del servicio", "Informes"};
                contexto.setText("Administrador  /  " + DatosPrototipo.PERIODO);
                identidad.setText("Vista de consulta");
                break;
            default: throw new IllegalArgumentException("Rol de demostración desconocido: " + role);
        }
        for (String page : pages) {
            JButton button = boton(page, false, () -> mostrarPagina(page));
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            button.setPreferredSize(new Dimension(214, 46));
            button.setBorder(new EmptyBorder(13, 15, 13, 15));
            botones.put(page, button);
            agregar(menu, button);
            menu.add(Box.createVerticalStrut(7));
        }
        menu.revalidate();
        cards.show(vistas, "escritorio");
        mostrarPagina(pages[0]);
        revalidate(); repaint();
    }

    void mostrarPagina(String page) {
        if (!botones.containsKey(page)) throw new IllegalArgumentException("Pantalla ajena a la vista seleccionada.");
        paginaActual = page;
        for (Map.Entry<String, JButton> entry : botones.entrySet()) {
            boolean selected = entry.getKey().equals(page);
            entry.getValue().setBackground(selected ? PALIDO : Color.WHITE);
            entry.getValue().setForeground(selected ? ACENTO : TINTA);
        }
        espacio.removeAll();
        JScrollPane scroll = new JScrollPane(PantallasPrototipo.crear(page, this::abrirFormulario));
        scroll.setBorder(null);
        scroll.getViewport().setBackground(FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        espacio.add(scroll, BorderLayout.CENTER);
        espacio.revalidate(); espacio.repaint();
    }

    void mostrarSelector() {
        rol = null;
        paginaActual = null;
        cards.show(vistas, "selector");
        revalidate(); repaint();
    }

    String getRol() { return rol; }
    String getPaginaActual() { return paginaActual; }

    private void abrirFormulario(String id) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, PantallasPrototipo.tituloFormulario(id), Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(FONDO);
        JScrollPane scroll = new JScrollPane(PantallasPrototipo.formulario(id));
        scroll.setBorder(null);
        scroll.getViewport().setBackground(FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        content.add(scroll, BorderLayout.CENTER);
        JPanel actions = acciones(boton("Ver qué haría esta acción", true, () ->
                JOptionPane.showMessageDialog(dialog,
                        "<html><body style='width:380px'>" + PantallasPrototipo.accionPrevista(id)
                        + "<br><br><b>Esta versión de muestra no guarda cambios.</b></body></html>",
                        "Acerca de esta acción", JOptionPane.INFORMATION_MESSAGE)),
                boton("Cerrar", false, dialog::dispose));
        actions.setBorder(new EmptyBorder(12, 18, 18, 18));
        content.add(actions, BorderLayout.SOUTH);
        dialog.setContentPane(content);
        dialog.setSize(700, 670);
        dialog.setMinimumSize(new Dimension(560, 480));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    /** Pequeño símbolo dibujado en Java, sin fuentes de íconos ni dependencias externas. */
    private static final class Marca extends JComponent {
        private final int lado;
        Marca(int lado) { this.lado = lado; setPreferredSize(new Dimension(lado, lado)); }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(ACENTO);
            g.fillRoundRect(0, 0, lado, lado, 10, 10);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int m = lado / 2;
            g.drawLine(m, 9, m, lado - 9);
            g.drawLine(9, m, lado - 9, m);
            g.dispose();
        }
    }

    private static final class SelectorPanel extends JPanel implements Scrollable {
        SelectorPanel() { super(new BorderLayout()); }
        @Override public Dimension getPreferredScrollableViewportSize() { return new Dimension(1130, 720); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 22; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return Math.max(22, r.height - 22); }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
