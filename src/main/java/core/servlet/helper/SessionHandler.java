package core.servlet.helper;

public interface SessionHandler {

    String getUsername();

    void setUsername(String username);

    boolean isAuthorized();

    void setAuthorized(boolean authorized);

}
