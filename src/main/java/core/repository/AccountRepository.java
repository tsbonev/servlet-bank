package core.repository;

import core.model.Account;

import java.sql.Connection;
import java.util.List;

public interface AccountRepository {

    void setConnection(Connection conn);

    Account getById(int id);

    List<Account> getAll();

    void save(Account account);

    void update(Account account);

    void deleteById(int id);

    void deleteAllRows();

}
