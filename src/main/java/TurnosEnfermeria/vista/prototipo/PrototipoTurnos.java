package TurnosEnfermeria.vista.prototipo;

import javax.swing.*;
import java.awt.*;

/** Entrada independiente para explorar el diseño, sin cargar ni guardar CSV. */
public final class PrototipoTurnos {
    private PrototipoTurnos() { }

    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("El bosquejo necesita un escritorio gráfico. Ejecútalo desde una sesión de escritorio.");
            return;
        }
        SwingUtilities.invokeLater(() -> {
            TemaPrototipo.configurar();
            JFrame window = new JFrame("Turnos de enfermería · Gestión del servicio");
            window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            window.setContentPane(new PrototipoPanel());
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(window.getGraphicsConfiguration());
            int width = Math.max(640, screen.width - insets.left - insets.right - 40);
            int height = Math.max(480, screen.height - insets.top - insets.bottom - 60);
            window.setSize(Math.min(1280, width), Math.min(860, height));
            window.setMinimumSize(new Dimension(Math.min(1024, width), Math.min(640, height)));
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}
