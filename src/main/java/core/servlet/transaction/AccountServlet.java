package core.servlet.transaction;

import core.model.User;
import core.repository.TransactionRepository;
import core.repository.UserRepository;
import core.servlet.filter.ConnectionPerRequest;
import core.servlet.helper.LoginSession;
import core.servlet.helper.Page;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {

    Page page;
    TransactionRepository transactionRepository;
    UserRepository userRepository;

    public AccountServlet(Page page, TransactionRepository transactionRepository,
                          UserRepository userRepository){
        this.page = page;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    protected void setConnection(TransactionRepository transactionRepository,
                                 UserRepository userRepository){
        userRepository.setConnection(ConnectionPerRequest.connection.get());
        transactionRepository.setConnection(ConnectionPerRequest.connection.get());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        setConnection(transactionRepository, userRepository);

        LoginSession session = (LoginSession) req.getSession().getAttribute("authorized");

        String username = req.getParameter("username");

        if(username == null){
            username = session.getUsername();
        }

        User user = userRepository.getByUsername(username);

        if(user.getId() == 0){
            page.redirectTo("/home", resp, req,
                    "errorMessage", "No such user exists!");
            return;
        }

        double balance = transactionRepository.getBalance(user.getId());

        req.setAttribute("balance", balance);
        req.setAttribute("passedUsername", req.getParameter("username"));

        page.getPage("view/transaction/account.jsp", req, resp);

    }
}
