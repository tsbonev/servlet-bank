package core.servlet.transaction;

import core.model.Transaction;
import core.repository.TransactionRepository;
import core.repository.UserRepository;
import core.servlet.filter.ConnectionPerRequest;
import core.servlet.helper.LoginSessionImpl;
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

        LoginSessionImpl session = (LoginSessionImpl) req.getSession().getAttribute("authorized");

        int currPage = getCurrentPage(req);

        boolean scopeIsGlobal = judgeScope(req);

        int userId = scopeIsGlobal ?
                userRepository.getByUsername(
                        session.getUsername()).getId() : 0;

        List<Transaction> transactions = getTransactions(currPage, scopeIsGlobal, userId);

        setUpPagination(userId, currPage, req, transactions);

        page.getPage("view/transaction/history.jsp", req, resp);

    }

    /**
     * Sets up the pagination attributes
     * needed for the jsp to properly paginate
     * the transaction results.
     *
     * @param userId to get the row count for
     * @param currPage to set the offset to
     * @param req servlet request
     * @param transactions the list of transactions
     */
    private void setUpPagination(int userId, int currPage,
                                 HttpServletRequest req, List<Transaction> transactions){

        int rowCount = transactionRepository.getRowsForUserId(userId);

        req.setAttribute("hasNext", hasNextPage(rowCount, currPage));
        req.setAttribute("currPage", currPage);
        req.setAttribute("totalPage", totalPages(rowCount));

        transactionRepository.fillUsernames(transactions);

        req.setAttribute("transactions", transactions);

    }


    /**
     * Checks whether the request has
     * a parameter defining the scope
     * as global.
     *
     * @param req servlet request
     * @return result of the check
     */
    private boolean judgeScope(HttpServletRequest req){

        String scope = req.getParameter("scope");

        if(!StringUtils.isEmpty(scope) && scope.equalsIgnoreCase("global")) {
            req.setAttribute("globalScope", true);
            return true;
        }
            req.setAttribute("globalScope", false);
            return false;

    }

    /**
     * Returns the transaction list for
     * the current page, user and scope.
     *
     * @param currPage current page
     * @param scopeIsGlobal scope to get the list from
     * @param userId id to get the list from
     * @return the list of transactions paginated
     */
    private List<Transaction> getTransactions(int currPage,
                                              boolean scopeIsGlobal, int userId){

        List<Transaction> transactions;

        if(scopeIsGlobal){
            transactions = transactionRepository.getAll(currPage);

        }else {

            transactions = transactionRepository.getByUserId(
                    userId, currPage
            );
        }

        return transactions;
    }

    /**
     * Returns the current page from a parameter,
     * if the parameter is invalid default to the first page.
     *
     * @param req servlet request
     * @return the current page
     */
    private int getCurrentPage(HttpServletRequest req){

        int currPage;

        String page = req.getParameter("page");

        try {
            currPage = (Integer.parseInt(page));
        }
        catch (NumberFormatException e){
            currPage = 1;
        }

        return currPage;

    }
}
