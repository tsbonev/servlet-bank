package core.repository;

import core.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class MySQLTransactionRepository implements TransactionRepository {

    private static int pageSize = 10;

    private Connection conn;

    public MySQLTransactionRepository() {
    }

    /**
     * Sets this classes' connection to an injected connection.
     *
     * @param conn that is injected
     */
    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    /**
     * Sets the max size for transaction's pagination.
     *
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        MySQLTransactionRepository.pageSize = pageSize;
    }

    /**
     * Returns the max size of the static pageSize variable.
     *
     * @return page size
     */
    public int getPageSize() {
        return MySQLTransactionRepository.pageSize;
    }

    /**
     * Gets a transaction by its id.
     *
     * @param id of the transaction
     * @return the found transaction or an empty transaction
     */
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

    /**
     * Returns all transactions paginated.
     *
     * @param page to return
     * @return transactions in a given page
     */
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

    /**
     * Sums up the balance of an account by looking up transactions.
     *
     * @param userId to look up in transactions
     * @return balance of the user
     */
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

    /**
     * Deletes a transaction by id.
     *
     * @param id to delete
     */
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

    /**
     * Clears the rows of the transaction database.
     */
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

    /**
     * Saves a transaction in the database.
     *
     * @param transaction to save
     */
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

    /**
     * Updates a transaction row in the database.
     *
     * @param transaction to update
     */
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

    /**
     * Gets all the transactions of a user paginated.
     *
     * @param id of the user
     * @param page to return
     * @return transactions in a given page
     */
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

    /**
     * Gets all transactions that have happened on a given date paginated.
     *
     * @param date to search for
     * @param page to return
     * @return transaction is a given page
     */
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

    /**
     * Returns all transactions with a given operation paginated.
     *
     * @param operation to search for
     * @param page to get
     * @return transactions in a given page
     */
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

    /**
     * Creates a transaction table in the database.
     */
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

    /**
     * Drops transaction table form the database.
     */
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

    /**
     * Takes the userIds in a transaction and gets their usernames.
     *
     * @param list transactions with usernames
     */
    public void fillUsernames(List<Transaction> list) {

        MySQLUserRepository userRepository = new MySQLUserRepository();
        userRepository.setConnection(conn);

        for (Transaction transaction : list) {
            transaction.setUsername(userRepository.getById(transaction.getUserId()).getUsername());
        }

    }

    /**
     * Counts the number of transactions that a given user has.
     *
     * @param userId of the user
     * @return count of the transaction rows
     */
    @Override
    public int getRowsForUserId(int userId) {

        int rows = 0;

        try {

            PreparedStatement select;

            if(userId == 0){
                select = conn.prepareStatement(
                        "SELECT COUNT(*) AS total FROM transactions"
                );
            }else {
                select = conn.prepareStatement(
                        "SELECT COUNT(*) AS total FROM transactions WHERE userId = ?"
                );
                select.setInt(1, userId);
            }

            ResultSet result = select.executeQuery();
            result.next();
            rows = result.getInt("total");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;

    }

    /**
     * Returns the metadata of the mysql table.
     * @return table metadata
     * @throws SQLException
     */
    public DatabaseMetaData getMetaData() throws SQLException {
        return conn.getMetaData();
    }
}