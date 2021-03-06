package TSPSimulator.Models;

import javafx.geometry.Point2D;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Product {
    private int _productId;
    private int _size;
    private Point2D _location;
    private boolean _isPickedUp;

    public Product(int productId, int size) {
        _productId = productId;
        _size = size;
    }

    public void setPosition(int x, int y) {
        _location = new Point2D(x, y);
    }

    /**
     * Creates a new Product object based on the provided ResultSet
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Product fromResultSet(ResultSet rs) throws SQLException {
        Product pr = new Product(rs.getInt("productid"), rs.getInt("size"));
        pr.setPosition(rs.getInt("X"), rs.getInt("Y"));
        return pr;
    }

    public Point2D getLocation() {
        return _location;
    }
}
