package es.ujaen.ssmmaa.curso2023_24.gui;

import javax.swing.*;
import java.awt.*;

public class TestTablero extends JFrame {

    public TestTablero() {
        setTitle("Test Tablero");
        setSize(300, 300); // Tamaño de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear el panel con GridLayout 3x3
        JPanel gridPanel = new JPanel(new GridLayout(3, 3));

        // Agregar elementos o imágenes a las casillas
        // En este ejemplo, simplemente agregamos etiquetas con números para identificar las casillas
        for (int i = 0; i < 9; i++) { // Iteramos 9 veces para llenar todas las casillas
            JLabel label = new JLabel(String.valueOf(i + 1)); // Cada etiqueta muestra un número
            gridPanel.add(label);

            // Si quisieras agregar una imagen a una casilla específica, podrías hacerlo aquí
            // Por ejemplo, para agregar una imagen a la primera casilla:
            // if (i == 0) {
            //     ImageIcon imageIcon = new ImageIcon("ruta/a/tu/imagen.jpg"); // Reemplaza la ruta con la tuya
            //     label.setIcon(imageIcon);
            // }
        }

        // Agregar el panel con la cuadrícula al frame
        add(gridPanel);

        setVisible(true);
    }
}

