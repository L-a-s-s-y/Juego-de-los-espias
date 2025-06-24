package es.ujaen.ssmmaa.curso2023_24.gui;

import es.ujaen.ssmmaa.Vocabulario;
import es.ujaen.ssmmaa.curso2023_24.agentes.AgenteOrganizador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrganizadorJFrame extends JFrame {

    private AgenteOrganizador myAgent;
    private FinalizacionDialog finalizacion;
    private JButton btnSelectFile;
    private JButton btnPartidaPrueba;
    private boolean partidaVisible = false;
    private JPanel panelPrincipal;

    Map<String, DefaultListModel<String>> listaPartidas = new HashMap<>();
    DefaultListModel<String> listaJuegos = new DefaultListModel<>();

    private Selector selector;

    public OrganizadorJFrame(AgenteOrganizador myAgent) {
        this.myAgent = myAgent;
        initComponents();
        this.setTitle(myAgent.getName());
    }

    private void initComponents() {

        this.setLayout(new BorderLayout());

        JLabel topLabel = new JLabel("Organizador Juegos", SwingConstants.CENTER);
        this.add(topLabel, BorderLayout.NORTH);
        panelPrincipal = new JPanel();

        btnSelectFile = new JButton("Cargar Partida");
        panelPrincipal.add(btnSelectFile);

        btnSelectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Partida seleccionada: " + selectedFile.getAbsolutePath());
                }
            }
        });

        //selector = new Selector();
        //panelPrincipal.add(selector);

//        btnPartidaPrueba = new JButton("Partida");
//        panelPrincipal.add(btnPartidaPrueba);
//
//        btnPartidaPrueba.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                new PartidaJFrame("asdf");
//            }
//        });


        //TODO: funcion que pinte el tablero (distribucion de objetivos, exploracion, estrategias)
        // funcion que pinte el panel principal con los juegos activos
        // panel principal scroll panel horizontal
        // dentro del scroll panel se crearan tantos panels como juegos haya
        // dentro de cada panel del juego, habrá que pintar los botones correspondientes a las partidas

        // Generar botones basados en el vector de elementos
//        for (String tablero : myAgent.getListaTableros()) {
//            JButton button = new JButton(tablero);
//            button.addActionListener(e -> openTestTablero());
//            rightPanel.add(button);
//        }

        this.add(panelPrincipal, BorderLayout.CENTER);

        // Botón de finalizar en la parte inferior
        JButton finalizarButton = new JButton("Finalizar");
        finalizarButton.addActionListener(e -> botonFinActionPerformed());
        this.add(finalizarButton, BorderLayout.SOUTH);

        // Configuración inicial
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                botonFinActionPerformed();
            }
        });
    }


    private void botonFinActionPerformed() {
        finalizacion = new FinalizacionDialog(this, true, myAgent);
        finalizacion.setVisible(true);
    }

    public void nuevoJuego(String idJuego){
        //selector.addJuego(idJuego);
    }

    public void crearTablero(String idJuego, String idPartida, String nombreEspia, String nombreSeguridad, ArrayList<Vocabulario.Objetivo> objetivos){
        //selector.addPartida(idPartida);
    }

}
