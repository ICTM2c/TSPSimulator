package TSPSimulator.Database;

import TSPSimulator.Database.Exceptions.OrderNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbProduct extends Db {
    private static DbProduct s_DbProduct;

    private DbProduct() {
    }

    /**
     * Returns the singleton of DbProduct
     *
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
     *
     * @param order
     * @return
     * @throws SQLException
     */
    public List<TSPSimulator.Models.Product> findProductsForOrder(int order) throws SQLException, OrderNotFoundException {
        if (!DbOrder.Get().orderExists(order)) {
            throw new OrderNotFoundException(order);
        }

        PreparedStatement stmt = getConnection().prepareStatement("SELECT p.*, s.* FROM Product p\n" +
                "INNER JOIN product_order o ON p.productid = o.orderid\n" +
                "INNER JOIN shelve s ON o.shelveid = s.ShelveId\n" +
                "WHERE o.orderid = ?");
        stmt.setInt(1, order);
        ResultSet rs = stmt.executeQuery();
        List<TSPSimulator.Models.Product> res = new ArrayList<>();
        while (rs.next()) {
            res.add(TSPSimulator.Models.Product.fromResultSet(rs));
        }
        stmt.close();
        return res;
    }
}
