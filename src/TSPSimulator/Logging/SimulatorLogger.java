package TSPSimulator.Logging;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class SimulatorLogger {
    private List<LogItem> _items;
    private Lock _lock;

    public SimulatorLogger() {
        _items = new ArrayList<>();

        // Insert a header for the log.
        _items.add(new LogItem("Simulator", "Time", "Distance"));
        _lock = new ReentrantLock();
    }

    /**
     * Inserts a new log item
     *
     * @param simulatorName
     * @param time
     * @param pathLength
     */
    public void logResult(String simulatorName, long time, double pathLength) {
        // Lock this object. It's used in multiple threads.
        _lock.lock();
        _items.add(new LogItem(simulatorName, time, pathLength));
        _lock.unlock();
    }

    /**
     * Searches in a list of T for the longest value.
     * This value is provided by a callback
     *
     * @param list
     * @param callback
     * @param <T>
     * @return
     */
    private <T> int getLargestItem(List<T> list, Function<T, Object> callback) {
        int largest = Integer.MIN_VALUE;
        for (T obj : list) {
            largest = Math.max(largest, String.valueOf(callback.apply(obj)).length());
        }
        return largest;
    }

    /**
     * Repeats the provided string X times.
     *
     * @param str
     * @param num
     * @return
     */
    private static String repeatString(String str, int num) {
        return String.join("", Collections.nCopies(num, str));
    }

    /**
     * Log all items to a file.
     * This file can be found in the Logs folder.
     * The filename is 'Log_{Unix Timestamp}.txt'
     *
     * @throws IOException
     */
    public void writeToFile() throws IOException {
        _lock.lock();
        int largestName = getLargestItem(_items, x -> x.getName());
        int largestTime = getLargestItem(_items, x -> x.getTime());

        // Create the Logs folder in case it doesn't exist.
        new File("Logs/").mkdir();

        // Create a filename for this log item. Use the unix timestamp to make every log unique.
        String fileName = "Logs/Log_" + (System.currentTimeMillis() / 1000L) + ".txt";

        // Open the log file.
        BufferedWriter file = new BufferedWriter(new FileWriter(fileName));

        // Write each log item and make sure every column is aligned.
        for (LogItem item : _items) {
            file.write(item.getName());
            file.write(repeatString(" ", largestName - item.getName().length()));
            file.write(" | ");
            file.write(item.getTime());
            file.write(repeatString(" ", largestTime - String.valueOf(item.getTime()).length()));
            file.write(" | ");
            file.write(item.getDistance());
            file.newLine();
        }
        file.close();

        // Open the txt file in the default editor of this computer.
        Desktop.getDesktop().open(new File(fileName));

        _lock.unlock();
    }
}
