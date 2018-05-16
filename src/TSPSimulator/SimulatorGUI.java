package TSPSimulator;

import TSPSimulator.Components.HeaderPanel;
import TSPSimulator.Components.JNumberTextField;
import TSPSimulator.Components.SimulatorPanel;
import TSPSimulator.Components.SimulatorRenderer;
import TSPSimulator.Database.DbProduct;
import TSPSimulator.Database.Exceptions.OrderNotFoundException;
import TSPSimulator.Models.Order;
import TSPSimulator.Simulators.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.geometry.Point2D;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SimulatorGUI extends JFrame implements ActionListener, ListSelectionListener {
    //region Components
    private SimulatorPanel pnlSimulator;
    private JList<Simulator> liSimulators;
    private JNumberTextField tbSizeX;
    private JNumberTextField tbSizeY;
    private SimulatorRenderer _selectedCellRenderer;
    private JButton btnStartCancelSimulation;
    private JButton btnChangeSize;
    private Panel pnlUserInteraction;
    private JButton btnInputOrderFile;
    private Timer _timer;
    //endregion

    //region Constants
    final int MAX_GRID_SIZE = 20;
    final int MIN_GRID_SIZE = 5;
    //endregion

    //region Constructors
    public SimulatorGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new FlowLayout());
        setSize(800, 500);

        pnlUserInteraction = new Panel();
        pnlUserInteraction.setLayout(new GridLayout(0, 2, 10, 10));

        addSimulatorComponents();
        addAdjustSizeComponents();
        addOrderFileInputComponents();
        addTimer();

        add(pnlUserInteraction);
        setVisible(true);


    }
    //endregion

    //region ComponentInitialisation

    /**
     * Creates a timer but won't start it.
     * It will be started when a simulation has started and stopped if the simulation is finished before the timer is called.
     * When the timer is called it will ask the user if the simulation should be canceled
     */
    private void addTimer() {
        SimulatorGUI self = this;
        _timer = new Timer(10000, arg0 -> {
            int res = JOptionPane.showConfirmDialog(self, "The simulator has been running for 10 seconds. Would you like to cancel it?", "Cancel?", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                pnlSimulator.cancelSimulations();
            }
        });
        _timer.setRepeats(false);
    }

    /**
     * Adds the components which the user can use to insert a order file.
     */
    private void addOrderFileInputComponents() {
        btnInputOrderFile = new JButton("Bestand");
        btnInputOrderFile.addActionListener(this);
        HeaderPanel panel = new HeaderPanel("Invoeren Order", btnInputOrderFile);
        pnlUserInteraction.add(panel);
    }

    /**
     * Adds the components which the user can use to select and see the result of simulations
     */
    private void addSimulatorComponents() {
        btnStartCancelSimulation = new JButton("Simulate");
        btnStartCancelSimulation.addActionListener(this);
        btnStartCancelSimulation.setEnabled(false);
        add(btnStartCancelSimulation);

        pnlSimulator = new SimulatorPanel();
        add(pnlSimulator);

        _selectedCellRenderer = new SimulatorRenderer();
        liSimulators = new JList<>(new Simulator[]{new SimulatorGreedy(), new SimulatorSmartGreedy(), new SimulatorTwoOpt(), new SimulatorBruteForce(),});
        liSimulators.setSelectedIndex(0);
        liSimulators.setCellRenderer(_selectedCellRenderer);
        liSimulators.addListSelectionListener(this);
        pnlUserInteraction.add(new HeaderPanel("Algorithms", btnStartCancelSimulation, liSimulators));

        List<Color> colors = pnlSimulator.setSimulators(liSimulators.getSelectedValuesList());
        pnlSimulator.registerOnEndSimulationCallback(this::onEndSimulation);
        pnlSimulator.registerOnStartSimulationCallback(this::onStartSimulation);
        pnlSimulator.registerOnSelectionChanged(this::onGridSelectionChanged);
        _selectedCellRenderer.setColors(colors);
    }


    /**
     * Adds the GUI components that allow the user to adjust the size of the grid.
     * This includes two text boxes for the X and Y and a button.
     */
    private void addAdjustSizeComponents() {
        tbSizeX = new JNumberTextField();
        tbSizeX.setText("5");
        tbSizeX.addActionListener(this);

        tbSizeY = new JNumberTextField();
        tbSizeY.setText("5");
        tbSizeY.addActionListener(this);

        btnChangeSize = new JButton("Wijzigen");
        btnChangeSize.addActionListener(this);
        HeaderPanel sizePanel = new HeaderPanel("Grootte panel", btnChangeSize, new JLabel("X - Y"), tbSizeX, tbSizeY);
        pnlUserInteraction.add(sizePanel);
    }
    //endregion

    //region PanelEvents

    /**
     * Called when the user selects or deselects squares on the grid.
     * @param points
     */
    private void onGridSelectionChanged(List<Point2D> points) {
        btnStartCancelSimulation.setEnabled(!points.isEmpty());
    }

    /**
     * Called when a new simulation is beeing executed.
     */
    private void onStartSimulation() {
        btnStartCancelSimulation.setText("Cancel");
        _timer.start();
    }

    /**
     * Called when the simulations are finished or the user canceled them.
     */
    private void onEndSimulation() {
        btnStartCancelSimulation.setText("Simulate");
        _timer.stop();
    }
    //endregion

    //region ComponentEvents
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == tbSizeX) {
            pnlSimulator.setSizeX(Util.clamp(tbSizeX.getNumber(), MIN_GRID_SIZE, MAX_GRID_SIZE));
        } else if (e.getSource() == tbSizeY) {
            pnlSimulator.setSizeY(Util.clamp(tbSizeY.getNumber(), MIN_GRID_SIZE, MAX_GRID_SIZE));
        } else if (e.getSource() == btnStartCancelSimulation) {
            if (btnStartCancelSimulation.getText() == "Cancel") {
                pnlSimulator.cancelSimulations();
            } else {
                pnlSimulator.simulateAndLog();
            }
        } else if (e.getSource() == btnChangeSize) {
            pnlSimulator.setSizeX(Util.clamp(tbSizeX.getNumber(), MIN_GRID_SIZE, MAX_GRID_SIZE));
            pnlSimulator.setSizeY(Util.clamp(tbSizeY.getNumber(), MIN_GRID_SIZE, MAX_GRID_SIZE));
        } else if (e.getSource() == btnInputOrderFile) {
            selectGridFromOrder();
        } else {
            System.out.println("Unknown event");
        }
    }


    /**
     * Called when the user adjusts the JList containing all the simulators.
     * @param e
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        List<Simulator> selectedSimulators = liSimulators.getSelectedValuesList();
        List<Color> colors = pnlSimulator.setSimulators(selectedSimulators);
        _selectedCellRenderer.setColors(colors);
        liSimulators.repaint();
    }

    /**
     * Called when the user presses the input file button.
     * Shows a filechooser dialog and tries to load it as an order file.
     */
    private void selectGridFromOrder() {
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();

        //In response to a button click:
        int returnVal = fc.showOpenDialog(this);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File f = fc.getSelectedFile();
        try (FileInputStream br = new FileInputStream(f.getAbsolutePath())) {
            // Read the order file.
            String json = new String(br.readAllBytes(), "UTF-8");

            // Deserialize it from Json to an Order object.
            Gson deserializer = new Gson();
            Order order = deserializer.fromJson(json, Order.class);

            // Find all the products which are linked to the order including their location.
            List<TSPSimulator.Models.Product> products = DbProduct.Get().findProductsForOrder(order.getOrder());

            // Reset the size of the grid.
            pnlSimulator.setSizeX(5);
            pnlSimulator.setSizeY(5);

            // Fill the products in the grid.
            for (TSPSimulator.Models.Product product : products) {
                pnlSimulator.setIsClicked((int) product.getLocation().getX(), (int) product.getLocation().getY(), true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to retrieve the order from the database.");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "The file could not be found. Has it been removed?");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to open the order file.");
        } catch (JsonSyntaxException e) {
            JOptionPane.showMessageDialog(this, "The file is not an order file.");
        } catch (OrderNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.toString());
        }
    }
    //endregion
}
