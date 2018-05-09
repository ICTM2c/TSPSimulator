package Simulators;

import Simulators.SimulatorGreedy;
import javafx.geometry.Point2D;

import java.awt.geom.Line2D;
import java.util.List;

public class SimulatorTwoOpt extends SimulatorSmartGreedy {
    @Override
    public List<Point2D> simulate(Point2D startEndPoint, List<Point2D> points) {
        List<Point2D> list = super.simulate(startEndPoint, points);
        twoOpt(list);

        return list;
    }

    private Line2D getLastLine(List<Point2D> points) {
        Point2D lastPoint = points.get(points.size() - 1);
        Point2D secondToLastPoint = points.get(points.size() - 2);
        return new Line2D.Double(lastPoint.getX(), lastPoint.getY(), secondToLastPoint.getX(), secondToLastPoint.getY());
    }

    public void twoOpt(List<Point2D> points) {
        int numPoints = points.size() - 2;
        boolean didIntersect = false;

        Line2D lastLine = getLastLine(points);
        for (int i1 = 1; i1 < numPoints; i1++) {
            for (int i2 = 1; i2 < numPoints; i2++) {
                if (i1 == i2) {
                    continue;
                }

                Point2D line1Point1 = points.get(i1);
                Point2D line1Point2 = points.get(i1 + 1);
                Point2D line2Point1 = points.get(i2);
                Point2D line2Point2 = points.get(i2 + 1);

                if (
                        line1Point1.equals(line2Point1) || line1Point1.equals(line2Point2) ||
                                line1Point2.equals(line2Point1) || line1Point2.equals(line2Point2)
                        ) {

                    float angle = getAngle(line1Point1, line1Point2) - getAngle(line2Point1, line2Point2);
                    if (angle == -90) {
                        swap(points, i1 + 1, i2 + 1);
                        didIntersect = true;

                    }

                    // Line2D.linesIntersect also returns true if the coordinates are the same.
                    continue;

                }


                boolean doesIntersect = Line2D.linesIntersect(
                        line1Point1.getX(), line1Point1.getY(),
                        line1Point2.getX(), line1Point2.getY(),
                        line2Point1.getX(), line2Point1.getY(),
                        line2Point2.getX(), line2Point2.getY()
                );


                if (doesIntersect && !lastLine.intersectsLine(line1Point1.getX(), line1Point1.getY(), line2Point2.getX(), line2Point2.getY()) && !lastLine.intersectsLine(line2Point1.getX(), line2Point1.getY(), line1Point2.getX(), line1Point2.getY())) {
                    swap(points, i1 + 1, i2 + 1);
                    didIntersect = true;
                }
            }
        }

        if (didIntersect) {
            twoOpt(points);
        }
    }

    public float getAngle(Point2D target1, Point2D target2) {
        float angle = (float) Math.toDegrees(Math.atan2(target1.getY() - target2.getY(), target1.getX() - target2.getX()));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    void swap(List<Point2D> lst, int x, int y) {
        Point2D temp = lst.get(x);
        lst.set(x, lst.get(y));
        lst.set(y, temp);
    }

    @Override
    public String toString() {
        return "Two-Opt";
    }
}
