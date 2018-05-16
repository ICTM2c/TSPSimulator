package TSPSimulator.Database.Exceptions;

public class OrderNotFoundException extends Throwable {
    private int _orderid;

    public OrderNotFoundException(int order) {
        _orderid = order;
    }

    @Override
    public String toString() {
        return "Order " + _orderid + " does not exist.";
    }
}
