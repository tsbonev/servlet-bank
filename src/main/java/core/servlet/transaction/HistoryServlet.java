package core.servlet.transaction;

import core.model.Transaction;
import core.service.TransactionService;
import core.service.UserService;
import core.servlet.helpers.LoginSession;
import core.servlet.helpers.PageImpl;
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

        int currPage;

        try {
            currPage = (Integer.parseInt(req.getParameter("page")));
        }
        catch (Exception e){
            currPage = 1;
        }

        req.setAttribute("hasNext", service.hasNextPage(currPage));
        req.setAttribute("currPage", currPage);
        req.setAttribute("totalPage", service.lastPage());

        LoginSession session = (LoginSession) req.getSession().getAttribute("authorized");

        String scope = req.getParameter("scope");

        List<Transaction> transactions;

        if(!StringUtils.isEmpty(scope) && scope.equalsIgnoreCase("global")){

            transactions = service.getAllTransactions(currPage);

        }else {
            transactions = service.getTransactionsByUserId(
                    userService.getUserByUsername(
                            session.getUsername()).getId(), currPage
            );
        }

        service.fillUsernames(transactions);

        req.setAttribute("transactions", transactions);

        PageImpl.getPage("view/transaction/history.jsp", req, resp);

    }
}
