package es.ujaen.ssmmaa.curso2023_24.gui;

import es.ujaen.ssmmaa.curso2023_24.agentes.AgenteMonitor;
import es.ujaen.ssmmaa.curso2023_24.agentes.AgenteOrganizador;
import jade.domain.FIPAAgentManagement.FailureException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FinalizacionDialog extends JDialog {
    private JButton buttonOK;
    private JButton buttonCancel;

    private JPanel contentPane;
    private AgenteOrganizador myAgent;


    public FinalizacionDialog(Frame parent, boolean modal, AgenteOrganizador a) {
        super(parent, modal);
        initComponents();
        this.myAgent = a;
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.setTitle("Finalizar " + myAgent.getName());

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE

    }

    private void initComponents() {

        buttonOK = new JButton();
        buttonCancel = new JButton();

        setTitle("Finalizar");

        buttonOK.setText("Finalizar");
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    onOK();
                } catch (FailureException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        buttonCancel.setText("Cancelar");
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                onCancel();
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addComponent(buttonCancel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                                .addComponent(buttonOK)
                                .addGap(37, 37, 37))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonOK)
                                        .addComponent(buttonCancel))
                                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }


    private void onOK() throws FailureException, InterruptedException {
        //TODO:
        myAgent.doDelete();
    }

    private void onCancel() {
        // add your code here if necessary
        this.dispose();
    }

}
