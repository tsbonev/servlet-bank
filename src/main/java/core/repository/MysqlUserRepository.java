package core.repository;

import core.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class MysqlUserRepository implements UserRepository {

    private Connection conn;

    public MysqlUserRepository(){ }

    /**
     * Sets this classes' connection to an injected connection.
     *
     * @param conn that is injected
     */
    public void setConnection(Connection conn){
        this.conn = conn;
    }

    /**
     * Gets a user from the database by id.
     *
     * @param id of the user
     * @return found user or empty user
     */
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
                user.setUsername(result.getString("username"));
                user.setPassword(result.getString("password"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;

    }

    /**
     * Returns all users from the database.
     *
     * @return list of all users
     */
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
                user.setUsername(result.getString("username"));
                user.setPassword(result.getString("password"));
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Deletes a user by id.
     *
     * @param id of the user
     */
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

    /**
     * Clears the rows of the user table.
     */
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

    /**
     * Saves a user into the database.
     *
     * @param user to be saved
     */
    public void save(User user) {
        try{
            PreparedStatement save = conn.prepareStatement(
                    "INSERT INTO userDb(username, password)" +
                            " VALUES(?, ?)"
            );

            save.setString(1, user.getUsername());
            save.setString(2, user.getPassword());

            save.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates a user in the database.
     *
     * @param user to be updated
     */
    public void update(User user) {
        try{
            PreparedStatement update = conn.prepareStatement(
                    "UPDATE userDb" +
                            " SET username = ?," +
                            "password = ?" +
                            " WHERE id = ?"
            );

            update.setString(1, user.getUsername());
            update.setString(2, user.getPassword());
            update.setInt(3, user.getId());

            update.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a user by his username.
     *
     * @param username to search for
     * @return found user or empty user
     */
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
                user.setUsername(result.getString("username"));
                user.setPassword(result.getString("password"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * Checks if a given user is in the database.
     *
     * @param user to check
     * @return whether or not the user exists in the given state
     */
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

    /**
     * Creates a user table in the database.
     */
    public void createUserTable() {

        try {
            PreparedStatement create = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS userDb(" +
                            "id int NOT NULL AUTO_INCREMENT," +
                            "username varchar(255) NOT NULL," +
                            "password varchar(255) NOT NULL," +
                            "PRIMARY KEY(id))"
            );
            create.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Drops the user table from the database.
     */
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

    /**
     * Returns the mysql metadata of the user table.
     *
     * @return table metadata
     * @throws SQLException
     */
    public DatabaseMetaData getMetaData() throws SQLException {
        return conn.getMetaData();
    }
}