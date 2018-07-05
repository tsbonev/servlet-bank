package core.repository;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnection {

    /**
     * Returns a jdbc connection to a mysql database.
     *
     * @return a connection
     */
    public static Connection getConnection() {

        try {
            String url = "jdbc:mysql://localhost:3306/bank?useSSL=false&useLegacyDatetimeCode=false&serverTimezone=Europe/Sofia";
            String username = "user";
            String password = "password";

            MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();

            dataSource.setUrl(url);
            dataSource.setUser(username);
            dataSource.setPassword(password);

            PooledConnection pooledConnection = dataSource.getPooledConnection();

            return pooledConnection.getConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

}
