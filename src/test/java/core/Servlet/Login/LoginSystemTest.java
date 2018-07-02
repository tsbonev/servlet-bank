package core.Servlet.Login;

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
    public HttpSession session;

    public LoginServlet loginServlet;
    public RegisterServlet registerServlet;
    public LogoutServlet logoutServlet;

    public AccountService accountService;
    public UserService userService;


    @Before
    public void setUp() {

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
    public void cleanUp() {

        UserService.clearInstance();
        AccountService.clearInstance();
        UserCounter.clearInstance();

    }


    final User realUser = new User("admin", "password");

    @Test
    public void loginWithCorrectAccount() throws IOException {

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));
            oneOf(userDAO).checkPassword(with(any(User.class)));
            will(returnValue(true));
            oneOf(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));
            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).setAttribute("successMessage", "Successfully logged in!");
            oneOf(resp).sendRedirect("/home");

        }});

        loginServlet.doPost(req, resp);

        assertThat(UserCounter.getInstance().getUsersCount(), is(1));

    }

    @Test
    public void userCounterDoesNotGoUpWithSameLogin() throws IOException {

        int count = 5;

        context.checking(new Expectations() {{

            allowing(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            allowing(req).getParameter("password");
            will(returnValue(realUser.getPassword()));
            allowing(userDAO).checkPassword(with(any(User.class)));
            will(returnValue(true));
            allowing(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));
            allowing(req).getSession();
            will(returnValue(session));
            allowing(session).setAttribute("successMessage", "Successfully logged in!");
            allowing(resp).sendRedirect("/home");

        }});

        for (int i = 0; i < count; i++) {
            loginServlet.doPost(req, resp);
        }

        assertThat(UserCounter.getInstance().getUsersCount(), is(1));

    }

    @Test
    public void userCounterGoesUpWithDifferentLogins() throws IOException {

        String username1 = "admin";
        String username2 = "user";
        String password1 = "admin";
        String password2 = "user";

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(username1));
            oneOf(req).getParameter("password");
            will(returnValue(password1));
            oneOf(userDAO).checkPassword(with(any(User.class)));
            will(returnValue(true));
            oneOf(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));
            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).setAttribute("successMessage", "Successfully logged in!");
            oneOf(resp).sendRedirect("/home");

            oneOf(req).getParameter("username");
            will(returnValue(username2));
            oneOf(req).getParameter("password");
            will(returnValue(password2));
            oneOf(userDAO).checkPassword(with(any(User.class)));
            will(returnValue(true));
            oneOf(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));
            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).setAttribute("successMessage", "Successfully logged in!");
            oneOf(resp).sendRedirect("/home");

        }});

        for (int i = 0; i < 2; i++) {
            loginServlet.doPost(req, resp);
        }

        assertThat(UserCounter.getInstance().getUsersCount(), is(2));

    }

    @Test
    public void loginWithEmptyForm() throws IOException {

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(""));
            oneOf(req).getParameter("password");
            will(returnValue(null));
            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).setAttribute("errorMessage", "Something went wrong!");
            oneOf(resp).sendRedirect("/login");

        }});

        loginServlet.doPost(req, resp);

    }

    @Test
    public void loginWithIncorrectAccount() throws IOException {

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));
            oneOf(userDAO).checkPassword(with(any(User.class)));
            will(returnValue(false));
            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).setAttribute("errorMessage", "User not registered!");
            oneOf(resp).sendRedirect("/login");

        }});

        loginServlet.doPost(req, resp);

        assertThat(UserCounter.getInstance().getUsersCount(), is(0));

    }

    @Test
    public void userLogsOut() throws IOException {

        LoginSession loginSession = new LoginSession();

        loginSession.setUsername("admin");
        loginSession.setAuthorized(true);

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));
            oneOf(userDAO).checkPassword(with(any(User.class)));
            will(returnValue(true));
            oneOf(req).getSession().setAttribute("authorized", with(any(LoginSession.class)));
            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).setAttribute("successMessage", "Successfully logged in!");
            oneOf(resp).sendRedirect("/home");

            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).getAttribute("authorized");
            will(returnValue(loginSession));
            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).setAttribute("infoMessage", "User logged out!");
            oneOf(resp).sendRedirect("/home");

        }});

        loginServlet.doPost(req, resp);

        assertThat(UserCounter.getInstance().getUsersCount(), is(1));

        logoutServlet.doGet(req, resp);

        assertThat(UserCounter.getInstance().getUsersCount(), is(0));

    }

    @Test
    public void usernameIsTaken() throws ServletException, IOException {

        realUser.setId(1);

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));
            oneOf(userDAO).getByUsername("admin");
            will(returnValue(realUser));

            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).setAttribute("errorMessage", "Username taken!");
            oneOf(resp).sendRedirect("/register");

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

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));
            oneOf(userDAO).getByUsername("admin");
            will(returnValue(realUser));

            oneOf(accountDAO).save(with(any(Account.class)));
            exactly(2).of(accountDAO).getAll();
            will(returnValue(accountList));
            oneOf(userDAO).save(with(any(User.class)));

            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).setAttribute("successMessage", "User registered successfully!");
            oneOf(resp).sendRedirect("/home");

        }});

        registerServlet.doPost(req, resp);

    }

    @Test
    public void registerWithEmptyForm() throws ServletException, IOException {

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(""));
            oneOf(req).getParameter("password");
            will(returnValue(null));

            oneOf(req).getSession();
            will(returnValue(session));
            oneOf(session).setAttribute("errorMessage", "Something went wrong!");
            oneOf(resp).sendRedirect("/register");

        }});

        registerServlet.doPost(req, resp);

    }

}
