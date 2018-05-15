package TSPSimulator.Logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

public class SimulatorLogger {
    private List<LogItem> _items;
    private Lock _lock;

    public SimulatorLogger()  {
        _items = new ArrayList<>();
        _items.add(new LogItem("Simulator", "Time", "Distance"));
        _lock = new ReentrantLock();
    }
    public void LogResult(String simulator, long time, double pathLength) {
        _lock.lock();
        _items.add(new LogItem(simulator, time, pathLength));
        _lock.unlock();
    }

    public <T> int getLargestItem(List<T> list, Function<T, Object> callback) {
        int largest = Integer.MIN_VALUE;
        for (T obj : list) {
            largest = Math.max(largest, String.valueOf(callback.apply(obj)).length());
        }
        return largest;
    }

    private static String repeatString(String str, int num) {
        return String.join("", Collections.nCopies(num, str));
    }

    public void writeToFile() throws IOException {
        int largestName = getLargestItem(_items, x -> x.getName());
        int largestTime = getLargestItem(_items, x -> x.getTime());
        int largestPathLength = getLargestItem(_items, x -> x.getDistance());

        // Create the Logs folder
        new File("Logs/").mkdir();
        BufferedWriter file = new BufferedWriter(new FileWriter("Logs/Log_" + (System.currentTimeMillis() / 1000L)));

        for (LogItem item : _items) {
            file.write(item.getName());
            file.write(repeatString(" ", largestName - item.getName().length()));
            file.write(" | ");
            file.write(item.getTime());
            file.write(repeatString(" ", largestTime - String.valueOf(item.getTime()).length()));
            file.write(" | ");
            file.write(item.getDistance());
            file.write(repeatString(" ", largestPathLength - String.valueOf(item.getDistance()).length()));
            file.newLine();
        }
        file.close();
    }
}
