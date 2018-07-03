package core.service;

import core.repository.AccountDAO;
import core.repository.AccountDAOImpl;
import core.model.Account;

import java.util.List;

public class AccountService {

    private static AccountDAO dao;

    private static AccountService instance;

    protected static AccountDAO wireDao(){
        return new AccountDAOImpl();
    }

    public static AccountService getInstance(){

        if(instance == null){
            instance = new AccountService(wireDao());
        }

        return instance;

    }

    public static AccountService getInstance(AccountDAO dao){

        instance = new AccountService(dao);

        return instance;

    }

    public static void clearInstance(){

        instance = null;

    }

    private AccountService(AccountDAO dao){
        this.dao = dao;
    }

    public Account getAccountById(int id){
        return dao.getById(id);
    }

    public void saveAccount(Account account){
        dao.save(account);
    }

    public void updateAccount(Account account){
        dao.update(account);
    }

    public void deleteAccountById(int id){
        dao.deleteById(id);
    }

    public List<Account> getAllAccounts(){
        return dao.getAll();
    }

}
