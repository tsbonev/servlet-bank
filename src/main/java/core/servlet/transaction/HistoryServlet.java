package core.servlet.transaction;

import core.model.Transaction;
import core.model.User;
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
import java.util.List;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

    TransactionRepository transactionRepository;
    UserRepository userRepository;

    Page page;

    /**
     * Returns the total pages for the current user's transactions.
     *
     * @param size of all of the transactions
     * @return number of pages
     */
    private int totalPages(int size){
        return (size + transactionRepository.getPageSize() - 1) / transactionRepository.getPageSize();
    }

    /**
     * Checks if there is another page.
     *
     * @param size of the whole transaction list
     * @param currPage current page's index
     * @return whether a next page exists
     */
    private boolean hasNextPage(int size, int currPage){

        if (size <= transactionRepository.getPageSize() * currPage)
            return false;

        return true;

    }


    public HistoryServlet(Page page, TransactionRepository transactionRepository,
                          UserRepository userRepository){
        this.page = page;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Gets the connection from the ThreadLocal and
     * injects it into the repositories.
     *
     * @param transactionRepository
     * @param userRepository
     */
    protected void setConnection(TransactionRepository transactionRepository,
                                 UserRepository userRepository){
        userRepository.setConnection(ConnectionPerRequest.connection.get());
        transactionRepository.setConnection(ConnectionPerRequest.connection.get());
    }

    /**
     * Sets the connection for the repositories
     * and returns a jsp history page with
     * a paginated list of transactions for
     * the requested user.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setAttribute("title", "History");

        setConnection(transactionRepository, userRepository);

        int currPage;

        try {
            currPage = (Integer.parseInt(req.getParameter("page")));
        }
        catch (Exception e){
            currPage = 1;
        }

        LoginSession session = (LoginSession) req.getSession().getAttribute("authorized");

        String scope = req.getParameter("scope");

        int userId = 0;

        List<Transaction> transactions;

        if(!StringUtils.isEmpty(scope) && scope.equalsIgnoreCase("global")){
            req.setAttribute("globalScope", true);
            transactions = transactionRepository.getAll(currPage);

        }else {
            req.setAttribute("globalScope", false);
            userId = userRepository.getByUsername(
                    session.getUsername()).getId();

            transactions = transactionRepository.getByUserId(
                    userId, currPage
            );
        }

        int rowCount = transactionRepository.getRowsForUserId(userId);

        req.setAttribute("hasNext", hasNextPage(rowCount, currPage));
        req.setAttribute("currPage", currPage);
        req.setAttribute("totalPage", totalPages(rowCount));


        transactionRepository.fillUsernames(transactions);

        req.setAttribute("transactions", transactions);

        page.getPage("view/transaction/history.jsp", req, resp);

    }
}
