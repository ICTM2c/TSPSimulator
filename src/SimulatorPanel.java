import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import Simulators.Simulator;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimulatorPanel extends JPanel implements MouseListener {
    private int _sizeX = 5;
    private int _sizeY = 5;
    private boolean[] _clicked;
    private List<Simulator> _simulators;
    private static final Color[] s_pathColors = new Color[] {Color.RED, Color.GREEN, Color.MAGENTA, Color.ORANGE};

    public SimulatorPanel() {
        setPreferredSize(new Dimension(400, 400));
        this.addMouseListener(this);
        setBackground(Color.WHITE);
        fillClicked();
    }

    private void fillClicked() {
        _clicked = new boolean[_sizeX * _sizeY];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawPanel(g);

        drawSelectedSquares(g);

        drawRoute(g);
    }

    private void drawRoute(Graphics g) {
        g.setColor(Color.RED);
        Point2D startEndPoint = new Point2D(0, _sizeY - 1);
        List<Point2D> points = getPoints();
        double squareWidth = ((double)getWidth() / (double)_sizeX);
        double squareHeight = ((double)getHeight() / (double)_sizeY);

        List<List<Point2D>> paths = _simulators.parallelStream().map(simulator -> {
            // Make a copy of the selected point list. Otherwise simulators might interfere with each other.
            List<Point2D> copyOfPoints = new ArrayList<>(points);
            return simulator.simulate(startEndPoint, copyOfPoints);
        }).collect(Collectors.toList());

        double offsetWidth = squareWidth / (paths.size() + 1);
        double offsetHeight = squareHeight / (paths.size() + 1);
        int numPath = 0;
        for (List<Point2D> route : paths) {
            g.setColor(s_pathColors[numPath]);
            numPath++;
            for (int i = 0; i < route.size() - 1; i++) {
                Point2D current = route.get(i);
                Point2D next = route.get(i + 1);
                Util.drawArrowLine(g,
                        (int) (current.getX() * squareWidth +    (numPath * offsetWidth)),            //(squareWidth / 2.0)),
                        (int) (current.getY() * squareHeight +   (numPath * offsetHeight)),            //(squareHeight / 2.0)),
                        (int) (next.getX() * squareWidth +       (numPath * offsetWidth)),            //(squareWidth / 2.0)),
                        (int) (next.getY() * squareHeight +      (numPath * offsetHeight)),            //(squareHeight / 2.0)),
                        8, 8
                );
            }
        }
    }

    private void drawSelectedSquares(Graphics g) {
        g.setColor(Color.BLACK);
        for (int x = 0; x < _sizeX; x++) {
            int coordX = (int)((double)x / (double)_sizeX * (double)getWidth());
            for (int y = 0; y < _sizeY; y++) {
                int coordY = (int)((double)y / (double)_sizeY * (double)getHeight());
                if (isSelected(x, y)) {
                    g.fillRect(coordX, coordY, (int)Math.ceil((double)getWidth() / (double)_sizeX), (int)Math.ceil((double)getHeight() / (double)_sizeY));
                }
            }
        }
    }

    private void drawPanel(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < _sizeX; x++) {
            int absoluteX = (int)((double)getWidth() / (double)_sizeX * (double)x);
            g.drawLine(absoluteX, 0, absoluteX, getHeight());
        }
        g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());

        for (int y = 0; y < _sizeY; y++) {
            int absoluteY = (int)((double)getHeight() / (double)_sizeY * (double)y);
            g.drawLine(0, absoluteY, getWidth(), absoluteY);
        }
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }

    private void drawPanel() {
        repaint();
    }



    int map(int x, int in_min, int in_max, int out_min, int out_max)
    {
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

    public int makeIndex(int x, int y) {
        return x + y * _sizeX;
    }

    public boolean isSelected(int x, int y) {
        int index = makeIndex(x,y);
        return _clicked[index];
    }

    public void toggleIsSelected(int x, int y)  {
        int index = makeIndex(x,y);
        _clicked[index] = !_clicked[index];
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

    public List<Color> setSimulators(List<Simulator> selectedSimulators) {
        _simulators = selectedSimulators;
        List<Color> colors = new ArrayList<>();
        for (int i = 0; i < _simulators.size(); i++) {
            colors.add(s_pathColors[i]);
        }
        drawPanel();
        return colors;
    }
}
