package core.repository;

import core.model.Account;
import core.model.Transaction;
import core.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransactionRepositoryTest {

    AccountRepositoryImpl accountDao = new AccountRepositoryImpl();
    UserRepositoryImpl userDao = new UserRepositoryImpl();
    TransactionRepositoryImpl dao = new TransactionRepositoryImpl();

    Account account = new Account(200);
    User user = new User("admin", "admin");
    Transaction transaction;

    @Before
    public void setUp(){

        userDao.deleteAllRows();
        accountDao.deleteAllRows();

        accountDao.save(account);
        user.setAccountId(accountDao.getAll().get(0).getId());
        userDao.save(user);
        user.setId(userDao.getAll().get(0).getId());

        transaction = new Transaction();
        transaction.setAmount(200.0d);
        transaction.setOperation(Transaction.Operation.DEPOSIT);
        java.util.Date utilDate = Date.from(Instant.now());
        java.sql.Date sqlDate = new Date(utilDate.getTime());
        transaction.setDate(sqlDate);
        transaction.setUserId(user.getId());
    }

    @After
    public void cleanRows(){
        userDao.deleteAllRows();
        accountDao.deleteAllRows();
    }

    @Test
    public void addTransaction() {

        dao.save(transaction);

        assertThat(dao.getAll(1).get(0).getUserId(), is(user.getId()));

    }

    @Test
    public void deleteTransaction(){

        dao.save(transaction);

        dao.deleteById(dao.getAll(1).get(0).getId());

        assertThat(dao.getAll(1).size(), is(0));

    }

    @Test
    public void updateTransaction(){

        dao.save(transaction);

        transaction = dao.getById(dao.getAll(1).get(0).getId());

        transaction.setOperation(Transaction.Operation.WITHDRAW);
        transaction.setAmount(445.0d);

        dao.update(transaction);

        Transaction updatedTransaction = dao.getAll(1).get(0);

        assertThat(updatedTransaction.getAmount(), is(445.0d));
        assertThat(updatedTransaction.getOperation(), is(transaction.getOperation()));

    }

    @Test
    public void getTransactionByUserId(){

        dao.save(transaction);

        List<Transaction> dbTransactions = dao.getByUserId(transaction.getUserId(), 1);

        assertThat(dbTransactions.size(), is(1));
        assertThat(dbTransactions.get(0).getAmount(), is(transaction.getAmount()));

    }

    @Test
    public void getTransactionByDate(){

        dao.save(transaction);

        List<Transaction> dbTransactions = dao.getByDate(transaction.getDate(), 1);

        assertThat(dbTransactions.size(), is(1));
        assertThat(dbTransactions.get(0).getAmount(), is(transaction.getAmount()));

    }

    @Test
    public void getTransactionByOperation(){

        dao.save(transaction);

        List<Transaction> dbTransactions = dao.getByOperation(Transaction.Operation.DEPOSIT, 1);

        assertThat(dbTransactions.size(), is(1));
        assertThat(dbTransactions.get(0).getAmount(), is(transaction.getAmount()));

    }

    @Test
    public void paginateTransactions(){

        dao.setPageSize(10);

        List<Transaction> transactionList = new ArrayList<>();

        for(int i = 0; i < 44; i++){
            transaction.setAmount(i);
            transactionList.add(transaction);
            dao.save(transaction);
        }

        assertThat(dao.getAll(2).size(), is(10));
        assertThat(dao.getAll(2).get(0).getAmount(), is(10.0));

    }

    @Ignore
    @Test
    public void createTable() throws SQLException {

        dao.createTransactionTable();

        DatabaseMetaData dbm = dao.getMetaData();

        ResultSet tables = dbm.getTables(null, null, "transactions", null);

        assertThat(tables.next(), is(true));

    }

    @Ignore
    @Test
    public void dropTable() throws SQLException {

        DatabaseMetaData dbm = dao.getMetaData();

        dao.dropTransactionTable();

        ResultSet tables = dbm.getTables(null, null, "transactions", null);

        assertThat(tables.next(), is(false));

    }

}
