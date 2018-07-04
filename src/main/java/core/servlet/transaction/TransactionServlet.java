package core.servlet.transaction;

import core.model.Transaction;
import core.repository.TransactionRepository;
import core.repository.UserRepository;
import core.servlet.filter.ConnectionPerRequest;
import core.servlet.helper.LoginSession;
import core.servlet.helper.Page;
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

    Page page;

    protected void setConnection(TransactionRepository transactionRepository,
                                 UserRepository userRepository){
        userRepository.setConnection(ConnectionPerRequest.connection.get());
        transactionRepository.setConnection(ConnectionPerRequest.connection.get());
    }

    public TransactionServlet(Page page,
                              UserRepository userRepository,
                              TransactionRepository transactionRepository){
        this.page = page;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    private static double maxAmount = Double.MAX_VALUE / 4;

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

        LoginSession session = (LoginSession) req.getSession().getAttribute("authorized");

        double amount = Double.parseDouble(req.getParameter("amount").replace(",", "."));
        Transaction.Operation operation = Transaction.Operation.valueOf(req.getParameter("action").toUpperCase());

        if(amount > maxAmount
                || amount <= 0){
            page.redirectTo("/account", resp, req,
                    "errorMessage", "Transactions of that size are not permitted!");
            return;
        }

        String amountToString = Double.toString(amount);

        if(!amountToString.matches(wholeNumber)
            && !amountToString.matches(fraction)
                && !amountToString.matches(trailingZeroFraction)
                && !amountToString.matches(leadingZeroFraction)
                ){
            page.redirectTo("/account", resp, req,
                    "errorMessage", "Transaction amount format invalid!");
            return;
        }

        String username = req.getParameter("username");

        if((!session.getUsername().equalsIgnoreCase("admin")
                && !username.equalsIgnoreCase(session.getUsername()))
                || !session.isAuthorized()
                ){
            page.redirectTo("/account", resp, req,
                    "errorMessage", "Action not permitted!");
            return;
        }

        Transaction transaction = new Transaction();
        transaction.setDate(Date.valueOf(LocalDate.now()));
        transaction.setAmount(amount);
        transaction.setOperation(operation);
        transaction.setUserId(userRepository.getByUsername(username).getId());

        transactionRepository.save(transaction);

        page.redirectTo("/account?username=" + username, resp, req,
                "successMessage", "Transaction successful!");


    }
}
