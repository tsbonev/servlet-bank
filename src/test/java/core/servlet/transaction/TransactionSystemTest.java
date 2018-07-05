package core.servlet.transaction;

import core.model.Transaction;
import core.model.User;
import core.repository.TransactionRepository;
import core.repository.UserRepository;
import core.servlet.helper.SessionHandler;
import core.servlet.helper.PageHandler;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

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
    private SessionHandler loginSession;

    @Mock
    private PageHandler page;

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

    @Test
    public void userAccountValidationFails() throws IOException, ServletException {

        User invalidUser = new User();

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

            oneOf(userRepository).getByUsername("username");
            will(returnValue(invalidUser));

            oneOf(page).redirectTo("/home", resp, req,
                    "errorMessage", "No such user exists!");

        }});

        accountServlet.doGet(req, resp);

    }

    @Test
    public void historyShouldShowGlobalScope() throws ServletException, IOException {

        List<Transaction> transactionList = new ArrayList<>();

        context.checking(new Expectations(){{

            oneOf(req).setAttribute("title", "History");

            oneOf(userRepository).setConnection(conn);
            oneOf(transactionRepository).setConnection(conn);

            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).getAttribute("authorized");
            will(returnValue(loginSession));

            oneOf(req).getParameter("page");
            will(returnValue("1"));

            oneOf(req).getParameter("scope");
            will(returnValue("global"));
            oneOf(req).setAttribute("globalScope", true);

            oneOf(transactionRepository).getAll(1);
            will(returnValue(transactionList));

            oneOf(transactionRepository).getRowsForUserId(0);
            will(returnValue(1));

            exactly(3).of(transactionRepository).getPageSize();
            will(returnValue(1));

            oneOf(req).setAttribute("hasNext", false);
            oneOf(req).setAttribute("currPage", 1);
            oneOf(req).setAttribute("totalPage", 1);

            oneOf(transactionRepository).fillUsernames(transactionList);
            oneOf(req).setAttribute("transactions", transactionList);

            oneOf(page).getPage("view/transaction/history.jsp", req, resp);


        }});

        historyServlet.doGet(req, resp);

    }

    @Test
    public void historyShouldShowForUser() throws ServletException, IOException {

        List<Transaction> transactionList = new ArrayList<>();

        User user = new User();
        user.setId(1);
        user.setUsername("username");

        context.checking(new Expectations(){{

            oneOf(req).setAttribute("title", "History");

            oneOf(userRepository).setConnection(conn);
            oneOf(transactionRepository).setConnection(conn);

            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).getAttribute("authorized");
            will(returnValue(loginSession));

            oneOf(req).getParameter("page");
            will(returnValue("1"));

            oneOf(req).getParameter("scope");
            will(returnValue(""));
            oneOf(req).setAttribute("globalScope", false);

            oneOf(loginSession).getUsername();
            will(returnValue("username"));
            oneOf(userRepository).getByUsername("username");
            will(returnValue(user));

            oneOf(transactionRepository).getByUserId(user.getId(), 1);
            will(returnValue(transactionList));

            oneOf(transactionRepository).getRowsForUserId(1);
            will(returnValue(1));

            exactly(3).of(transactionRepository).getPageSize();
            will(returnValue(1));

            oneOf(req).setAttribute("hasNext", false);
            oneOf(req).setAttribute("currPage", 1);
            oneOf(req).setAttribute("totalPage", 1);

            oneOf(transactionRepository).fillUsernames(transactionList);
            oneOf(req).setAttribute("transactions", transactionList);

            oneOf(page).getPage("view/transaction/history.jsp", req, resp);


        }});

        historyServlet.doGet(req, resp);

    }

    @Test
    public void transactionForCurrentUserSuccessful() throws ServletException, IOException {

        User user = new User();
        user.setId(1);
        user.setUsername("username");

        context.checking(new Expectations(){{

            oneOf(transactionRepository).setConnection(conn);
            oneOf(userRepository).setConnection(conn);

            oneOf(req).getParameter("amount");
            will(returnValue("2.0"));
            oneOf(req).getParameter("action");
            will(returnValue("DEPOSIT"));

            oneOf(req).getParameter("username");
            will(returnValue("username"));

            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).getAttribute("authorized");
            will(returnValue(loginSession));
            oneOf(loginSession).getUsername();
            will(returnValue("username"));
            oneOf(loginSession).isAuthorized();
            will(returnValue(true));

            oneOf(userRepository).getByUsername("username");
            will(returnValue(user));

            oneOf(transactionRepository).save(with(any(Transaction.class)));

            oneOf(page).redirectTo("/account?username=username", resp, req,
                    "successMessage", "Transaction successful!");

        }});

        transactionServlet.doPost(req, resp);

    }

    @Test
    public void transactionFormatIsInvalid() throws IOException, ServletException {

        User user = new User();
        user.setId(1);
        user.setUsername("username");

        context.checking(new Expectations(){{

            oneOf(transactionRepository).setConnection(conn);
            oneOf(userRepository).setConnection(conn);

            oneOf(req).getParameter("amount");
            will(returnValue("2ld0"));

            oneOf(page).redirectTo("/account", resp, req,
                    "errorMessage", "Transaction amount format invalid!");

        }});

        transactionServlet.doPost(req, resp);

    }

    @Test
    public void transactionAmountIsNotPermitted() throws ServletException, IOException {

        transactionServlet.setMaxAmount(999);

        context.checking(new Expectations(){{

            oneOf(transactionRepository).setConnection(conn);
            oneOf(userRepository).setConnection(conn);

            oneOf(req).getParameter("amount");
            will(returnValue("999999999.0"));
            oneOf(req).getParameter("action");
            will(returnValue("DEPOSIT"));

            oneOf(page).redirectTo("/account", resp, req,
                    "errorMessage", "Transactions of that size are not permitted!");

        }});

        transactionServlet.doPost(req, resp);

    }

    @Test
    public void transactionNotPermittedForThisUser() throws ServletException, IOException {

        User userDepositing = new User();
        userDepositing.setId(2);
        userDepositing.setUsername("some other user");

        User userTargeted = new User();
        userTargeted.setId(1);
        userTargeted.setUsername("username");

        context.checking(new Expectations(){
            {

                oneOf(transactionRepository).setConnection(conn);
                oneOf(userRepository).setConnection(conn);

                oneOf(req).getParameter("amount");
                will(returnValue("2.0"));
                oneOf(req).getParameter("action");
                will(returnValue("DEPOSIT"));

                oneOf(req).getParameter("username");
                will(returnValue(userTargeted.getUsername()));

                oneOf(req).getSession();
                will(returnValue(session));
                oneOf(session).getAttribute("authorized");
                will(returnValue(loginSession));
                oneOf(loginSession).getUsername();
                will(returnValue(userDepositing.getUsername()));


                oneOf(page).redirectTo("/account", resp, req,
                        "errorMessage", "Action not permitted!");

            }});

        transactionServlet.doPost(req, resp);

    }

    @Test
    public void adminCanAccessAnyAccounts() throws ServletException, IOException {

        User admin = new User();
        admin.setId(1);
        admin.setUsername("admin");

        User userTargeted = new User();
        userTargeted.setId(2);
        userTargeted.setUsername("username");

        context.checking(new Expectations(){{

            oneOf(transactionRepository).setConnection(conn);
            oneOf(userRepository).setConnection(conn);

            oneOf(req).getParameter("amount");
            will(returnValue("2.0"));
            oneOf(req).getParameter("action");
            will(returnValue("DEPOSIT"));

            oneOf(req).getParameter("username");
            will(returnValue(userTargeted.getUsername()));

            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).getAttribute("authorized");
            will(returnValue(loginSession));
            oneOf(loginSession).getUsername();
            will(returnValue(admin.getUsername()));
            oneOf(loginSession).isAuthorized();
            will(returnValue(true));

            oneOf(userRepository).getByUsername("username");
            will(returnValue(admin));

            oneOf(transactionRepository).save(with(any(Transaction.class)));

            oneOf(page).redirectTo("/account?username=username", resp, req,
                    "successMessage", "Transaction successful!");

        }});

        transactionServlet.doPost(req, resp);

    }

}
