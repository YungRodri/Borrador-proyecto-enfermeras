package TurnosEnfermeria.vista;

import TurnosEnfermeria.controlador.TurnoControlador;
import TurnosEnfermeria.modelo.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Ventana de gestion de Turnos de una Enfermera especifica (Coleccion 2 anidada).
 * Implementa: Agregar, Listar, Buscar, Editar y Eliminar turnos.
 * Cumple SIA-7 y SIA-8 en la interfaz grafica (SIA-10).
 */
public class VentanaTurnos extends JFrame {

    private final Enfermera enfermera;
    private final VentanaEnfermeras ventanaPadre;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private static final String[] COLUMNAS = {
        "ID", "Tipo", "Fecha", "Hora Inicio", "Hora Fin", "Resumen"
    };

    /**
     * Constructor.
     * @param enfermera      enfermera a la que pertenecen los turnos
     * @param ventanaPadre   referencia a la ventana padre para refrescarla al cerrar
     */
    public VentanaTurnos(Enfermera enfermera, VentanaEnfermeras ventanaPadre) {
        this.enfermera    = enfermera;
        this.ventanaPadre = ventanaPadre;

        setTitle("Turnos de " + enfermera.getNombreCompleto() + " [" + enfermera.getRut() + "]");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(860, 540);
        setMinimumSize(new Dimension(700, 420));
        setLocationRelativeTo(ventanaPadre);
        getContentPane().setBackground(EstilosGUI.getColorFondo());
        setLayout(new BorderLayout(0, 0));

        // Refrescar la ventana de enfermeras al cerrar esta
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (ventanaPadre != null) ventanaPadre.cargarTabla();
            }
        });

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

       /** Muestra los datos de la enfermera y el resumen de sus turnos. */
    private JPanel crearPanelEncabezado() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel titulo = new JLabel("Turnos de " + enfermera.getNombreCompleto());
        titulo.setFont(new Font("Dialog", Font.BOLD, 20));
        titulo.setForeground(Color.BLACK);

        JLabel datos = EstilosGUI.crearLabel("RUT: " + enfermera.getRut()+ " | Área: " + enfermera.getAreaAsignada());

        JLabel especialidad = EstilosGUI.crearLabel("Especialidad: " + enfermera.getEspecialidad());

        JLabel resumen = EstilosGUI.crearLabel("Regulares: " + enfermera.contarTurnosRegulares() + " | Licencias: " + enfermera.contarLicencias() + " | Cambios: " + enfermera.contarCambios()+ " | Horas trabajadas: "+ String.format("%.1f",                 TurnoControlador.calcularHorasTrabajadas(enfermera)));

        panel.add(titulo);
        panel.add(datos);
        panel.add(especialidad);
        panel.add(resumen);
        return panel;
    }

    /** Panel central con la tabla de turnos. */
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EstilosGUI.getColorFondo());
        panel.setBorder(new EmptyBorder(12, 16, 0, 16));

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modeloTabla);
        EstilosGUI.estilizarTabla(tabla);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(360);

        panel.add(EstilosGUI.crearScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

        /** Muestra las operaciones disponibles para los turnos. */
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 8, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        JButton btnAgregar =
            EstilosGUI.crearBotonSecundario("Agregar");
        JButton btnEditar =
            EstilosGUI.crearBotonSecundario("Editar observación");
        JButton btnEliminar =
            EstilosGUI.crearBotonSecundario("Eliminar");
        JButton btnBuscar =
            EstilosGUI.crearBotonSecundario("Buscar por ID");
        JButton btnMostrar =
            EstilosGUI.crearBotonSecundario("Mostrar todos");

        btnAgregar.addActionListener(e -> mostrarDialogoAgregarTurno());
        btnEditar.addActionListener(e -> editarObservacion());
        btnEliminar.addActionListener(e -> eliminarTurno());
        btnBuscar.addActionListener(e -> buscarPorId());
        btnMostrar.addActionListener(e -> {
            cargarTabla();
            actualizarEncabezado();
        });

        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnBuscar);
        panel.add(btnMostrar);
        return panel;
    }

    // =====================================================================
    //  CARGA DE DATOS
    // =====================================================================

    /** Recarga los turnos de la enfermera en la tabla. */
    public void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Turno t : enfermera.getListaTurnos()) {
            modeloTabla.addRow(new Object[]{
                t.getId(),
                t.getTipo(),
                t.getFecha(),
                t.getHoraInicio().isEmpty() ? "—" : t.getHoraInicio(),
                t.getHoraFin().isEmpty()    ? "—" : t.getHoraFin(),
                t.getResumen()
            });
        }
    }

    // =====================================================================
    //  FUNCIONALIDADES CRUD
    // =====================================================================

    /** Abre el dialogo de seleccion de tipo de turno y luego el formulario correspondiente. */
    private void mostrarDialogoAgregarTurno() {
        String[] tipos = {"Turno Regular (Mañana/Tarde/Noche)", "Licencia", "Cambio de Turno"};
        int sel = JOptionPane.showOptionDialog(this,
            "Seleccione el tipo de turno a agregar:",
            "Agregar Turno",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, tipos, tipos[0]);
        if (sel < 0) return;
        switch (sel) {
            case 0: mostrarFormularioTurnoRegular(); break;
            case 1: mostrarFormularioLicencia();     break;
            case 2: mostrarFormularioCambioTurno();  break;
        }
    }

    /** Formulario para agregar un TurnoRegular. */
    private void mostrarFormularioTurnoRegular() {
        JDialog d = new JDialog(this, "Agregar Turno Regular", true);
        d.setSize(420, 280);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(EstilosGUI.getColorPanel());
        d.setLayout(new BorderLayout());

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(EstilosGUI.getColorPanel());
        campos.setBorder(new EmptyBorder(18, 24, 10, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField campoFecha = EstilosGUI.crearCampoTexto(14);
        campoFecha.setToolTipText("Formato: dd/MM/yyyy");
        JComboBox<String> comboTipo = EstilosGUI.crearComboBox(Utilidades.getTiposTurno());
        JTextField campoObs = EstilosGUI.crearCampoTexto(20);

        gbc.gridx=0; gbc.gridy=0; campos.add(EstilosGUI.crearLabel("Fecha (dd/MM/yyyy):"), gbc);
        gbc.gridx=1; campos.add(campoFecha, gbc);
        gbc.gridx=0; gbc.gridy=1; campos.add(EstilosGUI.crearLabel("Tipo de Turno:"), gbc);
        gbc.gridx=1; campos.add(comboTipo, gbc);
        gbc.gridx=0; gbc.gridy=2; campos.add(EstilosGUI.crearLabel("Observación:"), gbc);
        gbc.gridx=1; campos.add(campoObs, gbc);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(EstilosGUI.getColorPanel());
        JButton btnCancelar = EstilosGUI.crearBotonSecundario("Cancelar");
        JButton btnGuardar  = EstilosGUI.crearBotonPrimario("Agregar");
        btnCancelar.addActionListener(e -> d.dispose());
        btnGuardar.addActionListener(e -> {
            String fecha = campoFecha.getText().trim();
            if (!Utilidades.validarFecha(fecha)) {
                JOptionPane.showMessageDialog(d, "Fecha inválida. Use el formato dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String tipo = (String) comboTipo.getSelectedItem();
            String horaIni = Utilidades.horaInicioPorTipo(tipo);
            String horaFin = Utilidades.horaFinPorTipo(tipo);
            try {
                TurnoRegular nuevo = new TurnoRegular(Utilidades.generarIdTurno(),fecha,horaIni,horaFin,tipo,campoObs.getText().trim());

                TurnoControlador.registrar(enfermera, nuevo);
                cargarTabla();
                actualizarEncabezado();
                d.dispose();
            } catch (TurnoConflictoException ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage(), "Conflicto de Horario", JOptionPane.ERROR_MESSAGE);
            }
        });
        btns.add(btnCancelar); btns.add(btnGuardar);
        d.add(campos, BorderLayout.CENTER);
        d.add(btns,   BorderLayout.SOUTH);
        d.setVisible(true);
    }

    /** Formulario para agregar una Licencia. */
    private void mostrarFormularioLicencia() {
        JDialog d = new JDialog(this, "Agregar Licencia", true);
        d.setSize(420, 260);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(EstilosGUI.getColorPanel());
        d.setLayout(new BorderLayout());

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(EstilosGUI.getColorPanel());
        campos.setBorder(new EmptyBorder(18, 24, 10, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField campoFecha  = EstilosGUI.crearCampoTexto(14);
        JTextField campoMotivo = EstilosGUI.crearCampoTexto(20);
        JTextField campoObservacion = EstilosGUI.crearCampoTexto(20);
        JComboBox<String> comboTipo = EstilosGUI.crearComboBox(Utilidades.getTiposLicencia());

        gbc.gridx=0; gbc.gridy=0; campos.add(EstilosGUI.crearLabel("Fecha (dd/MM/yyyy):"), gbc);
        gbc.gridx=1; campos.add(campoFecha, gbc);
        gbc.gridx=0; gbc.gridy=1; campos.add(EstilosGUI.crearLabel("Tipo de Licencia:"), gbc);
        gbc.gridx=1; campos.add(comboTipo, gbc);
        gbc.gridx=0; gbc.gridy=2; campos.add(EstilosGUI.crearLabel("Motivo:"), gbc);
        gbc.gridx=1; campos.add(campoMotivo, gbc);
        gbc.gridx= 0;
        gbc.gridy= 5;
        campos.add(EstilosGUI.crearLabel("Observación:"), gbc);

        gbc.gridx = 1;
        campos.add(campoObservacion, gbc);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(EstilosGUI.getColorPanel());
        JButton btnCancelar = EstilosGUI.crearBotonSecundario("Cancelar");
        JButton btnGuardar  = EstilosGUI.crearBotonPrimario("Agregar");
        btnCancelar.addActionListener(e -> d.dispose());
        btnGuardar.addActionListener(e -> {
            String fecha = campoFecha.getText().trim();
            if (!Utilidades.validarFecha(fecha)) {
                JOptionPane.showMessageDialog(d, "Fecha inválida. Use el formato dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String motivo = campoMotivo.getText().trim();
            if (motivo.isEmpty()) {
                JOptionPane.showMessageDialog(d, "Ingrese el motivo de la licencia.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String id = Utilidades.generarIdTurno();
            Licencia lic = new Licencia(id, fecha, motivo, (String) comboTipo.getSelectedItem());
            try {
                TurnoControlador.registrar(enfermera, lic);
                cargarTabla();
                actualizarEncabezado();
                d.dispose();
            } catch (TurnoConflictoException ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btns.add(btnCancelar); btns.add(btnGuardar);
        d.add(campos, BorderLayout.CENTER);
        d.add(btns,   BorderLayout.SOUTH);
        d.setVisible(true);
    }

    /** Formulario para agregar un CambioTurno. */
    private void mostrarFormularioCambioTurno() {
        JDialog d = new JDialog(this, "Agregar Cambio de Turno", true);
        d.setSize(520, 420);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(EstilosGUI.getColorPanel());
        d.setLayout(new BorderLayout());

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(EstilosGUI.getColorPanel());
        campos.setBorder(new EmptyBorder(18, 24, 10, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField campoFecha     = EstilosGUI.crearCampoTexto(14);
        JTextField campoHoraIni   = EstilosGUI.crearCampoTexto(8);
        JTextField campoHoraFin   = EstilosGUI.crearCampoTexto(8);
        JTextField campoSustituta = EstilosGUI.crearCampoTexto(16);
        JTextField campoMotivo    = EstilosGUI.crearCampoTexto(20);

        campoHoraIni.setToolTipText("HH:mm (ej: 07:00)");
        campoHoraFin.setToolTipText("HH:mm (ej: 15:00)");
        campoSustituta.setToolTipText("RUT de la enfermera sustituta");

        gbc.gridx=0; gbc.gridy=0; campos.add(EstilosGUI.crearLabel("Fecha (dd/MM/yyyy):"), gbc);
        gbc.gridx=1; campos.add(campoFecha, gbc);
        gbc.gridx=0; gbc.gridy=1; campos.add(EstilosGUI.crearLabel("Hora inicio (HH:mm):"), gbc);
        gbc.gridx=1; campos.add(campoHoraIni, gbc);
        gbc.gridx=0; gbc.gridy=2; campos.add(EstilosGUI.crearLabel("Hora fin (HH:mm):"), gbc);
        gbc.gridx=1; campos.add(campoHoraFin, gbc);
        gbc.gridx=0; gbc.gridy=3; campos.add(EstilosGUI.crearLabel("RUT Sustituta:"), gbc);
        gbc.gridx=1; campos.add(campoSustituta, gbc);
        gbc.gridx=0; gbc.gridy=4; campos.add(EstilosGUI.crearLabel("Motivo:"), gbc);
        gbc.gridx=1; campos.add(campoMotivo, gbc);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(EstilosGUI.getColorPanel());
        JButton btnCancelar = EstilosGUI.crearBotonSecundario("Cancelar");
        JButton btnGuardar  = EstilosGUI.crearBotonPrimario("Agregar");
        btnCancelar.addActionListener(e -> d.dispose());
        btnGuardar.addActionListener(e -> {
            if (!Utilidades.validarFecha(campoFecha.getText().trim())) {
                JOptionPane.showMessageDialog(d, "Fecha inválida.", "Error", JOptionPane.ERROR_MESSAGE); return;
            }
            if (!Utilidades.validarHora(campoHoraIni.getText().trim()) || !Utilidades.validarHora(campoHoraFin.getText().trim())) {
                JOptionPane.showMessageDialog(d, "Hora inválida. Use formato HH:mm.", "Error", JOptionPane.ERROR_MESSAGE); return;
            }
            String id = Utilidades.generarIdTurno();
            CambioTurno cambio = new CambioTurno(
                id,
                campoFecha.getText().trim(),
                campoHoraIni.getText().trim(),
                campoHoraFin.getText().trim(),
                campoSustituta.getText().trim(),
                campoMotivo.getText().trim(),
                campoObservacion.getText().trim()
            );
            try {
                TurnoControlador.registrar(enfermera, cambio);
                cargarTabla();
                actualizarEncabezado();
                d.dispose();
            } catch (TurnoConflictoException ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage(), "Conflicto de Horario", JOptionPane.ERROR_MESSAGE);
            }
        });
        btns.add(btnCancelar); btns.add(btnGuardar);
        d.add(campos, BorderLayout.CENTER);
        d.add(btns,   BorderLayout.SOUTH);
        d.setVisible(true);
    }

    /** Permite editar la observacion del turno seleccionado. */
    private void editarObservacion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un turno de la tabla.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) modeloTabla.getValueAt(fila, 0);
        Turno turno = enfermera.buscarTurno(id);
        if (turno == null) return;

        String nuevaObs = JOptionPane.showInputDialog(this,
            "Ingrese la nueva observación para el turno " + id + ":",
            turno.getObservacion());
        if (nuevaObs != null) {
            TurnoControlador.editar(enfermera, id, nuevaObs.trim());
            cargarTabla();
        }
    }

    /** Elimina el turno seleccionado de la tabla. */
    private void eliminarTurno() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un turno de la tabla.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id     = (String) modeloTabla.getValueAt(fila, 0);
        String resumen= (String) modeloTabla.getValueAt(fila, 5);

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html>¿Eliminar el turno <b>" + id + "</b>?<br><small>" + resumen + "</small></html>",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (TurnoControlador.eliminar(enfermera, id)) {
                cargarTabla();
                actualizarEncabezado();
                JOptionPane.showMessageDialog(this, "Turno eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

        /** Busca un turno por ID en todo el registro. */
    private void buscarPorId() {
        String id = JOptionPane.showInputDialog(this, "Ingrese el ID del turno a buscar en todo el sistema:", "Buscar turno", JOptionPane.QUESTION_MESSAGE);

        if (id == null || id.trim().isEmpty()) return;

        Object[] resultado = TurnoControlador.buscarTurno(id.trim());

        if (resultado == null) {
            JOptionPane.showMessageDialog(this, "No se encontró ningún turno con el ID: " + id.trim(), "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Enfermera titular = (Enfermera) resultado[0];
        Turno turno = (Turno) resultado[1];

        if (titular.getRut().equals(enfermera.getRut())) {
            cargarTabla();

            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                if (turno.getId().equals(modeloTabla.getValueAt(i, 0))) {
                    tabla.setRowSelectionInterval(i, i);
                    tabla.scrollRectToVisible(tabla.getCellRect(i, 0, true));
                    break;
                }
            }
        }

        JTextArea texto = new JTextArea("Enfermera titular: " + titular.getNombreCompleto() + "\nRUT: " + titular.getRut() + "\nID: " + turno.getId() + "\nTipo: " + turno.getTipo() + "\nResumen: " + turno.getResumen() + "\nObservación: " + turno.getObservacion(), 8, 45);
        texto.setEditable(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setBackground(Color.WHITE);
        texto.setForeground(Color.BLACK);
        texto.setCaretPosition(0);

        JOptionPane.showMessageDialog(
            this,
            new JScrollPane(texto),
            "Turno encontrado",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /** Vuelve a construir el panel norte para reflejar estadisticas actualizadas. */
    private void actualizarEncabezado() {
        getContentPane().remove(((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.NORTH));
        add(crearPanelEncabezado(), BorderLayout.NORTH);
        revalidate();
        repaint();
    }
}
