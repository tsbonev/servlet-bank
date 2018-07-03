package core.repository;

import core.model.Transaction;
import core.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class TransactionRepositoryImpl implements TransactionRepository {

    private static int pageSize = 20;

    private Connection conn;

    public TransactionRepositoryImpl() {
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public void setPageSize(int pageSize) {
        TransactionRepositoryImpl.pageSize = pageSize;
    }

    public int getPageSize() {
        return TransactionRepositoryImpl.pageSize;
    }

    public Transaction getById(int id) {
        Transaction transaction = new Transaction();

        try {
            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM transactions" +
                            " WHERE id = ?"
            );

            get.setInt(1, id);

            ResultSet result = get.executeQuery();

            while (result.next()) {
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

    public List<Transaction> getAll(int page) {
        List<Transaction> transactionList = new ArrayList<Transaction>();

        try {
            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM transactions LIMIT ? OFFSET ?"
            );

            get.setInt(1, pageSize);
            get.setInt(2, (page - 1) * pageSize);

            ResultSet result = get.executeQuery();

            while (result.next()) {
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

    public double getBalance(int userId) {

        double balance = 0;

        try {

            PreparedStatement get = conn.prepareStatement(
                    "SELECT operation, amount FROM transactions WHERE userID = ?"
            );

            get.setInt(1, userId);

            ResultSet result = get.executeQuery();

            while (result.next()) {

                double amount = result.getDouble("amount");

                switch (result.getString("operation")){
                    case "DEPOSIT":
                        balance += amount;
                        break;
                    case "WITHDRAW":
                        balance -= amount;
                        break;
                    default:
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return balance;
    }


    public void deleteById(int id) {
        try {
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
        try {
            PreparedStatement save = conn.prepareStatement(
                    "INSERT INTO transactions(amount, userId, transactionDate, operation)" +
                            " VALUES(?, ?, ?, ?)"
            );


            save.setDouble(1, transaction.getAmount());
            save.setInt(2, transaction.getUserId());
            save.setDate(3, transaction.getDate());
            save.setString(4, transaction.getOperation().name());

            save.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Transaction transaction) {

        try {
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
            update.setInt(5, transaction.getId());

            update.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> getByUserId(int id, int page) {
        List<Transaction> transactionList = new ArrayList<Transaction>();

        try {

            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM transactions" +
                            " WHERE userId = ? LIMIT ? OFFSET ?"
            );

            get.setInt(1, id);
            get.setInt(2, pageSize);
            get.setInt(3, (page - 1) * pageSize);

            ResultSet result = get.executeQuery();

            while (result.next()) {

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

    public List<Transaction> getByDate(Date date, int page) {
        List<Transaction> transactionList = new ArrayList<Transaction>();

        try {

            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM transactions" +
                            " WHERE transactionDate LIKE ?" +
                            " LIMIT ? OFFSET ?"
            );

            get.setDate(1, date);
            get.setInt(2, pageSize);
            get.setInt(3, (page - 1) * pageSize);

            ResultSet result = get.executeQuery();

            while (result.next()) {

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

    public List<Transaction> getByOperation(Transaction.Operation operation, int page) {

        List<Transaction> transactionList = new ArrayList<Transaction>();

        String parsedOperation = operation.name();

        try {

            PreparedStatement get = conn.prepareStatement(
                    "SELECT * FROM transactions" +
                            " WHERE operation LIKE ?" +
                            " LIMIT ? OFFSET ?"
            );

            get.setString(1, parsedOperation);
            get.setInt(2, pageSize);
            get.setInt(3, (page - 1) * pageSize);

            ResultSet result = get.executeQuery();

            while (result.next()) {

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

    public int pageCount() {

        int numberOfRows = 0;

        try {
            PreparedStatement count = conn.prepareStatement(
                    "SELECT COUNT(*) as total FROM transactions"
            );

            ResultSet result = count.executeQuery();
            result.next();
            numberOfRows = result.getInt("total");


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return numberOfRows;

    }

    public boolean hasNextPage(int currPage) {
        int rowCount = this.pageCount();
        if (rowCount <= this.getPageSize() * currPage)
            return false;

        return true;
    }

    public int lastPage() {
        return (this.pageCount() + this.getPageSize() - 1) / this.getPageSize();
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

    public void fillUsernames(List<Transaction> list) {

        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        userRepository.setConnection(conn);

        for (Transaction transaction : list) {
            transaction.setUsername(userRepository.getById(transaction.getUserId()).getUsername());
        }

    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return conn.getMetaData();
    }
}