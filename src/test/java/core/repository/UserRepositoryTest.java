package core.repository;

import core.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class UserRepositoryTest {

    MySQLUserRepository userRepository = new MySQLUserRepository();
    Connection connection;

    @Before
    public void addAccount(){
        connection = MySQLConnection.getConnection();
        userRepository.setConnection(connection);
        userRepository.deleteAllRows();

    }

    @After
    public void cleanRows() throws SQLException {
        userRepository.deleteAllRows();
        connection.close();
    }

    @Test
    public void getByUsername(){

        User user = new User("admin", "admin");

        this.userRepository.save(user);

        assertThat(this.userRepository.getByUsername("admin").getPassword(), is(user.getPassword()));

    }

    @Test
    public void saveUser(){

        User user = new User("admin", "admin");

        this.userRepository.save(user);

        assertThat(this.userRepository.getAll().get(0).getUsername(), is(user.getUsername()));

    }

    @Test
    public void updateUser(){

        User user = new User("admin", "admin");

        this.userRepository.save(user);

        User updatedUser = this.userRepository.getAll().get(0);
        updatedUser.setUsername("new admin");

        this.userRepository.update(updatedUser);

        assertThat(this.userRepository.getAll().get(0).getUsername(), is(updatedUser.getUsername()));

    }

    @Test
    public void deleteUser(){

        User user = new User("admin", "admin");

        this.userRepository.save(user);
        this.userRepository.deleteById(this.userRepository.getAll().get(0).getId());

        assertThat(this.userRepository.getAll().size(), is(0));

    }

    @Test
    public void correctPassword(){

        User user = new User("admin", "admin");

        this.userRepository.save(user);

        assertThat(this.userRepository.checkPassword(user), is(true));

    }

    @Test
    public void incorrectPassword(){

        User user = new User("admin", "admin");

        this.userRepository.save(user);

        user.setPassword("not a correct password");

        assertThat(this.userRepository.checkPassword(user), is(false));

    }

    @Ignore
    @Test
    public void createTable() throws SQLException {

        userRepository.createUserTable();

        DatabaseMetaData dbm = userRepository.getMetaData();

        ResultSet tables = dbm.getTables(null, null, "userDb", null);

        assertThat(tables.next(), is(true));

    }

    @Ignore
    @Test
    public void dropTable() throws SQLException {

        DatabaseMetaData dbm = userRepository.getMetaData();

        userRepository.dropUserTable();

        ResultSet tables = dbm.getTables(null, null, "userDb", null);

        assertThat(tables.next(), is(false));

    }

}
