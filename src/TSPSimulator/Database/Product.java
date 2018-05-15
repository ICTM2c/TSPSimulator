package TSPSimulator.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private static Product s_Product;

    private Connection conn;
    private Product() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/magazijnrobot","root","");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Product Get() {
        if (s_Product == null) {
            s_Product = new Product();
        }
        return s_Product;
    }

    public List<TSPSimulator.Models.Product> FindProductsForOder(int order) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT p.*, s.* FROM Product p\n" +
                    "INNER JOIN product_order o ON p.productid = o.orderid\n" +
                    "INNER JOIN shelve s ON o.shelveid = s.ShelveId\n" +
                    "WHERE o.orderid = ?");
            stmt.setInt(1, order);
            ResultSet rs = stmt.executeQuery();
            List<TSPSimulator.Models.Product> res = new ArrayList<>();
            while (rs.next()) {
                res.add(TSPSimulator.Models.Product.FromResultSet(rs));
            }
            return res;
        }
        catch (Exception e) {
            return null;
        }
    }
}
