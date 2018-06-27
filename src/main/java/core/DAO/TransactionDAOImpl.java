package core.DAO;

import core.Model.Transaction;

import java.sql.*;
import java.util.List;

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
        return null;
    }

    public void deleteById(int id) {

    }

    public void deleteAllRows() {

    }

    public void save(Transaction transaction) {

    }

    public void update(Transaction transaction) {

    }

    public List<Transaction> getByUserId(int id) {
        return null;
    }

    public List<Transaction> getByDate(Date date) {
        return null;
    }

    public List<Transaction> getByOperation(Transaction.Operation operation) {
        return null;
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
