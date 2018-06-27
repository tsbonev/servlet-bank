package core.DAO;

import core.Model.User;

import java.util.List;

public interface UserDAO {

    User getById(int id);

    List<User> getAll();

    void deleteById(int id);

    void deleteAllRows();

    void save(User user);

    void update(User user);

    User getByUsername(String username);

    boolean checkPassword(User user);

}
