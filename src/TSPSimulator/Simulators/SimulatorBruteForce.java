package TSPSimulator.Simulators;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class SimulatorBruteForce implements Simulator {

    double _shortestLength;
    List<Point2D> _shortest;

    boolean isShorter(double length) {
        return length < _shortestLength;
    }

    void setCurrentPath(List<Point2D> path, double length) {
        _shortestLength = length;
        _shortest = path;
    }

    @Override
    public List<Point2D> simulate(Point2D startEndPoint, List<Point2D> points) {
        if (points.size() == 0) {
            return new ArrayList<Point2D>();
        }

        // Reset the saved path and length
        setCurrentPath(null, Double.MAX_VALUE);

        // Create a new permute object to generate all possible combinations
        Permute<Point2D> perm = new Permute<Point2D>();
        perm.listPermutations(points, track -> {
            track.add(0, startEndPoint);
            track.add(startEndPoint);
            double length = getLength(track);
            if (isShorter(length)) {
                setCurrentPath(track, length);
            }
        });

        return _shortest;
    }

    private static double getLength(List<Point2D> track) {
        double length = 0;
        for (int i = 0; i < track.size() - 1; i++) {
            Point2D current = track.get(i);
            Point2D next = track.get(i + 1);
            length += current.distance(next);
        }
        return length;
    }

    @Override
    public String toString() {
        return "Brute Force";
    }
}
