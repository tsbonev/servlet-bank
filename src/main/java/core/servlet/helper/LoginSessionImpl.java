package core.servlet.helper;

public class LoginSessionImpl implements LoginSession{

    private String username;
    private boolean authorized = false;

    public LoginSessionImpl() {}

    public LoginSessionImpl(String username, boolean authorized) {
        this.username = username;
        this.authorized = authorized;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}
