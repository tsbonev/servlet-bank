package core.Service;

import core.DAO.UserDAO;
import core.DAO.UserDAOImpl;
import core.Model.Account;
import core.Model.User;

import java.util.List;

public class UserService {

    private static UserDAO dao;

    private static UserService instance;

    protected static UserDAO wireDao(){
        return new UserDAOImpl();
    }

    public static UserService getInstance(){

        if(instance == null) {
            instance = new UserService(wireDao());
        }

        return instance;

    }

    public static UserService getInstance(UserDAO dao){

        instance = new UserService(dao);

        return instance;

    }

    public static void clearInstance(){

        instance = null;

    }

    private UserService(UserDAO dao){
        this.dao = dao;
    }

    public User getUserById(int id){
        return dao.getById(id);
    }

    public void saveUser(User user){

        user.setAccountId(createAccountForUser().getId());

        dao.save(user);
    }

    private Account createAccountForUser(){

        AccountService accountService = AccountService.getInstance();
        accountService.saveAccount(new Account(0.0d));

        int accountListSize = accountService.getAllAccounts().size();

        Account latest = accountService.getAllAccounts().get(accountListSize - 1);

        return latest;
    }

    public Account getUserAccount(String username){
        User user = getUserByUsername(username);
        AccountService accountService = AccountService.getInstance();
        return accountService.getAccountById(user.getAccountId());
    }

    public void updateUser(User user){
        dao.update(user);
    }

    public void deleteUserById(int id){
        dao.deleteById(id);
        deleteUserAccount(id);
    }

    private void deleteUserAccount(int id){
        AccountService accountService = AccountService.getInstance();
        accountService.deleteAccountById(id);
    }

    public List<User> getAllUsers(){
        return dao.getAll();
    }

    public User getUserByUsername(String username){
        return dao.getByUsername(username);
    }

    public boolean checkUserPassword(User user){
        return dao.checkPassword(user);
    }

}
