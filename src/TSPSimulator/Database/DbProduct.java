package TSPSimulator.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbProduct {
    private static DbProduct s_DbProduct;

    private Connection conn;
    private DbProduct() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/magazijnrobot","root","");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns the singleton of DbProduct
     * @return
     */
    public static DbProduct Get() {
        if (s_DbProduct == null) {
            s_DbProduct = new DbProduct();
        }
        return s_DbProduct;
    }

    /**
     * Finds all the products of an order including the location.
     * @param order
     * @return
     * @throws SQLException
     */
    public List<TSPSimulator.Models.Product> findProductsForOrder(int order) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT p.*, s.* FROM Product p\n" +
                "INNER JOIN product_order o ON p.productid = o.orderid\n" +
                "INNER JOIN shelve s ON o.shelveid = s.ShelveId\n" +
                "WHERE o.orderid = ?");
        stmt.setInt(1, order);
        ResultSet rs = stmt.executeQuery();
        List<TSPSimulator.Models.Product> res = new ArrayList<>();
        while (rs.next()) {
            res.add(TSPSimulator.Models.Product.fromResultSet(rs));
        }
        return res;
    }
}
