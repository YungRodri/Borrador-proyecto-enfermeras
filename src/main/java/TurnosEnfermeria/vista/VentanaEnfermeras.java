package TurnosEnfermeria.vista;

import TurnosEnfermeria.Main;
import TurnosEnfermeria.controlador.EnfermeraControlador;
import TurnosEnfermeria.modelo.EdadInvalidaException;
import TurnosEnfermeria.modelo.Enfermera;
import TurnosEnfermeria.modelo.NombreDuplicadoException;
import TurnosEnfermeria.modelo.NombreInvalidoException;
import TurnosEnfermeria.modelo.RutInvalidoException;
import TurnosEnfermeria.modelo.Utilidades;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Ventana de gestion de Enfermeras (Coleccion 1 - TreeMap).
 * Implementa: Agregar, Listar, Buscar, Editar y Eliminar enfermeras.
 * Cumple SIA-7 y SIA-8 en la interfaz grafica (SIA-10).
 */
public class VentanaEnfermeras extends JFrame {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;

    // Columnas de la tabla
    private static final String[] COLUMNAS = {
        "RUT", "Nombre Completo", "Edad", "Especialidad", "Area", "N° Turnos"
    };

    public VentanaEnfermeras() {
        setTitle("Gestión de Enfermeras – Sistema Turnos Hospital");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(750, 480));
        setLocationRelativeTo(null);
        getContentPane().setBackground(EstilosGUI.COLOR_FONDO);
        setLayout(new BorderLayout(0, 0));

        construirUI();
        cargarTabla();
    }

    // =====================================================================
    //  CONSTRUCCION DE UI
    // =====================================================================

    private void construirUI() {
        add(crearPanelEncabezado(), BorderLayout.NORTH);
        add(crearPanelTabla(),      BorderLayout.CENTER);
        add(crearPanelBotones(),    BorderLayout.SOUTH);
    }

    /** Panel superior con titulo y barra de busqueda. */
    private JPanel crearPanelEncabezado() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(EstilosGUI.COLOR_PANEL);
        panel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel titulo = EstilosGUI.crearLabelTitulo("👩‍⚕️  Gestión de Enfermeras");
        titulo.setFont(EstilosGUI.FUENTE_TITULO);

        // Panel busqueda a la derecha
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBusqueda.setOpaque(false);
        campoBusqueda = EstilosGUI.crearCampoTexto(16);
        campoBusqueda.putClientProperty("JTextField.placeholderText", "Buscar por RUT o nombre...");

        JButton btnBuscar = EstilosGUI.crearBotonSecundario("🔍 Buscar");
        btnBuscar.addActionListener(e -> buscarEnfermera());

        JButton btnLimpiar = EstilosGUI.crearBotonSecundario("✕ Limpiar");
        btnLimpiar.addActionListener(e -> { campoBusqueda.setText(""); cargarTabla(); });

        panelBusqueda.add(EstilosGUI.crearLabel("Buscar:"));
        panelBusqueda.add(campoBusqueda);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnLimpiar);

        panel.add(titulo,        BorderLayout.WEST);
        panel.add(panelBusqueda, BorderLayout.EAST);
        return panel;
    }

    /** Panel central con la tabla de enfermeras. */
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EstilosGUI.COLOR_FONDO);
        panel.setBorder(new EmptyBorder(12, 16, 0, 16));

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modeloTabla);
        EstilosGUI.estilizarTabla(tabla);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(80);

        // Doble click para ver turnos
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() >= 0) {
                    verTurnosEnfermera();
                }
            }
        });

        panel.add(EstilosGUI.crearScrollPane(tabla), BorderLayout.CENTER);

        // Etiqueta de ayuda
        JLabel ayuda = new JLabel("  💡 Seleccione una enfermera y use Gestionar Turnos para asignarle uno o más turnos");
        ayuda.setFont(EstilosGUI.FUENTE_PEQUENA);
        ayuda.setForeground(EstilosGUI.COLOR_TEXTO_SEC);
        ayuda.setBorder(new EmptyBorder(6, 0, 0, 0));
        panel.add(ayuda, BorderLayout.SOUTH);

        return panel;
    }

    /** Panel inferior con botones de accion. */
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 14));
        panel.setBackground(EstilosGUI.COLOR_PANEL);
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JButton btnAgregar  = EstilosGUI.crearBotonPrimario("➕ Agregar Enfermera");
        JButton btnEditar   = EstilosGUI.crearBotonSecundario("✏️ Editar");
        JButton btnEliminar = EstilosGUI.crearBotonPeligro("🗑 Eliminar");
        JButton btnRefrescar= EstilosGUI.crearBotonSecundario("🔄 Refrescar");
        JButton btnTurnos   = EstilosGUI.crearBotonSecundario("📋 Gestionar Turnos");

        btnAgregar.addActionListener(e  -> mostrarDialogoAgregar());
        btnEditar.addActionListener(e   -> editarEnfermera());
        btnEliminar.addActionListener(e -> eliminarEnfermera());
        btnRefrescar.addActionListener(e-> cargarTabla());
        btnTurnos.addActionListener(e   -> verTurnosEnfermera());

        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnTurnos);
        panel.add(btnRefrescar);
        return panel;
    }

    // =====================================================================
    //  CARGA DE DATOS EN TABLA
    // =====================================================================

    /** Carga (o recarga) todas las enfermeras desde el controlador en la tabla. */
    public void cargarTabla() {
        cargarEnEnfermeras(EnfermeraControlador.listar());
    }

    private void cargarEnEnfermeras(List<Enfermera> lista) {
        modeloTabla.setRowCount(0);
        for (Enfermera e : lista) {
            modeloTabla.addRow(new Object[]{
                e.getRut(),
                e.getNombreCompleto(),
                e.getEdad(),
                e.getEspecialidad(),
                e.getAreaAsignada(),
                e.getListaTurnos().size()
            });
        }
    }

    // =====================================================================
    //  FUNCIONALIDADES CRUD
    // =====================================================================

    /** Abre el dialogo de formulario para agregar una nueva enfermera. */
    private void mostrarDialogoAgregar() {
        JDialog dialogo = crearDialogoFormulario("Agregar Nueva Enfermera", null);
        dialogo.setVisible(true);
    }

    /** Abre el dialogo de formulario para editar la enfermera seleccionada. */
    private void editarEnfermera() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Por favor, seleccione una enfermera de la tabla.", "Sin selección",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        String rut = (String) modeloTabla.getValueAt(fila, 0);
        Enfermera e = EnfermeraControlador.obtener(rut);
        if (e == null) return;
        JDialog dialogo = crearDialogoFormulario("Editar Enfermera: " + e.getNombreCompleto(), e);
        dialogo.setVisible(true);
    }

    /** Elimina la enfermera seleccionada previa confirmacion. */
    private void eliminarEnfermera() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Por favor, seleccione una enfermera de la tabla.", "Sin selección",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        String rut    = (String) modeloTabla.getValueAt(fila, 0);
        String nombre = (String) modeloTabla.getValueAt(fila, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html>¿Está seguro de que desea eliminar a <b>" + nombre + "</b>?<br>" +
            "Se eliminarán también todos sus turnos registrados.</html>",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (EnfermeraControlador.eliminar(rut)) {
                mostrarMensaje("Enfermera eliminada correctamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                cargarTabla();
            } else {
                mostrarMensaje("No se pudo eliminar la enfermera.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Busca enfermeras por RUT o nombre segun el texto en el campo de busqueda. */
    private void buscarEnfermera() {
        String termino = campoBusqueda.getText().trim();
        if (termino.isEmpty()) { cargarTabla(); return; }

        // Intentar busqueda por RUT exacto primero
        Enfermera porRut = EnfermeraControlador.obtener(termino);
        if (porRut != null) {
            cargarEnEnfermeras(java.util.Collections.singletonList(porRut));
            return;
        }

        // Si no, buscar por nombre parcial
        List<Enfermera> todas = EnfermeraControlador.listar();
        List<Enfermera> resultado = new java.util.ArrayList<>();
        for (Enfermera e : todas) {
            if (e.getNombreCompleto().toLowerCase().contains(termino.toLowerCase())) {
                resultado.add(e);
            }
        }
        if (resultado.isEmpty()) {
            mostrarMensaje("No se encontraron enfermeras con el criterio: " + termino,
                "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        } else {
            cargarEnEnfermeras(resultado);
        }
    }

    /** Abre la ventana de turnos de la enfermera seleccionada. */
    private void verTurnosEnfermera() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarMensaje("Por favor, seleccione una enfermera de la tabla.", "Sin selección",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        String rut = (String) modeloTabla.getValueAt(fila, 0);
        Enfermera e = EnfermeraControlador.obtener(rut);
        if (e == null) return;
        abrirGestionTurnos(e);
    }

    /** Abre la pantalla donde una enfermera puede tener uno o mas turnos. */
    private void abrirGestionTurnos(Enfermera enfermera) {
        new VentanaTurnos(enfermera, this).setVisible(true);
    }

    // =====================================================================
    //  DIALOGO DE FORMULARIO (Agregar / Editar)
    // =====================================================================

    /**
     * Crea el dialogo de formulario para agregar o editar una enfermera.
     * @param titulo titulo del dialogo
     * @param enfermeraExistente null para agregar, objeto para editar
     */
    private JDialog crearDialogoFormulario(String titulo, Enfermera enfermeraExistente) {
        JDialog dialogo = new JDialog(this, titulo, true);
        dialogo.setSize(480, 460);
        dialogo.setLocationRelativeTo(this);
        dialogo.getContentPane().setBackground(EstilosGUI.COLOR_PANEL);
        dialogo.setLayout(new BorderLayout());
        dialogo.setResizable(false);

        // ------ Panel de campos ------
        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBackground(EstilosGUI.COLOR_PANEL);
        panelCampos.setBorder(new EmptyBorder(20, 28, 10, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Campos del formulario
        JTextField campoRut         = EstilosGUI.crearCampoTexto(18);
        JTextField campoNombre      = EstilosGUI.crearCampoTexto(18);
        JTextField campoApellidoP   = EstilosGUI.crearCampoTexto(18);
        JTextField campoApellidoM   = EstilosGUI.crearCampoTexto(18);
        JTextField campoEdad        = EstilosGUI.crearCampoTexto(6);
        JComboBox<String> comboEsp  = EstilosGUI.crearComboBox(Utilidades.ESPECIALIDADES);
        JComboBox<String> comboArea = EstilosGUI.crearComboBox(Utilidades.AREAS_HOSPITALARIAS);
        campoRut.setToolTipText("Formato: 12345678-5 o 10000013-K, sin puntos");
        campoEdad.setToolTipText("Edad permitida: entre 18 y 65 años");

        // Si es edicion, pre-llenar campos (RUT no editable)
        boolean esEdicion = enfermeraExistente != null;
        if (esEdicion) {
            campoRut.setText(enfermeraExistente.getRut());
            campoRut.setEditable(false);
            campoRut.setBackground(EstilosGUI.COLOR_FONDO);
            campoNombre.setText(enfermeraExistente.getNombre());
            campoApellidoP.setText(enfermeraExistente.getApellidoP());
            campoApellidoM.setText(enfermeraExistente.getApellidoM());
            campoEdad.setText(String.valueOf(enfermeraExistente.getEdad()));
            comboEsp.setSelectedItem(enfermeraExistente.getEspecialidad());
            comboArea.setSelectedItem(enfermeraExistente.getAreaAsignada());
        }

        // Fila: RUT
        agregarFilaFormulario(panelCampos, gbc, 0, "RUT (ej. 10000013-K):", campoRut);
        agregarFilaFormulario(panelCampos, gbc, 1, "Nombre:", campoNombre);
        agregarFilaFormulario(panelCampos, gbc, 2, "Apellido Paterno:", campoApellidoP);
        agregarFilaFormulario(panelCampos, gbc, 3, "Apellido Materno:", campoApellidoM);
        agregarFilaFormulario(panelCampos, gbc, 4, "Edad (18 a 65):", campoEdad);
        agregarFilaFormulario(panelCampos, gbc, 5, "Especialidad:", comboEsp);
        agregarFilaFormulario(panelCampos, gbc, 6, "Área:", comboArea);

        // ------ Panel de botones ------
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        panelBotones.setBackground(EstilosGUI.COLOR_PANEL);

        JButton btnCancelar = EstilosGUI.crearBotonSecundario("Cancelar");
        JButton btnGuardar  = EstilosGUI.crearBotonPrimario(esEdicion ? "Guardar Cambios" : "Agregar");

        btnCancelar.addActionListener(e -> dialogo.dispose());
        btnGuardar.addActionListener(e -> {
            // Validar y guardar
            if (esEdicion) {
                // Editar enfermera existente
                try {
                    int edad = Integer.parseInt(campoEdad.getText().trim());
                    boolean ok = EnfermeraControlador.editar(
                        enfermeraExistente.getRut(),
                        campoNombre.getText().trim(),
                        campoApellidoP.getText().trim(),
                        campoApellidoM.getText().trim(),
                        edad,
                        (String) comboEsp.getSelectedItem(),
                        (String) comboArea.getSelectedItem()
                    );
                    if (ok) {
                        mostrarMensaje("Enfermera actualizada correctamente.", "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                        cargarTabla();
                        dialogo.dispose();
                    }
                } catch (NumberFormatException ex) {
                    mostrarMensaje("La edad debe ser un número válido.", "Error de validación",
                        JOptionPane.ERROR_MESSAGE);
                } catch (EdadInvalidaException | NombreDuplicadoException
                        | NombreInvalidoException ex) {
                    mostrarMensaje(ex.getMessage(), "Error de validación",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Agregar nueva enfermera
                try {
                    int edad = Integer.parseInt(campoEdad.getText().trim());
                    Enfermera nueva = new Enfermera(
                        campoNombre.getText().trim(),
                        campoApellidoP.getText().trim(),
                        campoApellidoM.getText().trim(),
                        campoRut.getText().trim(),
                        edad,
                        (String) comboEsp.getSelectedItem(),
                        (String) comboArea.getSelectedItem()
                    );
                    if (EnfermeraControlador.agregar(nueva)) {
                        mostrarMensaje("Enfermera agregada exitosamente.", "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                        cargarTabla();
                        dialogo.dispose();
                    } else {
                        Enfermera existente = EnfermeraControlador.obtener(
                            campoRut.getText().trim());
                        Object[] opciones = {"Gestionar turnos", "Cancelar"};
                        int opcion = JOptionPane.showOptionDialog(dialogo,
                            "Ya existe una enfermera con este RUT.\n"
                            + "No debe crearla nuevamente para asignarle otro turno.\n"
                            + "Abra Gestionar Turnos y agregue allí todos sus turnos.",
                            "Enfermera ya registrada",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null, opciones, opciones[0]);
                        if (opcion == 0 && existente != null) {
                            dialogo.dispose();
                            abrirGestionTurnos(existente);
                        }
                    }
                } catch (NumberFormatException ex) {
                    mostrarMensaje("La edad debe ser un número válido.", "Error de validación",
                        JOptionPane.ERROR_MESSAGE);
                } catch (EdadInvalidaException | NombreDuplicadoException
                        | NombreInvalidoException ex) {
                    mostrarMensaje(ex.getMessage(), "Error de validación",
                        JOptionPane.ERROR_MESSAGE);
                } catch (RutInvalidoException ex) {
                    mostrarMensaje(ex.getMessage(),
                        "RUT inválido", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        dialogo.add(panelCampos,  BorderLayout.CENTER);
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        return dialogo;
    }

    /** Agrega una fila etiqueta-campo al GridBagLayout del formulario. */
    private void agregarFilaFormulario(JPanel panel, GridBagConstraints gbc,
                                       int fila, String etiqueta, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        JLabel lbl = EstilosGUI.crearLabel(etiqueta);
        lbl.setPreferredSize(new Dimension(140, 28));
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(campo, gbc);
    }

    // =====================================================================
    //  UTILIDADES
    // =====================================================================

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
}
