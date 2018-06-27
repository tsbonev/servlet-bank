package DAO;

import Model.Account;

import java.sql.Connection;
import java.util.List;

public class AccountDAOImpl implements AccountDAO {

    private Connection conn;

    protected Connection getConnection(){
        return MySQLConnection.getConnection();
    }

    public AccountDAOImpl(){
        this.conn = getConnection();
    }

    public Account getById(int id) {
        return null;
    }

    public List<Account> getAll() {
        return null;
    }

    public void save(Account account) {

    }

    public void deleteById(int id) {

    }

    public void createAccountTable(){

        

    }
}
