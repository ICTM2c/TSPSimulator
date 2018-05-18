package TSPSimulator.Simulators;

import TSPSimulator.Util;
import javafx.geometry.Point2D;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.List;

public class SimulatorTwoOpt extends SimulatorGreedy {
    @Override
    public List<Point2D> simulate(Point2D startEndPoint, List<Point2D> points) {
        List<Point2D> list = super.simulate(startEndPoint, points);
        twoOpt(list);

        return list;
    }

    @Override
    public Color getColor() {
        return Color.green;
    }

    public void twoOpt(List<Point2D> points) {
        int numPoints = points.size() - 2;
        boolean didIntersect = false;

        for (int i1 = 0; i1 < numPoints + 1; i1++) {
            for (int i2 = numPoints + 1; i2 > 0; i2--) {
                if (i1 == i2 + 1) {
                    continue;
                }

                Point2D line1Point1 = points.get(i1);
                Point2D line1Point2 = points.get(i1 + 1);
                Point2D line2Point1 = points.get(i2);
                Point2D line2Point2 = points.get(i2 - 1);

                if (
                        line1Point1.equals(line2Point1) || line1Point1.equals(line2Point2) ||
                        line1Point2.equals(line2Point1) || line1Point2.equals(line2Point2)
                        ) {

                    // Line2D.linesIntersect also returns true if the coordinates are the same.
                    continue;

                }


                boolean doesIntersect = Line2D.linesIntersect(
                        line1Point1.getX(), line1Point1.getY(),
                        line1Point2.getX(), line1Point2.getY(),
                        line2Point1.getX(), line2Point1.getY(),
                        line2Point2.getX(), line2Point2.getY()
                );


                if (doesIntersect) {
                    Util.swap(points, i1 + 1, i2 - 1);
                    didIntersect = true;
                }
            }
        }

        if (didIntersect) {
            twoOpt(points);
        }
    }



    @Override
    public String toString() {
        return "Two-Opt";
    }
}
