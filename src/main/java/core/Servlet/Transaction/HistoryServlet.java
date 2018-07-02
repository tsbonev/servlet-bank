package core.Servlet.Transaction;

import core.Model.Transaction;
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
import java.util.List;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

    TransactionService service = TransactionService.getInstance();
    UserService userService = UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        LoginSession session = (LoginSession) req.getSession().getAttribute("authorized");

        String scope = req.getParameter("scope");

        List<Transaction> transactions;

        if(!StringUtils.isEmpty(scope) && scope.equalsIgnoreCase("global")){

            transactions = service.getAllTransactions(1);

        }else {
            transactions = service.getTransactionsByUserId(
                    userService.getUserByUsername(
                            session.getUsername()).getId(), 1
            );
        }

        service.fillUsernames(transactions);

        req.setAttribute("transactions", transactions);

        Page.getPage("view/transaction/history.jsp", req, resp);

    }
}
