import Simulators.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SimulatorGUI extends JFrame implements ActionListener, ListSelectionListener {
    private SimulatorPanel pnlSimulator;
    private JList<Simulator> liSimulators;
    private TextField tbSizeX;
    private TextField tbSizeY;
    private SimulatorRenderer _selectedCellRenderer;
    private JButton btnCancel;

    public SimulatorGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new FlowLayout());
        setSize(800, 500);

        pnlSimulator = new SimulatorPanel();
        add(pnlSimulator);

        _selectedCellRenderer = new SimulatorRenderer();
        liSimulators = new JList<>(new Simulator[]{new SimulatorGreedy(), new SimulatorSmartGreedy(), new SimulatorTwoOpt(), new SimulatorBruteForce(),});
        liSimulators.setSelectedIndex(0);
        liSimulators.setCellRenderer(_selectedCellRenderer);
        liSimulators.addListSelectionListener(this);

        add(liSimulators);

        tbSizeX = new TextField();
        tbSizeX.addActionListener(this);
        add(tbSizeX);

        tbSizeY = new TextField();
        tbSizeY.addActionListener(this);
        add(tbSizeY);

        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        add(btnCancel);

        List<Color> colors = pnlSimulator.setSimulators(liSimulators.getSelectedValuesList());
        _selectedCellRenderer.setColors(colors);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == tbSizeX) {
            pnlSimulator.setSizeX(Integer.valueOf(tbSizeX.getText()));
        } else if (e.getSource() == tbSizeY) {
            pnlSimulator.setSizeY(Integer.valueOf(tbSizeY.getText()));
        } else if (e.getSource() == btnCancel) {
            pnlSimulator.cancelSimulations();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        List<Simulator> selectedSimulators = liSimulators.getSelectedValuesList();
        List<Color> colors = pnlSimulator.setSimulators(selectedSimulators);
        _selectedCellRenderer.setColors(colors);
        liSimulators.repaint();
    }
}
