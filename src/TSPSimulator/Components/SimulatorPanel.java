package TSPSimulator.Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import TSPSimulator.Logging.SimulatorLogger;
import TSPSimulator.Simulators.Simulator;
import TSPSimulator.Simulators.SimulatorBruteForce;
import TSPSimulator.Util;
import javafx.geometry.Point2D;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimulatorPanel extends JPanel implements MouseListener {
    private int _sizeX = 5;
    private int _sizeY = 5;
    private boolean[] _clicked;
    private List<Simulator> _simulators;
    private static final Color[] s_pathColors = new Color[]{Color.RED, Color.GREEN, Color.MAGENTA, Color.ORANGE};
    Thread _simulateThread = new Thread();
    private SimulatorLogger _simulatorLogger;

    private Runnable onStartSimulationCallback;
    private Runnable onEndSimulationCallback;

    public void registerOnStartSimulationCallback(Runnable callback) {
        onStartSimulationCallback = callback;
    }

    public void registerOnEndSimulationCallback(Runnable callback) {
        onEndSimulationCallback = callback;
    }

    public SimulatorPanel() {
        setPreferredSize(new Dimension(400, 400));
        this.addMouseListener(this);
        setBackground(Color.WHITE);
        fillClicked();
    }


    //region GridClicked
    private void fillClicked() {
        _clicked = new boolean[_sizeX * _sizeY];
    }
    public void clearClicked() {
        fillClicked();
    }

    public void setIsClicked(int x, int y, boolean clicked) {
        int index = makeIndex(x, y);
        _clicked[index] = clicked;
    }

    public int makeIndex(int x, int y) {
        return x + y * _sizeX;
    }

    public boolean isSelected(int x, int y) {
        int index = makeIndex(x, y);
        return _clicked[index];
    }

    public void toggleIsSelected(int x, int y) {
        int index = makeIndex(x, y);
        _clicked[index] = !_clicked[index];
    }
    //endregion


    //region Painting
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Notify the GUI that the simulation has started.
        onStartSimulationCallback.run();

        drawPanel(g);

        drawSelectedSquares(g);

        // If there is still a simulation going on stop it before starting new ones.
        if (_simulateThread.isAlive()) {
            _simulateThread.stop();
        }

        _simulateThread = new Thread(() -> {
            // Start calculating the paths in advance.
            Point2D startEndPoint = new Point2D(0, _sizeY - 1);
            List<Point2D> points = getPoints();
            _simulators.parallelStream().forEach(simulator -> {
                // Make a copy of the selected point list. Otherwise simulators might interfere with each other.
                List<Point2D> copyOfPoints = new ArrayList<>(points);

                long startTime = System.currentTimeMillis();
                List<Point2D> route = simulator.simulate(startEndPoint, copyOfPoints);
                long endTime = System.currentTimeMillis();
                if (_simulatorLogger != null) {
                    _simulatorLogger.logResult(simulator.toString(), endTime - startTime, SimulatorBruteForce.getLength(route));
                }

                int currentSimulatorIndex = _simulators.indexOf(simulator);

                // Draw the paths on the UI thread.
                SimulatorPanel self = this;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        drawRoute(self.getGraphics(), route, _simulators.size(), currentSimulatorIndex);
                    }
                });

            });

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    writeLog();
                    onEndSimulationCallback.run();
                }
            });
        });
        _simulateThread.start();
    }

    private void drawRoute(Graphics g, List<Point2D> route, int numSimulators, int currentSimulatorIndex) {
        g.setColor(Color.RED);
        double squareWidth = ((double) getWidth() / (double) _sizeX);
        double squareHeight = ((double) getHeight() / (double) _sizeY);

        double offsetWidth = squareWidth / (numSimulators + 1);
        double offsetHeight = squareHeight / (numSimulators + 1);

        g.setColor(s_pathColors[currentSimulatorIndex]);

        currentSimulatorIndex++;
        for (int i = 0; i < route.size() - 1; i++) {
            Point2D current = route.get(i);
            Point2D next = route.get(i + 1);
            Util.drawArrowLine(g,
                    (int) (current.getX() * squareWidth + (currentSimulatorIndex * offsetWidth)),
                    (int) (current.getY() * squareHeight + (currentSimulatorIndex * offsetHeight)),
                    (int) (next.getX() * squareWidth + (currentSimulatorIndex * offsetWidth)),
                    (int) (next.getY() * squareHeight + (currentSimulatorIndex * offsetHeight)),
                    8, 8
            );
        }
    }

    private void drawSelectedSquares(Graphics g) {
        g.setColor(Color.BLACK);
        for (int x = 0; x < _sizeX; x++) {
            int coordX = (int) ((double) x / (double) _sizeX * (double) getWidth());
            for (int y = 0; y < _sizeY; y++) {
                int coordY = (int) ((double) y / (double) _sizeY * (double) getHeight());
                if (isSelected(x, y)) {
                    g.fillRect(coordX, coordY, (int) Math.ceil((double) getWidth() / (double) _sizeX), (int) Math.ceil((double) getHeight() / (double) _sizeY));
                }
            }
        }
    }

    private void drawPanel(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < _sizeX; x++) {
            int absoluteX = (int) ((double) getWidth() / (double) _sizeX * (double) x);
            g.drawLine(absoluteX, 0, absoluteX, getHeight());
        }
        g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());

        for (int y = 0; y < _sizeY; y++) {
            int absoluteY = (int) ((double) getHeight() / (double) _sizeY * (double) y);
            g.drawLine(0, absoluteY, getWidth(), absoluteY);
        }
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }

    public void drawPanel() {
        repaint();
    }

    //endregion

    int map(int x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int clickX = e.getX();
        int clickY = e.getY();
        int x = map(e.getX(), 0, getWidth(), 0, _sizeX);
        int y = map(e.getY(), 0, getHeight(), 0, _sizeY);
        toggleIsSelected(x, y);

        drawPanel();
    }

    public void setSimulator(Simulator selectedItem) {
        _simulators = new ArrayList<Simulator>();
        _simulators.add(selectedItem);
        drawPanel();
    }


    public List<Point2D> getPoints() {
        List<Point2D> list = new ArrayList<>();
        for (int x = 0; x < _sizeX; x++) {
            for (int y = 0; y < _sizeY; y++) {
                if (isSelected(x, y)) {
                    list.add(new Point2D(x, y));
                }
            }
        }
        return list;
    }

    public void setSizeX(Integer integer) {
        _sizeX = integer;
        fillClicked();
        drawPanel();
    }

    public void setSizeY(Integer integer) {
        _sizeY = integer;
        fillClicked();
        drawPanel();
    }

    /*
    Executes the provided simulators
     */

    public List<Color> setSimulators(List<Simulator> selectedSimulators) {
        _simulatorLogger = null;
        _simulators = selectedSimulators;
        List<Color> colors = new ArrayList<>();
        for (int i = 0; i < _simulators.size(); i++) {
            colors.add(s_pathColors[i]);
        }
        drawPanel();
        return colors;
    }

    /**
     If the simulator thread is working it stops it and writes "Interrupted" on the panel.
      */
    public void cancelSimulations() {
        if (_simulateThread.isAlive()) {
            _simulateThread.stop();
            Graphics g = this.getGraphics();
            g.setColor(Color.RED);
            g.drawString("Interrupted", 10, 10);
            _simulatorLogger = null;
            onEndSimulationCallback.run();
        }
    }
    public void simulateAndLog() {
        cancelSimulations();
        _simulatorLogger = new SimulatorLogger();
        drawPanel();
    }


    private void writeLog() {
        if (_simulatorLogger == null) {
            return;
        }
        try {
            _simulatorLogger.writeToFile();
            _simulatorLogger = null;
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    //region UnusedEvents
    // Lots of unised events because Oracle thought it was a good idea to automatically register all events.
    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
    //endregion
}
