import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import Simulators.Simulator;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

public class SimulatorPanel extends JPanel implements MouseListener {
    private int _sizeX = 5;
    private int _sizeY = 5;
    private boolean[] _clicked;
    private Simulator _simulator;

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

        drawGrid(g);

        drawSelectedSquares(g);

        drawRoute(g);
    }

    private void drawRoute(Graphics g) {
        g.setColor(Color.RED);
        Point2D startEndPoint = new Point2D(0, _sizeY - 1);
        List<Point2D> points = getPoints();
        List<Point2D> route = _simulator.simulate(startEndPoint, points);
        double squareWidth = ((double)getWidth() / (double)_sizeX);
        double squareHeight = ((double)getHeight() / (double)_sizeY);
        for (int i = 0; i < route.size() - 1; i++) {
            Point2D current = route.get(i);
            Point2D next = route.get(i + 1);
            Util.drawArrowLine(g,
                    (int)(current.getX() * squareWidth  + (squareWidth  / 2.0)),
                    (int)(current.getY() * squareHeight + (squareHeight / 2.0)),
                    (int)(next.getX()    * squareWidth  + (squareWidth  / 2.0)),
                    (int)(next.getY()    * squareHeight + (squareHeight / 2.0)),
                    8,8
            );
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

    private void drawGrid(Graphics g) {
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

    private void drawGrid() {
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

        drawGrid();
    }

    public void setSimulator(Simulator selectedItem) {
        _simulator = selectedItem;
        drawGrid();
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
        drawGrid();
    }

    public void setSizeY(Integer integer) {
        _sizeY = integer;
        fillClicked();
        drawGrid();
    }
}
