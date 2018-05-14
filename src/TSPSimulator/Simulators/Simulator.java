package TSPSimulator.Simulators;

import javafx.geometry.Point2D;

import java.util.List;

public interface Simulator {
    List<Point2D> simulate(Point2D startEndPoint, List<Point2D> points);
}
