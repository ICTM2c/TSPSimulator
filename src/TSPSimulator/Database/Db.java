package TSPSimulator.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides a single SQL connection for all database classes which extend from it.
 */
abstract class Db {
    private static Connection s_connection;

    /**
     * Returns a sql connection. If it doesn't exist yet it creates it first.
     *
     * @return
     */
    protected Connection getConnection() {
        if (s_connection != null) {
            return s_connection;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            s_connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/magazijnrobot", "root", "");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return s_connection;
    }
}
