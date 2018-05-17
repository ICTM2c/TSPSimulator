package TSPSimulator.Simulators;

import javafx.geometry.Point2D;

import java.util.List;

public class SimulatorGreedyForce implements Simulator {
    private SimulatorBruteForce _bruteForce = new SimulatorBruteForce();
    private SimulatorGreedy _greedy = new SimulatorGreedy();

    @Override
    public List<Point2D> simulate(Point2D startEndPoint, List<Point2D> points) {
        if (points.size() > 10) {
            return _greedy.simulate(startEndPoint, points);
        }
        else {
            return _bruteForce.simulate(startEndPoint, points);
        }
    }

    @Override
    public String toString() {
        return "GreedyForce";
    }
}
