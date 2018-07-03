package core.repository;

import core.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserRepository {

    void setConnection(Connection conn);

    User getById(int id);

    List<User> getAll();

    void deleteById(int id);

    void deleteAllRows();

    void save(User user);

    void update(User user);

    User getByUsername(String username);

    boolean checkPassword(User user);

}
