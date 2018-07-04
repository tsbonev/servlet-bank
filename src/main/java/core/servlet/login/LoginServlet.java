package core.servlet.login;

import core.model.User;
import core.repository.UserRepository;
import core.servlet.filter.ConnectionPerRequest;
import core.servlet.helper.LoginSession;
import core.servlet.helper.LoginSessionImpl;
import core.servlet.helper.Page;
import core.servlet.helper.UserCounter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    UserRepository repo;
    Page page;

    public LoginServlet(Page page, UserRepository repository){
        this.page = page;
        this.repo = repository;
    }

    /**
     * Gets a connection from the ThreadLocal and injects it into the repositories.
     *
     * @param userRepository
     */
    protected void setConnection(UserRepository userRepository){
        userRepository.setConnection(ConnectionPerRequest.connection.get());
    }

    /**
     * Gets the login jsp form and changes the title to login.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setAttribute("title", "Login");
        page.getPage("view/user/login.jsp", req, resp);

    }

    /**
     * Connects to the database, checks the data
     * from the sent login form and authenticates it.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        setConnection(repo);

        User user = new User();

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if(!validateUserForm(username, password, req, resp)) return;

        user.setUsername(username);
        user.setPassword(password);

        if(userIsInSystem(user, resp, req)){

            loginUserIntoSession(user, req, resp);

            page.redirectTo("/home", resp, req,
                    "successMessage", "Successfully logged in!");
        }
    }

    /**
     * Adds the user to the session
     * and starts up the authorized session attribute;
     * adds the username to the user counter if not
     * already present.
     *
     * @param user to log into session
     * @param req servlet request
     * @param resp servlet response
     */
    private void loginUserIntoSession(User user, HttpServletRequest req, HttpServletResponse resp){

        UserCounter counter = UserCounter.getInstance();

        if(!counter.userIsLoggedIn(user.getUsername())){
            counter.addUserToCount(user.getUsername());
        }

        LoginSession session = new LoginSessionImpl(user.getUsername(), true);
        req.getSession().setAttribute("authorized", session);

    }

    /**
     * Checks if the username is in the database
     * and if it is redirects to the login page with
     * an error.
     *
     * @param user to check
     * @param resp servlet response
     * @param req servlet request
     * @return result of the check
     * @throws IOException
     */
    private boolean userIsInSystem(User user, HttpServletResponse resp, HttpServletRequest req) throws IOException {

        if(!repo.checkPassword(user)){
            page.redirectTo("/login", resp, req,
                    "errorMessage", "User not registered!");
            return false;
        }
        return true;
    }

    /**
     * Checks if the form was sent empty
     * and redirects to an error page if it is.
     *
     * @param username to check
     * @param password to check
     * @param req servlet request
     * @param resp servlet request
     * @return result of the check
     * @throws IOException
     */
    private boolean validateUserForm(String username, String password,
                                     HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){

            page.redirectTo("/login", resp, req,
                    "errorMessage", "Something went wrong!");
            return false;

        }
        return true;
    }

}
