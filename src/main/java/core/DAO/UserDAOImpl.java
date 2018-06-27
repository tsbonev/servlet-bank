package core.DAO;

import core.Model.Account;
import core.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {


    private Connection conn;

    protected Connection getConnection(){
        return MySQLConnection.getConnection();
    }

    public UserDAOImpl(){
        this.conn = getConnection();
    }

    public User getById(int id) {

        User user = new User();

        try{
            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM users" +
                            " WHERE id = ?"
            );

            get.setInt(1, id);

            ResultSet result = get.executeQuery();

            while (result.next()){
                user.setId(result.getInt("id"));
                user.setAccountId(result.getInt("accountId"));
                user.setUsername(result.getString("username"));
                user.setPassword(result.getString("password"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;

    }

    public List<User> getAll() {
        List<User> users = new ArrayList<User>();

        try{
            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM users"
            );

            ResultSet result = get.executeQuery();

            while (result.next()){
                User user = new User();
                user.setId(result.getInt("id"));
                user.setAccountId(result.getInt("accountId"));
                user.setUsername(result.getString("username"));
                user.setPassword(result.getString("password"));
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public void deleteById(int id) {
        try{
            PreparedStatement delete = conn.prepareStatement(
                    "DELETE FROM users" +
                            " WHERE id = ?"
            );

            delete.setInt(1, id);

            delete.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllRows() {
        try {
            PreparedStatement delete = conn.prepareStatement(
                    "DELETE FROM users"
            );

            delete.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(User user) {
        try{
            PreparedStatement save = conn.prepareStatement(
                    "INSERT INTO users(username, password, accountId)" +
                            " VALUES(?, ?, ?)"
            );

            save.setString(1, user.getUsername());
            save.setString(2, user.getPassword());
            save.setInt(3, user.getAccountId());

            save.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(User user) {
        try{
            PreparedStatement update = conn.prepareStatement(
                    "UPDATE users" +
                            " SET username = ?," +
                            "password = ?," +
                            "accountId = ?" +
                            " WHERE id = ?"
            );

            update.setString(1, user.getUsername());
            update.setString(2, user.getPassword());
            update.setInt(3, user.getAccountId());
            update.setInt(4, user.getId());

            update.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getByUsername(String username) {
        return null;
    }

    public boolean checkPassword(User user) {
        return false;
    }
}
