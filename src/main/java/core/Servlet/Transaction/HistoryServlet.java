package core.Servlet.Transaction;

import core.Model.Transaction;
import core.Service.TransactionService;
import core.Service.UserService;
import core.Servlet.Helpers.LoginCheck;
import core.Servlet.Helpers.LoginSession;
import core.Servlet.Helpers.Page;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

    TransactionService service = TransactionService.getInstance();
    UserService userService = UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        LoginSession session = (LoginSession) req.getSession().getAttribute("authorized");

        if(!LoginCheck.checkLogged(session, req, resp)) {
            return;
        }

        String scope = req.getParameter("scope");

        List<Transaction> transactions;

        if(!StringUtils.isEmpty(scope) && scope.equalsIgnoreCase("global")){

            transactions = service.getAllTransactions();

        }else {
            transactions = service.getTransactionsByUserId(
                    userService.getUserByUsername(
                            session.getUsername()).getId()
            );
        }

        service.fillUsernames(transactions);

        System.out.println("Passed check!");

        req.setAttribute("transactions", transactions);

        System.out.println(transactions.size());

        Page.getPage("view/transaction/history.jsp", req, resp);

    }
}
