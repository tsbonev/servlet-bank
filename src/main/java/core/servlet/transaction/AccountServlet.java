package core.servlet.transaction;

import core.model.User;
import core.repository.TransactionRepository;
import core.repository.UserRepository;
import core.servlet.filter.ConnectionPerRequest;
import core.servlet.helper.SessionHandler;
import core.servlet.helper.PageHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {

    PageHandler page;
    TransactionRepository transactionRepository;
    UserRepository userRepository;

    public AccountServlet(PageHandler page, TransactionRepository transactionRepository,
                          UserRepository userRepository){
        this.page = page;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Gets a connection from the ThreadLocal and
     * injects it into the repositories.
     *
     * @param transactionRepository
     * @param userRepository
     */
    protected void setConnection(TransactionRepository transactionRepository,
                                 UserRepository userRepository){
        userRepository.setConnection(ConnectionPerRequest.connection.get());
        transactionRepository.setConnection(ConnectionPerRequest.connection.get());
    }

    /**
     * Sets the connection of the repositories,
     * gets the LoginSession and counts up the
     * balance of the session username or the
     * username given in the url.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setAttribute("title", "Account");

        setConnection(transactionRepository, userRepository);

        User user = getUser(req);

        if(!validateUser(user, req, resp)) return;

        double balance = transactionRepository.getBalance(user.getId());

        String username = req.getParameter("username");

        req.setAttribute("balance", balance);
        req.setAttribute("passedUsername", username);

        page.getPage("view/transaction/account.jsp", req, resp);

    }

    /**
     * Validates a user object.
     *
     * @param user to validate
     * @param req servlet request
     * @param resp servlet response
     * @return result of the validation
     * @throws IOException
     */
    private boolean validateUser(User user, HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(user.getId() == 0){
            page.redirectTo("/home", resp, req,
                    "errorMessage", "No such user exists!");
            return false;
        }
        return true;
    }

    /**
     * Gets the user from the specified
     * url or if no parameter has been
     * passed the user from the session.
     *
     * @param req servlet request
     * @return the user
     */
    private User getUser(HttpServletRequest req){

        SessionHandler session = (SessionHandler) req.getSession().getAttribute("authorized");

        String username = req.getParameter("username");

        if(username == null){
            username = session.getUsername();
        }

        return userRepository.getByUsername(username);

    }
}
