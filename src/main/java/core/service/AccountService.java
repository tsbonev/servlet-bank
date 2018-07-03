package core.service;

import core.repository.AccountRepository;
import core.repository.AccountRepositoryImpl;
import core.model.Account;

import java.util.List;

public class AccountService {

    private static AccountRepository dao;

    private static AccountService instance;

    protected static AccountRepository wireDao(){
        return new AccountRepositoryImpl();
    }

    public static AccountService getInstance(){

        if(instance == null){
            instance = new AccountService(wireDao());
        }

        return instance;

    }

    public static AccountService getInstance(AccountRepository dao){

        instance = new AccountService(dao);

        return instance;

    }

    public static void clearInstance(){

        instance = null;

    }

    private AccountService(AccountRepository dao){
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
