package core.repository;

import core.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class UserRepositoryImpl implements UserRepository {


    private Connection conn;

    protected Connection getConnection(){
        return MySQLConnection.getConnection();
    }

    public UserRepositoryImpl(){
        this.conn = getConnection();
    }

    public User getById(int id) {

        User user = new User();

        try{
            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM userDb" +
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
                    "SELECT * FROM userDb"
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
                    "DELETE FROM userDb" +
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
                    "DELETE FROM userDb"
            );

            delete.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(User user) {
        try{
            PreparedStatement save = conn.prepareStatement(
                    "INSERT INTO userDb(username, password, accountId)" +
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
                    "UPDATE userDb" +
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
        User user = new User();

        try {
            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM userDb" +
                            " WHERE username LIKE ?"
            );

            get.setString(1, username);

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

    public boolean checkPassword(User user) {

        try {
            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM userDb WHERE username LIKE ? AND password LIKE ? LIMIT 1"
            );

            get.setString(1, user.getUsername());
            get.setString(2, user.getPassword());

            ResultSet result = get.executeQuery();
            if(result.next()) return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void createUserTable() {

        try {
            PreparedStatement create = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS userDb(" +
                            "id int NOT NULL AUTO_INCREMENT," +
                            "username varchar(255) NOT NULL," +
                            "password varchar(255) NOT NULL," +
                            "accountId int NOT NULL," +
                            "PRIMARY KEY(id)," +
                            "FOREIGN KEY(accountId) REFERENCES account(id) ON DELETE CASCADE)"
            );
            create.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void dropUserTable() {

        try {
            PreparedStatement drop = conn.prepareStatement(
                    "DROP TABLE userDb"
            );

            drop.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return conn.getMetaData();
    }
}