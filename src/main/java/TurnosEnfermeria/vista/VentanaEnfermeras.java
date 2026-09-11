package TurnosEnfermeria.vista;

import TurnosEnfermeria.Main;
import TurnosEnfermeria.controlador.EnfermeraControlador;
import TurnosEnfermeria.modelo.Enfermera;
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


    public VentanaEnfermeras() {
        setTitle("Gestión de Enfermeras – Sistema Turnos Hospital");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(750, 480));
        setLocationRelativeTo(null);
        getContentPane().setBackground(EstilosGUI.getColorFondo());
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
        /** Muestra el titulo y los controles de busqueda. */
    private JPanel crearPanelEncabezado() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 20, 12, 20));

        JLabel titulo = new JLabel("Gestión de enfermeras");
        titulo.setFont(new Font("Dialog", Font.BOLD, 20));
        titulo.setForeground(Color.BLACK);

        JPanel panelBusqueda =
            new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelBusqueda.setBackground(Color.WHITE);

        campoBusqueda = EstilosGUI.crearCampoTexto(18);
        campoBusqueda.addActionListener(e -> buscarEnfermera());

        JButton btnBuscar =
            EstilosGUI.crearBotonSecundario("Buscar");
        btnBuscar.addActionListener(e -> buscarEnfermera());

        JButton btnLimpiar =
            EstilosGUI.crearBotonSecundario("Limpiar");
        btnLimpiar.addActionListener(e -> {
            campoBusqueda.setText("");
            cargarTabla();
        });

        panelBusqueda.add(EstilosGUI.crearLabel("RUT o nombre:"));
        panelBusqueda.add(campoBusqueda);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnLimpiar);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(panelBusqueda, BorderLayout.CENTER);
        return panel;
    }

    /** Panel central con la tabla de enfermeras. */
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EstilosGUI.getColorFondo());
        panel.setBorder(new EmptyBorder(12, 16, 0, 16));

        String[] columnas = {
            "RUT", "Nombre Completo", "Edad",
            "Especialidad", "Area", "N° Turnos"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
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
        JLabel ayuda = new JLabel("Doble clic en una fila para consultar los turnos.");
        ayuda.setFont(EstilosGUI.getFuentePequena());
        ayuda.setForeground(EstilosGUI.getColorTextoSecundario());
        ayuda.setBorder(new EmptyBorder(6, 0, 0, 0));
        panel.add(ayuda, BorderLayout.SOUTH);

        return panel;
    }

    /** Panel inferior con botones de accion. */
        /** Muestra las operaciones disponibles para las enfermeras. */
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 8, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        JButton btnAgregar =
            EstilosGUI.crearBotonSecundario("Agregar");
        JButton btnEditar =
            EstilosGUI.crearBotonSecundario("Editar");
        JButton btnEliminar =
            EstilosGUI.crearBotonSecundario("Eliminar");
        JButton btnTurnos =
            EstilosGUI.crearBotonSecundario("Ver turnos");
        JButton btnListar =
            EstilosGUI.crearBotonSecundario("Mostrar todas");

        btnAgregar.addActionListener(e -> mostrarDialogoAgregar());
        btnEditar.addActionListener(e -> editarEnfermera());
        btnEliminar.addActionListener(e -> eliminarEnfermera());
        btnTurnos.addActionListener(e -> verTurnosEnfermera());
        btnListar.addActionListener(e -> {
            campoBusqueda.setText("");
            cargarTabla();
        });

        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnTurnos);
        panel.add(btnListar);
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
                mostrarMensaje("No se pudo eliminar la enfermera.\n" + "Puede estar registrada como sustituta en cambios de otra enfermera.\n" + "Revise esos cambios antes de intentar eliminarla.", "Eliminación no realizada",JOptionPane.WARNING_MESSAGE);
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
        new VentanaTurnos(e, this).setVisible(true);
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
        dialogo.getContentPane().setBackground(EstilosGUI.getColorPanel());
        dialogo.setLayout(new BorderLayout());
        dialogo.setResizable(false);

        // ------ Panel de campos ------
        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBackground(EstilosGUI.getColorPanel());
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
        JComboBox<String> comboEsp = EstilosGUI.crearComboBox(Utilidades.getEspecialidades());
        JComboBox<String> comboArea = EstilosGUI.crearComboBox(Utilidades.getAreasHospitalarias());

        // Si es edicion, pre-llenar campos (RUT no editable)
        boolean esEdicion = enfermeraExistente != null;
        if (esEdicion) {
            campoRut.setText(enfermeraExistente.getRut());
            campoRut.setEditable(false);
            campoRut.setBackground(EstilosGUI.getColorFondo());
            campoNombre.setText(enfermeraExistente.getNombre());
            campoApellidoP.setText(enfermeraExistente.getApellidoP());
            campoApellidoM.setText(enfermeraExistente.getApellidoM());
            campoEdad.setText(String.valueOf(enfermeraExistente.getEdad()));
            comboEsp.setSelectedItem(enfermeraExistente.getEspecialidad());
            comboArea.setSelectedItem(enfermeraExistente.getAreaAsignada());
        }

        // Fila: RUT
        agregarFilaFormulario(panelCampos, gbc, 0, "RUT:", campoRut);
        agregarFilaFormulario(panelCampos, gbc, 1, "Nombre:", campoNombre);
        agregarFilaFormulario(panelCampos, gbc, 2, "Apellido Paterno:", campoApellidoP);
        agregarFilaFormulario(panelCampos, gbc, 3, "Apellido Materno:", campoApellidoM);
        agregarFilaFormulario(panelCampos, gbc, 4, "Edad:", campoEdad);
        agregarFilaFormulario(panelCampos, gbc, 5, "Especialidad:", comboEsp);
        agregarFilaFormulario(panelCampos, gbc, 6, "Área:", comboArea);

        // ------ Panel de botones ------
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        panelBotones.setBackground(EstilosGUI.getColorPanel());

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
                } catch (IllegalArgumentException ex) {
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
                        mostrarMensaje("Ya existe una enfermera con el RUT ingresado.", "RUT duplicado",
                            JOptionPane.WARNING_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    mostrarMensaje("La edad debe ser un número válido.", "Error de validación",
                        JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    mostrarMensaje(ex.getMessage(), "Error de validación",
                        JOptionPane.ERROR_MESSAGE);
                } catch (RutInvalidoException ex) {
                    mostrarMensaje("El RUT ingresado no es válido.\n" + ex.getMessage(),
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
