package core.servlet.helper;

import java.util.ArrayList;
import java.util.List;

public class UserCounter {

    private static UserCounter instance = new UserCounter();

    public static UserCounter getInstance(){

        if(instance == null) instance = new UserCounter();
        return instance;
    }

    public static void clearInstance(){
        instance = null;
    }

    private UserCounter(){}


    List<String> users = new ArrayList<>();

    public int getUsersCount(){
        return users.size();
    }

    public boolean userIsLoggedIn(String username){
        return users.contains(username);
    }

    public void addUserToCount(String username){
        users.add(username);
    }

    public void removeUserFromCount(String username){
        users.remove(username);
    }

}
