package core.DAO;

import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class ConnectionTest {

    @Test
    public void shouldConnect() throws SQLException {

        Connection conn = MySQLConnection.getConnection();

        assertThat(conn, is(notNullValue()));

        conn.close();

    }

}
