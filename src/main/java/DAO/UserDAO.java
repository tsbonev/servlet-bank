package DAO;

import Model.User;

import java.util.List;

public interface UserDAO {

    User getById(int id);

    List<User> getAll();

    void deleteById(int id);

    void save(User user);

    User getByUsername(String username);

    boolean checkPassword(User user);

}
