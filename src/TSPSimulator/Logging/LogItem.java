package TSPSimulator.Logging;

public class LogItem {
    private String _name;
    private String _time;
    private String _distance;

    public LogItem(String name, long time, double distance) {
        this(name, String.valueOf(time) + "ms", String.valueOf(distance));
    }

    public LogItem(String name, String time, String distance) {
        _name = name;
        _time = time;
        _distance = distance;
    }

    public String getName() {
        return _name;
    }

    public String getTime() {
        return _time;
    }

    public String getDistance() {
        return _distance;
    }
}
