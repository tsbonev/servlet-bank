package core.Servlet.Transaction;

import core.DAO.TransactionDAOImpl;
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

        Page.getPage("view/transaction/history.jsp", req, resp);

    }
}
