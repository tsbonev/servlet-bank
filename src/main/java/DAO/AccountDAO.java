package DAO;

import Model.Account;

import java.util.List;

public interface AccountDAO {

    Account getById(int id);

    List<Account> getAll();

    void save(Account account);

    void deleteById(int id);

}
