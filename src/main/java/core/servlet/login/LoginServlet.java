package core.servlet.login;

import core.model.User;
import core.repository.UserRepository;
import core.servlet.filter.ConnectionPerRequest;
import core.servlet.helper.LoginSession;
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

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){

            page.redirectTo("/login", resp, req,
                    "errorMessage", "Something went wrong!");
            return;

        }

        user.setUsername(username);
        user.setPassword(password);

        boolean isInSystem = repo.checkPassword(user);

        if(isInSystem){

            if(!UserCounter.getInstance().userIsLoggedIn(user.getUsername())){
                UserCounter.getInstance().addUserToCount(user.getUsername());
            }

            LoginSession session = new LoginSession(username, true);
            req.getSession().setAttribute("authorized", session);


            page.redirectTo("/home", resp, req,
                    "successMessage", "Successfully logged in!");
        }
        else {
            page.redirectTo("/login", resp, req,
                    "errorMessage", "User not registered!");
        }


    }
}
