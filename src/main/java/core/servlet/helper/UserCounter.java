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

    /**
     * Returns the number of usernames in the logged usernames list.
     *
     * @return
     */
    public int getUsersCount(){
        return users.size();
    }

    /**
     * Checks if a username is already in the logged usernames list.
     *
     * @param username to check
     * @return
     */
    public boolean userIsLoggedIn(String username){
        return users.contains(username);
    }

    /**
     * Adds a username to the logged usernames list.
     *
     * @param username to add
     */
    public void addUserToCount(String username){
        users.add(username);
    }

    /**
     * Removes a username from the logged usernames list.
     *
     * @param username to remove
     */
    public void removeUserFromCount(String username){
        users.remove(username);
    }

}
