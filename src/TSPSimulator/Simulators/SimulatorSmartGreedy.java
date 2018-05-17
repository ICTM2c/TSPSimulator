package TSPSimulator.Simulators;

import javafx.geometry.Point2D;

import java.awt.*;
import java.util.List;

public class SimulatorSmartGreedy extends SimulatorGreedy {
    private Point2D _startEndPoint;

    @Override
    public List<Point2D> simulate(Point2D startEndPoint, List<Point2D> points) {
        if (points.isEmpty()) {
            return super.simulate(startEndPoint, points);
        }
        _startEndPoint = startEndPoint;
        return super.simulate(startEndPoint, points);
    }

    @Override
    public Color getColor() {
        return Color.blue;
    }

    /**
     * If two points are equally far away prefer the one which is the farthest from the start/end point.
     *
     * @param current
     * @param leftOver
     * @return
     */
    @Override
    public Point2D findClosest(Point2D current, List<Point2D> leftOver) {
        Point2D closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (Point2D point : leftOver) {
            double distance = point.distance(current);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = point;
            } else if (distance == closestDistance) {
                // Prefer the point which is the farthest away from the start/end point
                if (closest.distance(_startEndPoint) < point.distance(_startEndPoint)) {
                    closestDistance = distance;
                    closest = point;
                }
            }
        }
        return closest;
    }

    @Override
    public String toString() {
        return "Smart Greedy";
    }
}
