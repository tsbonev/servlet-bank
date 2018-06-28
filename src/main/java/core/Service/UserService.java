package core.Service;

import core.DAO.UserDAO;
import core.DAO.UserDAOImpl;
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
        dao.save(user);
    }

    public void updateUser(User user){
        dao.update(user);
    }

    public void deleteUserById(int id){
        dao.deleteById(id);
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
