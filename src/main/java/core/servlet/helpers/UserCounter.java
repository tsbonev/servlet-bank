package core.servlet.helpers;

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

    public synchronized int getUsersCount(){
        return users.size();
    }

    public synchronized boolean userIsLoggedIn(String username){
        return users.contains(username);
    }

    public synchronized void addUserToCount(String username){
        users.add(username);
    }

    public synchronized void removeUserFromCount(String username){
        users.remove(username);
    }

}
