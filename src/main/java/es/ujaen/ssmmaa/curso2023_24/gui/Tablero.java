/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package es.ujaen.ssmmaa.curso2023_24.gui;

import jade.util.leap.ArrayList;
import es.ujaen.ssmmaa.Vocabulario.TipoAgenteJugador;
import java.awt.Color;
import es.ujaen.ssmmaa.Vocabulario.Objetivo;


/**
 * @author salvador
 * 
 * Comportamientos autonomos:
 * La intensidad inicial de una zona se calcula automaticamente en base a la lista de objetivos recibida
 * El tablero muestra quien ha ganado una zona de forma automatica atendiendo a las estrategias asignadas a ella
 * 
 * Comportamientos manuales:
 * Para establecer un nick distinto al predefinido, setNick(TipoAgenteJugador tipoAgente, String nick)
 * Para cambiar las puntuaciones mostradas, usar setPuntuacion(TipoAgenteJugador tipoAgente, int puntuacion)
 * Para indicar que un agente ha logrado explorar una casilla, explorar(TipoAgenteJugador tipoAgente, int casilla)
 * Para asignar una estrategia, asignarEstrategia(TipoAgenteJugador tipoAgente, int casilla, String estrategia)
 * 
 */
public class Tablero extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     * @param objetivos
     */
    public Tablero(ArrayList objetivos) {
        initComponents();
        for (int i = 1; i <= 9; i++){
            setIntensidad((Objetivo)objetivos.get(i-1), i);
        }
    }
    
    public void setNick(TipoAgenteJugador tipoAgente, String nick){
        if (tipoAgente == TipoAgenteJugador.ESPIA) Espia.setText(nick);
        else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) Seguridad.setText(nick);
    }
    
    public void setPuntuacion(TipoAgenteJugador tipoAgente, int puntuacion){
        if (tipoAgente == TipoAgenteJugador.ESPIA) Puntos_esp.setText(String.valueOf(puntuacion));
        else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) Puntos_seg.setText(String.valueOf(puntuacion));
    }
    
    public void explorar(TipoAgenteJugador tipoAgente, int casilla){
        switch (casilla){
            case 1 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) visibilidad1esp.setBackground(Color.red);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) visibilidad1seg.setBackground(Color.blue);
            }
            case 2 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) visibilidad2esp.setBackground(Color.red);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) visibilidad2seg.setBackground(Color.blue);
            }
            case 3 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) visibilidad3esp.setBackground(Color.red);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) visibilidad3seg.setBackground(Color.blue);
            }
            case 4 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) visibilidad4esp.setBackground(Color.red);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) visibilidad4seg.setBackground(Color.blue);
            }
            case 5 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) visibilidad5esp.setBackground(Color.red);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) visibilidad5seg.setBackground(Color.blue);
            }
            case 6 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) visibilidad6esp.setBackground(Color.red);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) visibilidad6seg.setBackground(Color.blue);
            }
            case 7 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) visibilidad7esp.setBackground(Color.red);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) visibilidad7seg.setBackground(Color.blue);
            }
            case 8 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) visibilidad8esp.setBackground(Color.red);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) visibilidad8seg.setBackground(Color.blue);
            }
            case 9 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) visibilidad9esp.setBackground(Color.red);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) visibilidad9seg.setBackground(Color.blue);
            }
        }
    }
    
    public void asignarEstrategia(TipoAgenteJugador tipoAgente, int casilla, String estrategia){
        switch (casilla){
            case 1 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) estrategia1esp.setText(estrategia);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) estrategia1seg.setText(estrategia);
            }
            case 2 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) estrategia2esp.setText(estrategia);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) estrategia2seg.setText(estrategia);
            }
            case 3 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) estrategia3esp.setText(estrategia);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) estrategia3seg.setText(estrategia);
            }
            case 4 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) estrategia4esp.setText(estrategia);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) estrategia4seg.setText(estrategia);
            }
            case 5 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) estrategia5esp.setText(estrategia);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) estrategia5seg.setText(estrategia);
            }
            case 6 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) estrategia6esp.setText(estrategia);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) estrategia6seg.setText(estrategia);
            }
            case 7 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) estrategia7esp.setText(estrategia);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) estrategia7seg.setText(estrategia);
            }
            case 8 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) estrategia8esp.setText(estrategia);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) estrategia8seg.setText(estrategia);
            }
            case 9 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) estrategia9esp.setText(estrategia);
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) estrategia9seg.setText(estrategia);
            }
        }
        checkWin(casilla);
    }
    
    //Devuelve un azul de igual intensidad que el color recibido
    public Color switchAzul(Color actual){
        int r;
        int g;
        int b;
        int mayor = actual.getRed();
        int menor = actual.getRed();
        if (actual.getGreen() > mayor) mayor = actual.getGreen();
        if (actual.getGreen() < menor) menor = actual.getGreen();
        if (actual.getBlue() > mayor) mayor = actual.getBlue();
        if (actual.getBlue() < menor) menor = actual.getBlue();
        r = menor;
        g = menor;
        b = mayor;
        return new Color(r,g,b);
    }
    //Devuelve un rojo de igual intensidad que el color recibido
    public Color switchRojo(Color actual){
        int r;
        int g;
        int b;
        int mayor = actual.getRed();
        int menor = actual.getRed();
        if (actual.getGreen() > mayor) mayor = actual.getGreen();
        if (actual.getGreen() < menor) menor = actual.getGreen();
        if (actual.getBlue() > mayor) mayor = actual.getBlue();
        if (actual.getBlue() < menor) menor = actual.getBlue();
        r = mayor;
        g = menor;
        b = menor;
        return new Color(r,g,b);
    }
    
    //Comprueba si por ahora un agente ha ganado una zona determinada
    private void checkWin(int casilla){
        String NO_ASIGNADO = "N/A";
        String estrategiaEsp = NO_ASIGNADO;
        String estrategiaSeg = NO_ASIGNADO;
        boolean espiaAsignado;
        boolean seguridadAsignado;
        switch (casilla){
            case 1 -> {
                estrategiaEsp = estrategia1esp.getText();
                estrategiaSeg = estrategia1seg.getText();
            }
            case 2 -> {
                estrategiaEsp = estrategia2esp.getText();
                estrategiaSeg = estrategia2seg.getText();
            }
            case 3 -> {
                estrategiaEsp = estrategia3esp.getText();
                estrategiaSeg = estrategia3seg.getText();
            }
            case 4 -> {
                estrategiaEsp = estrategia4esp.getText();
                estrategiaSeg = estrategia4seg.getText();
            }
            case 5 -> {
                estrategiaEsp = estrategia5esp.getText();
                estrategiaSeg = estrategia5seg.getText();
            }
            case 6 -> {
                estrategiaEsp = estrategia6esp.getText();
                estrategiaSeg = estrategia6seg.getText();
            }
            case 7 -> {
                estrategiaEsp = estrategia7esp.getText();
                estrategiaSeg = estrategia7seg.getText();
            }
            case 8 -> {
                estrategiaEsp = estrategia8esp.getText();
                estrategiaSeg = estrategia8seg.getText();
            }
            case 9 -> {
                estrategiaEsp = estrategia9esp.getText();
                estrategiaSeg = estrategia9seg.getText();
            }
        }
        espiaAsignado = estrategiaEsp.equals(NO_ASIGNADO);
        seguridadAsignado = estrategiaSeg.equals(NO_ASIGNADO);
        if (espiaAsignado && !seguridadAsignado) ganar(TipoAgenteJugador.ESPIA, casilla);
        else if (!espiaAsignado && seguridadAsignado) ganar(TipoAgenteJugador.SEGURIDAD, casilla);
        else if (espiaAsignado && seguridadAsignado){
            switch (estrategiaEsp) {
                case "EO" -> {
                    if (estrategiaSeg.equals("NE")) ganar(TipoAgenteJugador.SEGURIDAD, casilla);
                    else if (!estrategiaSeg.equals("IC")) ganar(TipoAgenteJugador.ESPIA, casilla);
                }
                case "PI" -> {
                    if (estrategiaSeg.equals("NE")) ganar(TipoAgenteJugador.SEGURIDAD, casilla);
                    else if (estrategiaSeg.equals("IC") || estrategiaSeg.equals("FD")) ganar(TipoAgenteJugador.ESPIA, casilla);
                }
                case "EE" -> {
                    if (!estrategiaSeg.equals("NE")) ganar(TipoAgenteJugador.SEGURIDAD, casilla);
                    else if (estrategiaSeg.equals("IC")) ganar(TipoAgenteJugador.ESPIA, casilla);
                }
                case "EI" -> {
                    if (estrategiaSeg.equals("DI") || estrategiaSeg.equals("NE")) ganar(TipoAgenteJugador.SEGURIDAD, casilla);
                    else if (estrategiaSeg.equals("IC") || estrategiaSeg.equals("CI")) ganar(TipoAgenteJugador.ESPIA, casilla);
                }
                case "DE" -> {
                    if (estrategiaSeg.equals("IC") || estrategiaSeg.equals("CI")) ganar(TipoAgenteJugador.SEGURIDAD, casilla);
                    else ganar(TipoAgenteJugador.ESPIA, casilla);
                }
                default -> {
                }
            }
        }
    }
    
    private void ganar(TipoAgenteJugador tipoAgente, int casilla){
        switch (casilla){
            case 1 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) Casilla1.setBackground(switchAzul(Casilla1.getBackground()));
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) Casilla1.setBackground(switchRojo(Casilla1.getBackground()));
            }
            case 2 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) Casilla2.setBackground(switchAzul(Casilla2.getBackground()));
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) Casilla2.setBackground(switchRojo(Casilla2.getBackground()));
            }
            case 3 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) Casilla3.setBackground(switchAzul(Casilla3.getBackground()));
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) Casilla3.setBackground(switchRojo(Casilla3.getBackground()));
            }
            case 4 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) Casilla4.setBackground(switchAzul(Casilla4.getBackground()));
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) Casilla4.setBackground(switchRojo(Casilla4.getBackground()));
            }
            case 5 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) Casilla5.setBackground(switchAzul(Casilla5.getBackground()));
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) Casilla5.setBackground(switchRojo(Casilla5.getBackground()));
            }
            case 6 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) Casilla6.setBackground(switchAzul(Casilla6.getBackground()));
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) Casilla6.setBackground(switchRojo(Casilla6.getBackground()));
            }
            case 7 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) Casilla7.setBackground(switchAzul(Casilla7.getBackground()));
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) Casilla7.setBackground(switchRojo(Casilla7.getBackground()));
            }
            case 8 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) Casilla8.setBackground(switchAzul(Casilla8.getBackground()));
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) Casilla8.setBackground(switchRojo(Casilla8.getBackground()));
            }
            case 9 -> {
                if (tipoAgente == TipoAgenteJugador.ESPIA) Casilla9.setBackground(switchAzul(Casilla9.getBackground()));
                else if (tipoAgente == TipoAgenteJugador.SEGURIDAD) Casilla9.setBackground(switchRojo(Casilla9.getBackground()));
            }
        }
    }
    
    //Solo constructor
    private void setIntensidad(Objetivo objetivo, int casilla){
        int r = 255;
        int g = 255;
        int b = 255;
        switch (objetivo) {
            case BAJA_PRIORIDAD -> {
                r = 240;
                b = 240;
            }
            case PRIORIDAD_NORMAL -> {
                r = 200;
                b = 200;
            }
            case ALTA_PRIORIDAD -> {
                r = 160;
                b = 160;
            }
            case MAXIMA_PRIORIDAD -> {
                r = 100;
                b = 100;
            }
        }
        Color color = new Color(r, g, b);
        switch (casilla){
            case 1 -> {
                Casilla1.setBackground(color);
            }
            case 2 -> {
                Casilla2.setBackground(color);
            }
            case 3 -> {
                Casilla3.setBackground(color);
            }
            case 4 -> {
                Casilla4.setBackground(color);
            }
            case 5 -> {
                Casilla5.setBackground(color);
            }
            case 6 -> {
                Casilla6.setBackground(color);
            }
            case 7 -> {
                Casilla7.setBackground(color);
            }
            case 8 -> {
                Casilla8.setBackground(color);
            }
            case 9 -> {
                Casilla9.setBackground(color);
            }
        }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Fondo = new javax.swing.JPanel();
        Titulo = new javax.swing.JLabel();
        Seguridad = new javax.swing.JLabel();
        Puntos_seg = new javax.swing.JLabel();
        vs = new javax.swing.JLabel();
        Espia = new javax.swing.JLabel();
        Puntos_esp = new javax.swing.JLabel();
        Casilla1 = new javax.swing.JPanel();
        visibilidad1esp = new javax.swing.JPanel();
        visibilidad1seg = new javax.swing.JPanel();
        estrategia1esp = new javax.swing.JLabel();
        estrategia1seg = new javax.swing.JLabel();
        Casilla2 = new javax.swing.JPanel();
        visibilidad2esp = new javax.swing.JPanel();
        visibilidad2seg = new javax.swing.JPanel();
        estrategia2esp = new javax.swing.JLabel();
        estrategia2seg = new javax.swing.JLabel();
        Casilla3 = new javax.swing.JPanel();
        visibilidad3esp = new javax.swing.JPanel();
        visibilidad3seg = new javax.swing.JPanel();
        estrategia3esp = new javax.swing.JLabel();
        estrategia3seg = new javax.swing.JLabel();
        Casilla4 = new javax.swing.JPanel();
        visibilidad4esp = new javax.swing.JPanel();
        visibilidad4seg = new javax.swing.JPanel();
        estrategia4esp = new javax.swing.JLabel();
        estrategia4seg = new javax.swing.JLabel();
        Casilla5 = new javax.swing.JPanel();
        visibilidad5esp = new javax.swing.JPanel();
        visibilidad5seg = new javax.swing.JPanel();
        estrategia5esp = new javax.swing.JLabel();
        estrategia5seg = new javax.swing.JLabel();
        Casilla6 = new javax.swing.JPanel();
        visibilidad6esp = new javax.swing.JPanel();
        visibilidad6seg = new javax.swing.JPanel();
        estrategia6esp = new javax.swing.JLabel();
        estrategia6seg = new javax.swing.JLabel();
        Casilla7 = new javax.swing.JPanel();
        visibilidad7esp = new javax.swing.JPanel();
        visibilidad7seg = new javax.swing.JPanel();
        estrategia7esp = new javax.swing.JLabel();
        estrategia7seg = new javax.swing.JLabel();
        Casilla8 = new javax.swing.JPanel();
        visibilidad8esp = new javax.swing.JPanel();
        visibilidad8seg = new javax.swing.JPanel();
        estrategia8esp = new javax.swing.JLabel();
        estrategia8seg = new javax.swing.JLabel();
        Casilla9 = new javax.swing.JPanel();
        visibilidad9esp = new javax.swing.JPanel();
        visibilidad9seg = new javax.swing.JPanel();
        estrategia9esp = new javax.swing.JLabel();
        estrategia9seg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Fondo.setMaximumSize(new java.awt.Dimension(400, 500));
        Fondo.setMinimumSize(new java.awt.Dimension(400, 500));
        Fondo.setPreferredSize(new java.awt.Dimension(400, 500));

        Titulo.setFont(new java.awt.Font("Cantarell", 0, 24)); // NOI18N
        Titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Titulo.setText("Partida");
        Titulo.setFocusable(false);
        Titulo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Titulo.setMaximumSize(new java.awt.Dimension(100, 40));
        Titulo.setMinimumSize(new java.awt.Dimension(100, 40));
        Titulo.setPreferredSize(new java.awt.Dimension(100, 40));

        Seguridad.setFont(new java.awt.Font("Cantarell", 0, 24)); // NOI18N
        Seguridad.setForeground(new java.awt.Color(0, 0, 160));
        Seguridad.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Seguridad.setText("Seguridad");
        Seguridad.setFocusable(false);
        Seguridad.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Seguridad.setMaximumSize(new java.awt.Dimension(100, 40));
        Seguridad.setMinimumSize(new java.awt.Dimension(100, 40));
        Seguridad.setPreferredSize(new java.awt.Dimension(100, 40));

        Puntos_seg.setFont(new java.awt.Font("Cantarell", 0, 24)); // NOI18N
        Puntos_seg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Puntos_seg.setText("0");
        Puntos_seg.setFocusable(false);
        Puntos_seg.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Puntos_seg.setMaximumSize(new java.awt.Dimension(100, 40));
        Puntos_seg.setMinimumSize(new java.awt.Dimension(100, 40));
        Puntos_seg.setPreferredSize(new java.awt.Dimension(100, 40));

        vs.setFont(new java.awt.Font("Cantarell", 0, 20)); // NOI18N
        vs.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        vs.setText("vs");

        Espia.setFont(new java.awt.Font("Cantarell", 0, 24)); // NOI18N
        Espia.setForeground(new java.awt.Color(160, 0, 0));
        Espia.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Espia.setText("Espia");
        Espia.setFocusable(false);
        Espia.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Espia.setMaximumSize(new java.awt.Dimension(100, 40));
        Espia.setMinimumSize(new java.awt.Dimension(100, 40));
        Espia.setPreferredSize(new java.awt.Dimension(100, 40));

        Puntos_esp.setFont(new java.awt.Font("Cantarell", 0, 24)); // NOI18N
        Puntos_esp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Puntos_esp.setText("0");
        Puntos_esp.setFocusable(false);
        Puntos_esp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Puntos_esp.setMaximumSize(new java.awt.Dimension(100, 40));
        Puntos_esp.setMinimumSize(new java.awt.Dimension(100, 40));
        Puntos_esp.setPreferredSize(new java.awt.Dimension(100, 40));

        Casilla1.setBackground(new java.awt.Color(220, 255, 220));
        Casilla1.setMaximumSize(new java.awt.Dimension(120, 120));
        Casilla1.setMinimumSize(new java.awt.Dimension(120, 120));
        Casilla1.setPreferredSize(new java.awt.Dimension(120, 120));
        Casilla1.setRequestFocusEnabled(false);

        visibilidad1esp.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad1espLayout = new javax.swing.GroupLayout(visibilidad1esp);
        visibilidad1esp.setLayout(visibilidad1espLayout);
        visibilidad1espLayout.setHorizontalGroup(
            visibilidad1espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad1espLayout.setVerticalGroup(
            visibilidad1espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        visibilidad1seg.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad1segLayout = new javax.swing.GroupLayout(visibilidad1seg);
        visibilidad1seg.setLayout(visibilidad1segLayout);
        visibilidad1segLayout.setHorizontalGroup(
            visibilidad1segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad1segLayout.setVerticalGroup(
            visibilidad1segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        estrategia1esp.setText("N/A");

        estrategia1seg.setText("N/A");

        javax.swing.GroupLayout Casilla1Layout = new javax.swing.GroupLayout(Casilla1);
        Casilla1.setLayout(Casilla1Layout);
        Casilla1Layout.setHorizontalGroup(
            Casilla1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Casilla1Layout.createSequentialGroup()
                        .addComponent(visibilidad1esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(visibilidad1seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Casilla1Layout.createSequentialGroup()
                        .addComponent(estrategia1esp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(estrategia1seg)))
                .addContainerGap())
        );
        Casilla1Layout.setVerticalGroup(
            Casilla1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visibilidad1seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(visibilidad1esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addGroup(Casilla1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(estrategia1esp)
                    .addComponent(estrategia1seg))
                .addContainerGap())
        );

        Casilla2.setBackground(new java.awt.Color(220, 255, 220));
        Casilla2.setMaximumSize(new java.awt.Dimension(120, 120));
        Casilla2.setMinimumSize(new java.awt.Dimension(120, 120));
        Casilla2.setRequestFocusEnabled(false);

        visibilidad2esp.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad2espLayout = new javax.swing.GroupLayout(visibilidad2esp);
        visibilidad2esp.setLayout(visibilidad2espLayout);
        visibilidad2espLayout.setHorizontalGroup(
            visibilidad2espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad2espLayout.setVerticalGroup(
            visibilidad2espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        visibilidad2seg.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad2segLayout = new javax.swing.GroupLayout(visibilidad2seg);
        visibilidad2seg.setLayout(visibilidad2segLayout);
        visibilidad2segLayout.setHorizontalGroup(
            visibilidad2segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad2segLayout.setVerticalGroup(
            visibilidad2segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        estrategia2esp.setText("N/A");

        estrategia2seg.setText("N/A");

        javax.swing.GroupLayout Casilla2Layout = new javax.swing.GroupLayout(Casilla2);
        Casilla2.setLayout(Casilla2Layout);
        Casilla2Layout.setHorizontalGroup(
            Casilla2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Casilla2Layout.createSequentialGroup()
                        .addComponent(visibilidad2esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(visibilidad2seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Casilla2Layout.createSequentialGroup()
                        .addComponent(estrategia2esp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(estrategia2seg)))
                .addContainerGap())
        );
        Casilla2Layout.setVerticalGroup(
            Casilla2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visibilidad2seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(visibilidad2esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addGroup(Casilla2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(estrategia2esp)
                    .addComponent(estrategia2seg))
                .addContainerGap())
        );

        Casilla3.setBackground(new java.awt.Color(220, 255, 220));
        Casilla3.setMaximumSize(new java.awt.Dimension(120, 120));
        Casilla3.setMinimumSize(new java.awt.Dimension(120, 120));
        Casilla3.setRequestFocusEnabled(false);

        visibilidad3esp.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad3espLayout = new javax.swing.GroupLayout(visibilidad3esp);
        visibilidad3esp.setLayout(visibilidad3espLayout);
        visibilidad3espLayout.setHorizontalGroup(
            visibilidad3espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad3espLayout.setVerticalGroup(
            visibilidad3espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        visibilidad3seg.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad3segLayout = new javax.swing.GroupLayout(visibilidad3seg);
        visibilidad3seg.setLayout(visibilidad3segLayout);
        visibilidad3segLayout.setHorizontalGroup(
            visibilidad3segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad3segLayout.setVerticalGroup(
            visibilidad3segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        estrategia3esp.setText("N/A");

        estrategia3seg.setText("N/A");

        javax.swing.GroupLayout Casilla3Layout = new javax.swing.GroupLayout(Casilla3);
        Casilla3.setLayout(Casilla3Layout);
        Casilla3Layout.setHorizontalGroup(
            Casilla3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Casilla3Layout.createSequentialGroup()
                        .addComponent(visibilidad3esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(visibilidad3seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Casilla3Layout.createSequentialGroup()
                        .addComponent(estrategia3esp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(estrategia3seg)))
                .addContainerGap())
        );
        Casilla3Layout.setVerticalGroup(
            Casilla3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visibilidad3seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(visibilidad3esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addGroup(Casilla3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(estrategia3esp)
                    .addComponent(estrategia3seg))
                .addContainerGap())
        );

        Casilla4.setBackground(new java.awt.Color(220, 255, 220));
        Casilla4.setMaximumSize(new java.awt.Dimension(120, 120));
        Casilla4.setMinimumSize(new java.awt.Dimension(120, 120));
        Casilla4.setRequestFocusEnabled(false);

        visibilidad4esp.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad4espLayout = new javax.swing.GroupLayout(visibilidad4esp);
        visibilidad4esp.setLayout(visibilidad4espLayout);
        visibilidad4espLayout.setHorizontalGroup(
            visibilidad4espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad4espLayout.setVerticalGroup(
            visibilidad4espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        visibilidad4seg.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad4segLayout = new javax.swing.GroupLayout(visibilidad4seg);
        visibilidad4seg.setLayout(visibilidad4segLayout);
        visibilidad4segLayout.setHorizontalGroup(
            visibilidad4segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad4segLayout.setVerticalGroup(
            visibilidad4segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        estrategia4esp.setText("N/A");

        estrategia4seg.setText("N/A");

        javax.swing.GroupLayout Casilla4Layout = new javax.swing.GroupLayout(Casilla4);
        Casilla4.setLayout(Casilla4Layout);
        Casilla4Layout.setHorizontalGroup(
            Casilla4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Casilla4Layout.createSequentialGroup()
                        .addComponent(visibilidad4esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(visibilidad4seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Casilla4Layout.createSequentialGroup()
                        .addComponent(estrategia4esp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(estrategia4seg)))
                .addContainerGap())
        );
        Casilla4Layout.setVerticalGroup(
            Casilla4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visibilidad4seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(visibilidad4esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addGroup(Casilla4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(estrategia4esp)
                    .addComponent(estrategia4seg))
                .addContainerGap())
        );

        Casilla5.setBackground(new java.awt.Color(220, 255, 220));
        Casilla5.setMaximumSize(new java.awt.Dimension(120, 120));
        Casilla5.setMinimumSize(new java.awt.Dimension(120, 120));
        Casilla5.setRequestFocusEnabled(false);

        visibilidad5esp.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad5espLayout = new javax.swing.GroupLayout(visibilidad5esp);
        visibilidad5esp.setLayout(visibilidad5espLayout);
        visibilidad5espLayout.setHorizontalGroup(
            visibilidad5espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad5espLayout.setVerticalGroup(
            visibilidad5espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        visibilidad5seg.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad5segLayout = new javax.swing.GroupLayout(visibilidad5seg);
        visibilidad5seg.setLayout(visibilidad5segLayout);
        visibilidad5segLayout.setHorizontalGroup(
            visibilidad5segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad5segLayout.setVerticalGroup(
            visibilidad5segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        estrategia5esp.setText("N/A");

        estrategia5seg.setText("N/A");

        javax.swing.GroupLayout Casilla5Layout = new javax.swing.GroupLayout(Casilla5);
        Casilla5.setLayout(Casilla5Layout);
        Casilla5Layout.setHorizontalGroup(
            Casilla5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Casilla5Layout.createSequentialGroup()
                        .addComponent(visibilidad5esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(visibilidad5seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Casilla5Layout.createSequentialGroup()
                        .addComponent(estrategia5esp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(estrategia5seg)))
                .addContainerGap())
        );
        Casilla5Layout.setVerticalGroup(
            Casilla5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visibilidad5seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(visibilidad5esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addGroup(Casilla5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(estrategia5esp)
                    .addComponent(estrategia5seg))
                .addContainerGap())
        );

        Casilla6.setBackground(new java.awt.Color(220, 255, 220));
        Casilla6.setMaximumSize(new java.awt.Dimension(120, 120));
        Casilla6.setMinimumSize(new java.awt.Dimension(120, 120));
        Casilla6.setRequestFocusEnabled(false);

        visibilidad6esp.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad6espLayout = new javax.swing.GroupLayout(visibilidad6esp);
        visibilidad6esp.setLayout(visibilidad6espLayout);
        visibilidad6espLayout.setHorizontalGroup(
            visibilidad6espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad6espLayout.setVerticalGroup(
            visibilidad6espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        visibilidad6seg.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad6segLayout = new javax.swing.GroupLayout(visibilidad6seg);
        visibilidad6seg.setLayout(visibilidad6segLayout);
        visibilidad6segLayout.setHorizontalGroup(
            visibilidad6segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad6segLayout.setVerticalGroup(
            visibilidad6segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        estrategia6esp.setText("N/A");

        estrategia6seg.setText("N/A");

        javax.swing.GroupLayout Casilla6Layout = new javax.swing.GroupLayout(Casilla6);
        Casilla6.setLayout(Casilla6Layout);
        Casilla6Layout.setHorizontalGroup(
            Casilla6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Casilla6Layout.createSequentialGroup()
                        .addComponent(visibilidad6esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(visibilidad6seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Casilla6Layout.createSequentialGroup()
                        .addComponent(estrategia6esp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(estrategia6seg)))
                .addContainerGap())
        );
        Casilla6Layout.setVerticalGroup(
            Casilla6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visibilidad6seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(visibilidad6esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addGroup(Casilla6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(estrategia6esp)
                    .addComponent(estrategia6seg))
                .addContainerGap())
        );

        Casilla7.setBackground(new java.awt.Color(220, 255, 220));
        Casilla7.setMaximumSize(new java.awt.Dimension(120, 120));
        Casilla7.setMinimumSize(new java.awt.Dimension(120, 120));
        Casilla7.setRequestFocusEnabled(false);

        visibilidad7esp.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad7espLayout = new javax.swing.GroupLayout(visibilidad7esp);
        visibilidad7esp.setLayout(visibilidad7espLayout);
        visibilidad7espLayout.setHorizontalGroup(
            visibilidad7espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad7espLayout.setVerticalGroup(
            visibilidad7espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        visibilidad7seg.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad7segLayout = new javax.swing.GroupLayout(visibilidad7seg);
        visibilidad7seg.setLayout(visibilidad7segLayout);
        visibilidad7segLayout.setHorizontalGroup(
            visibilidad7segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad7segLayout.setVerticalGroup(
            visibilidad7segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        estrategia7esp.setText("N/A");

        estrategia7seg.setText("N/A");

        javax.swing.GroupLayout Casilla7Layout = new javax.swing.GroupLayout(Casilla7);
        Casilla7.setLayout(Casilla7Layout);
        Casilla7Layout.setHorizontalGroup(
            Casilla7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Casilla7Layout.createSequentialGroup()
                        .addComponent(visibilidad7esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(visibilidad7seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Casilla7Layout.createSequentialGroup()
                        .addComponent(estrategia7esp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(estrategia7seg)))
                .addContainerGap())
        );
        Casilla7Layout.setVerticalGroup(
            Casilla7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visibilidad7seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(visibilidad7esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addGroup(Casilla7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(estrategia7esp)
                    .addComponent(estrategia7seg))
                .addContainerGap())
        );

        Casilla8.setBackground(new java.awt.Color(220, 255, 220));
        Casilla8.setMaximumSize(new java.awt.Dimension(120, 120));
        Casilla8.setMinimumSize(new java.awt.Dimension(120, 120));
        Casilla8.setRequestFocusEnabled(false);

        visibilidad8esp.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad8espLayout = new javax.swing.GroupLayout(visibilidad8esp);
        visibilidad8esp.setLayout(visibilidad8espLayout);
        visibilidad8espLayout.setHorizontalGroup(
            visibilidad8espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad8espLayout.setVerticalGroup(
            visibilidad8espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        visibilidad8seg.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad8segLayout = new javax.swing.GroupLayout(visibilidad8seg);
        visibilidad8seg.setLayout(visibilidad8segLayout);
        visibilidad8segLayout.setHorizontalGroup(
            visibilidad8segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad8segLayout.setVerticalGroup(
            visibilidad8segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        estrategia8esp.setText("N/A");

        estrategia8seg.setText("N/A");

        javax.swing.GroupLayout Casilla8Layout = new javax.swing.GroupLayout(Casilla8);
        Casilla8.setLayout(Casilla8Layout);
        Casilla8Layout.setHorizontalGroup(
            Casilla8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Casilla8Layout.createSequentialGroup()
                        .addComponent(visibilidad8esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(visibilidad8seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Casilla8Layout.createSequentialGroup()
                        .addComponent(estrategia8esp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(estrategia8seg)))
                .addContainerGap())
        );
        Casilla8Layout.setVerticalGroup(
            Casilla8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visibilidad8seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(visibilidad8esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addGroup(Casilla8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(estrategia8esp)
                    .addComponent(estrategia8seg))
                .addContainerGap())
        );

        Casilla9.setBackground(new java.awt.Color(220, 255, 220));
        Casilla9.setMaximumSize(new java.awt.Dimension(120, 120));
        Casilla9.setMinimumSize(new java.awt.Dimension(120, 120));
        Casilla9.setRequestFocusEnabled(false);

        visibilidad9esp.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad9espLayout = new javax.swing.GroupLayout(visibilidad9esp);
        visibilidad9esp.setLayout(visibilidad9espLayout);
        visibilidad9espLayout.setHorizontalGroup(
            visibilidad9espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad9espLayout.setVerticalGroup(
            visibilidad9espLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        visibilidad9seg.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout visibilidad9segLayout = new javax.swing.GroupLayout(visibilidad9seg);
        visibilidad9seg.setLayout(visibilidad9segLayout);
        visibilidad9segLayout.setHorizontalGroup(
            visibilidad9segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        visibilidad9segLayout.setVerticalGroup(
            visibilidad9segLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        estrategia9esp.setText("N/A");

        estrategia9seg.setText("N/A");

        javax.swing.GroupLayout Casilla9Layout = new javax.swing.GroupLayout(Casilla9);
        Casilla9.setLayout(Casilla9Layout);
        Casilla9Layout.setHorizontalGroup(
            Casilla9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Casilla9Layout.createSequentialGroup()
                        .addComponent(visibilidad9esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(visibilidad9seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Casilla9Layout.createSequentialGroup()
                        .addComponent(estrategia9esp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(estrategia9seg)))
                .addContainerGap())
        );
        Casilla9Layout.setVerticalGroup(
            Casilla9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Casilla9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Casilla9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visibilidad9seg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(visibilidad9esp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addGroup(Casilla9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(estrategia9esp)
                    .addComponent(estrategia9seg))
                .addContainerGap())
        );

        javax.swing.GroupLayout FondoLayout = new javax.swing.GroupLayout(Fondo);
        Fondo.setLayout(FondoLayout);
        FondoLayout.setHorizontalGroup(
            FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Titulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(FondoLayout.createSequentialGroup()
                                .addComponent(Puntos_esp, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Puntos_seg, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FondoLayout.createSequentialGroup()
                                .addComponent(Espia, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(vs, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Seguridad, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6))))
                    .addGroup(FondoLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(FondoLayout.createSequentialGroup()
                                .addComponent(Casilla4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Casilla5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Casilla6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(FondoLayout.createSequentialGroup()
                                .addComponent(Casilla1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Casilla2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Casilla3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(FondoLayout.createSequentialGroup()
                                .addComponent(Casilla7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Casilla8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Casilla9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        FondoLayout.setVerticalGroup(
            FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FondoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Titulo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Seguridad, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vs, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Espia, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Puntos_seg, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Puntos_esp, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Casilla1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Casilla3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Casilla2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Casilla4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Casilla6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Casilla5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(FondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Casilla7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Casilla9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Casilla8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Fondo, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Fondo, javax.swing.GroupLayout.PREFERRED_SIZE, 573, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Casilla1;
    private javax.swing.JPanel Casilla2;
    private javax.swing.JPanel Casilla3;
    private javax.swing.JPanel Casilla4;
    private javax.swing.JPanel Casilla5;
    private javax.swing.JPanel Casilla6;
    private javax.swing.JPanel Casilla7;
    private javax.swing.JPanel Casilla8;
    private javax.swing.JPanel Casilla9;
    private javax.swing.JLabel Espia;
    private javax.swing.JPanel Fondo;
    private javax.swing.JLabel Puntos_esp;
    private javax.swing.JLabel Puntos_seg;
    private javax.swing.JLabel Seguridad;
    private javax.swing.JLabel Titulo;
    private javax.swing.JLabel estrategia1esp;
    private javax.swing.JLabel estrategia1seg;
    private javax.swing.JLabel estrategia2esp;
    private javax.swing.JLabel estrategia2seg;
    private javax.swing.JLabel estrategia3esp;
    private javax.swing.JLabel estrategia3seg;
    private javax.swing.JLabel estrategia4esp;
    private javax.swing.JLabel estrategia4seg;
    private javax.swing.JLabel estrategia5esp;
    private javax.swing.JLabel estrategia5seg;
    private javax.swing.JLabel estrategia6esp;
    private javax.swing.JLabel estrategia6seg;
    private javax.swing.JLabel estrategia7esp;
    private javax.swing.JLabel estrategia7seg;
    private javax.swing.JLabel estrategia8esp;
    private javax.swing.JLabel estrategia8seg;
    private javax.swing.JLabel estrategia9esp;
    private javax.swing.JLabel estrategia9seg;
    private javax.swing.JPanel visibilidad1esp;
    private javax.swing.JPanel visibilidad1seg;
    private javax.swing.JPanel visibilidad2esp;
    private javax.swing.JPanel visibilidad2seg;
    private javax.swing.JPanel visibilidad3esp;
    private javax.swing.JPanel visibilidad3seg;
    private javax.swing.JPanel visibilidad4esp;
    private javax.swing.JPanel visibilidad4seg;
    private javax.swing.JPanel visibilidad5esp;
    private javax.swing.JPanel visibilidad5seg;
    private javax.swing.JPanel visibilidad6esp;
    private javax.swing.JPanel visibilidad6seg;
    private javax.swing.JPanel visibilidad7esp;
    private javax.swing.JPanel visibilidad7seg;
    private javax.swing.JPanel visibilidad8esp;
    private javax.swing.JPanel visibilidad8seg;
    private javax.swing.JPanel visibilidad9esp;
    private javax.swing.JPanel visibilidad9seg;
    private javax.swing.JLabel vs;
    // End of variables declaration//GEN-END:variables
}
