package core.servlet.helper;

public interface LoginSession {

    String getUsername();

    void setUsername(String username);

    boolean isAuthorized();

    void setAuthorized(boolean authorized);

}
