package core.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import core.model.Account;

public class AccountDAOImpl implements AccountDAO {

    private Connection conn;

    protected Connection getConnection(){
        return MySQLConnection.getConnection();
    }

    public AccountDAOImpl(){
        this.conn = getConnection();
    }

    public Account getById(int id) {
        Account account = new Account();

        try{
            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM account" +
                            " WHERE id = ?"
            );

            get.setInt(1, id);

            ResultSet result = get.executeQuery();

            while (result.next()){
                account.setId(result.getInt("id"));
                account.setAmount(result.getDouble("amount"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

            return account;

    }

    public List<Account> getAll() {
        List<Account> accounts = new ArrayList<Account>();

        try{
            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM account"
            );

            ResultSet result = get.executeQuery();

            while (result.next()){
                Account account = new Account();
                account.setId(result.getInt("id"));
                account.setAmount(result.getDouble("amount"));
                accounts.add(account);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

            return accounts;

    }

    public void save(Account account) {

        try{
            PreparedStatement save = conn.prepareStatement(
                    "INSERT INTO account(amount)" +
                            " VALUES(?)"
            );

            save.setDouble(1, account.getAmount());

            save.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void update(Account account){

        try{
            PreparedStatement update = conn.prepareStatement(
                    "UPDATE account" +
                            " SET amount = ?" +
                            " WHERE id = ?"
            );

            update.setDouble(1, account.getAmount());
            update.setInt(2, account.getId());

            update.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void deleteById(int id) {

        try{
            PreparedStatement delete = conn.prepareStatement(
                    "DELETE FROM account" +
                            " WHERE id = ?"
            );

            delete.setInt(1, id);

            delete.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllRows(){

        try {
            PreparedStatement delete = conn.prepareStatement(
                    "DELETE FROM account"
            );

            delete.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void createAccountTable() {

        try {
            PreparedStatement create = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS account(" +
                            "id int NOT NULL AUTO_INCREMENT," +
                            "amount double NOT NULL," +
                            "PRIMARY KEY(id))"
            );
            create.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void dropAccountTable() {

        try {
            PreparedStatement drop = conn.prepareStatement(
                    "DROP TABLE account"
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