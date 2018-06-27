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

public class AccountDAOTest {


    AccountDAOImpl dao = new AccountDAOImpl();

    @Before
    public void cleanRows(){

        dao.deleteAllRows();

    }

    @Test
    public void saveGetAndDeleteAccount(){

        Account account = new Account(154.0d);

        dao.save(account);

        Account dbAccount = dao.getById(1);

        dao.deleteById(dbAccount.getId());

        assertThat(dbAccount.getAmount(), is(154.0d));

        assertThat(dao.getById(1).getId(), is(0));

    }

    @Test
    public void getAllAccounts(){

        Account accountOne = new Account(154.0d);
        Account accountTwo = new Account(164.0d);

        dao.save(accountOne);
        dao.save(accountTwo);

        assertThat(dao.getAll().size(), is(2));

    }

    @Ignore
    @Test
    public void createTable() throws SQLException {

        DatabaseMetaData dbm = dao.getMetaData();

        dao.createAccountTable();

        ResultSet tables = dbm.getTables(null, null, "account", null);

        assertThat(tables.next(), is(true));

    }

    @Ignore
    @Test
    public void dropTable() throws SQLException {

        DatabaseMetaData dbm = dao.getMetaData();

        dao.dropAccountTable();

        ResultSet tables = dbm.getTables(null, null, "account", null);

        assertThat(tables.next(), is(false));

    }



}
