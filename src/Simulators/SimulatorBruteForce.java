package Simulators;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SimulatorBruteForce implements Simulator {

    @Override
    public List<Point2D> simulate(Point2D startEndPoint, List<Point2D> points) {
        if (points.size() == 0) {
            return new ArrayList<Point2D>();
        }

        // Create a new permute object to generate all possible combinations
        Permute<Point2D> perm = new Permute<Point2D>();
        Set<List<Point2D>> combinations = perm.listPermutations(points);


        List<Point2D> shortest = null;
        double shortestLength = Double.MAX_VALUE;

        // Go through every combination and check if it's the shortest we found yet.
        for (List<Point2D> track : combinations) {
            track.add(0, startEndPoint);
            track.add(startEndPoint);
            double length = getLength(track);
            if (length < shortestLength) {
                shortestLength = length;
                shortest = track;
            }
        }

        return shortest;
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
