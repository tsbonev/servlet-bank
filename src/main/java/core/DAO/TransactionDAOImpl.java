package core.DAO;

import core.Model.Account;
import core.Model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class TransactionDAOImpl implements TransactionDAO {

    private Connection conn;

    protected Connection getConnection(){
        return MySQLConnection.getConnection();
    }

    public TransactionDAOImpl(){
        this.conn = getConnection();
    }

    public Transaction getById(int id) {
        Transaction transaction = new Transaction();

        try{
            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM transactions" +
                            " WHERE id = ?"
            );

            get.setInt(1, id);

            ResultSet result = get.executeQuery();

            while (result.next()){
                transaction.setId(result.getInt("id"));
                transaction.setUserId(result.getInt("userId"));
                transaction.setAmount(result.getDouble("amount"));
                transaction.setOperation(Transaction.Operation.valueOf(result.getString("operation")));
                transaction.setDate(result.getDate("transactionDate"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transaction;
    }

    public List<Transaction> getAll() {
        List<Transaction> transactionList = new ArrayList<Transaction>();

        try{
            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM transactions"
            );

            ResultSet result = get.executeQuery();

            while (result.next()){
                Transaction transaction = new Transaction();
                transaction.setId(result.getInt("id"));
                transaction.setUserId(result.getInt("userId"));
                transaction.setDate(result.getDate("transactionDate"));
                transaction.setOperation(Transaction.Operation.valueOf(result.getString("operation")));
                transaction.setAmount(result.getDouble("amount"));

                transactionList.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionList;
    }

    public void deleteById(int id) {
        try{
            PreparedStatement delete = conn.prepareStatement(
                    "DELETE FROM transactions" +
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
                    "DELETE FROM transactions"
            );

            delete.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(Transaction transaction) {

    }

    public void update(Transaction transaction) {
        try{
            PreparedStatement update = conn.prepareStatement(
                    "UPDATE transactions" +
                            " SET amount = ?," +
                            "userId = ?," +
                            "transactionDate = ?," +
                            "operation = ?" +
                            " WHERE id = ?"
            );

            update.setDouble(1, transaction.getAmount());
            update.setInt(2, transaction.getUserId());
            update.setDate(3, transaction.getDate());
            update.setString(4, transaction.getOperation().name());

            update.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> getByUserId(int id) {
        List<Transaction> transactionList = new ArrayList<Transaction>();

        try {

            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM transactions" +
                            " WHERE userId = ?"
            );

            get.setInt(1, id);

            ResultSet result = get.executeQuery();

            while (result.next()){

                Transaction transaction = new Transaction();

                transaction.setId(result.getInt("id"));
                transaction.setUserId(id);
                transaction.setDate(result.getDate("transactionDate"));
                transaction.setOperation(Transaction.Operation.valueOf(result.getString("operation")));
                transaction.setAmount(result.getDouble("amount"));

                transactionList.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionList;
    }

    public List<Transaction> getByDate(Date date) {
        List<Transaction> transactionList = new ArrayList<Transaction>();

        try {

            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM transactions" +
                            " WHERE transactionDate LIKE ?"
            );

            get.setDate(1, date);

            ResultSet result = get.executeQuery();

            while (result.next()){

                Transaction transaction = new Transaction();

                transaction.setId(result.getInt("id"));
                transaction.setUserId(result.getInt("userId"));
                transaction.setDate(result.getDate("transactionDate"));
                transaction.setOperation(Transaction.Operation.valueOf(result.getString("operation")));
                transaction.setAmount(result.getDouble("amount"));

                transactionList.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionList;

    }

    public List<Transaction> getByOperation(Transaction.Operation operation) {

        List<Transaction> transactionList = new ArrayList<Transaction>();

        String parsedOperation = operation.name();

        try {

            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM transactions" +
                            " WHERE operation LIKE ?"
            );

            get.setString(1, parsedOperation);

            ResultSet result = get.executeQuery();

            while (result.next()){

                Transaction transaction = new Transaction();

                transaction.setId(result.getInt("id"));
                transaction.setUserId(result.getInt("userId"));
                transaction.setDate(result.getDate("transactionDate"));
                transaction.setOperation(operation);
                transaction.setAmount(result.getDouble("amount"));

                transactionList.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionList;

    }

    public void createTransactionTable() {

        try {
            PreparedStatement create = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS transactions(" +
                            "id int NOT NULL AUTO_INCREMENT," +
                            "userId int NOT NULL," +
                            "operation varchar(255) NOT NULL," +
                            "amount double NOT NULL," +
                            "transactionDate date NOT NULL," +
                            "PRIMARY KEY(id)," +
                            "FOREIGN KEY(userId) REFERENCES userDb(id) ON DELETE CASCADE)"
            );
            create.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void dropTransactionTable() {

        try {
            PreparedStatement drop = conn.prepareStatement(
                    "DROP TABLE transactions"
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
