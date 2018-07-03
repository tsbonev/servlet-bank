package core.servlet.transaction;

import core.model.Transaction;
import core.repository.TransactionRepository;
import core.repository.TransactionRepositoryImpl;
import core.repository.UserRepository;
import core.repository.UserRepositoryImpl;
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
import java.util.List;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

    TransactionRepository transactionRepository;
    UserRepository userRepository;

    Page page;

    public HistoryServlet(Page page, TransactionRepository transactionRepository,
                          UserRepository userRepository){
        this.page = page;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    protected void setConnection(TransactionRepository transactionRepository,
                                 UserRepository userRepository){
        userRepository.setConnection(ConnectionPerRequest.connection.get());
        transactionRepository.setConnection(ConnectionPerRequest.connection.get());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        setConnection(transactionRepository, userRepository);

        int currPage;

        try {
            currPage = (Integer.parseInt(req.getParameter("page")));
        }
        catch (Exception e){
            currPage = 1;
        }

        req.setAttribute("hasNext", transactionRepository.hasNextPage(currPage));
        req.setAttribute("currPage", currPage);
        req.setAttribute("totalPage", transactionRepository.lastPage());

        LoginSession session = (LoginSession) req.getSession().getAttribute("authorized");

        String scope = req.getParameter("scope");

        List<Transaction> transactions;

        if(!StringUtils.isEmpty(scope) && scope.equalsIgnoreCase("global")){

            transactions = transactionRepository.getAll(currPage);

        }else {
            transactions = transactionRepository.getByUserId(
                    userRepository.getByUsername(
                            session.getUsername()).getId(), currPage
            );
        }

        transactionRepository.fillUsernames(transactions);

        req.setAttribute("transactions", transactions);

        page.getPage("view/transaction/history.jsp", req, resp);

    }
}
