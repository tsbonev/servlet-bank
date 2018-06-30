package core.DAO;

import core.Model.Account;
import org.junit.After;
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
    @After
    public void cleanRows(){

        dao.deleteAllRows();

    }

    @Test
    public void getAccount(){

        Account account = new Account(154.0d);

        dao.save(account);

        assertThat(dao.getAll().get(0).getAmount(), is(account.getAmount()));
    }

    @Test
    public void deleteAccount(){

        Account account = new Account(154.0d);

        dao.save(account);

        Account dbAccount = dao.getAll().get(0);

        dao.deleteById(dbAccount.getId());


        assertThat(dao.getAll().size(), is(0));

    }

    @Test
    public void updateAccount(){

        Account account = new Account(154.0d);

        dao.save(account);

        Account dbAccount = dao.getAll().get(0);

        dbAccount.setAmount(1000.0d);

        dao.update(dbAccount);

        assertThat(dao.getAll().get(0).getAmount(), is(dbAccount.getAmount()));

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

        dao.createAccountTable();

        DatabaseMetaData dbm = dao.getMetaData();

        ResultSet tables = dbm.getTables(null, null, "account", null);

        assertThat(tables.next(), is(true));

    }

    @Ignore
    @Test
    public void dropTable() throws SQLException {

        dao.dropAccountTable();

        DatabaseMetaData dbm = dao.getMetaData();

        ResultSet tables = dbm.getTables(null, null, "account", null);

        assertThat(tables.next(), is(false));

    }



}
