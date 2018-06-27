package core.DAO;

import core.Model.Account;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class UserDAOTest {

    AccountDAOImpl accountDao = new AccountDAOImpl();
    UserDAOImpl dao = new UserDAOImpl();

    @Before
    public void cleanRows(){

        dao.deleteAllRows();
        accountDao.deleteAllRows();
        accountDao.save(new Account(200));

    }

    @Test
    public void saveUser(){



    }

    @Ignore
    @Test
    public void createTable() throws SQLException {

        dao.createUserTable();

        DatabaseMetaData dbm = dao.getMetaData();

        ResultSet tables = dbm.getTables(null, null, "userDb", null);

        assertThat(tables.next(), is(true));

    }

    @Ignore
    @Test
    public void dropTable() throws SQLException {

        DatabaseMetaData dbm = dao.getMetaData();

        dao.dropUserTable();

        ResultSet tables = dbm.getTables(null, null, "userDb", null);

        assertThat(tables.next(), is(false));

    }

}
