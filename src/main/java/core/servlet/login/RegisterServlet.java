package core.servlet.login;

import core.model.Transaction;
import core.model.User;
import core.repository.TransactionRepository;
import core.repository.UserRepository;
import core.servlet.filter.ConnectionPerRequest;
import core.servlet.helper.PageHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static String usernamePattern = "^[\\w]{5,15}$";
    private static String passwordPattern = "^[\\w]{8,20}$";
    private static double minAmount = 5;

    UserRepository userRepository;
    TransactionRepository transactionRepository;

    PageHandler page;

    /**
     * Gets a connection from the ThreadLocal and injects it into the repositories.
     *
     * @param userRepository
     * @param transactionRepository
     */
    protected void setConnection(UserRepository userRepository, TransactionRepository transactionRepository){
        userRepository.setConnection(ConnectionPerRequest.connection.get());
        transactionRepository.setConnection(ConnectionPerRequest.connection.get());
    }

    public RegisterServlet(PageHandler page, UserRepository userRepository, TransactionRepository transactionRepository){
        this.page = page;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Gets the register jsp form and sets the title to Register.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("title", "Register");
        page.getPage("view/user/register.jsp", req, resp);
    }

    /**
     * Sets the connections for the repositories,
     * checks if the register form fields are valid,
     * checks if the username is not taken and
     * registers the user if all checks are passed
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        setConnection(userRepository, transactionRepository);

        User user = new User();
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if(!validateForm(username, password, req, resp)) return;

        user.setUsername(username);
        user.setPassword(password);

        if(!usernameIsRegistered(username, req, resp)){
            userRepository.save(user);

            createInitialTransaction(user);

            page.redirectTo("/home", resp, req,
                    "successMessage", "User registered successfully!");
        }

    }

    /**
     * Checks if the username is already
     * in the database and redirects
     * to the register form if it is.
     *
     * @param username to check
     * @param req servlet request
     * @param resp servlet response
     * @return check result
     * @throws IOException
     */
    private boolean usernameIsRegistered(String username,
                                         HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(userRepository.getByUsername(username).getId() != 0){
            page.redirectTo("/register", resp, req,
                    "errorMessage", "Username taken!");
            return true;
        }
        return false;
    }

    /**
     * Validates the username and password
     * from the register form and
     * redirects to an error page if
     * validations fail.
     *
     * @param username to validate
     * @param password to validate
     * @param req servlet request
     * @param resp servlet response
     * @return validation result
     * @throws IOException
     */
    private boolean validateForm(String username, String password,
                                 HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(!Pattern.matches(usernamePattern, username) || !Pattern.matches(passwordPattern, password)){
            page.redirectTo("/register", resp, req,
                    "errorMessage", "Something went wrong!");
            return false;
        }
        return true;
    }

    /**
     * Creates an initial transaction for each registered
     * user with an amount.
     *
     * @param user
     */
    private void createInitialTransaction(User user){

        Transaction transaction = new Transaction();
        transaction.setAmount(minAmount);
        transaction.setDate(Date.valueOf(LocalDate.now()));
        transaction.setOperation(Transaction.Operation.DEPOSIT);
        transaction.setUsername(user.getUsername());
        transaction.setUserId(userRepository.getByUsername(user.getUsername()).getId());
        transactionRepository.save(transaction);

    }

}
