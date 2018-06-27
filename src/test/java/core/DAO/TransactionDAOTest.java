package core.DAO;

import core.Model.Account;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransactionDAOTest {

    AccountDAOImpl accountDao = new AccountDAOImpl();
    UserDAOImpl userDao = new UserDAOImpl();
    TransactionDAOImpl dao = new TransactionDAOImpl();

    @Before
    public void addAccount(){
        accountDao.save(new Account(200));
    }

    @After
    public void cleanRows(){
        userDao.deleteAllRows();
        accountDao.deleteAllRows();
    }

    @Ignore
    @Test
    public void createTable() throws SQLException {

        dao.createTransactionTable();

        DatabaseMetaData dbm = dao.getMetaData();

        ResultSet tables = dbm.getTables(null, null, "transactions", null);

        assertThat(tables.next(), is(true));

    }

    @Ignore
    @Test
    public void dropTable() throws SQLException {

        DatabaseMetaData dbm = dao.getMetaData();

        dao.dropTransactionTable();

        ResultSet tables = dbm.getTables(null, null, "transactions", null);

        assertThat(tables.next(), is(false));

    }

}
