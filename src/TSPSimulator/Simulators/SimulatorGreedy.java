package TSPSimulator.Simulators;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class SimulatorGreedy implements Simulator {
    @Override
    public List<Point2D> simulate(Point2D startEndPoint, List<Point2D> points) {
        Point2D currentPoint = startEndPoint;
        List<Point2D> route = new ArrayList<>();
        route.add(currentPoint);
        while (!points.isEmpty()) {
            Point2D closest = findClosest(currentPoint, points);
            currentPoint = closest;
            boolean isRemoved = points.remove(closest);
            route.add(closest);
        }
        route.add(startEndPoint);
        return route;
    }

    public Point2D findClosest(Point2D current, List<Point2D> leftOver) {
        Point2D closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (Point2D point : leftOver) {
            double distance = point.distance(current);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = point;
            }
        }
        return closest;
    }

    @Override
    public String toString() {
        return "Greedy";
    }
}
