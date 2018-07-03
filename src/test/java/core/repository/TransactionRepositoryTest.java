package core.repository;

import core.model.Transaction;
import core.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransactionRepositoryTest {

    UserRepositoryImpl userRepository = new UserRepositoryImpl();
    TransactionRepositoryImpl transactionRepository = new TransactionRepositoryImpl();

    User user = new User("admin", "admin");
    Transaction transaction;

    Connection connection;

    @Before
    public void setUp(){

         connection = MySQLConnection.getConnection();

        userRepository.setConnection(connection);
        transactionRepository.setConnection(connection);

        userRepository.deleteAllRows();

        userRepository.save(user);
        user.setId(userRepository.getAll().get(0).getId());

        transaction = new Transaction();
        transaction.setAmount(200.0d);
        transaction.setOperation(Transaction.Operation.DEPOSIT);
        java.util.Date utilDate = Date.from(Instant.now());
        java.sql.Date sqlDate = new Date(utilDate.getTime());
        transaction.setDate(sqlDate);
        transaction.setUserId(user.getId());
    }

    @After
    public void cleanRows() throws SQLException {
        userRepository.deleteAllRows();
        connection.close();
    }

    @Test
    public void addTransaction() {

        transactionRepository.save(transaction);

        assertThat(transactionRepository.getAll(1).get(0).getUserId(), is(user.getId()));

    }

    @Test
    public void deleteTransaction(){

        transactionRepository.save(transaction);

        transactionRepository.deleteById(transactionRepository.getAll(1).get(0).getId());

        assertThat(transactionRepository.getAll(1).size(), is(0));

    }

    @Test
    public void updateTransaction(){

        transactionRepository.save(transaction);

        transaction = transactionRepository.getById(transactionRepository.getAll(1).get(0).getId());

        transaction.setOperation(Transaction.Operation.WITHDRAW);
        transaction.setAmount(445.0d);

        transactionRepository.update(transaction);

        Transaction updatedTransaction = transactionRepository.getAll(1).get(0);

        assertThat(updatedTransaction.getAmount(), is(445.0d));
        assertThat(updatedTransaction.getOperation(), is(transaction.getOperation()));

    }

    @Test
    public void getTransactionByUserId(){

        transactionRepository.save(transaction);

        List<Transaction> dbTransactions = transactionRepository.getByUserId(transaction.getUserId(), 1);

        assertThat(dbTransactions.size(), is(1));
        assertThat(dbTransactions.get(0).getAmount(), is(transaction.getAmount()));

    }

    @Test
    public void getTransactionByDate(){

        transactionRepository.save(transaction);

        List<Transaction> dbTransactions = transactionRepository.getByDate(transaction.getDate(), 1);

        assertThat(dbTransactions.size(), is(1));
        assertThat(dbTransactions.get(0).getAmount(), is(transaction.getAmount()));

    }

    @Test
    public void getTransactionByOperation(){

        transactionRepository.save(transaction);

        List<Transaction> dbTransactions = transactionRepository.getByOperation(Transaction.Operation.DEPOSIT, 1);

        assertThat(dbTransactions.size(), is(1));
        assertThat(dbTransactions.get(0).getAmount(), is(transaction.getAmount()));

    }

    @Test
    public void paginateTransactions(){

        transactionRepository.setPageSize(10);

        List<Transaction> transactionList = new ArrayList<>();

        for(int i = 0; i < 44; i++){
            transaction.setAmount(i);
            transactionList.add(transaction);
            transactionRepository.save(transaction);
        }

        assertThat(transactionRepository.getAll(2).size(), is(10));
        assertThat(transactionRepository.getAll(2).get(0).getAmount(), is(10.0));

    }

    @Ignore
    @Test
    public void createTable() throws SQLException {

        transactionRepository.createTransactionTable();

        DatabaseMetaData dbm = transactionRepository.getMetaData();

        ResultSet tables = dbm.getTables(null, null, "transactions", null);

        assertThat(tables.next(), is(true));

    }

    @Ignore
    @Test
    public void dropTable() throws SQLException {

        DatabaseMetaData dbm = transactionRepository.getMetaData();

        transactionRepository.dropTransactionTable();

        ResultSet tables = dbm.getTables(null, null, "transactions", null);

        assertThat(tables.next(), is(false));

    }

}
