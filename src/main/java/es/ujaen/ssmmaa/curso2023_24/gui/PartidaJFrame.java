package es.ujaen.ssmmaa.curso2023_24.gui;

import javax.swing.*;
import java.awt.*;

public class PartidaJFrame extends JFrame {
    public PartidaJFrame(String partidaId) {
        setTitle("Partida - " + partidaId);
        setSize(600, 300);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Etiqueta superior para el ID de la partida
        JLabel lblPartidaId = new JLabel(partidaId);
        add(lblPartidaId, BorderLayout.NORTH);

        // Panel central para el tablero de juego
        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        add(boardPanel, BorderLayout.CENTER);

        // Inicializar el tablero con imágenes vacías (puedes cambiar estas por tus propias imágenes)
        ImageIcon emptyIcon = new ImageIcon(""); // Reemplaza "" con la ruta de tu imagen vacía
        for (int i = 0; i < 9; i++) { // 9 celdas en total
            JLabel cell = new JLabel("a");
            cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            boardPanel.add(cell);
        }

        // Dos etiquetas a la izquierda y derecha
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        // Etiqueta Izquierda
        JLabel lblLeft = new JLabel("Etiqueta Izquierda");
        sidePanel.add(Box.createRigidArea(new Dimension(10, 0))); // Espacio antes de la etiqueta izquierda
        sidePanel.add(lblLeft);

        // Etiqueta Derecha
        JLabel lblRight = new JLabel("Etiqueta Derecha");
        sidePanel.add(lblRight);
        sidePanel.add(Box.createRigidArea(new Dimension(10, 0))); // Espacio después de la etiqueta derecha

        // Añadir el panel lateral al marco
        add(sidePanel, BorderLayout.EAST);

        setVisible(true);
    }
}