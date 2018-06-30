package core.Servlet;

import core.DAO.AccountDAO;
import core.DAO.UserDAO;
import core.Model.Account;
import core.Model.User;
import core.Service.AccountService;
import core.Service.UserService;
import core.Servlet.Helpers.LoginSession;
import core.Servlet.Helpers.UserCounter;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

@SuppressWarnings("Duplicates")
public class LoginSystemTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    @Mock
    public UserDAO userDAO;

    @Mock
    public AccountDAO accountDAO;

    @Mock
    public HttpServletRequest req;

    @Mock
    public HttpServletResponse resp;

    @Mock
    public RequestDispatcher rd;

    @Mock
    public HttpSession session;

    public LoginServlet loginServlet;
    public RegisterServlet registerServlet;
    public LogoutServlet logoutServlet;

    public AccountService accountService;
    public UserService userService;


    @Before
    public void setUp(){

        UserService.clearInstance();
        AccountService.clearInstance();
        UserCounter.clearInstance();

        userService = UserService.getInstance(userDAO);
        accountService = AccountService.getInstance(accountDAO);

        loginServlet = new LoginServlet();
        logoutServlet = new LogoutServlet();
        registerServlet = new RegisterServlet();

    }

    @After
    public void cleanUp(){

        UserService.clearInstance();
        AccountService.clearInstance();
        UserCounter.clearInstance();

    }


    final User realUser = new User("admin", "admin");

    @Test
    public void loginWithCorrectAccount() throws ServletException, IOException {

        context.checking(new Expectations(){{

            oneOf(req).getParameter("username"); will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password"); will(returnValue(realUser.getPassword()));
            oneOf(userDAO).checkPassword(with(any(User.class))); will(returnValue(true));
            oneOf(req).setAttribute("successMessage", "Successfully logged in!");
            oneOf(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));
            oneOf(req).getRequestDispatcher("/home"); will(returnValue(rd));
            oneOf(rd).forward(req, resp);

        }});

        loginServlet.doPost(req, resp);

        assertThat(UserCounter.getInstance().getUsersCount(), is(1));

    }

    @Test
    public void userCounterDoesNotGoUpWithSameLogin() throws ServletException, IOException {

        int count = 5;

        context.checking(new Expectations(){{

            allowing(req).getParameter("username"); will(returnValue(realUser.getUsername()));
            allowing(req).getParameter("password"); will(returnValue(realUser.getPassword()));
            allowing(userDAO).checkPassword(with(any(User.class))); will(returnValue(true));
            allowing(req).setAttribute("successMessage", "Successfully logged in!");
            allowing(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));
            allowing(req).getRequestDispatcher("/home"); will(returnValue(rd));
            allowing(rd).forward(req, resp);

        }});

        for(int i = 0; i < count; i++){
            loginServlet.doPost(req, resp);
        }

        assertThat(UserCounter.getInstance().getUsersCount(), is(1));

    }

    @Test
    public void userCounterGoesUpWithDifferentLogins() throws ServletException, IOException {

        String username1 = "admin";
        String username2 = "user";
        String password1 = "admin";
        String password2 = "user";

        context.checking(new Expectations(){{

            oneOf(req).getParameter("username"); will(returnValue(username1));
            oneOf(req).getParameter("password"); will(returnValue(password1));
            oneOf(userDAO).checkPassword(with(any(User.class))); will(returnValue(true));
            oneOf(req).setAttribute("successMessage", "Successfully logged in!");
            oneOf(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));
            oneOf(req).getRequestDispatcher("/home"); will(returnValue(rd));
            oneOf(rd).forward(req, resp);

            oneOf(req).getParameter("username"); will(returnValue(username2));
            oneOf(req).getParameter("password"); will(returnValue(password2));
            oneOf(userDAO).checkPassword(with(any(User.class))); will(returnValue(true));
            oneOf(req).setAttribute("successMessage", "Successfully logged in!");
            oneOf(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));
            oneOf(req).getRequestDispatcher("/home"); will(returnValue(rd));
            oneOf(rd).forward(req, resp);

        }});

        for(int i = 0; i < 2; i++){
            loginServlet.doPost(req, resp);
        }

        assertThat(UserCounter.getInstance().getUsersCount(), is(2));

    }

    @Test
    public void loginWithEmptyForm() throws ServletException, IOException {

        context.checking(new Expectations(){{

            oneOf(req).getParameter("username"); will(returnValue(""));
            oneOf(req).getParameter("password"); will(returnValue(null));
            oneOf(req).setAttribute("errorMessage", "Something went wrong!");
            oneOf(req).setAttribute("title", "Login");
            oneOf(req).getRequestDispatcher("view/user/login.jsp"); will(returnValue(rd));
            oneOf(rd).forward(req, resp);

        }});

        loginServlet.doPost(req, resp);

    }

    @Test
    public void loginWithIncorrectAccount() throws ServletException, IOException {

        context.checking(new Expectations(){{

            oneOf(req).getParameter("username"); will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password"); will(returnValue(realUser.getPassword()));
            oneOf(userDAO).checkPassword(with(any(User.class))); will(returnValue(false));
            oneOf(req).setAttribute("errorMessage", "User not registered!");
            oneOf(req).setAttribute("title", "Login");
            oneOf(req).getRequestDispatcher("view/user/login.jsp"); will(returnValue(rd));
            oneOf(rd).forward(req, resp);

        }});

        loginServlet.doPost(req, resp);

        assertThat(UserCounter.getInstance().getUsersCount(), is(0));

    }

    @Test
    public void userLogsOut() throws ServletException, IOException {

        LoginSession loginSession = new LoginSession();

        loginSession.setUsername("admin");
        loginSession.setAuthorized(true);

        context.checking(new Expectations(){{

            oneOf(req).getParameter("username"); will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password"); will(returnValue(realUser.getPassword()));
            oneOf(userDAO).checkPassword(with(any(User.class))); will(returnValue(true));
            oneOf(req).setAttribute("successMessage", "Successfully logged in!");
            oneOf(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));
            oneOf(req).getRequestDispatcher("/home"); will(returnValue(rd));
            oneOf(rd).forward(req, resp);
            oneOf(req).getSession(); will(returnValue(session));
            oneOf(session).getAttribute("authorized"); will(returnValue(loginSession));
            oneOf(req).setAttribute("infoMessage", "User logged out!");
            oneOf(req).getRequestDispatcher("/home"); will(returnValue(rd));
            oneOf(rd).forward(req, resp);

        }});

        loginServlet.doPost(req, resp);

        assertThat(UserCounter.getInstance().getUsersCount(), is(1));

        logoutServlet.doGet(req, resp);

        assertThat(UserCounter.getInstance().getUsersCount(), is(0));

    }

    @Test
    public void usernameIsTaken() throws ServletException, IOException {

        realUser.setId(1);

        context.checking(new Expectations(){{

            oneOf(req).getParameter("username"); will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password"); will(returnValue(realUser.getPassword()));
            oneOf(userDAO).getByUsername("admin"); will(returnValue(realUser));

            oneOf(req).setAttribute("errorMessage", "Username taken!");
            oneOf(req).setAttribute("title", "Register");

            oneOf(req).getRequestDispatcher("view/user/register.jsp"); will(returnValue(rd));
            oneOf(rd).forward(req, resp);

        }});

        registerServlet.doPost(req, resp);

    }

    @Test
    public void registerUser() throws ServletException, IOException {

        Account account = new Account();
        account.setId(1);

        List<Account> accountList = new ArrayList<>();

        accountList.add(account);

        realUser.setId(0);

        context.checking(new Expectations(){{

            oneOf(req).getParameter("username"); will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password"); will(returnValue(realUser.getPassword()));
            oneOf(userDAO).getByUsername("admin"); will(returnValue(realUser));
            oneOf(req).setAttribute("title", "Login");

            oneOf(accountDAO).save(with(any(Account.class)));
            exactly(2).of(accountDAO).getAll(); will(returnValue(accountList));
            oneOf(userDAO).save(with(any(User.class)));


            oneOf(req).setAttribute("successMessage", "User registered successfully!");
            oneOf(req).getRequestDispatcher("/home"); will(returnValue(rd));
            oneOf(rd).forward(req, resp);

        }});

        registerServlet.doPost(req, resp);

    }

    @Test
    public void registerWithEmptyForm() throws ServletException, IOException {

        context.checking(new Expectations(){{

            oneOf(req).getParameter("username"); will(returnValue(""));
            oneOf(req).getParameter("password"); will(returnValue(null));
            oneOf(req).setAttribute("errorMessage", "Something went wrong!");
            oneOf(req).setAttribute("title", "Register");
            oneOf(req).getRequestDispatcher("view/user/register.jsp"); will(returnValue(rd));
            oneOf(rd).forward(req, resp);

        }});

        registerServlet.doPost(req, resp);

    }

}
