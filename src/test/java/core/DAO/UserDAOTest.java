package core.DAO;

import core.Model.Account;
import core.Model.User;
import org.junit.After;
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
    public void addAccount(){

        dao.deleteAllRows();
        accountDao.deleteAllRows();

        accountDao.save(new Account(200));
    }

    @After
    public void cleanRows(){
        dao.deleteAllRows();
        accountDao.deleteAllRows();
    }

    @Test
    public void getByUsername(){

        User user = new User("admin", "admin");

        user.setAccountId(accountDao.getAll().get(0).getId());

        dao.save(user);

        assertThat(dao.getByUsername("admin").getPassword(), is(user.getPassword()));

    }

    @Test
    public void saveUser(){

        User user = new User("admin", "admin");

        user.setAccountId(accountDao.getAll().get(0).getId());

        dao.save(user);

        assertThat(dao.getAll().get(0).getUsername(), is(user.getUsername()));

    }

    @Test
    public void updateUser(){

        User user = new User("admin", "admin");

        user.setAccountId(accountDao.getAll().get(0).getId());

        dao.save(user);

        User updatedUser = dao.getAll().get(0);
        updatedUser.setUsername("new admin");

        dao.update(updatedUser);

        assertThat(dao.getAll().get(0).getUsername(), is(updatedUser.getUsername()));

    }

    @Test
    public void deleteUser(){

        User user = new User("admin", "admin");

        user.setAccountId(accountDao.getAll().get(0).getId());

        dao.save(user);
        dao.deleteById(dao.getAll().get(0).getId());

        assertThat(dao.getAll().size(), is(0));

    }

    @Test
    public void correctPassword(){

        User user = new User("admin", "admin");

        user.setAccountId(accountDao.getAll().get(0).getId());

        dao.save(user);

        assertThat(dao.checkPassword(user), is(true));

    }

    @Test
    public void incorrectPassword(){

        User user = new User("admin", "admin");

        user.setAccountId(accountDao.getAll().get(0).getId());

        dao.save(user);

        user.setPassword("not a correct password");

        assertThat(dao.checkPassword(user), is(false));

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
