package core.repository;

import core.model.Account;

import java.util.List;

public interface AccountDAO {

    Account getById(int id);

    List<Account> getAll();

    void save(Account account);

    void update(Account account);

    void deleteById(int id);

    void deleteAllRows();

}
