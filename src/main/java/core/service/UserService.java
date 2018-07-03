package core.service;

import core.repository.UserRepository;
import core.repository.UserRepositoryImpl;
import core.model.User;

import java.util.List;

public class UserService {

    private static UserRepository dao;

    private static UserService instance;

    protected static UserRepository wireDao(){
        return new UserRepositoryImpl();
    }

    public static UserService getInstance(){

        if(instance == null) {
            instance = new UserService(wireDao());
        }

        return instance;

    }

    public static UserService getInstance(UserRepository dao){

        instance = new UserService(dao);

        return instance;

    }

    public static void clearInstance(){

        instance = null;

    }

    private UserService(UserRepository dao){
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
