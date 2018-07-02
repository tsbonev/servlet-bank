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

    TransactionService service = TransactionService.getInstance();
    UserService userService = UserService.getInstance();
    AccountService accountService = AccountService.getInstance();

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
