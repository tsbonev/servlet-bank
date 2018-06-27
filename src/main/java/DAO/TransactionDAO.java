package DAO;

import Model.Transaction;

import java.sql.Date;
import java.util.List;

public interface TransactionDAO {

    Transaction getById(int id);

    List<Transaction> getAll();

    void deleteById(int id);

    void save(Transaction transaction);

    void update(Transaction transaction);

    List<Transaction> getByUserId(int id);

    List<Transaction> getByDate(Date date);

    List<Transaction> getByOperation(Transaction.Operation operation);


}
