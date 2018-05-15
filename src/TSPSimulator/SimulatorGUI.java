package TSPSimulator;

import TSPSimulator.Components.HeaderPanel;
import TSPSimulator.Components.SimulatorPanel;
import TSPSimulator.Components.SimulatorRenderer;
import TSPSimulator.Database.Product;
import TSPSimulator.Models.Order;
import TSPSimulator.Simulators.*;
import com.google.gson.Gson;
import com.oracle.tools.packager.IOUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class SimulatorGUI extends JFrame implements ActionListener, ListSelectionListener {
    private SimulatorPanel pnlSimulator;
    private JList<Simulator> liSimulators;
    private TextField tbSizeX;
    private TextField tbSizeY;
    private SimulatorRenderer _selectedCellRenderer;
    private JButton btnStartCancelSimulation;
    private JButton btnChangeSize;
    private Panel pnlUserInteraction;
    private JButton btnInputOrderFile;

    public SimulatorGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new FlowLayout());
        setSize(800, 500);

        pnlUserInteraction = new Panel();
        pnlUserInteraction.setLayout(new GridLayout(0, 2, 10, 10));

        addSimulatorComponents();
        addAdjustSizeComponents();
        addOrderFileInputComponents();

        add(pnlUserInteraction);
        setVisible(true);
    }

    private void addOrderFileInputComponents() {
        btnInputOrderFile = new JButton("Bestand");
        btnInputOrderFile.addActionListener(this);
        HeaderPanel panel = new HeaderPanel("Invoeren Order", btnInputOrderFile);
        pnlUserInteraction.add(panel);
    }

    private void addSimulatorComponents() {
        btnStartCancelSimulation = new JButton("Simulate");
        btnStartCancelSimulation.addActionListener(this);
        add(btnStartCancelSimulation);

        pnlSimulator = new SimulatorPanel();
        add(pnlSimulator);

        _selectedCellRenderer = new SimulatorRenderer();
        liSimulators = new JList<>(new Simulator[]{new SimulatorGreedy(), new SimulatorSmartGreedy(), new SimulatorTwoOpt(), new SimulatorBruteForce(),});
        liSimulators.setSelectedIndex(0);
        liSimulators.setCellRenderer(_selectedCellRenderer);
        liSimulators.addListSelectionListener(this);
        pnlUserInteraction.add(new HeaderPanel("Algorithmes", btnStartCancelSimulation, liSimulators));

        List<Color> colors = pnlSimulator.setSimulators(liSimulators.getSelectedValuesList());
        pnlSimulator.registerOnEndSimulationCallback(this::onEndSimulation);
        pnlSimulator.registerOnStartSimulationCallback(this::onStartSimulation);
        _selectedCellRenderer.setColors(colors);
    }


    /**
     * Adds the GUI components that allow the user to adjust the size of the grid.
     * This includes two text boxes for the X and Y and a button.
     */
    private void addAdjustSizeComponents() {
        tbSizeX = new TextField();
        tbSizeX.addActionListener(this);

        tbSizeY = new TextField();
        tbSizeY.addActionListener(this);

        btnChangeSize = new JButton("Wijzigen");
        btnChangeSize.addActionListener(this);
        HeaderPanel sizePanel = new HeaderPanel("Grootte panel", btnChangeSize, new JLabel("X - Y"), tbSizeX, tbSizeY);
        pnlUserInteraction.add(sizePanel);
    }

    private void onStartSimulation() {
        btnStartCancelSimulation.setText("Cancel");
    }

    public void onEndSimulation() {
        btnStartCancelSimulation.setText("Simulate");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == tbSizeX) {
            pnlSimulator.setSizeX(Integer.valueOf(tbSizeX.getText()));
        } else if (e.getSource() == tbSizeY) {
            pnlSimulator.setSizeY(Integer.valueOf(tbSizeY.getText()));
        } else if (e.getSource() == btnStartCancelSimulation) {
            if (btnStartCancelSimulation.getText() == "Cancel") {
                pnlSimulator.cancelSimulations();
            }
            else {
                pnlSimulator.simulateAndLog();
            }
        } else if (e.getSource() == btnChangeSize) {
            pnlSimulator.setSizeX(Integer.valueOf(tbSizeX.getText()));
            pnlSimulator.setSizeY(Integer.valueOf(tbSizeY.getText()));
        }
        else if (e.getSource() == btnInputOrderFile) {
            selectGridFromOrder();
        }
        else {
            System.out.println("Unknown event");
        }
    }

    private void selectGridFromOrder() {
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();

        //In response to a button click:
        int returnVal = fc.showOpenDialog(this);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File f = fc.getSelectedFile();
//        Files.readAllBytes(f);
        try {
            try(FileInputStream br = new FileInputStream(f.getAbsolutePath())) {
                String json = new String(br.readAllBytes(), "UTF-8");
                Gson deserialize = new Gson();
                Order order = deserialize.fromJson(json, Order.class);
                System.out.println(order.getOrder());
                List<TSPSimulator.Models.Product> products = Product.Get().FindProductsForOder(order.getOrder());
                pnlSimulator.clearClicked();
                pnlSimulator.setSize(5, 5);
                for (TSPSimulator.Models.Product product : products) {
                    pnlSimulator.setIsClicked((int)product.getLocation().getX(), (int)product.getLocation().getY(), true);
                }
            }
        }
        catch (FileNotFoundException e) {

        }
        catch (IOException e) {

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
