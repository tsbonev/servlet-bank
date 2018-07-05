package core.servlet.transaction;

import core.model.Transaction;
import core.repository.TransactionRepository;
import core.repository.UserRepository;
import core.servlet.filter.ConnectionPerRequest;
import core.servlet.helper.SessionHandler;
import core.servlet.helper.PageHandler;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

@WebServlet("/transaction")
public class TransactionServlet extends HttpServlet {

    private static String fraction = "^[1-9]{1}[0-9]{0,9}[.,]{1}[0-9]{1,5}$";
    private static String wholeNumber = "^[1-9]{1}[0-9]{0,9}$";
    private static String leadingZeroFraction = "^[0]{1}[.,]{1}[0-9]{0,4}[1-9]{1}$";
    private static String trailingZeroFraction = "^[0]{1}[.,]{1}[1-9]{0,4}[1-9]{1}$";

    UserRepository userRepository;
    TransactionRepository transactionRepository;

    PageHandler page;

    protected void setConnection(TransactionRepository transactionRepository,
                                 UserRepository userRepository){
        userRepository.setConnection(ConnectionPerRequest.connection.get());
        transactionRepository.setConnection(ConnectionPerRequest.connection.get());
    }

    public TransactionServlet(PageHandler page,
                              UserRepository userRepository,
                              TransactionRepository transactionRepository){
        this.page = page;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    private static double maxAmount = Double.MAX_VALUE / 8;

    public void setMaxAmount(double amount){
        maxAmount = amount;
    }

    /**
     * Gets a transaction form and
     * sets the title to Transaction.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setAttribute("title", "Transaction");

        String action = req.getParameter("action");
        action = StringUtils.capitalize(action);
        req.setAttribute("action", action);

        page.getPage("view/transaction/doTransaction.jsp", req, resp);

    }

    /**
     * Sets the connection of the repositories,
     * gets the session, validates the transaction amount,
     * checks if the request was sent by a valid user
     * to a valid account and saves the transaction
     * if all checks are passed.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        setConnection(transactionRepository, userRepository);

        String amountString = req.getParameter("amount");

        if(!validateAmountFormat(amountString, req, resp)) return;

        double amount = parseAmount(amountString);
        Transaction.Operation operation = getOperation(req);

        if(!validateAmountSize(amount, req, resp)) return;

        String username = req.getParameter("username");

        if(!validateUser(resp, req, username)) return;

        Transaction transaction = setUpTransaction(amount, operation, username);

        transactionRepository.save(transaction);

        page.redirectTo("/account?username=" + username, resp, req,
                "successMessage", "Transaction successful!");
    }

    /**
     * Creates a transaction from a given amount, operation
     * and username.
     *
     * @param amount for the transaction
     * @param operation of the transaction
     * @param username of the transaction initiator
     * @return the built transaction
     */
    private Transaction setUpTransaction(double amount, Transaction.Operation operation, String username){
        Transaction transaction = new Transaction();
        transaction.setDate(Date.valueOf(LocalDate.now()));
        transaction.setAmount(amount);
        transaction.setOperation(operation);
        transaction.setUserId(userRepository.getByUsername(username).getId());
        return transaction;
    }

    /**
     * Validates whether the user that started
     * a transaction is the user the transaction
     * is aimed at or is the admin.
     *
     * @param resp servlet response
     * @param req servlet request
     * @param username to check
     * @return result of the check
     * @throws IOException
     */
    private boolean validateUser(HttpServletResponse resp, HttpServletRequest req, String username) throws IOException {

        SessionHandler session = (SessionHandler) req.getSession().getAttribute("authorized");

        String sessionUsername = session.getUsername();

        if((!sessionUsername.equalsIgnoreCase("admin")
                && !username.equalsIgnoreCase(sessionUsername))
                || !session.isAuthorized()
                ){
            page.redirectTo("/account", resp, req,
                    "errorMessage", "Action not permitted!");
            return false;
        }
        return true;
    }

    /**
     * Validates whether the string amount passed
     * is in the correct format.
     *
     * @param amount passed as string
     * @param req servlet request
     * @param resp servlet response
     * @return result of the validation
     * @throws IOException
     */
    private boolean validateAmountFormat(String amount,
                                         HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(!amount.matches(wholeNumber)
                && !amount.matches(fraction)
                && !amount.matches(trailingZeroFraction)
                && !amount.matches(leadingZeroFraction)
                ){
            page.redirectTo("/account", resp, req,
                    "errorMessage", "Transaction amount format invalid!");
            return false;
        }
        return true;
    }

    /**
     * Validates whether the double amount exceeds
     * the max amount permitted.
     *
     * @param amount to check
     * @param req servlet request
     * @param resp servlet response
     * @return the result of the check
     * @throws IOException
     */
    private boolean validateAmountSize(double amount, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(amount > maxAmount
                || amount <= 0){
            page.redirectTo("/account", resp, req,
                    "errorMessage", "Transactions of that size are not permitted!");
            return false;
        }
        return true;
    }

    /**
     * Parses a string into a double.
     *
     * @param amount string to be parsed
     * @return parsed double
     */
    private double parseAmount(String amount){
        return Double.parseDouble(amount.replace(",", "."));
    }

    /**
     * Parses a string to an operation.
     *
     * @param req servlet request
     * @return parsed operation
     */
    private Transaction.Operation getOperation(HttpServletRequest req){
        return Transaction.Operation.valueOf(req.getParameter("action").toUpperCase());
    }

}
