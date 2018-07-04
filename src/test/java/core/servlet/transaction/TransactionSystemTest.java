package core.servlet.transaction;

import core.model.Transaction;
import core.model.User;
import core.repository.TransactionRepository;
import core.repository.UserRepository;
import core.servlet.helper.LoginSession;
import core.servlet.helper.Page;
import core.servlet.helper.UserCounter;
import core.servlet.login.LoginServlet;
import core.servlet.login.LogoutServlet;
import core.servlet.login.RegisterServlet;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;

public class TransactionSystemTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private HttpSession session;

    @Mock
    private LoginSession loginSession;

    @Mock
    private Page page;

    @Mock
    private Connection conn;

    private TransactionServlet transactionServlet;
    private HistoryServlet historyServlet;
    private AccountServlet accountServlet;



    @Before
    public void setUp() {

        transactionServlet = new TransactionServlet(page, userRepository, transactionRepository){
            @Override
            protected void setConnection(TransactionRepository transactionRepository,
                                         UserRepository userRepository){
                userRepository.setConnection(conn);
                transactionRepository.setConnection(conn);
            }
        };
        historyServlet = new HistoryServlet(page, transactionRepository, userRepository){
            @Override
            protected void setConnection(TransactionRepository transactionRepository,
                                         UserRepository userRepository){
                userRepository.setConnection(conn);
                transactionRepository.setConnection(conn);
            }
        };
        accountServlet = new AccountServlet(page, transactionRepository, userRepository){
            @Override
            protected void setConnection(TransactionRepository transactionRepository,
                                         UserRepository userRepository){
                userRepository.setConnection(conn);
                transactionRepository.setConnection(conn);
            }
        };

    }

    @Test
    public void accountShouldBeCalculated() throws ServletException, IOException {

        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setId(1);

        context.checking(new Expectations(){{

            oneOf(req).setAttribute("title", "Account");

            oneOf(userRepository).setConnection(conn);
            oneOf(transactionRepository).setConnection(conn);

            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(req).getParameter("username");
            will(returnValue("username"));

            oneOf(session).getAttribute("authorized");
            will(returnValue(loginSession));
            oneOf(req).getParameter("username");
            will(returnValue("username"));
            oneOf(userRepository).getByUsername("username");
            will(returnValue(user));

            oneOf(transactionRepository).getBalance(user.getId());
            will(returnValue(1.0));

            oneOf(req).setAttribute("balance", 1.0);
            oneOf(req).setAttribute("passedUsername", "username");

            oneOf(page).getPage("view/transaction/account.jsp", req, resp);

        }});

        accountServlet.doGet(req, resp);

    }

}
