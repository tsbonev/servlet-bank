package core.Service;

import core.DAO.TransactionDAO;
import core.DAO.TransactionDAOImpl;
import core.Model.Transaction;

import java.sql.Date;
import java.util.List;

public class TransactionService {

    private static TransactionDAO dao;

    private static TransactionService instance;

    protected static TransactionDAO wireDao(){
        return new TransactionDAOImpl();
    }

    public static TransactionService getInstance(){

        if(instance == null) {
            instance = new TransactionService(wireDao());
        }

        return instance;

    }

    public static TransactionService getInstance(TransactionDAO dao){

        instance = new TransactionService(dao);

        return instance;

    }

    public static void clearInstance(){

        instance = null;

    }

    private TransactionService(TransactionDAO dao){
        this.dao = dao;
    }

    public Transaction getTransactionById(int id){
        return dao.getById(id);
    }

    public void saveTransaction(Transaction transaction){
        dao.save(transaction);
    }

    public void updateTransaction(Transaction transaction){
        dao.update(transaction);
    }

    public void deleteTransactionById(int id){
        dao.deleteById(id);
    }

    public void setPageSize(int pageSize){

        dao.setPageSize(pageSize);

    }

    public boolean hasNextPage(int currPage){

        int rowCount = dao.getCount();
        if(rowCount <= dao.getPageSize() * currPage)
            return false;

            return true;

    }

    public int pageCount(){
        return dao.getCount();
    }

    public int lastPage(){
        return (dao.getCount() + dao.getPageSize() - 1) / dao.getPageSize();
    }

    public List<Transaction> getAllTransactions(int page){
        return dao.getAll(page);
    }

    public List<Transaction> getTransactionsByOperation(Transaction.Operation operation, int page){
        return dao.getByOperation(operation, page);
    }

    public List<Transaction> getTransactionsByDate(Date date, int page){
        return dao.getByDate(date, page);
    }

    public List<Transaction> getTransactionsByUserId(int userId, int page){
        return dao.getByUserId(userId, page);
    }

    public void fillUsernames(List<Transaction> list){

        UserService userService = UserService.getInstance();

        for (Transaction transaction : list) {
            transaction.setUsername(userService.getUserById(transaction.getUserId()).getUsername());
        }

    }

}
