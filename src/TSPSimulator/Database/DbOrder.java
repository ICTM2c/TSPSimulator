package TSPSimulator.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbOrder extends Db {
    private static DbOrder s_dbOrder;

    private DbOrder() {
    }

    public static DbOrder Get() {
        if (s_dbOrder == null) {
            s_dbOrder = new DbOrder();
        }
        return s_dbOrder;
    }

    /**
     * Returns true of the provided order exists.
     *
     * @param orderId The Id of the requested order.
     * @return
     * @throws SQLException
     */
    public boolean orderExists(int orderId) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement("SELECT * FROM `order` WHERE OrderId = ?");
        stmt.setInt(1, orderId);
        ResultSet rs = stmt.executeQuery();
        boolean exists = rs.next();
        stmt.close();
        return exists;
    }
}
