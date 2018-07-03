package core.servlet.login;

import core.repository.AccountRepository;
import core.repository.UserRepository;
import core.model.Account;
import core.model.User;
import core.service.AccountService;
import core.service.UserService;
import core.servlet.helpers.LoginSession;
import core.servlet.helpers.Page;
import core.servlet.helpers.UserCounter;
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
    public UserRepository userRepository;

    @Mock
    public AccountRepository accountRepository;

    @Mock
    public HttpServletRequest req;

    @Mock
    public HttpServletResponse resp;

    @Mock
    public HttpSession session;

    @Mock
    public Page page;

    public LoginServlet loginServlet;
    public RegisterServlet registerServlet;
    public LogoutServlet logoutServlet;



    @Before
    public void setUp() {

        loginServlet = new LoginServlet(page, userRepository);
        logoutServlet = new LogoutServlet(page);
        registerServlet = new RegisterServlet(page, userRepository);

    }

    @After
    public void cleanUp() {


    }


    final User realUser = new User("admin", "password");

    @Test
    public void loginWithCorrectAccount() throws IOException {

        context.checking(new Expectations() {{

            oneOf(req).getParameter("username");
            will(returnValue(realUser.getUsername()));
            oneOf(req).getParameter("password");
            will(returnValue(realUser.getPassword()));
            oneOf(userRepository).checkPassword(with(any(User.class)));
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
            allowing(userRepository).checkPassword(with(any(User.class)));
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
            oneOf(userRepository).checkPassword(with(any(User.class)));
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
            oneOf(userRepository).checkPassword(with(any(User.class)));
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
            oneOf(userRepository).checkPassword(with(any(User.class)));
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
            oneOf(userRepository).checkPassword(with(any(User.class)));
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
            oneOf(userRepository).getByUsername("admin");
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
            oneOf(userRepository).getByUsername("admin");
            will(returnValue(realUser));

            oneOf(accountRepository).save(with(any(Account.class)));
            exactly(2).of(accountRepository).getAll();
            will(returnValue(accountList));
            oneOf(userRepository).save(with(any(User.class)));

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
