import Simulators.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimulatorGUI extends JFrame implements ActionListener {
    private SimulatorPanel pnlSimulator;
    private JComboBox<Simulator> cbSimulators;
    private TextField tbSizeX;
    private TextField tbSizeY;

    public SimulatorGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new FlowLayout());
        setSize(800, 500);

        pnlSimulator = new SimulatorPanel();
        add(pnlSimulator);

        cbSimulators = new JComboBox<>();
        cbSimulators.addActionListener(this);
        cbSimulators.addItem(new SimulatorGreedy());
        cbSimulators.addItem(new SimulatorSmartGreedy());
        cbSimulators.addItem(new SimulatorTwoOpt());
        cbSimulators.addItem(new SimulatorBruteForce());
        add(cbSimulators);

        tbSizeX = new TextField();
        tbSizeX.addActionListener(this);
        add(tbSizeX);

        tbSizeY = new TextField();
        tbSizeY.addActionListener(this);
        add(tbSizeY);

        pnlSimulator.setSimulator((Simulator)cbSimulators.getSelectedItem());
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cbSimulators) {
            pnlSimulator.setSimulator((Simulator)cbSimulators.getSelectedItem());
        }
        else if (e.getSource() == tbSizeX) {
            pnlSimulator.setSizeX(Integer.valueOf(tbSizeX.getText()));
        }
        else if (e.getSource() == tbSizeY) {
            pnlSimulator.setSizeY(Integer.valueOf(tbSizeY.getText()));
        }
    }
}
