package core.Servlet.Transaction;

import core.Model.Account;
import core.Model.Transaction;
import core.Service.AccountService;
import core.Service.TransactionService;
import core.Service.UserService;
import core.Servlet.Helpers.LoginSession;
import core.Servlet.Helpers.Page;
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
    private static String wholeNumber = "^[1-9]{1}[0-9]{0,9}$/";
    private static String leadingZeroFraction = "^[0]{1}[.,]{1}[0-9]{0,4}[1-9]{1}$";
    private static String trailingZeroFraction = "^[0]{1}[.,]{1}[1-9]{0,4}[1-9]{1}$";


    TransactionService service = TransactionService.getInstance();
    UserService userService = UserService.getInstance();
    AccountService accountService = AccountService.getInstance();

    private static double maxAmount = Double.MAX_VALUE / 4;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String action = req.getParameter("action");
        action = StringUtils.capitalize(action);
        req.setAttribute("action", action);

        Page.getPage("view/transaction/doTransaction.jsp", req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        LoginSession session = (LoginSession) req.getSession().getAttribute("authorized");

        double amount = Double.parseDouble(req.getParameter("amount").replace(",", "."));
        Transaction.Operation operation = Transaction.Operation.valueOf(req.getParameter("action").toUpperCase());

        Account account = userService.getUserAccount(session.getUsername());

        if(amount > maxAmount
                || amount <= 0){
            Page.redirectTo("/account", resp, req,
                    "errorMessage", "Transactions of that size are not permitted!");
            return;
        }

        String amountToString = Double.toString(amount);

        if(!amountToString.matches(wholeNumber)
            || !amountToString.matches(fraction)
                || !amountToString.matches(trailingZeroFraction)
                || !amountToString.matches(leadingZeroFraction)
                ){
            Page.redirectTo("/account", resp, req,
                    "errorMessage", "Transaction amount format invalid!");
            return;
        }

        Transaction transaction = new Transaction();
        transaction.setDate(Date.valueOf(LocalDate.now()));
        transaction.setAmount(amount);
        transaction.setOperation(operation);
        transaction.setUserId(userService.getUserByUsername(session.getUsername()).getId());

        if(operation.equals(Transaction.Operation.WITHDRAW)){
            amount *= -1;
        }

        account.setAmount(account.getAmount() + amount);
        accountService.updateAccount(account);
        service.saveTransaction(transaction);

        Page.redirectTo("/account?username=" + session.getUsername(), resp, req,
                "successMessage", "Transaction successful!");


    }
}
