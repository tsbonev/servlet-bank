package core.repository;

import core.model.Transaction;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

public interface TransactionRepository {

    void setConnection(Connection conn);

    Transaction getById(int id);

    List<Transaction> getAll(int page);

    void deleteById(int id);

    void deleteAllRows();

    void save(Transaction transaction);

    void update(Transaction transaction);

    List<Transaction> getByUserId(int id, int page);

    List<Transaction> getByDate(Date date, int page);

    List<Transaction> getByOperation(Transaction.Operation operation, int page);

    void fillUsernames(List<Transaction> list);

    void setPageSize(int pageSize);
    int getPageSize();
    boolean hasNextPage(int currPage);
    int pageCount();
    int lastPage();
}
